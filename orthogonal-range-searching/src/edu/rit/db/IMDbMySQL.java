package edu.rit.db;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Properties;
import edu.rit.Movie;

public class IMDbMySQL {

  private Connection conn;
  // private static final String DATABASE = "imdb_data";
  private static final String DATABASE = "imdb";
  private static final String USER = "root";
  private static final String PASSWORD = "ogames";
  // private static final String ID = "idmovies";
  private static final String ID = "id";

  public IMDbMySQL open() {
    try {
      final Properties props = new Properties();
      props.setProperty("user", USER);
      props.setProperty("password", PASSWORD);
      props.setProperty("useSSL", "false");
      conn = DriverManager.getConnection("jdbc:mysql://localhost/" + DATABASE, props);
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
      final IMDbMySQL db = new IMDbMySQL().open();
      ResultSet rs = db.runQuery("SELECT count(*) FROM movies");
      rs.next();
      final int size = rs.getInt(1);
      final Movie[] result = new Movie[size];
      rs = db.runQuery("SELECT " + ID + ", title, year FROM movies");
      int i = 0;
      while (rs.next()) {
        final int id = rs.getInt(1);
        final String title = rs.getString(2);
        final int year = rs.getInt(3);
        result[i++] = new Movie(id, title, year);
      }
      db.conn.close();
      return result;
    } catch (final SQLException e) {
      e.printStackTrace();
    }
    return null;
  }

  public static Movie[] getMovies(final int yearLo, final int yearHi) {
    try {
      final IMDbMySQL db = new IMDbMySQL().open();
      ResultSet rs = db
          .runQuery("SELECT count(*) FROM movies WHERE year BETWEEN " + yearLo + " AND " + yearHi);
      rs.next();
      final int size = rs.getInt(1);
      final Movie[] result = new Movie[size];
      rs = db.runQuery("SELECT " + ID + ", title, year FROM movies WHERE year BETWEEN " + yearLo
          + " AND " + yearHi);
      int i = 0;
      while (rs.next()) {
        final int id = rs.getInt(1);
        final String title = rs.getString(2);
        final int year = rs.getInt(3);
        result[i++] = new Movie(id, title, year);
      }
      db.conn.close();
      return result;
    } catch (final SQLException e) {
      e.printStackTrace();
    }
    return null;
  }

}
