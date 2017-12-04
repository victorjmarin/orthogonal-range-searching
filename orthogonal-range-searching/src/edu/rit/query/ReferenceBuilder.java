package edu.rit.query;

public interface ReferenceBuilder<T> {

  Reference<T> referenceForAttributeRange(final AttributeRange attrRange);

}
