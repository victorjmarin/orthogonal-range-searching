package edu.rit.index.tree;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.function.Function;
import edu.rit.index.Index;

public class RangeTree2D2<T> implements Index {

  private final Comparator<T> d1cp;
  private final Comparator<T> idcp;
  private final Comparator<T> auxd1cp;
  private final Comparator<T> auxd2cp;
  Function<T, Double> d1Getter;
  private final Node<T> t;

  public RangeTree2D2(final List<T> P, final Function<T, Double> d1Getter,
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
    // Sort points using first dimension
    Collections.sort(P, auxd1cp);
    t = build2DRangeTree(P);
  }

  private Node<T> build2DRangeTree(final List<T> P) {
    if (P.size() == 1) {
      final T element = P.get(0);
      final AVLTree<T> yTree = new AVLTree<T>(auxd2cp);
      yTree.insert(element);
      return new Node<>(element, yTree);
    }
    final T median = median(P);
    final List<T> L = smaller(P, median);
    final List<T> R = larger(P, median);
    final Node<T> t = new Node<>(median);
    t.left = build2DRangeTree(L);
    t.right = build2DRangeTree(R);
    t.d3Tree = mergeYTree(t.left.d3Tree, t.right.d3Tree);
    return t;
  }

  private AVLTree<T> mergeYTree2(final AVLTree<T> t1, final AVLTree<T> t2) {
    final List<T> l1 = new ArrayList<>();
    AVLTree<T>.Node u = t1.getSmallest();
    l1.add(u.getElement());
    while ((u = u.getNext()) != null) {
      l1.add(u.getElement());
    }
    u = t2.getSmallest();
    l1.add(u.getElement());
    while ((u = u.getNext()) != null) {
      l1.add(u.getElement());
    }
    Collections.sort(l1, auxd2cp);
    final AVLTree<T>.Node n = buildAVLTree(l1);
    final AVLTree<T> result = new AVLTree<T>(auxd2cp);
    result.setTop(n);
    return result;
  }

  private AVLTree<T>.Node buildAVLTree(final List<T> l) {
    if (l.isEmpty())
      return null;
    if (l.size() == 1) {
      final AVLTree<T> yTree = new AVLTree<T>(auxd2cp);
      yTree.insert(l.get(0));
      return yTree.getTop();
    }
    final AVLTree<T> yTree = new AVLTree<T>(auxd2cp);
    final T median = median(l);
    final List<T> L = smallerY(l, median);
    final List<T> R = larger(l, median);
    yTree.insert(median);
    final AVLTree<T>.Node left = buildAVLTree(L);
    if (left != null) {
      left.setParent(yTree.getTop());
      yTree.getTop().setLeft(left);
    }
    final AVLTree<T>.Node right = buildAVLTree(R);
    if (right != null) {
      right.setParent(yTree.getTop());
      yTree.getTop().setRight(right);
    }
    return yTree.getTop();
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

  public Node<T> getRoot() {
    return t;
  }

  private List<T> smallerY(final List<T> P, final T v) {
    final int idx = (P.size() - 1) / 2;
    return P.subList(0, idx);
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

  public List<T> searchInRange(final T d1LoRef, final T d1HiRef, final T d2LoRef, final T d2HiRef) {
    return searchInRange(t, d1LoRef, d1HiRef, d2LoRef, d2HiRef);
  }

  private List<T> searchInRange(final Node<T> t, final T d1LoRef, final T d1HiRef, final T d2LoRef,
      final T d2HiRef) {
    List<T> result = new ArrayList<>();
    if (t.isLeaf()) {
      final boolean inRangeD1 =
          auxd1cp.compare(t.element, d1LoRef) >= 0 && auxd1cp.compare(t.element, d1HiRef) <= 0;
      final boolean inRangeD2 =
          auxd2cp.compare(t.element, d2LoRef) >= 0 && auxd2cp.compare(t.element, d2HiRef) <= 0;
      if (inRangeD1 && inRangeD2) {
        result.add(t.element);
      }
      return result;
    }
    if (inside(t, d1LoRef, d1HiRef)) {
      result = queryY(t.d3Tree, d2LoRef, d2HiRef);
      return result;
    } else {
      if (intersects(t.left, d1LoRef, d1HiRef)) {
        final List<T> part1 = searchInRange(t.left, d1LoRef, d1HiRef, d2LoRef, d2HiRef);
        result.addAll(part1);
      }
      if (intersects(t.right, d1LoRef, d1HiRef)) {
        final List<T> part2 = searchInRange(t.right, d1LoRef, d1HiRef, d2LoRef, d2HiRef);
        result.addAll(part2);
      }
    }
    return result;
  }

  private List<T> queryY(final AVLTree<T> t, final T d2LoRef, final T d2HiRef) {
    return t.searchInRange(d2LoRef, d2HiRef);
  }

  private boolean inside(final Node<T> t, final T d1LoRef, final T d1HiRef) {
    final T left = t.left.element;
    final T right = t.right.element;
    final boolean c1 = auxd1cp.compare(left, d1LoRef) >= 0;
    final boolean c2 = auxd1cp.compare(right, d1HiRef) <= 0;
    return c1 && c2;
  }

  private boolean intersects(final Node<T> t, final T d1LoRef, final T d1HiRef) {
    final T left = t.left.element;
    final T right = t.right.element;
    final boolean c1 = auxd1cp.compare(left, d1HiRef) <= 0;
    final boolean c2 = auxd1cp.compare(right, d1LoRef) > 0;
    return c1 && c2;
  }

  /**
   * Alternative impl
   * 
   */

  public List<T> searchInRange2(final T d1LoRef, final T d1HiRef, final T d2LoRef,
      final T d2HiRef) {
    return searchInRange2(t, d1LoRef, d1HiRef, d2LoRef, d2HiRef);
  }

  private List<T> searchInRange2(final Node<T> t, final T d1LoRef, final T d1HiRef, final T d2LoRef,
      final T d2HiRef) {
    final List<T> result = new ArrayList<>();
    final Node<T> vSplit = findSplitNode(t, d1LoRef, d1HiRef);
    if (vSplit.isLeaf()) {
      final boolean inRangeD1 = auxd1cp.compare(vSplit.element, d1LoRef) >= 0
          && auxd1cp.compare(vSplit.element, d1HiRef) <= 0;
      final boolean inRangeD2 = auxd2cp.compare(vSplit.element, d2LoRef) >= 0
          && auxd2cp.compare(vSplit.element, d2HiRef) <= 0;
      if (inRangeD1 && inRangeD2) {
        result.add(vSplit.element);
      }
    } else {
      Node<T> v = vSplit.left;
      while (!v.isLeaf()) {
        final boolean cp = auxd1cp.compare(d1LoRef, v.element) <= 0;
        if (cp) {
          final List<T> part = queryY(v.right.d3Tree, d2LoRef, d2HiRef);
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
          final List<T> part = queryY(v.left.d3Tree, d2LoRef, d2HiRef);
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

  private Node<T> findSplitNode(Node<T> T, final T x, final T xp) {
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

}
