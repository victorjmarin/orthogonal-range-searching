package edu.rit.query;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import edu.rit.Movie;
import edu.rit.db.IMDbSQLite;
import edu.rit.index.Index;
import edu.rit.index.Index1D;
import edu.rit.index.tree.NewRangeTree2D;
import edu.rit.index.tree.NewRangeTree3D;

public class QueryEngine {

  private final Movie[] allMovies;
  private final List<Movie> allMoviesLst;
  private final HashMap<IndexKey, Index> indexes;

  public QueryEngine() {
    System.out.print("Retrieving movies from the DB... ");
    allMovies = IMDbSQLite.getAllMovies();
    allMoviesLst = Arrays.asList(allMovies);
    System.out.println("(" + allMovies.length + " rows)\n");
    indexes = new HashMap<>();
  }

  public void buildIndexes() {
    build1DIndexYear();
    build1DIndexLength();
    build2DIndex();
    build3DIndex();
  }

  private void build1DIndexYear() {
    final long tic = System.currentTimeMillis();
    System.out.print("Building index on year... ");
    final Index1D<Movie> movieYearIndex = new Index1D<>(Movie.byYear());
    movieYearIndex.build(allMovies);
    final IndexKey idxKey = new IndexKey(Table.MOVIES, Attribute.YEAR);
    indexes.put(idxKey, movieYearIndex);
    final long toc = System.currentTimeMillis();
    System.out.println("(" + (toc - tic) + " ms)");
  }

  private void build1DIndexLength() {
    final long tic = System.currentTimeMillis();
    System.out.print("Building index on length... ");
    final Index1D<Movie> movieLengthIndex = new Index1D<>(Movie.byLength());
    movieLengthIndex.build(allMovies);
    final IndexKey idxKey = new IndexKey(Table.MOVIES, Attribute.LENGTH);
    indexes.put(idxKey, movieLengthIndex);
    final long toc = System.currentTimeMillis();
    System.out.println("(" + (toc - tic) + " ms)");
  }

  private void build2DIndex() {
    final long tic = System.currentTimeMillis();
    System.out.print("Building index on imdb_rating and imdb_votes... ");
    final NewRangeTree2D<Movie> ratingVotesIndex =
        new NewRangeTree2D<>(allMoviesLst, Movie::getRating, Movie::getVotes, Movie::getId);
    final IndexKey idxKey = new IndexKey(Table.MOVIES, Attribute.RATING, Attribute.VOTES);
    indexes.put(idxKey, ratingVotesIndex);
    final long toc = System.currentTimeMillis();
    System.out.println("(" + (toc - tic) + " ms)");
  }

  private void build3DIndex() {
    final long tic = System.currentTimeMillis();
    System.out.print("Building index on budget, year and imdb_rating... ");
    final NewRangeTree3D<Movie> budgetYearRatingIndex = new NewRangeTree3D<>(allMoviesLst,
        Movie::getBudget, Movie::getYear, Movie::getRating, Movie::getId);
    final IndexKey idxKey =
        new IndexKey(Table.MOVIES, Attribute.BUDGET, Attribute.YEAR, Attribute.RATING);
    indexes.put(idxKey, budgetYearRatingIndex);
    final long toc = System.currentTimeMillis();
    System.out.println("(" + (toc - tic) + " ms)\n");
  }

  public <T> List<T> runQuery(final RangeSearchQuery<T> query) {
    if (query == null)
      throw new IllegalArgumentException();
    System.out.println("\n" + query);
    final IndexKey idxKey = query.getIndexKey();
    final Index index = indexes.get(idxKey);
    List<T> result = new ArrayList<>();
    switch (query.getDimension()) {
      case 1:
        if (index == null) {
          result = runQueryWithoutIndex(query);
        } else {
          System.out.print("Running query using index... ");
          final Reference<T> xReference = query.referenceForX();
          final Index1D<T> index1D = (Index1D<T>) index;
          result = index1D.searchInRange(xReference.getLo(), xReference.getHi());
        }
        break;
      case 2:
        if (index == null) {
          result = runQueryWithoutIndex(query);
        } else {
          System.out.print("Running query using index... ");
          final Reference<T> xReference = query.referenceForX();
          final Reference<T> yReference = query.referenceForY();
          final NewRangeTree2D<T> index2D = (NewRangeTree2D<T>) index;
          result = index2D.searchInRange(xReference.getLo(), xReference.getHi(), yReference.getLo(),
              yReference.getHi());
        }
        break;
      case 3:
        if (index == null) {
          result = runQueryWithoutIndex(query);
        } else {
          System.out.print("Running query using index... ");
          final Reference<T> xReference = query.referenceForX();
          final Reference<T> yReference = query.referenceForY();
          final Reference<T> zReference = query.referenceForZ();
          final NewRangeTree3D<T> index3D = (NewRangeTree3D<T>) index;
          result = index3D.searchInRange(xReference.getLo(), xReference.getHi(), yReference.getLo(),
              yReference.getHi(), zReference.getLo(), zReference.getHi());
        }
        break;
      default:
        result = runQueryWithoutIndex(query);
        break;
    }
    return result;
  }

  public <T> List<T> runQueryWithoutIndex(final RangeSearchQuery<T> query) {
    System.out.print("Running query without using any index... ");
    final List<T> result = new ArrayList<>();
    for (final Movie m : allMovies) {
      final String xAttr = query.xAttribute();
      final String xVal = m.getAttribute(xAttr);
      if (!check(query.getxRange(), xVal))
        continue;
      final String yAttr = query.yAttribute();
      final String yVal = m.getAttribute(yAttr);
      if (!check(query.getyRange(), yVal))
        continue;
      final String zAttr = query.zAttribute();
      final String zVal = m.getAttribute(zAttr);
      if (!check(query.getzRange(), zVal))
        continue;
      result.add((T) m);
    }
    return result;
  }

  private boolean check(final AttributeRange range, final String value) {
    if (range == null)
      return true;
    final Double dValue = Double.valueOf(value);
    return dValue >= range.getMin() && dValue <= range.getMax();
  }

}
