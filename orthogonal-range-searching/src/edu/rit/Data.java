package edu.rit;

import java.util.concurrent.ThreadLocalRandom;

public class Data {

  public static Movie[] rndMovies(final int amount, final int lo, final int hi) {
    final Movie[] result = new Movie[amount];
    for (int i = 0; i < amount; i++) {
      final int year = ThreadLocalRandom.current().nextInt(lo, hi + 1);
      result[i] = Movie.withYear(year);
    }
    return result;
  }

}
