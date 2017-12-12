package edu.rit.search;

import java.util.ArrayList;
import java.util.List;
import edu.rit.index.Point2D;
import edu.rit.index.tree.RangeTree2D2;

public class RangeTree2DPointTest {

  public static void main(final String[] args) {
    final List<Point2D> P = new ArrayList<>();
    P.add(new Point2D(6, 4));
    P.add(new Point2D(6, 4));
    P.add(new Point2D(7, 2));
    P.add(new Point2D(2, 1));
    P.add(new Point2D(9, 1));
    final Point2D d1LoRef = new Point2D(0, 0);
    final Point2D d1HiRef = new Point2D(6, 0);
    final Point2D d2LoRef = new Point2D(0, 0);
    final Point2D d2HiRef = new Point2D(0, 4);
    final RangeTree2D2<Point2D> rtree =
        new RangeTree2D2<>(P, Point2D::getX, Point2D::getY, Point2D::getId);
    final List<Point2D> result = rtree.searchInRange2(d1LoRef, d1HiRef, d2LoRef, d2HiRef);
    System.out.println(result);
  }

}
