package edu.rit.index;

import java.util.List;
import java.util.function.Function;
import edu.rit.index.tree.KDTree;

public class Index2D<T> {

  private KDTree<T> tree;

  public static <T> Index2D<T> forInts(final T[] data, final Function<T, Integer> d1,
      final Function<T, Integer> d2) {
    final Index2D<T> index = new Index2D<T>();
    index.tree = new KDTree<>(data, d1.andThen(i -> (double) i), d2.andThen(i -> (double) i));
    return index;
  }

  private Index2D() {}

  public Index2D(final T[] data, final Function<T, Double> d1, final Function<T, Double> d2) {
    tree = new KDTree<>(data, d1, d2);
  }

  public List<T> searchInRange(final double xlo, final double xhi, final double ylo,
      final double yhi) {
    final KDTree<T>.RectangularRange R =
        tree.new RectangularRange(tree.new LinearRange(xlo, xhi), tree.new LinearRange(ylo, yhi));
    return tree.searchRange(R);
  }


}
