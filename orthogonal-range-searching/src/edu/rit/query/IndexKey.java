package edu.rit.query;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class IndexKey {

  private final String table;
  private final List<String> attributes;

  public IndexKey(final String table, final String... attrs) {
    this.table = table.toLowerCase();
    attributes = new ArrayList<>();
    for (final String a : attrs) {
      attributes.add(a.toLowerCase());
    }
  }

  public String getTable() {
    return table;
  }

  public List<String> getAttributes() {
    return attributes;
  }

  @Override
  public int hashCode() {
    return Objects.hash(table, attributes);
  }

  @Override
  public boolean equals(final Object o) {
    if (this == o)
      return true;
    if (!(o instanceof IndexKey))
      return false;
    final IndexKey idxKey = (IndexKey) o;
    return table.equals(idxKey.getTable()) && attributes.equals(idxKey.getAttributes());
  }

}
