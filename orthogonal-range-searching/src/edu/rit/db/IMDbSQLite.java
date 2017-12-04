package edu.rit.db;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import edu.rit.Movie;

public class IMDbSQLite {

  private Connection conn;
  private static final String DATABASE =
      "/Users/goal/Desktop/data-movies-master/data-movies/movies.sqlite3";

  public IMDbSQLite open() {
    try {
      final String url = "jdbc:sqlite:" + DATABASE;
      conn = DriverManager.getConnection(url);
    } catch (final SQLException e) {
      e.printStackTrace();
    }
    return this;
  }

  public void close() {
    try {
      conn.close();
    } catch (final SQLException e) {
      e.printStackTrace();
    }
  }

  public ResultSet runQuery(final String sql) {
    try {
      final Statement stmt = conn.createStatement();
      final ResultSet result = stmt.executeQuery(sql);
      return result;
    } catch (final SQLException e) {
      e.printStackTrace();
    }
    return null;
  }

  public static Movie[] getAllMovies() {
    try {
      final IMDbSQLite db = new IMDbSQLite().open();
      ResultSet rs = db.runQuery("SELECT count(*) FROM movies where id < 100000");
      rs.next();
      final int size = rs.getInt(1);
      final Movie[] result = new Movie[size];
      rs = db
          .runQuery("SELECT id, title, year, budget, length, imdb_rating, imdb_votes FROM movies where id < 100000");
      int i = 0;
      while (rs.next()) {
        final int id = rs.getInt(1);
        final String title = rs.getString(2);
        final int year = rs.getInt(3);
        final Integer budget = rs.getInt(4);
        final Integer length = rs.getInt(5);
        final Double rating = rs.getDouble(6);
        final Integer votes = rs.getInt(7);
        result[i++] = new Movie(id, title, year, budget, length, rating, votes);
      }
      db.conn.close();
      return result;
    } catch (final SQLException e) {
      e.printStackTrace();
    }
    return null;
  }

  // public static Movie[] getMovies(final int yearLo, final int yearHi) {
  // try {
  // final IMDbSQLite db = new IMDbSQLite().open();
  // ResultSet rs = db
  // .runQuery("SELECT count(*) FROM movies WHERE year BETWEEN " + yearLo + " AND " + yearHi);
  // rs.next();
  // final int size = rs.getInt(1);
  // final Movie[] result = new Movie[size];
  // rs = db.runQuery("SELECT " + ID + ", title, year FROM movies WHERE year BETWEEN " + yearLo
  // + " AND " + yearHi);
  // int i = 0;
  // while (rs.next()) {
  // final int id = rs.getInt(1);
  // final String title = rs.getString(2);
  // final int year = rs.getInt(3);
  // result[i++] = new Movie(id, title, year);
  // }
  // db.conn.close();
  // return result;
  // } catch (final SQLException e) {
  // e.printStackTrace();
  // }
  // return null;
  // }

}
