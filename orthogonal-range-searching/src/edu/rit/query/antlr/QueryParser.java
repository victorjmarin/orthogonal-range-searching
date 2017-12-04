package edu.rit.query.antlr;

import java.util.ArrayList;
import java.util.List;
import org.antlr.v4.runtime.CharStream;
import org.antlr.v4.runtime.CharStreams;
import org.antlr.v4.runtime.CommonTokenStream;
import org.antlr.v4.runtime.Lexer;
import org.antlr.v4.runtime.Parser;
import org.antlr.v4.runtime.tree.ParseTree;
import edu.rit.Movie;
import edu.rit.query.AttributeRange;
import edu.rit.query.MovieReferenceBuilder;
import edu.rit.query.RangeSearchQuery;
import edu.rit.query.antlr.SQLiteParser.Any_nameContext;
import edu.rit.query.antlr.SQLiteParser.ExprContext;
import edu.rit.query.antlr.SQLiteParser.Literal_valueContext;
import edu.rit.query.antlr.SQLiteParser.Result_columnContext;

public class QueryParser extends SQLiteBaseVisitor<RangeSearchQuery<Movie>> {

  @Override
  protected RangeSearchQuery<Movie> aggregateResult(final RangeSearchQuery<Movie> aggregate,
      final RangeSearchQuery<Movie> nextResult) {
    return nextResult != null ? nextResult : aggregate;
  }

  public RangeSearchQuery<Movie> parse(final String query) {
    final CharStream input = CharStreams.fromString(query);
    final Lexer lexer = new SQLiteLexer(input);
    final CommonTokenStream tokens = new CommonTokenStream(lexer);
    final Parser parser = new SQLiteParser(tokens);
    final ParseTree tree = ((SQLiteParser) parser).parse();
    final RangeSearchQuery<Movie> result = visit(tree);
    return result;
  }

  @Override
  public RangeSearchQuery<Movie> visitSelect_core(final SQLiteParser.Select_coreContext ctx) {
    final String table = ctx.table_or_subquery(0).getText();
    if (!"movies".equals(table))
      throw new IllegalArgumentException("Table " + table + " is not supported.");
    final RangeSearchQuery<Movie> result =
        new RangeSearchQuery<>(new MovieReferenceBuilder()).table(table);
    final ExprContext expr = ctx.expr(0);
    final List<AttributeRange> attrRange = new ArrayList<>();
    exprr(expr, attrRange);
    switch (attrRange.size()) {
      case 3:
        result.setZRange(attrRange.get(2));
      case 2:
        result.setYRange(attrRange.get(1));
      case 1:
        result.setXRange(attrRange.get(0));
        break;
      default:
        throw new IllegalArgumentException("Only up to 3 dimensions supported.");
    }
    for (final Result_columnContext rc : ctx.result_column()) {
      final String columnName = rc.getText();
      result.projectColumn(columnName);
    }
    return result;
  }

  private String attr;
  private Double lo;
  private Double hi;
  private boolean isLo = true;

  private void exprr(final ParseTree expr, final List<AttributeRange> attrRange) {
    for (int i = 0; i < expr.getChildCount(); i++) {
      final ParseTree pt = expr.getChild(i);
      if (pt instanceof Any_nameContext) {
        attr = pt.getText();
      } else if (pt instanceof Literal_valueContext) {
        if (isLo) {
          isLo = false;
          lo = Double.valueOf(pt.getText());
        } else {
          isLo = true;
          hi = Double.valueOf(pt.getText());
          attrRange.add(new AttributeRange(attr, lo, hi));
        }
      } else {
        exprr(pt, attrRange);
      }
    }
  }

}
