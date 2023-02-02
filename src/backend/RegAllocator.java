package backend;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.Set;
import java.util.Stack;

import asm.Function;
import asm.inst.BaseInst;
import asm.inst.LoadInst;
import asm.inst.MvInst;
import asm.inst.StoreInst;
import asm.operand.PhysicalReg;
import asm.operand.Reg;
import asm.operand.StackOffset;
import asm.operand.VirtualReg;
import asm.operand.StackOffset.StackOffsetType;
import backend.InterferenceGraph.Edge;
import backend.InterferenceGraph.Node;

public class RegAllocator {
  /**
   * Node work-lists, sets, and stacks.
   * 
   * The following lists and sets are always mutually disjoint and
   * every node is always in exactly one of the sets or lists.
   */
  /** machine registers, preassigned a color. */
  final HashSet<Reg> precolored = new LinkedHashSet<>(PhysicalReg.regMap.values());
  /** temporary registers, not precolored and not yet processed. */
  final HashSet<Reg> initial = new LinkedHashSet<>();
  /** list of low-degree non-move-related nodes. */
  final HashSet<Reg> simplifyWorklist = new LinkedHashSet<>();
  /** low-degree move-related nodes. */
  final HashSet<Reg> freezeWorklist = new LinkedHashSet<>();
  /** high-degree nodes. */
  final HashSet<Reg> spillWorklist = new LinkedHashSet<>();
  /** nodes marked for spilling during this round; initially empty. */
  final HashSet<Reg> spilledNodes = new LinkedHashSet<>();
  /**
   * registers that have been coalesced; when u<-v is coalesced, v
   * is added to this set and u is put back on some work-list.
   */
  final HashSet<Reg> coalescedNodes = new LinkedHashSet<>();
  /**
   * when a move u<-v has been coalesced, and v put in {@code
   * coalescedNodes}, then alias(v) = u. Path compression is used.
   */
  final HashMap<Reg, Reg> alias = new HashMap<>();
  /** nodes successfully colored. */
  final HashSet<Reg> coloredNodes = new LinkedHashSet<>();

  /** stack containing temporaries removed from the graph. */
  final Stack<Reg> selectStack = new Stack<>();

  /**
   * Move sets.
   * 
   * There are five sets of move instructions, and every move is in
   * exactly one of these sets (after Build through the end of Main).
   */
  /** moves that have been coalesced. */
  final HashSet<MvInst> coalescedMoves = new LinkedHashSet<>();
  /** moves whose source and target interfere. */
  final HashSet<MvInst> constrainedMoves = new LinkedHashSet<>();
  /** moves that will no longer be considered for coalescing. */
  final HashSet<MvInst> frozenMoves = new LinkedHashSet<>();
  /** moves enabled for possible coalescing. */
  final HashSet<MvInst> worklistMoves = new LinkedHashSet<>();
  /** moves not yet ready for coalescing. */
  final HashSet<MvInst> activeMoves = new LinkedHashSet<>();

  final static int K = PhysicalReg.assignable.size();
  Function curFunc;
  InterferenceGraph G = new InterferenceGraph();

  /**
   * Interference graph for spilled registers, used to color
   * spilled registers
   */
  InterferenceGraph GSpilled = new InterferenceGraph();
  /**
   * Temporary registers introduced by the load and store of
   * previously spilled registers.
   */
  final HashSet<Reg> introduced = new HashSet<>();

  public void runOnModule(asm.Module module) {
    for (var func : module.funcs) {
      runOnFunc(func);
    }
  }

  public void runOnFunc(Function func) {
    introduced.clear();
    curFunc = func;
    graphColoring();
    removeUselessMv();
  }

  public void graphColoring() {
    init();
    new LivenessAnalyzer().runOnFunc(curFunc);
    build();
    makeWorklist();
    do {
      if (!simplifyWorklist.isEmpty())
        simplify();
      else if (!worklistMoves.isEmpty())
        coalesce();
      else if (!freezeWorklist.isEmpty())
        freeze();
      else if (!spillWorklist.isEmpty())
        selectSpill();
    } while (!simplifyWorklist.isEmpty() || !worklistMoves.isEmpty() || !freezeWorklist.isEmpty()
        || !spillWorklist.isEmpty());

    assignColors();
    if (!spilledNodes.isEmpty()) {
      initSpilled();
      buildSpilled();
      assignColorsSpilled();
      rewriteProgram();
      graphColoring();
    }
  }

