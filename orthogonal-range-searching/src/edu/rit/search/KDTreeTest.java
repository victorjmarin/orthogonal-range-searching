package edu.rit.search;

import java.util.List;
import edu.rit.index.Point2D;
import edu.rit.index.tree.KDTree;

public class KDTreeTest {

  public static void main(final String[] args) {
    // TODO: 2 points with same coordinates
    final Point2D[] p = new Point2D[4];
    p[0] = new Point2D(0, 6);
    p[1] = new Point2D(6, 4);
    p[2] = new Point2D(7, 2);
    p[3] = new Point2D(9, 1);
    final KDTree<Point2D> kdtree = new KDTree<>(p, Point2D::getX, Point2D::getY);
    final KDTree<Point2D>.RectangularRange R =
        kdtree.new RectangularRange(kdtree.new LinearRange(0, 10), kdtree.new LinearRange(0, 5));
    final List<Point2D> inRange = kdtree.searchRange(R);
    System.out.println(inRange);
  }

}
