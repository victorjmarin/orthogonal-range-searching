package edu.rit.query;

public class AttributeRange {

  private final String attribute;
  private final double min;
  private final double max;

  public AttributeRange(final String attribute, final double min, final double max) {
    super();
    this.attribute = attribute;
    this.min = min;
    this.max = max;
  }

  public String getAttribute() {
    return attribute;
  }

  public double getMin() {
    return min;
  }

  public double getMax() {
    return max;
  }

  @Override
  public String toString() {
    final StringBuilder sb = new StringBuilder();
    sb.append(attribute);
    sb.append(" BETWEEN ");
    sb.append(fmt(min));
    sb.append(" AND ");
    sb.append(fmt(max));
    return sb.toString();
  }

  private String fmt(final double d) {
    if (d == (long) d)
      return String.format("%d", (long) d);
    else
      return String.format("%s", d);
  }

}
