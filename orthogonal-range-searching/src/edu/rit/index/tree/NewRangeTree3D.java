package edu.rit.index.tree;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.function.Function;
import edu.rit.index.Index;

public class NewRangeTree3D<T> implements Index {

  private final Comparator<T> auxd1cp;
  private final Comparator<T> auxd2cp;
  private final Comparator<T> auxd3cp;
  private final Comparator<T> idcp;
  private final Comparator<T> d1cp;
  private final Comparator<T> d2cp;
  private final Comparator<T> d3cp;
  private final Function<T, Double> d2Getter;
  private final Function<T, Double> d3Getter;
  private final Function<T, Double> idGetter;

  private final Node3D<T> t;

  public NewRangeTree3D(final List<T> P, final Function<T, Double> d1Getter,
      final Function<T, Double> d2Getter, final Function<T, Double> d3Getter,
      final Function<T, Double> idGetter) {
    auxd1cp = Comparator.comparing(d1Getter);
    auxd2cp = Comparator.comparing(d2Getter);
    auxd3cp = Comparator.comparing(d3Getter);
    idcp = Comparator.comparing(idGetter);
    d1cp = new Comparator<T>() {
      @Override
      public int compare(final T o1, final T o2) {
        int cp = auxd1cp.compare(o1, o2);
        if (cp == 0)
          cp = auxd2cp.compare(o1, o2);
        if (cp == 0)
          cp = auxd3cp.compare(o1, o2);
        if (cp == 0)
          cp = idcp.compare(o1, o2);
        return cp;
      }
    };
    d2cp = new Comparator<T>() {
      @Override
      public int compare(final T o1, final T o2) {
        int cp = auxd2cp.compare(o1, o2);
        if (cp == 0)
          cp = auxd1cp.compare(o1, o2);
        if (cp == 0)
          cp = auxd3cp.compare(o1, o2);
        if (cp == 0)
          cp = idcp.compare(o1, o2);
        return cp;
      }
    };
    d3cp = new Comparator<T>() {
      @Override
      public int compare(final T o1, final T o2) {
        int cp = auxd3cp.compare(o1, o2);
        if (cp == 0)
          cp = auxd1cp.compare(o1, o2);
        if (cp == 0)
          cp = auxd2cp.compare(o1, o2);
        if (cp == 0)
          cp = idcp.compare(o1, o2);
        return cp;
      }
    };
    this.d2Getter = d2Getter;
    this.d3Getter = d3Getter;
    this.idGetter = idGetter;
    // Sort points using first dimension
    Collections.sort(P, auxd1cp);
    t = build3DRangeTree(P);
  }

  private Node3D<T> build3DRangeTree(final List<T> P) {
    if (P.size() == 1) {
      final T element = P.get(0);
      final List<T> list = new ArrayList<>();
      list.add(element);
      final NewRangeTree2D<T> tree2d = new NewRangeTree2D<>(list, d2Getter, d3Getter, idGetter);
      return new Node3D<>(element, tree2d);
    }
    final T median = median(P);
    final List<T> L = smaller(P, median);
    final List<T> R = larger(P, median);
    final Node3D<T> t = new Node3D<>(median);
    t.left = build3DRangeTree(L);
    t.right = build3DRangeTree(R);
    t.yTree = mergeYTree(t.left.yTree, t.right.yTree);
    return t;
  }

  private NewRangeTree2D<T> mergeYTree(final NewRangeTree2D<T> t1, final NewRangeTree2D<T> t2) {
    final List<T> P = t1.getRoot().getLeaves();
    P.addAll(t2.getRoot().getLeaves());
    final NewRangeTree2D<T> result = new NewRangeTree2D<>(P, d2Getter, d3Getter, idGetter);
    return result;
  }

  private List<T> smaller(final List<T> P, final T v) {
    final int idx = (P.size() - 1) / 2;
    return P.subList(0, idx + 1);
  }

  private List<T> larger(final List<T> P, final T v) {
    final int idx = (P.size() - 1) / 2;
    return P.subList(idx + 1, P.size());
  }

  private T median(final List<T> P) {
    final int idx = (P.size() - 1) / 2;
    return P.get(idx);
  }

