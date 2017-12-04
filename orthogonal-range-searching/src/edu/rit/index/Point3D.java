package edu.rit.index;

import java.util.concurrent.ThreadLocalRandom;

public class Point3D {

  private final double id;
  private final double x;
  private final double y;
  private final double z;

  private static double ID = 0d;

  public Point3D(final double x, final double y, final double z) {
    super();
    id = ID++;
    this.x = x;
    this.y = y;
    this.z = z;
  }

  public double getId() {
    return id;
  }

  public double getX() {
    return x;
  }

  public double getY() {
    return y;
  }

  public double getZ() {
    return z;
  }

  public static Point3D rnd(final int fromX, final int toX, final int fromY, final int toY,
      final int fromZ, final int toZ) {
    final int x = ThreadLocalRandom.current().nextInt(fromX, toX + 1);
    final int y = ThreadLocalRandom.current().nextInt(fromY, toY + 1);
    final int z = ThreadLocalRandom.current().nextInt(fromZ, toZ + 1);
    return new Point3D(x, y, z);
  }

  @Override
  public String toString() {
    return "(" + x + ", " + y + ", " + z + ")";
  }

}
