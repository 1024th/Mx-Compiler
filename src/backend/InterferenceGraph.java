package backend;

import java.util.HashSet;
import java.util.LinkedHashSet;

import asm.inst.MvInst;
import asm.operand.PhysicalReg;
import asm.operand.Reg;

public class InterferenceGraph {
  static public class Edge {
    public Reg u, v;

    public Edge(Reg u, Reg v) {
      this.u = u;
      this.v = v;
    }

    @Override
    public boolean equals(Object o) {
      return o instanceof Edge e && (e.u == u && e.v == v);
    }

    @Override
    public int hashCode() {
      return u.hashCode() ^ v.hashCode();
    }
  }

  public class Node {
    /** the set of nodes that interfere with this node. */
    public HashSet<Reg> adjList = new LinkedHashSet<>();
    /** the list of move instructions associated with this node. */
    public HashSet<MvInst> moveList = new LinkedHashSet<>();
    /**
     * @apiNote The degree of precolored node (machine register) is
     *          considered as "infinite" to prevent it from being simplified.
     */
    public int degree;
    public double frequency;

    public void init(boolean precolored) {
      this.adjList.clear();
      this.moveList.clear();
      this.frequency = 0;
      this.degree = precolored ? Integer.MAX_VALUE : 0;
    }
  }

  /**
   * The set of interference edges (u, v) in the graph.
   * If (u, v) in {@code adjSet}, then (v, u) in {@code adjSet}.
   */
  public HashSet<Edge> adjSet = new LinkedHashSet<>();

  public void addEdge(Reg u, Reg v) {
    if (u == v)
      return;
    var edge1 = new Edge(u, v);
    if (!adjSet.contains(edge1)) {
      var edge2 = new Edge(v, u);
      adjSet.add(edge1);
      adjSet.add(edge2);
      if (!(u instanceof PhysicalReg)) {
        u.node.adjList.add(v);
        u.node.degree++;
      }
      if (!(v instanceof PhysicalReg)) {
        v.node.adjList.add(u);
        v.node.degree++;
      }
    }
  }

  public void init() {
    adjSet.clear();
  }
}