  void removeUselessMv() {
    for (var block : curFunc.blocks) {
      var oldInsts = block.insts;
      block.insts = new ArrayList<BaseInst>();
      for (var inst : oldInsts) {
        if (inst instanceof MvInst mv && mv.rs.color == mv.rd.color)
          continue;
        block.insts.add(inst);
      }
    }
  }

  void init() {
    initial.clear();
    simplifyWorklist.clear();
    freezeWorklist.clear();
    spillWorklist.clear();
    spilledNodes.clear();
    coalescedNodes.clear();
    alias.clear();
    coloredNodes.clear();
    selectStack.clear();

    coalescedMoves.clear();
    constrainedMoves.clear();
    frozenMoves.clear();
    worklistMoves.clear();
    activeMoves.clear();

    G.init();

    // add all virtual registers to initial
    for (var block : curFunc.blocks) {
      for (var inst : block.insts) {
        for (var reg : inst.uses())
          if (reg instanceof VirtualReg)
            initial.add(reg);
        for (var reg : inst.defs())
          if (reg instanceof VirtualReg)
            initial.add(reg);
      }
    }

    for (var reg : initial) {
      reg.color = null;
      reg.node.init(false);
    }
    for (var reg : precolored) {
      reg.color = (PhysicalReg) reg;
      reg.node.init(true);
    }

    // frequency of a register = sum (uses + defs) * 10^loopDepth
    for (var block : curFunc.blocks) {
      double weight = Math.pow(10, block.loopDepth);
      for (var inst : block.insts) {
        for (var reg : inst.defs())
          reg.node.frequency += weight;
        for (var reg : inst.uses())
          reg.node.frequency += weight;
      }
    }
  }

  void initSpilled() {
    GSpilled.init();
    for (var node : spilledNodes) {
      node.nodeSpilled.init(false);
    }
  }

  /**
   * Constructs the interference graph using the results of static
   * liveness analysis, and initializes the {@code worklistMoves} to
   * contain all the moves in the program.
   */
  void build() {
    for (var block : curFunc.blocks) {
      var lives = new HashSet<>(block.liveOut);

      // iterate in reverse order
      for (int i = block.insts.size() - 1; i >= 0; i--) {
        var inst = block.insts.get(i);
        var uses = inst.uses();
        var defs = inst.defs();
        if (inst instanceof MvInst mv) {
          // Note: not to create artifical interferences between the source
          // and destination of a move. Consider this program:
          // t <- s (MOVE instruction)
          // use of s
          // use of t
          // After the MOVE instruction, both s and t are live.
          // But we do not need separate registers for s and t, since they
          // contain the same value.
          // The solution is just not to add an interference edge (t, s) in
          // this case. Of course, if there is a later NON-MOVE definition
          // of t while s is still live, that will create the interference
          // edge (t, s).
          lives.removeAll(uses);
          uses.forEach(reg -> reg.node.moveList.add(mv));
          defs.forEach(reg -> reg.node.moveList.add(mv));
          worklistMoves.add(mv);
        }

        lives.add(PhysicalReg.zero);
        lives.addAll(defs);
        for (var def : defs) {
          for (var live : lives) {
            G.addEdge(live, def);
          }
        }
        lives.removeAll(defs);
        lives.addAll(uses);
      }
    }
  }

  /** Note: this function changes the input set. */
  HashSet<Reg> filterSpilled(HashSet<Reg> set) {
    set.retainAll(spilledNodes);
    return set;
  }

  void buildSpilled() {
    for (var block : curFunc.blocks) {
      var lives = filterSpilled(new HashSet<>(block.liveOut));

      // iterate in reverse order
      for (int i = block.insts.size() - 1; i >= 0; i--) {
        var inst = block.insts.get(i);
        var uses = filterSpilled(inst.uses());
        var defs = filterSpilled(inst.defs());
        if (inst instanceof MvInst mv) {
          lives.removeAll(uses);
          uses.forEach(reg -> reg.nodeSpilled.moveList.add(mv));
          defs.forEach(reg -> reg.nodeSpilled.moveList.add(mv));
        }

        lives.addAll(defs);
        for (var def : defs) {
          for (var live : lives) {
            GSpilled.addEdgeSpilled(live, def);
          }
        }
        lives.removeAll(defs);
        lives.addAll(uses);
      }
    }
  }

  void makeWorklist() {
    for (var reg : initial) {
      if (reg.node.degree >= K) {
        spillWorklist.add(reg);
      } else if (moveRelated(reg)) {
        freezeWorklist.add(reg);
      } else {
        simplifyWorklist.add(reg);
      }
    }
    initial.clear();
  }

