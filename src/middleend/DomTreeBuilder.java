package middleend;

import java.util.ArrayList;
import java.util.HashSet;

import ir.BasicBlock;
import utils.TextUtils;

/** Dominator Tree Builder */
public class DomTreeBuilder {

  public void runOnFunc(ir.Function func) {
    computeIDom(func);
    computeChlidren(func);
    computeDF(func.entryBlock.dtNode);

    // func.blocks.forEach(this::debugPrint);
  }

  void debugPrint(BasicBlock block) {
    var p = System.out;
    p.printf("%s:\n", block.name);
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
    for (var nxtBlock : node.origin.nexts) {
      var nxt = nxtBlock.dtNode;
      if (nxt.idom != node)
        S.add(nxt);
    }
    for (var c : node.children) {
      computeDF(c);
      for (var w : c.domFrontier) {
        if (!node.isDominatorOf(w))
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
      dfn = 0;
      semi = samedom = idom = ancestor = best = null;
      bucket.clear();
    }

    public boolean isDominatorOf(Node o) {
      while (o != null) {
        if (o.idom == this)
          return true;
        o = o.idom;
      }
      return false;
    }
  }

  ArrayList<Node> dfnOrder = new ArrayList<>();

  private int dfn;

  private void dfs(Node p, Node n) {
    if (n.dfn != 0)
      return;
    n.dfn = dfn;
    dfnOrder.add(n);
    n.parent = p;
    dfn++;
    for (var succ : n.origin.nexts) {
      dfs(n, succ.dtNode);
    }
  }

  void computeIDom(ir.Function func) {
    dfn = 0;
    dfnOrder.clear();
    for (var b : func.blocks) {
      b.dtNode.clear();
    }
    dfs(null, func.entryBlock.dtNode);
    for (int i = dfn - 1; i >= 1; --i) {
      var n = dfnOrder.get(i);
      var p = n.parent;
      var s = p;
      for (var v : n.origin.prevs) {
        Node ss;
        if (v.dtNode.dfn < n.dfn)
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
