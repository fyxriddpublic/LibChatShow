package com.fyxridd.lib.show.chat;

import org.da.expressionj.expr.parser.EquationParser;
import org.da.expressionj.expr.parser.ParseException;
import org.da.expressionj.model.Equation;
import org.da.expressionj.model.Expression;
import org.da.expressionj.util.ExpressionCombiner;
import org.da.expressionj.util.ExpressionExporter;
import org.da.expressionj.util.ExpressionSimplifier;

public class Test {
    public static void main(String... args) {
        try {
            Equation expr = EquationParser.parse("(a && b)||c");
//            System.out.println(expr.getExpression().evalAsBoolean());

            Equation a = EquationParser.parse("3>2");
            Equation b = EquationParser.parse("false");
            Equation c = EquationParser.parse("false");

            ExpressionCombiner combiner = new ExpressionCombiner();
            combiner.replace(expr, "a", a);
            combiner.replace(expr, "b", b);
            Expression expression = combiner.replace(expr, "c", c);
            System.out.println(expression.evalAsBoolean());

//            ExpressionSimplifier simplifier = new ExpressionSimplifier();
//            System.out.println(simplifier.simplify(expr).eval());
//            ExpressionExporter exporter = new ExpressionExporter();
//            System.out.println(exporter.export(expr));
        } catch (ParseException e) {
            e.printStackTrace();
        }
    }
}
