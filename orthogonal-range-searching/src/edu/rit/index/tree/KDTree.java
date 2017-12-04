package edu.rit.index.tree;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;
import java.util.function.Function;

public class KDTree<T> {

  private Node root;
  private Function<T, Double> d1;
  private Function<T, Double> d2;
  private T[] Px;
  private T[] Py;
  private Comparator<T> xcp;
  private Comparator<T> ycp;

  public KDTree(final T[] P, final Function<T, Double> d1, final Function<T, Double> d2) {
    if (P == null || P.length == 0)
      return;
    this.d1 = d1;
    this.d2 = d2;
    Px = Arrays.copyOf(P, P.length);
    Py = Arrays.copyOf(P, P.length);
    xcp = Comparator.comparing(d1);
    ycp = Comparator.comparing(d2);
    Arrays.sort(Px, xcp);
    Arrays.sort(Py, ycp);
    root = build(0, P.length, 0, null, false);
  }

  private Node build(final int lo, final int hi, final int depth, final Node parent,
      final boolean isLeftChild) {
    if (hi - 1 == lo) {
      return new Node(Px[lo]);
    }
    Partition partition = null;
    final Node v = new Node();
    if (depth % 2 == 0) {
      partition = partition(lo, hi, true);
      v.l = partition.l;
      v.lt = LineType.VER;
    } else {
      partition = partition(lo, hi, false);
      v.l = partition.l;
      v.lt = LineType.HOR;
    }
    if (parent != null)
      v.region = region(parent, isLeftChild);
    v.left = build(partition.P1lo, partition.P1hi, depth + 1, v, true);
    v.right = build(partition.P2lo, partition.P2hi, depth + 1, v, false);
    return v;
  }

  private RectangularRange region(final Node v, final boolean isLeftChild) {
    return v.region.intersect(v.l, v.lt, isLeftChild);
  }

  private void setChildrenRegion(final Node v) {
    setChildrenRegion(v, true);
    setChildrenRegion(v, false);
  }

  private void setChildrenRegion(final Node v, final boolean isLeftChild) {
    final Node c = isLeftChild ? v.left : v.right;
    final RectangularRange region = v.region.intersect(v.l, v.lt, isLeftChild);
    c.region = region;
  }

  private Partition partition(final int lo, final int hi, final boolean evenDepth) {
    Partition result = null;
    final int idx = (hi + lo - 1) / 2;
    final int P1lo = lo;
    final int P1hi = idx + 1;
    final int P2lo = idx + 1 > hi ? hi : idx + 1;
    final int P2hi = hi;
    final T m = evenDepth ? Px[idx] : Py[idx];
    final double l = evenDepth ? d1.apply(m) : d2.apply(m);
    result = new Partition(l, P1lo, P1hi, P2lo, P2hi);
    return result;
  }

  private List<T> searchRange(final Node v, final RectangularRange R) {
    final List<T> result = new ArrayList<>();
    if (v.isLeaf()) {
      if (R.contains(v))
        result.add(v.element);
    } else {
      if (R.fullyContains(v.left.region)) {
        final List<T> subtree = reportSubtree(v.left);
        result.addAll(subtree);
      } else if (v.left.region.intersects(R)) {
        final List<T> subtree = searchRange(v.left, R);
        result.addAll(subtree);
      }
      if (R.fullyContains(v.right.region)) {
        final List<T> subtree = reportSubtree(v.right);
        result.addAll(subtree);
      } else if (v.right.region.intersects(R)) {
        final List<T> subtree = searchRange(v.right, R);
        result.addAll(subtree);
      }
    }
    return result;
  }

  private List<T> reportSubtree(final Node v) {
    final List<T> result = new ArrayList<>();
    if (v.isLeaf()) {
      result.add(v.element);
      return result;
    }
    if (v.left != null) {
      final List<T> l = reportSubtree(v.left);
      result.addAll(l);
    }
    if (v.right != null) {
      final List<T> r = reportSubtree(v.right);
      result.addAll(r);
    }
    return result;
  }


  public List<T> searchRange(final RectangularRange R) {
    return searchRange(root, R);
  }

  public Node getRoot() {
    return root;
  }

  private class Partition {

    double l;
    int P1lo;
    int P1hi;
    int P2lo;
    int P2hi;

    public Partition(final double l, final int p1lo, final int p1hi, final int p2lo,
        final int p2hi) {
      super();
      this.l = l;
      P1lo = p1lo;
      P1hi = p1hi;
      P2lo = p2lo;
      P2hi = p2hi;
    }

  }

  public enum LineType {
    HOR, VER;
  }

  public class Node {

    Double l;
    LineType lt;
    RectangularRange region;

    T element;
    Node left;
    Node right;

    public Node() {
      region = new RectangularRange();
    }

    public Node(final T t) {
      region = new RectangularRange();
      element = t;
    }

    public boolean isLeaf() {
      return left == null && right == null;
    }

  }

  public class LinearRange {

    double min;
    double max;

    public LinearRange(final double min, final double max) {
      this.min = min;
      this.max = max;
    }

    public LinearRange(final LinearRange lr) {
      min = lr.min;
      max = lr.max;
    }

    public boolean contains(final double p) {
      return p >= min && p <= max;
    }

    public boolean contains(final LinearRange lr) {
      return min <= lr.min && max >= lr.max;
    }

  }

  public class RectangularRange {

    LinearRange xRange;
    LinearRange yRange;

    public RectangularRange() {
      xRange = new LinearRange(Double.MIN_VALUE, Double.MAX_VALUE);
      yRange = new LinearRange(Double.MIN_VALUE, Double.MAX_VALUE);
    }

    public RectangularRange(final LinearRange xRange, final LinearRange yRange) {
      this.xRange = xRange;
      this.yRange = yRange;
    }

    public RectangularRange(final RectangularRange R) {
      xRange = new LinearRange(R.xRange);
      yRange = new LinearRange(R.yRange);
    }

    public RectangularRange intersect(final double l, final LineType lt,
        final boolean isLeftChild) {
      final RectangularRange result = new RectangularRange(this);
      final boolean isVertical = LineType.VER.equals(lt);
      if (isVertical) {
        if (isLeftChild)
          result.xRange.max = l;
        else
          result.xRange.min = l;
      } else {
        if (isLeftChild)
          result.yRange.max = l;
        else
          result.yRange.min = l;
      }
      return result;
    }

    public boolean intersects(final RectangularRange R) {
      return xRange.min < R.xRange.max && xRange.max > R.xRange.min && yRange.min < R.yRange.max
          && yRange.max > R.yRange.min;
    }

    public boolean fullyContains(final RectangularRange R) {
      return xRange.contains(R.xRange) && yRange.contains(R.yRange);
    }

    public boolean contains(final Node n) {
      return xRange.contains(d1.apply(n.element)) && yRange.contains(d2.apply(n.element));
    }

  }

}
