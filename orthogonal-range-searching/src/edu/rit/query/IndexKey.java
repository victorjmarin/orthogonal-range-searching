package edu.rit.query;

import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

public class IndexKey {

  private final String table;
  private final Set<String> attributes;

  public IndexKey(final String table, final String... attrs) {
    this.table = table.toLowerCase();
    attributes = new HashSet<>();
    for (final String a : attrs) {
      attributes.add(a.toLowerCase());
    }
  }

  public String getTable() {
    return table;
  }

  public Set<String> getAttributes() {
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
