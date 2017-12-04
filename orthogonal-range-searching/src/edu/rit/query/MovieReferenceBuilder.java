package edu.rit.query;

import edu.rit.Movie;

public class MovieReferenceBuilder implements ReferenceBuilder<Movie> {

  @Override
  public Reference<Movie> referenceForAttributeRange(final AttributeRange attrRange) {
    Reference<Movie> result;
    Movie lo;
    Movie hi;
    final double min = attrRange.getMin();
    final double max = attrRange.getMax();
    switch (attrRange.getAttribute()) {
      case "year":
        lo = Movie.withYear((int) min);
        hi = Movie.withYear((int) max);
        break;
      case "imdb_rating":
        lo = Movie.withRating(min);
        hi = Movie.withRating(max);
        break;
      case "imdb_votes":
        lo = Movie.withVotes((int) min);
        hi = Movie.withVotes((int) max);
        break;
      case "budget":
        lo = Movie.withBudget((int) min);
        hi = Movie.withBudget((int) max);
        break;
      case "length":
        lo = Movie.withLength((int) min);
        hi = Movie.withLength((int) max);
        break;
      default:
        throw new IllegalArgumentException(
            "Unknown attribute (" + attrRange.getAttribute() + ") for table Movies.");
    }
    result = new Reference<Movie>(lo, hi);
    return result;
  }

}
