package edu.rit.query;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class RangeSearchQuery<T> {

  private String table;
  private AttributeRange xRange;
  private AttributeRange yRange;
  private AttributeRange zRange;
  private final ReferenceBuilder<T> refBuilder;
  private final List<String> project;

  public RangeSearchQuery(final ReferenceBuilder<T> refBuilder) {
    this.refBuilder = refBuilder;
    project = new ArrayList<>();
  }

  public RangeSearchQuery<T> table(final String table) {
    this.table = table;
    return this;
  }

  public RangeSearchQuery<T> xRange(final String attribute, final int min, final int max) {
    final AttributeRange range = new AttributeRange(attribute, min, max);
    xRange = range;
    return this;
  }

  public RangeSearchQuery<T> yRange(final String attribute, final int min, final int max) {
    final AttributeRange range = new AttributeRange(attribute, min, max);
    yRange = range;
    return this;
  }

  public RangeSearchQuery<T> zRange(final String attribute, final int min, final int max) {
    final AttributeRange range = new AttributeRange(attribute, min, max);
    zRange = range;
    return this;
  }

  public void projectColumn(final String columnName) {
    project.add(columnName);
  }

  public String getTable() {
    return table;
  }

  public AttributeRange getxRange() {
    return xRange;
  }

  public AttributeRange getyRange() {
    return yRange;
  }

  public AttributeRange getzRange() {
    return zRange;
  }

  @Override
  public String toString() {
    final StringBuilder sb = new StringBuilder();
    String projection = null;
    if (!project.isEmpty())
      projection = project.stream().collect(Collectors.joining(", "));
    else
      projection = "*";
    sb.append("SELECT " + projection + " FROM " + table);
    if (xRange != null) {
      sb.append(" WHERE ");
      sb.append(xRange.toString());
    }
    if (yRange != null) {
      sb.append(" AND ");
      sb.append(yRange.toString());
    }
    if (zRange != null) {
      sb.append(" AND ");
      sb.append(zRange.toString());
    }
    sb.append(";");
    return sb.toString();
  }

  public IndexKey getIndexKey() {
    return new IndexKey(table, getAttributes());
  }

  public int getDimension() {
    int result = 0;
    if (xRange != null)
      result += 1;
    if (yRange != null)
      result += 1;
    if (zRange != null)
      result += 1;
    return result;
  }

  public String[] getAttributes() {
    final int d = getDimension();
    final String[] result = new String[d];
    if (xRange != null)
      result[0] = xRange.getAttribute();
    if (yRange != null)
      result[1] = yRange.getAttribute();
    if (zRange != null)
      result[2] = zRange.getAttribute();
    return result;
  }

  public String xAttribute() {
    return xRange == null ? null : xRange.getAttribute();
  }

  public String yAttribute() {
    return yRange == null ? null : yRange.getAttribute();
  }

  public String zAttribute() {
    return zRange == null ? null : zRange.getAttribute();
  }

  public Reference<T> referenceForX() {
    return refBuilder.referenceForAttributeRange(xRange);
  }

  public Reference<T> referenceForY() {
    return refBuilder.referenceForAttributeRange(yRange);
  }

  public Reference<T> referenceForZ() {
    return refBuilder.referenceForAttributeRange(zRange);
  }

  public void setXRange(final AttributeRange xRange) {
    this.xRange = xRange;
  }

  public void setYRange(final AttributeRange yRange) {
    this.yRange = yRange;
  }

  public void setZRange(final AttributeRange zRange) {
    this.zRange = zRange;
  }

  public List<String> getProject() {
    return project;
  }

}