  public List<T> searchInRange(final T d1LoRef, final T d1HiRef, final T d2LoRef, final T d2HiRef,
      final T d3LoRef, final T d3HiRef) {
    return searchInRange(t, d1LoRef, d1HiRef, d2LoRef, d2HiRef, d3LoRef, d3HiRef);
  }

  private List<T> searchInRange(final Node3D<T> t, final T d1LoRef, final T d1HiRef, final T d2LoRef,
      final T d2HiRef, final T d3LoRef, final T d3HiRef) {
    final List<T> result = new ArrayList<>();
    final Node3D<T> vSplit = findSplitNode(t, d1LoRef, d1HiRef);
    if (vSplit.isLeaf()) {
      final boolean inRangeD1 = auxd1cp.compare(vSplit.element, d1LoRef) >= 0
          && auxd1cp.compare(vSplit.element, d1HiRef) <= 0;
      final boolean inRangeD2 =
          auxd2cp.compare(vSplit.element, d2LoRef) >= 0 && auxd2cp.compare(vSplit.element, d2HiRef) <= 0;
      final boolean inRangeD3 =
          auxd3cp.compare(vSplit.element, d3LoRef) >= 0 && auxd3cp.compare(vSplit.element, d3HiRef) <= 0;
      if (inRangeD1 && inRangeD2 && inRangeD3) {
        result.add(vSplit.element);
      }
    } else {
      Node3D<T> v = vSplit.left;
      while (!v.isLeaf()) {
        final boolean cp = auxd1cp.compare(d1LoRef, v.element) <= 0;
        if (cp) {
          final List<T> part = queryY(v.right.yTree, d2LoRef, d2HiRef, d3LoRef, d3HiRef);
          result.addAll(part);
          v = v.left;
        } else {
          v = v.right;
        }
      }
      boolean inRangeD1 =
          auxd1cp.compare(v.element, d1LoRef) >= 0 && auxd1cp.compare(v.element, d1HiRef) <= 0;
      boolean inRangeD2 =
          auxd2cp.compare(v.element, d2LoRef) >= 0 && auxd2cp.compare(v.element, d2HiRef) <= 0;
      boolean inRangeD3 =
          auxd3cp.compare(v.element, d3LoRef) >= 0 && auxd3cp.compare(v.element, d3HiRef) <= 0;
      if (inRangeD1 && inRangeD2 && inRangeD3) {
        result.add(v.element);
      }

      v = vSplit.right;
      while (!v.isLeaf()) {
        final boolean cp = auxd1cp.compare(d1HiRef, v.element) >= 0;
        if (cp) {
          final List<T> part = queryY(v.left.yTree, d2LoRef, d2HiRef, d3LoRef, d3HiRef);
          result.addAll(part);
          v = v.right;
        } else {
          v = v.left;
        }
      }
      inRangeD1 =
          auxd1cp.compare(v.element, d1LoRef) >= 0 && auxd1cp.compare(v.element, d1HiRef) <= 0;
      inRangeD2 = auxd2cp.compare(v.element, d2LoRef) >= 0 && auxd2cp.compare(v.element, d2HiRef) <= 0;
      inRangeD3 = auxd3cp.compare(v.element, d3LoRef) >= 0 && auxd3cp.compare(v.element, d3HiRef) <= 0;
      if (inRangeD1 && inRangeD2 && inRangeD3) {
        result.add(v.element);
      }
    }
    return result;
  }

  private Node3D<T> findSplitNode(Node3D<T> T, final T x, final T xp) {
    boolean cp1;
    while (!T.isLeaf()
        && ((cp1 = auxd1cp.compare(xp, T.element) < 0) || auxd1cp.compare(x, T.element) > 0)) {
      if (cp1)
        T = T.left;
      else
        T = T.right;
    }
    return T;
  }

  private List<T> queryY(final NewRangeTree2D<T> t, final T d2LoRef, final T d2HiRef, final T d3LoRef,
      final T d3HiRef) {
    return t.searchInRange(d2LoRef, d2HiRef, d3LoRef, d3HiRef);
  }

}