  HashSet<MvInst> nodeMoves(Reg reg) {
    var ret = new HashSet<MvInst>();
    for (var mv : reg.node.moveList) {
      if (activeMoves.contains(mv))
        ret.add(mv);
      else if (worklistMoves.contains(mv))
        ret.add(mv);
    }
    return ret;
  }

  boolean moveRelated(Reg reg) {
    return !nodeMoves(reg).isEmpty();
  }

  HashSet<Reg> adjacent(Reg reg) {
    var ret = new HashSet<>(reg.node.adjList);
    ret.removeAll(selectStack);
    ret.removeAll(coloredNodes);
    return ret;
  }

  void simplify() {
    var iter = simplifyWorklist.iterator();
    var reg = iter.next();
    iter.remove();
    selectStack.push(reg);
    adjacent(reg).forEach(this::decrementDegree);
  }

  /**
   * Removing a node from the graph involves decrementing the degree of its
   * <em>current</em> neighbors. If the degree of a neighbor is already less
   * than K−1 then the neighbor must be move-related, and is not added to the
   * {@code simplifyWorklist}. When the degree of a neighbor transitions from K
   * to K−1, moves associated with <em>its</em> neighbors may be enabled.
   */
  void decrementDegree(Reg reg) {
    int d = reg.node.degree;
    reg.node.degree--;
    if (d == K) {
      var adj = adjacent(reg);
      adj.add(reg);
      enableMoves(adj);
      spillWorklist.remove(reg);
      if (moveRelated(reg))
        freezeWorklist.add(reg);
      else
        simplifyWorklist.add(reg);
    }
  }

  void enableMoves(Set<Reg> regs) {
    for (Reg reg : regs) {
      var moves = nodeMoves(reg);
      for (var mv : moves)
        if (activeMoves.contains(mv)) {
          activeMoves.remove(mv);
          worklistMoves.add(mv);
        }
    }
  }

  void addWorklist(Reg reg) {
    if (!(reg instanceof PhysicalReg) &&
        !moveRelated(reg) && reg.node.degree < K) {
      freezeWorklist.remove(reg);
      simplifyWorklist.add(reg);
    }
  }

  void coalesce() {
    var iter = worklistMoves.iterator();
    var mv = iter.next();

    var u = getAlias(mv.rd);
    var v = getAlias(mv.rs);
    if (v instanceof PhysicalReg) {
      var tmp = u;
      u = v;
      v = tmp;
    }
    var edge = new Edge(u, v);
    iter.remove();

    if (u == v) {
      coalescedMoves.add(mv);
      addWorklist(u);
    } else if (v instanceof PhysicalReg || G.adjSet.contains(edge)) {
      constrainedMoves.add(mv);
      addWorklist(u);
      addWorklist(v);
    } else if (u instanceof PhysicalReg && george(u, v)
        || !(u instanceof PhysicalReg) && briggs(u, v)) {
      coalescedMoves.add(mv);
      combine(u, v);
      addWorklist(u);
    } else {
      activeMoves.add(mv);
    }
  }

  Reg getAlias(Reg reg) {
    if (!coalescedNodes.contains(reg))
      return reg;
    var a = getAlias(alias.get(reg));
    alias.put(reg, a);
    return a;
  }

  /**
   * George strategy of coalescing.
   * Nodes u and v can be coalesced if, for every neighbor t of u, either t
   * already interferes with v or t is of insignificant degree.
   */
  boolean george(Reg u, Reg v) {
    for (var t : adjacent(u)) {
      if (t.node.degree < K)
        continue;
      if (t instanceof PhysicalReg)
        continue;
      if (G.adjSet.contains(new Edge(t, v)))
        continue;
      return false;
    }
    return true;
  }

  /**
   * Briggs strategy of coalescing.
   * Nodes u and v can be coalesced if the resulting node uv will have fewer
   * than K neighbors of significant degree (i.e., having >= K edges).
   */
  boolean briggs(Reg u, Reg v) {
    var adj = adjacent(u);
    adj.addAll(adjacent(v));
    int cnt = 0;
    for (var n : adj) {
      if (n.node.degree >= K)
        cnt++;
    }
    return cnt < K;
  }

  void combine(Reg u, Reg v) {
    if (freezeWorklist.contains(v))
      freezeWorklist.remove(v);
    else
      spillWorklist.remove(v);
    coalescedNodes.add(v);
    alias.put(v, u);
    u.node.moveList.addAll(v.node.moveList);
    enableMoves(Set.of(v));
    for (var t : adjacent(v)) {
      G.addEdge(t, u);
      decrementDegree(t);
    }
    if (u.node.degree >= K && freezeWorklist.contains(u)) {
      freezeWorklist.remove(u);
      spillWorklist.add(u);
    }
  }

