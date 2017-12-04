package edu.rit.query;

import java.text.DecimalFormat;
import java.util.List;
import java.util.Scanner;
import edu.rit.Movie;
import edu.rit.query.antlr.QueryParser;

public class Main {

  private static final int PEEK = 5;

  public static void main(final String[] args) {

    final QueryEngine qEngine = new QueryEngine();
    qEngine.buildIndexes();

    oneDimensionQuery(qEngine);
    twoDimensionQuery(qEngine);
    threeDimensionQuery(qEngine);

    requestInput(qEngine);
  }

  private static void requestInput(final QueryEngine qEngine) {
    final Scanner sc = new Scanner(System.in);
    final QueryParser parser = new QueryParser();
    for (;;) {
      try {
        System.out.println("Enter a range query:");
        final String str = sc.nextLine();
        final RangeSearchQuery<Movie> query = parser.parse(str);
        runQuery(qEngine, query);
      } catch (final Exception e) {
        continue;
      }
    }
  }

  private static void runQuery(final QueryEngine qEngine, final RangeSearchQuery<Movie> query) {
    final DecimalFormat df = new DecimalFormat("#.00");
    long tic = System.nanoTime();
    final List<Movie> movies = qEngine.runQuery(query);
    long toc = System.nanoTime();
    final double indexTime = ((toc - tic) * 1e-6);
    System.out.print("(" + df.format(indexTime) + " ms) ");
    System.out.println("(" + movies.size() + " matches)");

    tic = System.nanoTime();
    final List<Movie> movies2 = qEngine.runQueryWithoutIndex(query);
    toc = System.nanoTime();
    final double noIndexTime = ((toc - tic) * 1e-6);
    System.out.print("(" + df.format(noIndexTime) + " ms) ");
    System.out.println("(" + movies2.size() + " matches)");

    speedup(indexTime, noIndexTime);

    printTop(movies, PEEK, query.getProject());
  }

  private static void oneDimensionQuery(final QueryEngine qEngine) {
    final ReferenceBuilder<Movie> refBuilder = new MovieReferenceBuilder();
    final RangeSearchQuery<Movie> query =
        new RangeSearchQuery<>(refBuilder).table(Table.MOVIES).xRange(Attribute.YEAR, 2000, 2015);

    long tic = System.currentTimeMillis();
    final List<Movie> movies = qEngine.runQuery(query);
    long toc = System.currentTimeMillis();
    final long indexTime = toc - tic;
    System.out.print("(" + indexTime + " ms) ");
    System.out.println("(" + movies.size() + " matches)");

    tic = System.currentTimeMillis();
    final List<Movie> movies2 = qEngine.runQueryWithoutIndex(query);
    toc = System.currentTimeMillis();
    final long noIndexTime = toc - tic;
    System.out.print("(" + noIndexTime + " ms) ");
    System.out.println("(" + movies2.size() + " matches)");

    speedup(indexTime, noIndexTime);
  }

  private static void twoDimensionQuery(final QueryEngine qEngine) {
    final ReferenceBuilder<Movie> refBuilder = new MovieReferenceBuilder();
    final RangeSearchQuery<Movie> query = new RangeSearchQuery<>(refBuilder).table(Table.MOVIES)
        .xRange(Attribute.RATING, 0, 7).yRange(Attribute.VOTES, 0, 500);

    long tic = System.currentTimeMillis();
    final List<Movie> movies = qEngine.runQuery(query);
    long toc = System.currentTimeMillis();
    final long indexTime = toc - tic;
    System.out.print("(" + indexTime + " ms) ");
    System.out.println("(" + movies.size() + " matches)");


    tic = System.currentTimeMillis();
    final List<Movie> movies2 = qEngine.runQueryWithoutIndex(query);
    toc = System.currentTimeMillis();
    final long noIndexTime = toc - tic;
    System.out.print("(" + noIndexTime + " ms) ");
    System.out.println("(" + movies2.size() + " matches)");


    speedup(indexTime, noIndexTime);
  }

  private static void threeDimensionQuery(final QueryEngine qEngine) {
    final ReferenceBuilder<Movie> refBuilder = new MovieReferenceBuilder();
    final RangeSearchQuery<Movie> query = new RangeSearchQuery<>(refBuilder).table(Table.MOVIES)
        .xRange(Attribute.BUDGET, 30000, 50000).yRange(Attribute.YEAR, 1970, 2005)
        .zRange(Attribute.RATING, 5, 7);

    // SELECT count(*) FROM movies WHERE budget BETWEEN 10000 AND 50000 AND year BETWEEN 1970 AND
    // 2005 AND imdb_rating BETWEEN 5.0 AND 7.0 AND id < 100000

    long tic = System.currentTimeMillis();
    final List<Movie> movies = qEngine.runQuery(query);
    long toc = System.currentTimeMillis();
    final long indexTime = toc - tic;
    System.out.print("(" + indexTime + " ms) ");
    System.out.println("(" + movies.size() + " matches)");


    tic = System.currentTimeMillis();
    final List<Movie> movies2 = qEngine.runQueryWithoutIndex(query);
    toc = System.currentTimeMillis();
    final long noIndexTime = toc - tic;
    System.out.print("(" + noIndexTime + " ms) ");
    System.out.println("(" + movies2.size() + " matches)");


    speedup(indexTime, noIndexTime);
  }

  private static void speedup(final double indexTime, final double noIndexTime) {
    final DecimalFormat df = new DecimalFormat("#.00");
    System.out.println(df.format(noIndexTime / indexTime) + "x speed up\n");
  }

  private static void printTop(final List<Movie> movies, int limit, final List<String> project) {
    if (limit < 0)
      return;
    System.out.println("Printing first " + limit + " results.\n");
    while (limit-- > 0)
      System.out.println(movies.get(limit).project(project));
    System.out.println();
  }

}
