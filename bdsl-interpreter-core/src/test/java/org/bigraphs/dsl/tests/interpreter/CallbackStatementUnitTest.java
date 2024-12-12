package org.bigraphs.dsl.tests.interpreter;

import org.bigraphs.dsl.bDSL.BDSLDocument;
import org.bigraphs.dsl.bDSL.BRSVarReference;
import org.bigraphs.dsl.bDSL.ExecuteBRSMethod;
import org.bigraphs.dsl.bDSL.PredicateMatchCallback;
import org.bigraphs.dsl.interpreter.BdslStatementInterpreterResult;
import org.bigraphs.dsl.interpreter.expressions.main.MainBlockEvalVisitorImpl;
import org.bigraphs.dsl.interpreter.expressions.main.MainStatementEvalVisitorImpl;
import org.bigraphs.dsl.interpreter.extensions.main.MainBlockVisitableExtension;
import org.bigraphs.dsl.interpreter.extensions.variables.BigraphExpressionVisitableExtension;
import org.bigraphs.dsl.utils.BDSLUtil;
import lombok.experimental.ExtensionMethod;
import org.junit.jupiter.api.Test;

import java.util.Iterator;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertNotNull;

/**
 * @author Dominik Grzelak
 */
@ExtensionMethod({MainBlockVisitableExtension.class, BigraphExpressionVisitableExtension.class})
public class CallbackStatementUnitTest extends BaseUnitTestSupport {

    @Test
    void brs_predicate_callback_test_01() throws Exception {
        BDSLDocument bdslDoc = parse("sample-scripts/callback/brs-callback-01.bdsl");
        validate(bdslDoc);

        MainStatementEvalVisitorImpl evalVisitor = new MainStatementEvalVisitorImpl();
        MainBlockEvalVisitorImpl mainEvalVisitor = new MainBlockEvalVisitorImpl(evalVisitor);

        ExecuteBRSMethod executeBRSMethod = (ExecuteBRSMethod) bdslDoc.getMain().getBody().getStatements().get(1);
        BRSVarReference brs = executeBRSMethod.getBrs();
        List<PredicateMatchCallback> allPredicateMatchCallbacksFor = BDSLUtil.getAllPredicateMatchCallbacksFor(brs.getValue(), bdslDoc);

//        List<BdslStatementInterpreterResult> results = (List<BdslStatementInterpreterResult>) bdslDoc.getMain().interpret(mainEvalVisitor);
        List<BdslStatementInterpreterResult> output = (List<BdslStatementInterpreterResult>) bdslDoc.getMain().interpret(mainEvalVisitor);
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
