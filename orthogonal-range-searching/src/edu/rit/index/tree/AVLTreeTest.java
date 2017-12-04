package edu.rit.index.tree;

import java.util.Comparator;
import java.util.concurrent.ThreadLocalRandom;

public class AVLTreeTest {

  public static void main(final String[] args) {
    final AVLTree<Integer> tree = new AVLTree<>(Comparator.<Integer>naturalOrder());
    long tic = System.currentTimeMillis();
    for (int i = 0; i < 1000000; i++) {
      tree.insert(i);
    }
    long toc = System.currentTimeMillis();
    System.out.println(toc - tic);
    tic = System.currentTimeMillis();
    for (int i = 0; i < 999999; i++) {
      final int rnd = ThreadLocalRandom.current().nextInt();
      tree.insert(rnd);
    }
    toc = System.currentTimeMillis();
    System.out.println(toc - tic);
  }

}
