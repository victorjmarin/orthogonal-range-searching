package edu.rit;

import java.util.Comparator;
import java.util.HashMap;
import java.util.Map;

import edu.rit.index.Indexable;

public class Movie implements Indexable<Movie> {

    private int year;
    private Map<String, Object> attributes;

    public Movie() {
    }

    public Movie(final Integer year) {
	super();
	this.year = year == null ? Integer.MIN_VALUE : year;
	attributes = new HashMap<>();
	attributes.put("year", year);
    }

    public static Comparator<Movie> byYear() {
	return (m1, m2) -> new Integer(m1.year).compareTo(m2.year);
    }

    @Override
    public Movie buildIntReference(final int value) {
	final Movie result = new Movie();
	result.year = value;
	return result;
    }

    @Override
    public String toString() {
	return String.valueOf(year);
    }

}
