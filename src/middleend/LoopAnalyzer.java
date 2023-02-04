package middleend;

import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.Queue;
import java.util.Stack;

import ir.BasicBlock;

public class LoopAnalyzer {
  ir.Function curFunc;

  public void runOnFunc(ir.Function func) {
    curFunc = func;
    new CFGBuilder().runOnFunc(func);
    new DomTreeBuilder(false).runOnFunc(func);
    func.rootLoops.clear();
    for (var block : func.blocks) {
      block.loopDepth = 0;
    }

    findNaturalLoops();

    visited.clear();
    buildLoopNestTree(func.entryBlock);
  }

  void findNaturalLoops() {
    for (var block : curFunc.blocks) {
      for (var suc : block.nexts) {
        if (suc.dtNode.isDominatorOf(block.dtNode)) {
          buildNaturalLoop(suc, block);
          break;
        }
      }
    }
  }

  HashMap<BasicBlock, Loop> headToLoop = new HashMap<>();

  void buildNaturalLoop(BasicBlock head, BasicBlock tail) {
    var loop = headToLoop.get(head);
    if (loop == null) {
      loop = new Loop(head);
      headToLoop.put(head, loop);
    }
    loop.tailers.add(tail);
    loop.blocks.add(head);
    loop.blocks.add(tail);

    Queue<BasicBlock> queue = new LinkedList<>();
    queue.offer(tail);
    while (!queue.isEmpty()) {
      var block = queue.poll();
      for (var pre : block.prevs) {
        if (loop.blocks.contains(pre))
          continue;
        loop.blocks.add(pre);
        queue.offer(pre);
      }
    }
  }

  Stack<Loop> loopStack = new Stack<>();
  HashSet<BasicBlock> visited = new HashSet<>();

  Loop topLoop() {
    return loopStack.isEmpty() ? null : loopStack.peek();
  }

  void buildLoopNestTree(BasicBlock block) {
    if (visited.contains(block))
      return;
    visited.add(block);

    var outerLoop = topLoop();
    while (outerLoop != null && !outerLoop.blocks.contains(block)) {
      loopStack.pop();
      outerLoop = topLoop();
    }

    var loop = headToLoop.get(block);
    if (loop != null) {
      if (outerLoop != null) {
        loop.outerLoop = outerLoop;
        outerLoop.innerLoops.add(loop);
      } else {
        curFunc.rootLoops.add(loop);
      }
      loopStack.push(loop);
    }

    block.loopDepth = loopStack.size();

    for (var suc : block.nexts) {
      buildLoopNestTree(suc);
    }
  }

}
