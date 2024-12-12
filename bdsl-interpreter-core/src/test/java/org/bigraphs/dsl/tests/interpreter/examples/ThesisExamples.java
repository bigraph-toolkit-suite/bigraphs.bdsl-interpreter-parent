package org.bigraphs.dsl.tests.interpreter.examples;

import org.bigraphs.dsl.bDSL.BDSLDocument;
import org.bigraphs.dsl.interpreter.BdslStatementInterpreterResult;
import org.bigraphs.dsl.interpreter.expressions.main.MainBlockEvalVisitorImpl;
import org.bigraphs.dsl.interpreter.expressions.main.MainStatementEvalVisitorImpl;
import org.bigraphs.dsl.interpreter.extensions.main.MainBlockVisitableExtension;
import org.bigraphs.dsl.interpreter.extensions.variables.BigraphExpressionVisitableExtension;
import org.bigraphs.dsl.tests.interpreter.BaseUnitTestSupport;
import lombok.experimental.ExtensionMethod;
import org.junit.jupiter.api.Test;

import java.util.Iterator;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertNotNull;

@ExtensionMethod({BigraphExpressionVisitableExtension.class, MainBlockVisitableExtension.class})
public class ThesisExamples extends BaseUnitTestSupport {

    @Test
    void simulate_01() throws Exception {
        BDSLDocument document = parse("sample-scripts/thesis/running-example.bdsl");
        validate(document);

        MainBlockEvalVisitorImpl mainEvalVisitor = new MainBlockEvalVisitorImpl(
                new MainStatementEvalVisitorImpl()
        );

        List<BdslStatementInterpreterResult> output = (List<BdslStatementInterpreterResult>) document
                .getMain().interpret(mainEvalVisitor);
        Iterator<BdslStatementInterpreterResult> iterator = output.iterator();
        while (iterator.hasNext()) {
            BdslStatementInterpreterResult next = iterator.next();
            Optional<Object> call = next.getBdslExecutableStatement().call();
            if (call.isPresent()) {
                assertNotNull(call.get());
            }
        }
    }

    @Test
    void simulate_02() throws Exception {
        BDSLDocument document = parse("sample-scripts/thesis/minimal-example.bdsl");
        validate(document);

        MainBlockEvalVisitorImpl mainEvalVisitor = new MainBlockEvalVisitorImpl(
                new MainStatementEvalVisitorImpl()
        );

        List<BdslStatementInterpreterResult> output = (List<BdslStatementInterpreterResult>) document
                .getMain().interpret(mainEvalVisitor);
        Iterator<BdslStatementInterpreterResult> iterator = output.iterator();
        while (iterator.hasNext()) {
            BdslStatementInterpreterResult next = iterator.next();
            Optional<Object> call = next.getBdslExecutableStatement().call();
            if (call.isPresent()) {
                assertNotNull(call.get());
            }
        }
    }
}
