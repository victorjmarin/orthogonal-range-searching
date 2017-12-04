package edu.rit.search;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;
import edu.rit.Movie;
import edu.rit.db.IMDbMySQL;
import edu.rit.index.Index1D;

public class Search1D {

  public static int DATA_AMOUNT = 1000000;
  public static int YEAR_LO = 2005;
  public static int YEAR_HI = 2007;

  public static void main(final String[] args) throws SQLException {
    final Movie[] data = IMDbMySQL.getAllMovies();
    final long t1 = System.currentTimeMillis();
    final Index1D<Movie> yearIndex = new Index1D<>(Movie.byYear());
    yearIndex.build(data);
    final long t2 = System.currentTimeMillis();
    System.out.println(data.length + " elements indexed in " + (t2 - t1) + " ms.");
    final List<Movie> movies =
        yearIndex.searchInRange(Movie.withYear(YEAR_LO), Movie.withYear(YEAR_HI));
    final long t3 = System.currentTimeMillis();
    System.out.println(movies.size() + " matches found in " + (t3 - t2) + " ms.");
    final IMDbMySQL db = new IMDbMySQL();
    db.open();
    final long before = System.currentTimeMillis();
    final ResultSet rs =
        db.runQuery("SELECT * FROM movies WHERE year BETWEEN " + YEAR_LO + " AND " + YEAR_HI);
    final long after = System.currentTimeMillis();
    System.out.println(after - before);
  }

}
