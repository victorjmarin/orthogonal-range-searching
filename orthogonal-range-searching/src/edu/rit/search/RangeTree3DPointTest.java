package edu.rit.search;

import java.util.ArrayList;
import java.util.List;
import edu.rit.index.Point3D;
import edu.rit.index.tree.RangeTree3D2;

public class RangeTree3DPointTest {

  private static final int MIN_X = 0;
  private static final int MAX_X = 5;
  private static final int MIN_Y = 0;
  private static final int MAX_Y = 6;
  private static final int MIN_Z = 0;
  private static final int MAX_Z = 7;

  public static void main(final String[] args) {
    final List<Point3D> P = rnd(100);
    final Point3D d1LoRef = new Point3D(MIN_X, 0, 0);
    final Point3D d1HiRef = new Point3D(MAX_X, 0, 0);
    final Point3D d2LoRef = new Point3D(0, MIN_Y, 0);
    final Point3D d2HiRef = new Point3D(0, MAX_Y, 0);
    final Point3D d3LoRef = new Point3D(0, 0, MIN_Z);
    final Point3D d3HiRef = new Point3D(0, 0, MAX_Z);
    long t1 = System.currentTimeMillis();
    final RangeTree3D2<Point3D> rtree =
        new RangeTree3D2<>(P, Point3D::getX, Point3D::getY, Point3D::getZ, Point3D::getId);
    long t2 = System.currentTimeMillis();
    System.out.println(t2 - t1 + " ms. to build the index.");
    t1 = System.currentTimeMillis();
    final List<Point3D> inRange =
        rtree.searchInRange(d1LoRef, d1HiRef, d2LoRef, d2HiRef, d3LoRef, d3HiRef);
    t2 = System.currentTimeMillis();
    System.out.println(t2 - t1 + " ms. to query the index.");
    System.out.println(inRange.size());
    t1 = System.currentTimeMillis();
    final int inRange2 = countInRange(P, d1LoRef, d1HiRef, d2LoRef, d2HiRef, d3LoRef, d3HiRef);
    t2 = System.currentTimeMillis();
    System.out.println(t2 - t1 + " ms. to query full scan.");
    System.out.println(inRange2);
  }

  private static List<Point3D> rnd(final int size) {
    final List<Point3D> result = new ArrayList<>();
    for (int i = 0; i < size; i++) {
      result.add(Point3D.rnd(MIN_X, MAX_X * 2, MIN_Y, MAX_Y * 2, MIN_Z, MAX_Z * 2));
    }
    return result;
  }

  private static int countInRange(final List<Point3D> P, final Point3D d1LoRef,
      final Point3D d1HiRef, final Point3D d2LoRef, final Point3D d2HiRef, final Point3D d3LoRef,
      final Point3D d3HiRef) {
    int result = 0;
    for (final Point3D p : P) {
      if (p.getX() >= d1LoRef.getX() && p.getX() <= d1HiRef.getX() && p.getY() >= d2LoRef.getY()
          && p.getY() <= d2HiRef.getY() && p.getZ() >= d3LoRef.getZ()
          && p.getZ() <= d3HiRef.getZ()) {
        result++;
      }
    }
    return result;
  }

}
