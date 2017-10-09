package edu.rit.index;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Comparator;
import java.util.LinkedList;
import java.util.List;

public class Index1D<T extends Indexable<T>> {

    private final T t;
    private final AVLTree<T> tree;

    public Index1D(final T t, final Comparator<T> comparator) {
	this.t = t;
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

    public List<T> searchInRange(final int lo, final int hi) {
	if (tree.getTop() == null)
	    throw new IllegalStateException("Add elements to the index before querying it.");
	if (hi < lo)
	    throw new IllegalArgumentException("hi must be >= lo.");
	final List<T> result = new LinkedList<>();
	final T loRef = t.buildIntReference(lo);
	final T hiRef = t.buildIntReference(hi);
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

}