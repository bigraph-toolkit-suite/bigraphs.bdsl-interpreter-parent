package org.bigraphs.dsl.tests.interpreter;

import org.bigraphs.framework.core.Bigraph;
import org.bigraphs.framework.core.BigraphFileModelManagement;
import org.bigraphs.framework.core.EcoreBigraph;
import org.bigraphs.framework.core.impl.signature.DefaultDynamicSignature;
import org.bigraphs.framework.core.reactivesystem.ReactionRule;
import org.bigraphs.framework.core.reactivesystem.ReactiveSystemPredicate;
import org.bigraphs.dsl.bDSL.*;
import org.bigraphs.dsl.interpreter.BdslStatementInterpreterResult;
import org.bigraphs.dsl.interpreter.expressions.main.MainBlockEvalVisitorImpl;
import org.bigraphs.dsl.interpreter.expressions.main.MainStatementEvalVisitorImpl;
import org.bigraphs.dsl.interpreter.expressions.variables.PureBigraphExpressionEvalVisitorImpl;
import org.bigraphs.dsl.interpreter.expressions.variables.PurePredicateEvalVisitorImpl;
import org.bigraphs.dsl.interpreter.expressions.variables.PureReactionRuleEvalVisitorImpl;
import org.bigraphs.dsl.interpreter.extensions.main.MainBlockVisitableExtension;
import org.bigraphs.dsl.interpreter.extensions.variables.BigraphExpressionVisitableExtension;
import org.bigraphs.framework.visualization.BigraphGraphvizExporter;
import lombok.experimental.ExtensionMethod;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.io.IOException;
import java.util.Iterator;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

/**
 * @author Dominik Grzelak
 */
@ExtensionMethod({MainBlockVisitableExtension.class, BigraphExpressionVisitableExtension.class})
public class MethodStatementsUnitTest extends BaseUnitTestSupport {

    @Test
    void exportingReactionRule_test_01() throws IOException {
        BDSLDocument bdslDocument = parse("sample-scripts/car/bdsl-pathfinding.bdsl");
        validate(bdslDocument);

        PureBigraphExpressionEvalVisitorImpl evalVisitor0 = new PureBigraphExpressionEvalVisitorImpl();
        LocalVarDecl car = getLocalVarDecl(bdslDocument, 0);
        assertNotNull(car);
        assertEquals("car", car.getName());
        Bigraph carInterpreted = (Bigraph) car.interpretStart(evalVisitor0);
        assertNotNull(carInterpreted);
        BigraphGraphvizExporter.toPNG(carInterpreted, true, new File("src/test/resources/sample-scripts/car/car.png"));

        LocalVarDecl map = getLocalVarDecl(bdslDocument, 1);
        assertNotNull(map);
        assertEquals("map", map.getName());
        Bigraph mapInterpreted = (Bigraph) map.interpretStart(evalVisitor0);
        assertNotNull(mapInterpreted);
        BigraphGraphvizExporter.toPNG(mapInterpreted, true, new File("src/test/resources/sample-scripts/car/map.png"));

        LocalRuleDecl rule0 = getLocalRuleDecl(bdslDocument, 0);
        assertNotNull(rule0);
        assertEquals("moveCar", rule0.getName());
        PureReactionRuleEvalVisitorImpl evalVisitor = new PureReactionRuleEvalVisitorImpl();
        ReactionRule ruleInterpreted = (ReactionRule) rule0.interpretStart(evalVisitor);
        assertNotNull(ruleInterpreted);

        BigraphGraphvizExporter.toPNG(ruleInterpreted.getRedex(), true, new File("src/test/resources/sample-scripts/car/redex.png"));
        BigraphGraphvizExporter.toPNG(ruleInterpreted.getReactum(), true, new File("src/test/resources/sample-scripts/car/reactum.png"));


        LocalPredicateDeclaration predDecl = getLocalPredDecl(bdslDocument, 0);
        assertNotNull(predDecl);
        assertEquals("targetReached", predDecl.getName());
        PurePredicateEvalVisitorImpl evalVisitor1 = new PurePredicateEvalVisitorImpl();
        ReactiveSystemPredicate predInterpreted = (ReactiveSystemPredicate) predDecl.interpretStart(evalVisitor1);
        assertNotNull(predInterpreted);
        BigraphGraphvizExporter.toPNG(predInterpreted.getBigraph(), true, new File("src/test/resources/sample-scripts/car/predicate.png"));
    }

    @Test
    void exportMethod_test_01() throws Exception {
        BDSLDocument bdslDoc = parse("sample-scripts/methods/export_method_01.bdsl");
        validate(bdslDoc);

        MainStatementEvalVisitorImpl statementEvalVisitor = new MainStatementEvalVisitorImpl();
        MainBlockEvalVisitorImpl mainEvalVisitor = new MainBlockEvalVisitorImpl(statementEvalVisitor);

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

    @Test
    void randomBigraph_MainBlock_Assignment_Test() throws Exception {
        BDSLDocument brsModel = parse("sample-scripts/methods/method_randomBigraph_01.bdsl");
        validate(brsModel);

        MainStatementEvalVisitorImpl mainDeclarationEvalVisitor = new MainStatementEvalVisitorImpl();

        AbstractMainStatements mainStatement = getMainStatement(brsModel, 0);
        BdslStatementInterpreterResult<Object> result = (BdslStatementInterpreterResult<Object>) mainStatement.interpret(mainDeclarationEvalVisitor);
        System.out.println(result);
        Bigraph o = (Bigraph) result.getBdslExecutableStatement().call().get();
        assertNotNull(o);
        BigraphFileModelManagement.Store.exportAsInstanceModel((EcoreBigraph) o, System.out);
    }

    @Test
    void randomBigraph_Global_Assignment_Test() throws IOException {
        BDSLDocument brsModel = parse("sample-scripts/methods/method_randomBigraph_01.bdsl");
        validate(brsModel);

        DefaultDynamicSignature defaultDynamicSignature = getDefaultDynamicSignature(brsModel);
        PureBigraphExpressionEvalVisitorImpl pureExpressionEvalVisitor = new PureBigraphExpressionEvalVisitorImpl(defaultDynamicSignature);

        LocalVarDecl localVarDecl0 = getLocalVarDecl(brsModel, 0);
        Bigraph bigraph = (Bigraph) localVarDecl0.interpretStart(pureExpressionEvalVisitor);
        assertNotNull(bigraph);
        BigraphFileModelManagement.Store.exportAsInstanceModel((EcoreBigraph) bigraph, System.out);

    }
}
