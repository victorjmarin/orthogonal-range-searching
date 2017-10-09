package edu.rit;

import java.util.List;

import edu.rit.index.Index1D;

public class Search1D {

    public static int DATA_AMOUNT = 1000000;
    public static int YEAR_LO = 1900;
    public static int YEAR_HI = 2017;

    public static void main(final String[] args) {
	final Movie[] data = Data.rndMovies(DATA_AMOUNT, YEAR_LO, YEAR_HI);
	final long t1 = System.currentTimeMillis();
	final Index1D<Movie> yearIndex = new Index1D<>(new Movie(), Movie.byYear());
	yearIndex.build(data);
	final long t2 = System.currentTimeMillis();
	System.out.println(data.length + " elements indexed in " + (t2 - t1) + " ms.");
	final List<Movie> movies = yearIndex.searchInRange(1988, 2000);
	final long t3 = System.currentTimeMillis();
	System.out.println(movies.size() + " matches found in " + (t3 - t2) + " ms.");
    }

}
