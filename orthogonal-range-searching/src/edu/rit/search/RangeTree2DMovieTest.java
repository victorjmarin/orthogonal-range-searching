package edu.rit.search;

import java.util.Arrays;
import java.util.List;
import edu.rit.Movie;
import edu.rit.db.IMDbSQLite;
import edu.rit.index.tree.RangeTree2D2;

public class RangeTree2DMovieTest {

  public static void main(final String[] args) {
    final Movie[] data = IMDbSQLite.getAllMovies();
    final List<Movie> ldata = Arrays.asList(data);
    long t1 = System.currentTimeMillis();
    final RangeTree2D2<Movie> rtree =
        new RangeTree2D2<>(ldata, Movie::getYear, Movie::getId, Movie::getId);
    long t2 = System.currentTimeMillis();
    System.out.println(t2 - t1 + " ms. to build the index.");
    final Movie d1LoRef = Movie.withYear(1900);
    final Movie d1HiRef = Movie.withYear(2016);
    final Movie d2LoRef = Movie.withId(2000000);
    final Movie d2HiRef = Movie.withId(3001049);
    t1 = System.nanoTime();
    final List<Movie> result = rtree.searchInRange2(d1LoRef, d1HiRef, d2LoRef, d2HiRef);
    t2 = System.nanoTime();
    System.out.println((t2 - t1) * 1e-9 + " s. to query the index.");
    System.out.println(result.size());
    t1 = System.nanoTime();
    final int fullScanCount = countInRange(ldata, d1LoRef, d1HiRef, d2LoRef, d2HiRef);
    t2 = System.nanoTime();
    System.out.println((t2 - t1) * 1e-9 + " s. to query full scan.");
    System.out.println(fullScanCount);
  }

  public static int countInRange(final List<Movie> data, final Movie d1LoRef, final Movie d1HiRef,
      final Movie d2LoRef, final Movie d2HiRef) {
    int result = 0;
    for (final Movie m : data) {
      if (m.getYear() >= d1LoRef.getYear() && m.getYear() <= d1HiRef.getYear()
          && m.getId() >= d2LoRef.getId() && m.getId() <= d2HiRef.getId())
        result++;
    }
    return result;
  }
}
