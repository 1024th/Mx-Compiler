package backend;

import java.util.HashMap;
import java.util.HashSet;
import java.util.PriorityQueue;

import asm.operand.Reg;
import asm.Block;

/** Liveness Analysis */
public class LivenessAnalyzer implements asm.FuncPass {
  class Node implements Comparable<Node> {
    public int dfn;
    public Block block;

    public Node(int dfn, Block block) {
      this.dfn = dfn;
      this.block = block;
    }

    @Override
    public int compareTo(Node o) {
      if (this.dfn > o.dfn)
        return 1;
      if (this.dfn < o.dfn)
        return -1;
      return 0;
    }
  }

  HashMap<Block, Node> nodeMap = new HashMap<>();

  HashMap<Block, HashSet<Reg>> blockUsesMap = new HashMap<>(), blockDefsMap = new HashMap<>();
  PriorityQueue<Node> queue = new PriorityQueue<>();

  @Override
  public void runOnFunc(asm.Function func) {
    // clear old data
    for (var block : func.blocks) {
      block.liveIn.clear();
      block.liveOut.clear();
    }
    func.blocks.forEach(this::combineBlock);

    dfn = 1;
    dfs(func.exitBlock);

    queue.offer(nodeMap.get(func.exitBlock));
    while (!queue.isEmpty()) {
      var node = queue.poll();
      var block = node.block;

      // out[n] = U_{s in succ[n]} in[s]
      var newLiveOut = new HashSet<Reg>();
      block.nexts.forEach(suc -> newLiveOut.addAll(suc.liveIn));

      // in[n] = use[n] U (out[n] - def[n])
      var newLiveIn = new HashSet<Reg>(newLiveOut);
      newLiveIn.removeAll(blockDefsMap.get(block));
      newLiveIn.addAll(blockUsesMap.get(block));

      if (!newLiveIn.equals(block.liveIn) || !newLiveOut.equals(block.liveOut)) {
        block.liveIn.addAll(newLiveIn);
        block.liveOut.addAll(newLiveOut);
        block.prevs.forEach(pre -> queue.offer(nodeMap.get(pre)));
      }
    }
  }

  private int dfn;

  private void dfs(Block block) {
    nodeMap.put(block, new Node(dfn, block));
    dfn++;
    for (var b : block.prevs) {
      if (nodeMap.containsKey(b))
        continue;
      dfs(b);
    }
  }

  /** Combines all the instructions of a basic block */
  private void combineBlock(asm.Block block) {
    // p -> n.
    // n has only one predecessor, p.
    // p has only one successor, n.
    // use[pn] = use[p] U (use[n] − def[p])
    // def[pn] = def[p] U def[n]
    var blockUses = new HashSet<Reg>();
    var blockDefs = new HashSet<Reg>();
    for (var i : block.insts) {
      i.uses().forEach(use -> {
        if (!blockDefs.contains(use))
          blockUses.add(use);
      });
      blockDefs.addAll(i.defs());
    }
    blockUsesMap.put(block, blockUses);
    blockDefsMap.put(block, blockDefs);
  }
}
