package edu.rit.query;

public class Reference<T> {

  private final T lo;
  private final T hi;

  public Reference(final T lo, final T hi) {
    super();
    this.lo = lo;
    this.hi = hi;
  }

  public T getLo() {
    return lo;
  }

  public T getHi() {
    return hi;
  }

}