  void freeze() {
    var iter = freezeWorklist.iterator();
    var reg = iter.next();
    iter.remove();
    simplifyWorklist.add(reg);
    freezeMoves(reg);
  }

  void freezeMoves(Reg u) {
    for (var mv : nodeMoves(u)) {
      Reg v = getAlias(mv.rs);
      if (getAlias(u) == v)
        v = getAlias(mv.rd);
      activeMoves.remove(mv);
      frozenMoves.add(mv);
      if (nodeMoves(v).isEmpty() && v.node.degree < K) {
        freezeWorklist.remove(v);
        simplifyWorklist.add(v);
      }
    }
  }

  void selectSpill() {
    Reg minReg = null;
    double minCost = Double.POSITIVE_INFINITY;
    for (var reg : spillWorklist) {
      double regCost = reg.node.frequency / reg.node.degree;
      // avoid choosing nodes introduced by the load and store of
      // previously spilled registers
      if (introduced.contains(reg))
        regCost += 1e10;
      if (regCost < minCost) {
        minReg = reg;
        minCost = regCost;
      }
    }
    spillWorklist.remove(minReg);
    simplifyWorklist.add(minReg);
    freezeMoves(minReg);
  }

  void assignColors() {
    while (!selectStack.empty()) {
      var reg = selectStack.pop();
      var availColors = new ArrayList<>(PhysicalReg.assignable);
      for (var t : reg.node.adjList) {
        t = getAlias(t);
        if (t instanceof PhysicalReg || coloredNodes.contains(t))
          availColors.remove(t.color);
      }
      if (availColors.isEmpty()) {
        spilledNodes.add(reg);
      } else {
        coloredNodes.add(reg);
        reg.color = availColors.iterator().next();
      }
    }

    for (var reg : coalescedNodes) {
      reg.color = getAlias(reg).color;
    }
  }

  void assignColorsSpilled() {
    int i = 0, num = spilledNodes.size();
    Node[] nodes = new Node[num];
    for (var reg : spilledNodes) {
      System.out.printf("spilledNodes contains %s\n", reg);
      nodes[i++] = reg.nodeSpilled;
    }
    var colors = new HashSet<Reg>();
    i = 0;
    while (i != num) {
      Arrays.sort(nodes, i, num);
      var node = nodes[i++];
      var reg = node.origin;
      var availColors = new HashSet<>(colors);
      for (var t : node.adjList) {
        t = getAlias(t);
        availColors.remove(t.color);
        t.nodeSpilled.degree--;
      }
      if (availColors.isEmpty()) {
        colors.add(reg);
        reg.color = reg;
      } else {
        reg.color = availColors.iterator().next();
      }
      System.out.printf("spilled %s color: %s\n", reg, reg.color);
    }
    // allocate stack memory for spilled registers
    for (var reg : colors) {
      reg.stackOffset = new StackOffset(
          curFunc.spilledReg, StackOffsetType.spill);
      curFunc.spilledReg++;
    }
  }

  void rewriteProgram() {
    // create a temporary register for each def and use of spilled register,
    // remove coalesced moves, apply alias
    for (var block : curFunc.blocks) {
      var oldInsts = block.insts;
      block.insts = new ArrayList<BaseInst>();
      for (var inst : oldInsts) {
        // delete coalesced move
        if (inst instanceof MvInst mv && coalescedMoves.contains(mv)) {
          System.out.printf("delete %s\n", mv);
          continue;
        }
        if (inst instanceof MvInst)
          System.out.printf("reserved: %s\n", inst);

        for (var reg : inst.uses()) {
          var regAlias = getAlias(reg);
          if (regAlias != reg) {
            inst.replaceUse(reg, regAlias);
          }

          if (!spilledNodes.contains(reg))
            continue;
          var tmp = new VirtualReg(((VirtualReg) reg).size);
          new LoadInst(tmp.size, tmp, PhysicalReg.sp, regAlias.color.stackOffset, block);
          inst.replaceUse(reg, tmp);
          introduced.add(tmp);
        }
        block.insts.add(inst);
        for (var reg : inst.defs()) {
          var regAlias = getAlias(reg);
          if (regAlias != reg) {
            inst.replaceDef(reg, regAlias);
          }

          if (!spilledNodes.contains(reg))
            continue;
          var tmp = new VirtualReg(((VirtualReg) reg).size);
          new StoreInst(tmp.size, tmp, PhysicalReg.sp, regAlias.color.stackOffset, block);
          inst.replaceDef(reg, tmp);
          introduced.add(tmp);
        }
      }
    }
  }
}
