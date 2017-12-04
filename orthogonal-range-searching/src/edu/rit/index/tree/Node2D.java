package edu.rit.index.tree;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Stack;

public class Node2D<T> {

  public T element;
  public Node2D<T> left;
  public Node2D<T> right;
  public AVLTree<T> yTree;

  public Node2D(final T element) {
    this.element = element;
  }

  public Node2D(final T element, final Comparator<T> cmp) {
    this.element = element;
    yTree = new AVLTree<>(cmp);
  }

  public Node2D(final T element, final AVLTree<T> yTree) {
    this.element = element;
    this.yTree = yTree;
  }

  public boolean isLeaf() {
    return left == null && right == null;
  }

  @Override
  public String toString() {
    return element.toString();
  }

  public List<T> getLeaves() {
    return getLeavesIt(this);
  }

  private List<T> getLeavesIt(final Node2D<T> n) {
    if (n == null) {
      return new ArrayList<>();
    }
    final Stack<Node2D<T>> stack = new Stack<>();
    stack.push(n);

    final List<T> result = new ArrayList<>();

    while (!stack.isEmpty()) {
      final Node2D<T> node = stack.pop();
      if (node.left != null) {
        stack.add(node.left);
      }
      if (node.right != null) {
        stack.add(node.right);
      }
      if (node.isLeaf()) {
        result.add(node.element);
      }
    }
    return result;
  }



}
