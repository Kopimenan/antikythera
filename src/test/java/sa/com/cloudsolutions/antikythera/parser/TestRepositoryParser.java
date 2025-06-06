package sa.com.cloudsolutions.antikythera.parser;

import com.github.javaparser.StaticJavaParser;
import com.github.javaparser.ast.CompilationUnit;
import net.sf.jsqlparser.expression.Expression;
import net.sf.jsqlparser.expression.Function;
import net.sf.jsqlparser.expression.LongValue;
import net.sf.jsqlparser.expression.operators.conditional.AndExpression;
import net.sf.jsqlparser.expression.operators.relational.EqualsTo;
import net.sf.jsqlparser.expression.operators.relational.ExpressionList;
import net.sf.jsqlparser.expression.operators.relational.IsNullExpression;
import net.sf.jsqlparser.schema.Column;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import sa.com.cloudsolutions.antikythera.configuration.Settings;
import sa.com.cloudsolutions.antikythera.generator.RepositoryQuery;
import sa.com.cloudsolutions.antikythera.generator.TypeWrapper;

import java.io.File;
import java.io.IOException;

import static org.junit.jupiter.api.Assertions.assertEquals;

class TestRepositoryParser {
    private RepositoryParser parser;
    private CompilationUnit cu;

    @BeforeAll
    static void setUpAll() throws IOException {
        Settings.loadConfigMap(new File("src/test/resources/generator.yml"));
    }

    @BeforeEach
    void setUp() throws IOException {
        parser = new RepositoryParser();
        cu = StaticJavaParser.parse("""
                @Table(name = "table_name")
                public class AdmissionClearance implements Serializable {}
                """);
    }

    @Test
    void testFindTableName() {

        assertEquals("table_name", RepositoryParser.findTableName(new TypeWrapper(cu.getType(0))));

        cu = StaticJavaParser.parse("""
                public class AdmissionClearanceTable implements Serializable {}
                """);
        assertEquals("admission_clearance_table",
                RepositoryParser.findTableName(new TypeWrapper(cu.getType(0))));
    }

    @Test
    void convertExpressionToSnakeCaseAndExpression() {
        AndExpression andExpr = new AndExpression(new Column("firstName"), new Column("lastName"));
        Expression result = RepositoryQuery.convertExpressionToSnakeCase(andExpr);
        assertEquals("first_name AND last_name", result.toString());
    }

    @Test
    void convertExpressionToSnakeCaseIsNullExpression() {
        IsNullExpression isNullExpr = new IsNullExpression(new Column("middleName"));
        Expression result = RepositoryQuery.convertExpressionToSnakeCase(isNullExpr);
        assertEquals("middle_name IS NULL", result.toString());
    }

    @Test
    void convertExpressionToSnakeCaseComparisonOperator() {
        EqualsTo equalsExpr = new EqualsTo(new Column("salary"), new LongValue(5000));
        Expression result = RepositoryQuery.convertExpressionToSnakeCase(equalsExpr);
        assertEquals("salary = 5000", result.toString());
    }

    @Test
    void convertExpressionToSnakeCaseFunction() {
        Function functionExpr = new Function();
        functionExpr.setName("SUM");
        functionExpr.setParameters(new ExpressionList(new Column("totalAmount")));
        Expression result = RepositoryQuery.convertExpressionToSnakeCase(functionExpr);
        assertEquals("SUM(total_amount)", result.toString());
    }
}
