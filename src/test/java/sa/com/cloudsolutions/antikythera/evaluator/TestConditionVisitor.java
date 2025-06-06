package sa.com.cloudsolutions.antikythera.evaluator;

import com.github.javaparser.ast.CompilationUnit;
import com.github.javaparser.ast.body.MethodDeclaration;
import com.github.javaparser.ast.expr.Expression;
import com.github.javaparser.ast.stmt.IfStmt;
import com.github.javaparser.ast.visitor.VoidVisitorAdapter;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import sa.com.cloudsolutions.antikythera.configuration.Settings;
import sa.com.cloudsolutions.antikythera.parser.AbstractCompiler;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

class TestConditionVisitor {

    private CompilationUnit cu;
    private MethodDeclaration md;

    @BeforeAll
    static void setup() throws IOException {
        Settings.loadConfigMap(new File("src/test/resources/generator-field-tests.yml"));
        AbstractCompiler.preProcess();
    }

    @BeforeEach
    void loadCompilationUnitAndMethod() {
        String cls = "sa.com.cloudsolutions.antikythera.evaluator.Conditional";
        cu = AntikytheraRunTime.getCompilationUnit(cls);
        Branching.clear();
    }

    @ParameterizedTest
    @CsvSource({"conditional1, 1","conditional4, 2", "cannotControl, 0"})
    void testBranchingCount(String name, int count) {
        md = cu.findFirst(MethodDeclaration.class, f -> f.getNameAsString().equals(name)).orElseThrow();
        ConditionVisitor visitor = new ConditionVisitor();
        md.accept(visitor, null);
        assertEquals(count, Branching.size(md));
    }

    @Test
    void testCollectConditionsUpToMethod() {
        md = cu.findFirst(MethodDeclaration.class,
                f -> f.getNameAsString().equals("multiVariate")).orElseThrow();

        ConditionVisitor visitor = new ConditionVisitor();
        md.accept(visitor, null);

        md.accept(new VoidVisitorAdapter<Void>() {
                      @Override
                      public void visit(IfStmt n, Void arg) {
                          super.visit(n, arg);
                          List<Expression> conditions = ConditionVisitor.collectConditionsUpToMethod(n);
                          if (n.getCondition().toString().equals("a == 0")) {
                              assertEquals(0, conditions.size());
                          } else {
                              assertEquals(1, conditions.size());
                          }
                      }
                  }, null
        );
    }

    @Test
    void testCollectConditionsUpToMethodAgain() {
        md = cu.findFirst(MethodDeclaration.class,
                f -> f.getNameAsString().equals("multiVariateDeep")).orElseThrow();


        ConditionVisitor visitor = new ConditionVisitor();
        md.accept(visitor, null);

        md.accept(new VoidVisitorAdapter<Void>() {
                      @Override
                      public void visit(IfStmt n, Void arg) {
                          super.visit(n, arg);
                          List<Expression> conditions = ConditionVisitor.collectConditionsUpToMethod(n);
                          if (n.getCondition().toString().equals("a == 0")) {
                              assertEquals(0, conditions.size());
                          } else if (n.getCondition().toString().equals("b == 1")) {
                              assertEquals(2, conditions.size());
                          } else {
                              assertEquals(1, conditions.size());
                          }
                      }
                  }, null
        );
    }

    @Test
    void testGraph() {
        md = cu.findFirst(MethodDeclaration.class,
                f -> f.getNameAsString().equals("multiVariateDeep")).orElseThrow();
        md.accept(new ConditionVisitor(), null);
        List<LineOfCode> lines = new ArrayList<>(Branching.get(md));
        assertEquals(4, lines.size());

        for (LineOfCode line : lines) {
            assertTrue(line.isUntravelled());
        }

        for (int i = 0 ; i < lines.size() ; i++) {
            LineOfCode l = Branching.getHighestPriority(md);
            assertNotNull(l);
            assertTrue(l.isUntravelled());
            l.setPathTaken(LineOfCode.FALSE_PATH);
        }

        for (LineOfCode line : lines) {
            Branching.add(line);
        }

        for (int i = 0 ; i < lines.size() ; i++) {
            LineOfCode l = Branching.getHighestPriority(md);
            assertNotNull(l);
            assertTrue(l.isFalsePath());
            l.setPathTaken(LineOfCode.TRUE_PATH);
        }

        for (LineOfCode line : lines) {
            Branching.add(line);
        }

        for (int i = 0 ; i < lines.size() ; i++) {
            LineOfCode l = Branching.getHighestPriority(md);
            assertNotNull(l);
            assertTrue(l.isTruePath());
            l.setPathTaken(LineOfCode.BOTH_PATHS);
            Branching.add(l);
        }

        for (int i = 0 ; i < lines.size() ; i++) {
            LineOfCode l = Branching.getHighestPriority(md);
            assertNotNull(l);
            assertTrue(l.isFullyTravelled());
        }
    }
}
