package middleend;

import java.util.ArrayList;
import java.util.HashSet;

import ir.BasicBlock;
import utils.TextUtils;

/** Dominator Tree Builder */
public class DomTreeBuilder {
  boolean reversed;

  /**
   * @param reversed use reversed control flow graph
   */
  public DomTreeBuilder(boolean reversed) {
    this.reversed = reversed;
  }

  public DomTreeBuilder() {
    this(false);
  }

  BasicBlock entry(ir.Function func) {
    return reversed ? func.exitBlock : func.entryBlock;
  }

  ArrayList<BasicBlock> predecessor(Node node) {
    return reversed ? node.origin.nexts : node.origin.prevs;
  }

  ArrayList<BasicBlock> successor(Node node) {
    return reversed ? node.origin.prevs : node.origin.nexts;
  }

  public void runOnFunc(ir.Function func) {
    computeIDom(func);
    computeChlidren(func);
    computeDF(entry(func).dtNode);

    // func.blocks.forEach(this::debugPrint);
  }

  void debugPrint(BasicBlock block) {
    var p = System.out;
    p.printf("%s:  ; preds = %s\n", block.name, TextUtils.join(block.prevs));
    var node = block.dtNode;
    p.printf("; idom: %s\n", node.idom);
    if (!node.children.isEmpty())
      p.printf("; dtChildren: %s\n", TextUtils.join(node.children));
    p.printf("; df: %s\n", TextUtils.join(node.domFrontier));
  }

  void computeChlidren(ir.Function func) {
    for (var block : func.blocks) {
      var node = block.dtNode;
      if (node.idom != null)
        node.idom.children.add(node);
    }
  }

  void computeDF(Node node) {
    var S = new HashSet<Node>();
    for (var nxtBlock : successor(node)) {
      var nxt = nxtBlock.dtNode;
      if (nxt.idom != node)
        S.add(nxt);
    }
    for (var c : node.children) {
      computeDF(c);
      for (var w : c.domFrontier) {
        if (node == w || !node.isDominatorOf(w))
          S.add(w);
      }
    }
    node.domFrontier.clear();
    node.domFrontier.addAll(S);
  }

  static public class Node {
    public BasicBlock origin;
    public int dfn;
    public Node parent;
    public Node semi, samedom, idom;
    public ArrayList<Node> bucket = new ArrayList<>();
    public Node ancestor, best;
    public ArrayList<Node> children = new ArrayList<>();
    public ArrayList<Node> domFrontier = new ArrayList<>();

    public Node(BasicBlock origin) {
      this.origin = origin;
    }

    public void clear() {
      dfn = -1;
      parent = semi = samedom = idom = ancestor = best = null;
      bucket.clear();
      children.clear();
      domFrontier.clear();
    }

    public boolean isDominatorOf(Node o) {
      while (o != null) {
        if (o.idom == this)
          return true;
        o = o.idom;
      }
      return false;
    }

    @Override
    public String toString() {
      return origin.toString();
    }
  }

  ArrayList<Node> dfnOrder = new ArrayList<>();

  private int dfn;

  private void dfs(Node p, Node n) {
    if (n.dfn > 0)
      return;
    n.dfn = dfn;
    dfnOrder.add(n);
    n.parent = p;
    dfn++;
    for (var succ : successor(n)) {
      dfs(n, succ.dtNode);
    }
  }

  void computeIDom(ir.Function func) {
    dfn = 0;
    dfnOrder.clear();
    for (var b : func.blocks) {
      b.dtNode.clear();
    }
    dfs(null, entry(func).dtNode);
    for (int i = dfn - 1; i >= 1; --i) {
      var n = dfnOrder.get(i);
      var p = n.parent;
      var s = p;
      for (var v : predecessor(n)) {
        if (v.dtNode.dfn < 0)
          continue;
        Node ss;
        if (v.dtNode.dfn <= n.dfn)
          ss = v.dtNode;
        else
          ss = ancestorWithLowestSemi(v.dtNode).semi;
        if (ss.dfn < s.dfn)
          s = ss;
      }
      n.semi = s;
      s.bucket.add(n);
      link(p, n);
      for (var v : p.bucket) {
        var y = ancestorWithLowestSemi(v);
        if (y.semi == v.semi)
          v.idom = p;
        else
          v.samedom = y;
      }
      p.bucket.clear();
    }
    for (int i = 1; i <= dfn - 1; ++i) {
      var n = dfnOrder.get(i);
      if (n.samedom != null)
        n.idom = n.samedom.idom;
    }
  }

  Node ancestorWithLowestSemi(Node v) {
    var a = v.ancestor;
    if (a.ancestor != null) {
      var b = ancestorWithLowestSemi(a);
      v.ancestor = a.ancestor;
      if (b.semi.dfn < v.best.semi.dfn)
        v.best = b;
    }
    return v.best;
  }

  void link(Node p, Node n) {
    n.ancestor = p;
    n.best = n;
  }
}
