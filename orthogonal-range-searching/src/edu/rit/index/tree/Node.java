package edu.rit.index.tree;

import java.util.ArrayList;
import java.util.List;
import java.util.Stack;

public class Node<T> {

  public T element;
  public Node<T> left;
  public Node<T> right;
  public RangeTree2D<T> d2Tree;
  public AVLTree<T> d3Tree;

  public Node(final T element) {
    this.element = element;
  }

  public Node(final T element, final RangeTree2D<T> d2Tree) {
    this.element = element;
    this.d2Tree = d2Tree;
  }

  public Node(final T element, final AVLTree<T> dTree) {
    this.element = element;
    this.d3Tree = dTree;
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

  private List<T> getLeaves(final Node<T> n) {
    final List<T> result = new ArrayList<>();
    if (n == null)
      return result;
    if (n.isLeaf()) {
      result.add(n.element);
      return result;
    }
    final List<T> part1 = getLeaves(left);
    result.addAll(part1);
    final List<T> part2 = getLeaves(right);
    result.addAll(part2);
    return result;
  }

  private List<T> getLeavesIt(final Node<T> n) {
    if (n == null) {
      return new ArrayList<>();
    }
    final Stack<Node<T>> stack = new Stack<>();
    stack.push(n);

    final List<T> result = new ArrayList<>();

    while (!stack.isEmpty()) {
      final Node<T> node = stack.pop();
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
