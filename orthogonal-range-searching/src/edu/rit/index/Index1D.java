package edu.rit.index;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Comparator;
import java.util.LinkedList;
import java.util.List;
import edu.rit.index.tree.AVLTree;

public class Index1D<T> implements Index {

  private final AVLTree<T> tree;

  public Index1D(final Comparator<T> comparator) {
    tree = new AVLTree<>(comparator);
  }

  public void build(final T[] elements) {
    Arrays.sort(elements, tree.getComparator());
    for (final T t : elements) {
      tree.insert(t);
    }
  }

  public void build(final Collection<T> elements) {
    final List<T> elementsLst = new ArrayList<>(elements);
    elementsLst.sort(tree.getComparator());
    for (final T t : elementsLst) {
      tree.insert(t);
    }
  }

  public List<T> searchInRange(final T loRef, final T hiRef) {
    if (tree.getTop() == null)
      throw new IllegalStateException("Add elements to the index before querying it.");
    final List<T> result = new LinkedList<>();
    final AVLTree<T>.Node u = tree.getNotSmaller(loRef);
    final AVLTree<T>.Node v = tree.getNotLarger(hiRef);
    AVLTree<T>.Node next = u;
    while (next != null) {
      result.add(next.getElement());
      if (next == v)
        break;
      next = next.getNext();
    }
    return result;
  }

  public List<T> search(final T loRef, final T hiRef) {
    return rangeQuery1D(tree, loRef, hiRef);
  }

  private AVLTree<T>.Node findSplitNode(final AVLTree<T> T, final T x, final T xp) {
    AVLTree<T>.Node v = T.getTop();
    final boolean cp1 = T.getComparator().compare(xp, v.getElement()) <= 0;
    final boolean cp2 = T.getComparator().compare(x, v.getElement()) > 0;
    while (!v.isLeaf() && (cp1 || cp2)) {
      if (cp1)
        v = v.getLeft();
      else
        v = v.getRight();
    }
    return v;
  }

  private List<T> rangeQuery1D(final AVLTree<T> T, final T loRef, final T hiRef) {
    final List<T> result = new ArrayList<>();
    AVLTree<T>.Node v = null;
    final AVLTree<T>.Node vSplit = findSplitNode(T, loRef, hiRef);
    if (vSplit.isLeaf()) {
      // Check if vSplit should be reported
      final boolean cp = T.getComparator().compare(loRef, vSplit.getElement()) <= 0
          && T.getComparator().compare(hiRef, vSplit.getElement()) >= 0;
      if (cp)
        result.add(vSplit.getElement());
    } else {
      v = vSplit.getLeft();
      while (!v.isLeaf()) {
        final boolean cp1 = T.getComparator().compare(loRef, v.getElement()) <= 0;
        if (cp1) {
          final List<T> leaves = reportSubtree(v.getRight());
          result.addAll(leaves);
          v = v.getLeft();
        } else
          v = v.getRight();
      }
      // Check if v should be reported
      boolean cp = T.getComparator().compare(loRef, v.getElement()) <= 0
          && T.getComparator().compare(hiRef, v.getElement()) >= 0;
      if (cp)
        result.add(v.getElement());
      v = vSplit.getRight();
      while (!v.isLeaf()) {
        final boolean cp1 = T.getComparator().compare(hiRef, v.getElement()) >= 0;
        if (cp1) {
          final List<T> leaves = reportSubtree(v.getLeft());
          result.addAll(leaves);
          v = v.getRight();
        } else
          v = v.getLeft();
      }
      // Check if v should be reported
      cp = T.getComparator().compare(loRef, v.getElement()) <= 0
          && T.getComparator().compare(hiRef, v.getElement()) >= 0;
      if (cp)
        result.add(v.getElement());
    }
    return result;
  }

  private List<T> reportSubtree(final AVLTree<T>.Node v) {
    final List<T> result = new ArrayList<>();
    if (v == null)
      return result;
    if (v.isLeaf()) {
      result.add(v.getElement());
      return result;
    }
    if (v.getLeft() != null) {
      final List<T> l = reportSubtree(v.getLeft());
      result.addAll(l);
    }
    if (v.getRight() != null) {
      final List<T> r = reportSubtree(v.getRight());
      result.addAll(r);
    }
    return result;
  }

}
