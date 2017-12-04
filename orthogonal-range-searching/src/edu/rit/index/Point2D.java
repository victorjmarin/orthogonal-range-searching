package edu.rit.index;


import java.util.concurrent.ThreadLocalRandom;

public class Point2D implements Comparable<Point2D> {

  private final int id;
  public final double x;
  public final double y;

  private static int ID = 0;

  public Point2D(final double x, final double y) {
    super();
    id = ID++;
    this.x = x;
    this.y = y;
  }

  public double getId() {
    return id;
  }

  public String outputFormat() {
    return x + " " + y;
  }

  public static Point2D rnd(final int fromX, final int toX, final int fromY, final int toY) {
    final int x = ThreadLocalRandom.current().nextInt(fromX, toX + 1);
    final int y = ThreadLocalRandom.current().nextInt(fromY, toY + 1);
    return new Point2D(x, y);
  }

  public double getX() {
    return x;
  }

  public double getY() {
    return y;
  }


  @Override
  public String toString() {
    return "(" + x + "," + y + ")";
  }

  @Override
  public boolean equals(final Object o) {
    if (o instanceof Point2D) {
      final Point2D p = (Point2D) o;
      return id == p.id;
    }
    return super.equals(o);
  }

  @Override
  public int compareTo(final Point2D p) {
    if (x == p.x) {
      final double diff = y - p.y;
      if (diff > 0)
        return 1;
      if (diff < 0)
        return -1;
      return 0;
    } else {
      final double diff = x - p.x;
      if (diff > 0)
        return 1;
      if (diff < 0)
        return -1;
      return 0;
    }
  }

  // @Override
  // public int hashCode() {
  // long bits = Double.doubleToLongBits(x);
  // bits ^= Double.doubleToLongBits(y) * 31;
  // return (((int) bits) ^ ((int) (bits >> 32)));
  // }

  @Override
  public int hashCode() {
    return id;
  }

}
