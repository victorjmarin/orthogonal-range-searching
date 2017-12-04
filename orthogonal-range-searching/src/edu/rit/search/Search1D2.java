package edu.rit.search;

import java.sql.SQLException;
import java.util.List;
import edu.rit.Movie;
import edu.rit.index.Index1D;

public class Search1D2 {

  public static int DATA_AMOUNT = 1000000;
  public static int YEAR_LO = 2005;
  public static int YEAR_HI = 2016;

  public static void main(final String[] args) throws SQLException {
    final Movie m1 = Movie.withYear(1998);
    final Movie m2 = Movie.withYear(1990);
    final Movie m3 = Movie.withYear(2010);
    final Movie m4 = Movie.withYear(2005);
    final Movie m5 = Movie.withYear(2016);
    final Movie m6 = Movie.withYear(1993);
    final Movie[] data = new Movie[] {m1, m2, m3, m4, m5, m6};

    final Index1D<Movie> yearIndex = new Index1D<>(Movie.byYear());
    yearIndex.build(data);

    final List<Movie> movies = yearIndex.search(Movie.withYear(YEAR_LO), Movie.withYear(YEAR_HI));
    System.out.println(movies);
    System.out.println(movies.size() + " matches found.");
  }


}
