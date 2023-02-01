package middleend;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.PriorityQueue;

import ir.BasicBlock;
import utils.TextUtils;

/** Dominator Tree Builder */
public class DomTreeBuilder {

  public void runOnFunc(ir.Function func) {
    for (var block : func.blocks) {
      if (block == func.entryBlock) {
        block.doms = new HashSet<BasicBlock>();
        block.doms.add(block);
      } else {
        block.doms = new HashSet<>(func.blocks);
      }
    }

    dfn = 1;
    dfs(func.entryBlock);

    computeDoms(func);
    computeIDom(func);
    computeDF(func.entryBlock);

    // func.blocks.forEach(this::debugPrint);
  }

  void debugPrint(BasicBlock block) {
    var p = System.out;
    p.printf("%s:\n", block.name);
    p.printf("; doms: %s\n", TextUtils.join(block.doms));
    p.printf("; idom: %s\n", block.idom);
    if (!block.dtChildren.isEmpty())
      p.printf("; dtChildren: %s\n", TextUtils.join(block.dtChildren));
    p.printf("; df: %s\n", TextUtils.join(block.df));
  }

  void computeDoms(ir.Function func) {
    queue.clear();
    queue.offer(nodeMap.get(func.entryBlock));
    while (!queue.isEmpty()) {
      var node = queue.poll();
      while (!queue.isEmpty() && queue.peek().equals(node)) {
        queue.poll();
      }
      var block = node.block;

      for (var nxt : block.nexts) {
        var newNxtDoms = new HashSet<>(nxt.doms);
        newNxtDoms.retainAll(block.doms);
        newNxtDoms.add(nxt);
        if (!newNxtDoms.equals(nxt.doms)) {
          nxt.doms = newNxtDoms;
          queue.offer(nodeMap.get(nxt));
        }
      }
    }
  }

  void computeIDom(ir.Function func) {
    func.entryBlock.dtDepth = 1;
    func.entryBlock.idom = null;
    boolean changed = true;
    while (changed) {
      changed = false;
      for (var block : dfnOrder) {
        if (block.dtDepth != 0)
          continue;
        boolean finished = true;
        int depth = 0;
        BasicBlock idom = null;
        for (var d : block.doms) {
          if (d == block)
            continue;
          if (d.dtDepth == 0) {
            finished = false;
            break;
          }
          if (d.dtDepth >= depth) {
            depth = d.dtDepth + 1;
            idom = d;
          }
        }
        if (finished) {
          block.dtDepth = depth;
          block.idom = idom;
          idom.dtChildren.add(block);
          changed = true;
        } else {
          continue;
        }
      }
    }
  }

  void computeSons(ir.Function func) {
    for (var block : func.blocks) {
      block.idom.dtChildren.add(block);
    }
  }

  void computeDF(BasicBlock block) {
    var S = new HashSet<BasicBlock>();
    for (var nxt : block.nexts) {
      if (nxt.idom != block)
        S.add(nxt);
    }
    for (var c : block.dtChildren) {
      computeDF(c);
      for (var w : c.df) {
        if (!w.doms.contains(block))
          S.add(w);
      }
    }
    block.df = new ArrayList<>(S);
  }

  class QueueNode implements Comparable<QueueNode> {
    public int dfn;
    public BasicBlock block;

    public QueueNode(int dfn, BasicBlock block) {
      this.dfn = dfn;
      this.block = block;
    }

    @Override
    public int compareTo(QueueNode o) {
      if (this.dfn > o.dfn)
        return 1;
      if (this.dfn < o.dfn)
        return -1;
      return 0;
    }
  }

  HashMap<BasicBlock, QueueNode> nodeMap = new HashMap<>();
  ArrayList<BasicBlock> dfnOrder = new ArrayList<>();
  PriorityQueue<QueueNode> queue = new PriorityQueue<>();

  private int dfn;

  private void dfs(BasicBlock block) {
    nodeMap.put(block, new QueueNode(dfn, block));
    dfnOrder.add(block);
    dfn++;
    for (var b : block.nexts) {
      if (nodeMap.containsKey(b))
        continue;
      dfs(b);
    }
  }
}
