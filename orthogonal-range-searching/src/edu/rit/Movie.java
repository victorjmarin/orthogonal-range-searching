package edu.rit;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class Movie {

  private int id;
  private String title;
  private int year;
  private Integer budget;
  private Integer length;
  private Double rating;
  private Integer votes;
  private Map<String, String> attributes;

  public Movie() {}

  public static Movie withYear(final int year) {
    return new Movie(-1, null, year, null, null, null, null);
  }

  public static Movie withRating(final double rating) {
    return new Movie(-1, null, -1, null, null, rating, null);
  }

  public static Movie withVotes(final int votes) {
    return new Movie(-1, null, -1, null, null, null, votes);
  }

  public static Movie withBudget(final int budget) {
    return new Movie(-1, null, -1, budget, null, null, null);
  }

  public static Movie withLength(final int length) {
    return new Movie(-1, null, -1, null, length, null, null);
  }

  public static Movie withId(final int id) {
    return new Movie(id, null, -1, null, null, null, null);
  }

  public Movie(final int id, final String title, final int year) {
    this(id, title, year, null, null, null, null);
  }

  public Movie(final int id, final String title, final int year, final Integer budget,
      final Integer length, final Double rating, final Integer votes) {
    super();
    this.id = id;
    this.title = title;
    this.year = year;
    this.budget = budget;
    this.length = length;
    this.rating = rating;
    this.votes = votes;
    attributes = new HashMap<>();
    attributes.put("id", String.valueOf(id));
    attributes.put("title", title);
    attributes.put("year", String.valueOf(year));
    attributes.put("budget", String.valueOf(budget));
    attributes.put("length", String.valueOf(length));
    attributes.put("imdb_rating", String.valueOf(rating));
    attributes.put("imdb_votes", String.valueOf(votes));
  }

  public double getId() {
    return id;
  }

  public double getYear() {
    return year;
  }

  public String getTitle() {
    return title;
  }

  public Double getBudget() {
    return new Double(budget);
  }

  public Double getLength() {
    return new Double(length);
  }

  public Double getRating() {
    return rating;
  }

  public Double getVotes() {
    return new Double(votes);
  }

  public Map<String, String> getAttributes() {
    return attributes;
  }

  public String getAttribute(final String attr) {
    if (attr == null)
      return null;
    return attributes.get(attr);
  }

  public static Comparator<Movie> byYear() {
    return (m1, m2) -> new Integer(m1.year).compareTo(m2.year);
  }

  public static Comparator<Movie> byLength() {
    return (m1, m2) -> m1.length.compareTo(m2.length);
  }

  @Override
  public String toString() {
    return attributes.toString();
  }

  public String project(final List<String> project) {
    if (project == null || project.isEmpty() || "*".equals(project.get(0)))
      return toString();
    final List<String> projected = new ArrayList<>();
    for (final String s : project) {
      final String val = attributes.get(s);
      projected.add(s + "=" + val);
    }
    final String result = projected.stream().collect(Collectors.joining(", "));
    return "{" + result + "}";
  }

}
