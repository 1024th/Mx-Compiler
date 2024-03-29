package middleend;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Queue;
import java.util.Stack;

import ir.inst.AllocaInst;
import ir.inst.BaseInst;
import ir.inst.LoadInst;
import ir.inst.PhiInst;
import ir.inst.StoreInst;
import ir.type.PointerType;
import ir.BasicBlock;
import ir.constant.IntConst;
import ir.constant.NullptrConst;

/** Prerequisite: CFGBuilder */
public class Mem2Reg {
  ir.IRBuilder irBuilder;

  public Mem2Reg(ir.IRBuilder irBuilder) {
    this.irBuilder = irBuilder;
  }

  public void runOnFunc(ir.Function func) {
    new DomTreeBuilder().runOnFunc(func);
    collectAllocas(func);
    for (var alloca : allocas) {
      insertPhiFor(func, alloca);
    }
    variableRenaming(func.entryBlock);
  }

  HashMap<PhiInst, String> phiAllocaName = new HashMap<>();
  ArrayList<BaseInst> allocas = new ArrayList<>();
  HashMap<String, Stack<ir.Value>> nameStack = new HashMap<>();

  void collectAllocas(ir.Function func) {
    allocas.clear();
    for (var inst : func.entryBlock.insts) {
      if (inst instanceof ir.inst.AllocaInst)
        allocas.add(inst);
    }
  }

  void insertPhiFor(ir.Function func, BaseInst alloca) {
    Queue<BasicBlock> queue = new ArrayDeque<>();
    var visited = new HashSet<BasicBlock>();
    queue.add(alloca.parent);
    for (var user : alloca.users) {
      if (user instanceof StoreInst st && st.ptr() == alloca) {
        queue.offer(st.parent); // add all defs to queue
      }
    }
    while (!queue.isEmpty()) {
      var node = queue.poll();
      for (var frontier : node.dtNode.domFrontier) {
        if (visited.contains(frontier.origin))
          continue;
        visited.add(frontier.origin);

        queue.offer(frontier.origin);
        var phi = new PhiInst(((PointerType) alloca.type).elemType,
            irBuilder.rename(alloca.name), frontier.origin);
        phiAllocaName.put(phi, alloca.name);
      }
    }
  }

  ir.Value getReplace(String name) {
    var stack = nameStack.get(name);
    if (stack == null || stack.empty()) {
      return null;
    }
    return nameStack.get(name).lastElement();
  }

  void updateReplace(String name, ir.Value replace) {
    var stack = nameStack.get(name);
    if (stack == null) {
      stack = new Stack<>();
      nameStack.put(name, stack);
    }
    stack.push(replace);
  }

  void variableRenaming(BasicBlock block) {
    var popList = new ArrayList<String>();

    for (var phi : block.phiInsts) {
      var name = phiAllocaName.get(phi);
      if (name == null)
        continue;
      updateReplace(name, phi);
      popList.add(name);
    }

    var iter = block.insts.iterator();
    while (iter.hasNext()) {
      var inst = iter.next();
      if (inst instanceof AllocaInst alloca) {
        ir.Value val;
        if (((PointerType) alloca.type).elemType instanceof PointerType)
          val = new NullptrConst();
        else
          val = new IntConst(0);
        updateReplace(alloca.name, val);
        popList.add(alloca.name);
        iter.remove();
      }
      if (inst instanceof StoreInst store) {
        var ptr = store.ptr();
        if (!allocas.contains(ptr))
          continue;
        var name = ptr.name;
        updateReplace(name, store.val());
        popList.add(name);
        iter.remove();
      }
      if (inst instanceof LoadInst load) {
        var ptr = load.ptr();
        if (!allocas.contains(ptr))
          continue;
        var name = ptr.name;
        var replace = getReplace(name);
        inst.replaceAllUsesWith(replace);
        iter.remove();
      }
    }

    for (var suc : block.nexts) {
      for (var sucPhi : suc.phiInsts) {
        var name = phiAllocaName.get(sucPhi);
        if (name == null)
          continue;
        sucPhi.addBranch(getReplace(name), block);
      }
    }

    for (var node : block.dtNode.children) {
      variableRenaming(node.origin);
    }

    for (var name : popList) {
      nameStack.get(name).pop();
    }
  }
}
