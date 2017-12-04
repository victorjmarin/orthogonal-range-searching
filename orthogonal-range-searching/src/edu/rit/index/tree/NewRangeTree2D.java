package edu.rit.index.tree;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.function.Function;
import edu.rit.index.Index;

public class NewRangeTree2D<T> implements Index {

  private final Comparator<T> d1cp;
  private final Comparator<T> d2cp;
  private final Comparator<T> idcp;
  private final Comparator<T> auxd1cp;
  private final Comparator<T> auxd2cp;
  Function<T, Double> d1Getter;
  private Node2D<T> root;

  public NewRangeTree2D(final List<T> P, final Function<T, Double> d1Getter,
      final Function<T, Double> d2Getter, final Function<T, Double> idGetter) {
    this.d1Getter = d1Getter;
    auxd1cp = Comparator.comparing(d1Getter);
    auxd2cp = Comparator.comparing(d2Getter);
    idcp = Comparator.comparing(idGetter);
    d1cp = new Comparator<T>() {
      @Override
      public int compare(final T o1, final T o2) {
        int cp = auxd1cp.compare(o1, o2);
        if (cp == 0)
          cp = auxd2cp.compare(o1, o2);
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
          cp = idcp.compare(o1, o2);
        return cp;
      }
    };
    // Sort points using first dimension
    Collections.sort(P, auxd1cp);
    root = build2DRangeTree(P);
  }

  private Node2D<T> build2DRangeTree(final List<T> P) {
    if (P.size() == 1) {
      final T element = P.get(0);
      final AVLTree<T> tAssoc = new AVLTree<T>(auxd2cp);
      tAssoc.insert(element);
      return new Node2D<>(element, tAssoc);
    }
    final T xMid = median(P);
    final List<T> pLeft = smaller(P, xMid);
    final List<T> pRight = larger(P, xMid);
    final Node2D<T> v = new Node2D<>(xMid);
    v.left = build2DRangeTree(pLeft);
    v.right = build2DRangeTree(pRight);
    v.yTree = mergeYTree(v.left.yTree, v.right.yTree);
    return v;
  }

  private AVLTree<T> mergeYTree(final AVLTree<T> t1, final AVLTree<T> t2) {
    final AVLTree<T> t1copy = new AVLTree<>(t1.getComparator());
    AVLTree<T>.Node v = t1.getTop();
    AVLTree<T>.Node n = v.getSmallest();
    t1copy.insert(n.getElement());
    while ((n = n.getNext()) != null) {
      t1copy.insert(n.getElement());
    }
    v = t2.getTop();
    n = v.getSmallest();
    t1copy.insert(n.getElement());
    while ((n = n.getNext()) != null) {
      t1copy.insert(n.getElement());
    }
    return t1copy;
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

  private List<T> queryY(final AVLTree<T> t, final T d2LoRef, final T d2HiRef) {
    return t.searchInRange(d2LoRef, d2HiRef);
  }

  public List<T> searchInRange(final T d1LoRef, final T d1HiRef, final T d2LoRef, final T d2HiRef) {
    return searchInRange(root, d1LoRef, d1HiRef, d2LoRef, d2HiRef);
  }

  private List<T> searchInRange(final Node2D<T> t, final T d1LoRef, final T d1HiRef,
      final T d2LoRef, final T d2HiRef) {
    final List<T> result = new ArrayList<>();
    final Node2D<T> vSplit = findSplitNode(t, d1LoRef, d1HiRef);
    if (vSplit.isLeaf()) {
      final boolean inRangeD1 = auxd1cp.compare(vSplit.element, d1LoRef) >= 0
          && auxd1cp.compare(vSplit.element, d1HiRef) <= 0;
      final boolean inRangeD2 = auxd2cp.compare(vSplit.element, d2LoRef) >= 0
          && auxd2cp.compare(vSplit.element, d2HiRef) <= 0;
      if (inRangeD1 && inRangeD2) {
        result.add(vSplit.element);
      }
    } else {
      Node2D<T> v = vSplit.left;
      while (!v.isLeaf()) {
        final boolean cp = auxd1cp.compare(d1LoRef, v.element) <= 0;
        if (cp) {
          final List<T> part = queryY(v.right.yTree, d2LoRef, d2HiRef);
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
      if (inRangeD1 && inRangeD2) {
        result.add(v.element);
      }

      v = vSplit.right;
      while (!v.isLeaf()) {
        final boolean cp = auxd1cp.compare(d1HiRef, v.element) >= 0;
        if (cp) {
          final List<T> part = queryY(v.left.yTree, d2LoRef, d2HiRef);
          result.addAll(part);
          v = v.right;
        } else {
          v = v.left;
        }
      }
      inRangeD1 =
          auxd1cp.compare(v.element, d1LoRef) >= 0 && auxd1cp.compare(v.element, d1HiRef) <= 0;
      inRangeD2 =
          auxd2cp.compare(v.element, d2LoRef) >= 0 && auxd2cp.compare(v.element, d2HiRef) <= 0;
      if (inRangeD1 && inRangeD2) {
        result.add(v.element);
      }
    }
    return result;
  }

  private Node2D<T> findSplitNode(Node2D<T> T, final T x, final T xp) {
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

  public Node2D<T> getRoot() {
    return root;
  }
}
