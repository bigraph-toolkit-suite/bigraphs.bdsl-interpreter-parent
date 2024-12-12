package org.bigraphs.dsl.tests.interpreter;

import org.bigraphs.framework.core.Bigraph;
import org.bigraphs.framework.core.BigraphFileModelManagement;
import org.bigraphs.framework.core.EcoreBigraph;
import org.bigraphs.framework.core.impl.signature.DefaultDynamicSignature;
import org.bigraphs.framework.core.reactivesystem.ReactionRule;
import org.bigraphs.framework.core.reactivesystem.ReactiveSystem;
import org.bigraphs.framework.core.reactivesystem.ReactiveSystemPredicate;
import org.bigraphs.dsl.bDSL.*;
import org.bigraphs.dsl.interpreter.BdslStatementInterpreterResult;
import org.bigraphs.dsl.interpreter.expressions.main.MainStatementEvalVisitorImpl;
import org.bigraphs.dsl.interpreter.expressions.variables.*;
import org.bigraphs.dsl.interpreter.extensions.main.MainBlockVisitableExtension;
import org.bigraphs.dsl.interpreter.extensions.variables.BigraphExpressionVisitableExtension;
import org.bigraphs.framework.simulation.modelchecking.predicates.BigraphIsoPredicate;
import org.bigraphs.framework.simulation.modelchecking.predicates.SubBigraphMatchPredicate;
import lombok.experimental.ExtensionMethod;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.io.ByteArrayOutputStream;
import java.io.IOException;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit test to evaluate various bigraph variable declarations that are defined in the first level
 * of the {@link BDSLDocument}.
 * <p>
 * This tests interpret a simple node expression with only one level of nesting.
 * Two bigraphs are defined: one with an explicit root and one without an explicit root definition.
 * Both bigraphs contain the same number of nodes with the same controls.
 *
 * @author Dominik Grzelak
 */
@ExtensionMethod({BigraphExpressionVisitableExtension.class, MainBlockVisitableExtension.class})
public class BigraphDeclarationUnitTest extends BaseUnitTestSupport {

    @Test
    @DisplayName("Connecting nodes via an inner name (1)")
    void create_innername_test_01() throws IOException {
        BDSLDocument bdslDocument = parse("sample-scripts/variables/test_innerConnection_01.bdsl");
        validate(bdslDocument);

        PureBigraphExpressionEvalVisitorImpl bigraphEvalVisitor = new PureBigraphExpressionEvalVisitorImpl();
        LocalVarDecl resultVar = getLocalVarDecl(bdslDocument, 0);
        assertEquals("bigResult", resultVar.getName());
        LocalVarDecl var01 = getLocalVarDecl(bdslDocument, 1);
        assertEquals("big01", var01.getName());
        LocalVarDecl var02 = getLocalVarDecl(bdslDocument, 2);
        assertEquals("big02", var02.getName());

        Bigraph bigraphVar01 = (Bigraph) var01.interpretStart(bigraphEvalVisitor);
        BigraphFileModelManagement.Store.exportAsInstanceModel((EcoreBigraph) bigraphVar01, System.out);
        assertBigraphPlaceGraph(bigraphVar01, 1, 1, 0);
        assertBigraphLinkGraph(bigraphVar01, 0, 1, 0);

        Bigraph bigraphVar02 = (Bigraph) var02.interpretStart(bigraphEvalVisitor);
        BigraphFileModelManagement.Store.exportAsInstanceModel((EcoreBigraph) bigraphVar02, System.out);
        assertBigraphPlaceGraph(bigraphVar02, 1, 2, 2);
        assertBigraphLinkGraph(bigraphVar02, 1, 1, 0);

        Bigraph bigraphResult = (Bigraph) resultVar.interpretStart(bigraphEvalVisitor);
        BigraphFileModelManagement.Store.exportAsInstanceModel((EcoreBigraph) bigraphResult, System.out);
        assertBigraphPlaceGraph(bigraphResult, 1, 2, 2);
        assertBigraphLinkGraph(bigraphResult, 0, 1, 1);
    }

    @Test
    @DisplayName("Connecting nodes via an inner name (2)")
    void create_innername_test_02() throws IOException {
        BDSLDocument bdslDocument = parse("sample-scripts/variables/test_innerConnection_02.bdsl");
        validate(bdslDocument);

        PureBigraphExpressionEvalVisitorImpl bigraphEvalVisitor = new PureBigraphExpressionEvalVisitorImpl();
        LocalVarDecl resultVar = getLocalVarDecl(bdslDocument, 0);
        assertEquals("bigResult", resultVar.getName());
        LocalVarDecl var01 = getLocalVarDecl(bdslDocument, 1);
        assertEquals("big01", var01.getName());
        LocalVarDecl var02 = getLocalVarDecl(bdslDocument, 2);
        assertEquals("big02", var02.getName());

        Bigraph bigraphVar01 = (Bigraph) var01.interpretStart(bigraphEvalVisitor);
        BigraphFileModelManagement.Store.exportAsInstanceModel((EcoreBigraph) bigraphVar01, System.out);
        assertBigraphPlaceGraph(bigraphVar01, 1, 1, 0);
        assertBigraphLinkGraph(bigraphVar01, 0, 2, 0);

        Bigraph bigraphVar02 = (Bigraph) var02.interpretStart(bigraphEvalVisitor);
        BigraphFileModelManagement.Store.exportAsInstanceModel((EcoreBigraph) bigraphVar02, System.out);
        assertBigraphPlaceGraph(bigraphVar02, 1, 2, 2);
        assertBigraphLinkGraph(bigraphVar02, 2, 2, 0);

        Bigraph bigraphResult = (Bigraph) resultVar.interpretStart(bigraphEvalVisitor);
        BigraphFileModelManagement.Store.exportAsInstanceModel((EcoreBigraph) bigraphResult, System.out);
        assertBigraphPlaceGraph(bigraphResult, 1, 2, 2);
        assertBigraphLinkGraph(bigraphResult, 0, 2, 2);
    }

    @Test
    @DisplayName("Connecting nodes via an edge by closing outer names (1)")
    void create_edge_test_01() throws IOException {
        BDSLDocument bdslDocument = parse("sample-scripts/variables/test_edgeConnection_01.bdsl");
        validate(bdslDocument);

        PureBigraphExpressionEvalVisitorImpl bigraphEvalVisitor = new PureBigraphExpressionEvalVisitorImpl();
        LocalVarDecl resultVar = getLocalVarDecl(bdslDocument, 0);
        assertEquals("bigResult", resultVar.getName());
        LocalVarDecl var01 = getLocalVarDecl(bdslDocument, 1);
        assertEquals("big01", var01.getName());
        LocalVarDecl var02 = getLocalVarDecl(bdslDocument, 2);
        assertEquals("big02", var02.getName());

        Bigraph bigraphVar01 = (Bigraph) var01.interpretStart(bigraphEvalVisitor);
        BigraphFileModelManagement.Store.exportAsInstanceModel((EcoreBigraph) bigraphVar01, System.out);
        assertBigraphPlaceGraph(bigraphVar01, 1, 1, 0);
        assertBigraphLinkGraph(bigraphVar01, 0, 2, 0);
        Bigraph bigraphVar02 = (Bigraph) var02.interpretStart(bigraphEvalVisitor);
        BigraphFileModelManagement.Store.exportAsInstanceModel((EcoreBigraph) bigraphVar02, System.out);
        assertBigraphPlaceGraph(bigraphVar02, 1, 2, 2);
        assertBigraphLinkGraph(bigraphVar02, 2, 0, 0);

        Bigraph bigraphResult = (Bigraph) resultVar.interpretStart(bigraphEvalVisitor);
        BigraphFileModelManagement.Store.exportAsInstanceModel((EcoreBigraph) bigraphResult, System.out);
        assertBigraphPlaceGraph(bigraphResult, 1, 2, 2);
        assertBigraphLinkGraph(bigraphResult, 0, 0, 2);

    }

    @Test
    @DisplayName("Connecting nodes via an edge by closing outer names (2)")
    void create_edge_test_02() throws IOException {
        BDSLDocument bdslDocument = parse("sample-scripts/variables/test_edgeConnection_02.bdsl");
        validate(bdslDocument);

        PureBigraphExpressionEvalVisitorImpl bigraphEvalVisitor = new PureBigraphExpressionEvalVisitorImpl();
        LocalVarDecl resultVar = getLocalVarDecl(bdslDocument, 0);
        assertEquals("bigResult", resultVar.getName());
        LocalVarDecl var01 = getLocalVarDecl(bdslDocument, 1);
        assertEquals("big01", var01.getName());
        LocalVarDecl var02 = getLocalVarDecl(bdslDocument, 2);
        assertEquals("big02", var02.getName());

        Bigraph bigraphVar01 = (Bigraph) var01.interpretStart(bigraphEvalVisitor);
        BigraphFileModelManagement.Store.exportAsInstanceModel((EcoreBigraph) bigraphVar01, System.out);
        assertBigraphPlaceGraph(bigraphVar01, 1, 2, 0);
        assertBigraphLinkGraph(bigraphVar01, 0, 2, 0);
        Bigraph bigraphVar02 = (Bigraph) var02.interpretStart(bigraphEvalVisitor);
        BigraphFileModelManagement.Store.exportAsInstanceModel((EcoreBigraph) bigraphVar02, System.out);
        assertBigraphPlaceGraph(bigraphVar02, 2, 2, 2);
        assertBigraphLinkGraph(bigraphVar02, 2, 0, 0);

        Bigraph bigraphResult = (Bigraph) resultVar.interpretStart(bigraphEvalVisitor);
        BigraphFileModelManagement.Store.exportAsInstanceModel((EcoreBigraph) bigraphResult, System.out);
        assertBigraphPlaceGraph(bigraphResult, 1, 2, 2);
        assertBigraphLinkGraph(bigraphResult, 0, 0, 2);
    }

    @Test
    @DisplayName("Connecting nodes via an edge by closing outer names (3)")
    void create_edge_test_03() throws IOException {
        BDSLDocument bdslDocument = parse("sample-scripts/variables/test_edgeConnection_03.bdsl");
        validate(bdslDocument);

        PureBigraphExpressionEvalVisitorImpl bigraphEvalVisitor = new PureBigraphExpressionEvalVisitorImpl();
        LocalVarDecl resultVar = getLocalVarDecl(bdslDocument, 0);
        assertEquals("bigResult", resultVar.getName());
        LocalVarDecl var01 = getLocalVarDecl(bdslDocument, 1);
        assertEquals("big01", var01.getName());
        LocalVarDecl var02 = getLocalVarDecl(bdslDocument, 2);
        assertEquals("big02", var02.getName());

        Bigraph bigraphVar01 = (Bigraph) var01.interpretStart(bigraphEvalVisitor);
        BigraphFileModelManagement.Store.exportAsInstanceModel((EcoreBigraph) bigraphVar01, System.out);
        assertBigraphPlaceGraph(bigraphVar01, 3, 3, 0);
        assertBigraphLinkGraph(bigraphVar01, 0, 2, 0);
        Bigraph bigraphVar02 = (Bigraph) var02.interpretStart(bigraphEvalVisitor);
        BigraphFileModelManagement.Store.exportAsInstanceModel((EcoreBigraph) bigraphVar02, System.out);
        assertBigraphPlaceGraph(bigraphVar02, 3, 3, 3);
        assertBigraphLinkGraph(bigraphVar02, 2, 0, 0);

        Bigraph bigraphResult = (Bigraph) resultVar.interpretStart(bigraphEvalVisitor);
        BigraphFileModelManagement.Store.exportAsInstanceModel((EcoreBigraph) bigraphResult, System.out);
        assertBigraphPlaceGraph(bigraphResult, 3, 3, 3);
        assertBigraphLinkGraph(bigraphResult, 0, 0, 2);
    }

    @Test
    void brs_test_01() {
        BDSLDocument brsModel = parse("sample-scripts/variables/test_brs_01.bdsl");
        validate(brsModel);

        PureReactiveSystemEvalVisitorImpl pureReactiveSystemEvalVisitor = new PureReactiveSystemEvalVisitorImpl();

        BRSDefinition brsVarDecl = getBRSVarDecl(brsModel, 0);
        ReactiveSystem result = (ReactiveSystem) brsVarDecl.interpretStart(pureReactiveSystemEvalVisitor);
        assertNotNull(result);


        BRSDefinition brsVarDecl1 = getBRSVarDecl(brsModel, 1);
        ReactiveSystem result1 = (ReactiveSystem) brsVarDecl1.interpretStart(pureReactiveSystemEvalVisitor);
        assertNotNull(result1);

        assertEquals(result, result1);
    }

    @Test
    void brs_test_02() throws Exception {
        BDSLDocument brsModel = parse("sample-scripts/variables/test_brs_02.bdsl");
        parser.validate(brsModel);

        assertLocalVarDeclCount(brsModel, 1);
        LocalVarDecl varDecl0 = getLocalVarDecl(brsModel, 0);

        LocalRuleDecl rule0 = getLocalRuleDecl(brsModel, 0);
        assertNotNull(rule0);
        assertEquals("testReact1", rule0.getName());
        PureReactionRuleEvalVisitorImpl evalVisitor = new PureReactionRuleEvalVisitorImpl();
        ReactionRule ruleInterpreted = (ReactionRule) rule0.interpretStart(evalVisitor);
        assertNotNull(ruleInterpreted);


        BRSDefinition brsDefinition = (BRSDefinition) ((BDSLVariableDeclaration2) getMainStatement(brsModel, 0)).getVariable();
        assertEquals("example", brsDefinition.getName());
        PureReactiveSystemEvalVisitorImpl pureReactiveSystemEvalVisitor = new PureReactiveSystemEvalVisitorImpl();
        ReactiveSystem reactiveSystem = (ReactiveSystem) brsDefinition.interpretStart(pureReactiveSystemEvalVisitor);
        assertNotNull(reactiveSystem.getAgent());
//        assertEquals(2, reactiveSystem.getAgent());
        assertEquals(1, reactiveSystem.getReactionRules().size());
        assertEquals(1, reactiveSystem.getPredicates().size());
    }

    @Test
    void rule_test_01() {
        BDSLDocument brsModel = parse("sample-scripts/variables/test_rules_01.bdsl");
        validate(brsModel);

        PureReactionRuleEvalVisitorImpl ruleEvalVisitor = new PureReactionRuleEvalVisitorImpl();

        LocalRuleDecl localRuleDecl = getLocalRuleDecl(brsModel, 0);
        ReactionRule rule = (ReactionRule) localRuleDecl.interpretStart(ruleEvalVisitor);
        assertNotNull(rule);

//        LocalRuleDecl localRuleDecl1 = getLocalRuleDecl(brsModel, 1);
//        ReactionRule rule1 = (ReactionRule) localRuleDecl1.interpretStart(ruleEvalVisitor);
//        assertNotNull(rule1);

        LocalRuleDecl localRuleDecl2 = getLocalRuleDecl(brsModel, 2);
        ReactionRule rule2 = (ReactionRule) localRuleDecl2.interpretStart(ruleEvalVisitor);
        assertNotNull(rule2);

        assertEquals(rule, rule2);

//        LocalRuleDecl localRuleDecl3 = getLocalRuleDecl(brsModel, 3);
//        ReactionRule rule3 = (ReactionRule) localRuleDecl3.interpretStart(ruleEvalVisitor);
//        assertNotNull(rule3);


    }

    @Test
    void predicate_test_01() {
        BDSLDocument brsModel = parse("sample-scripts/variables/test_predicates_01.bdsl");
        validate(brsModel);

        PurePredicateEvalVisitorImpl predicateEvalVisitor = new PurePredicateEvalVisitorImpl();

        LocalPredicateDeclaration localPredDecl = getLocalPredDecl(brsModel, 0);
        ReactiveSystemPredicate reactiveSystemPredicates = (ReactiveSystemPredicate) localPredDecl.interpretStart(predicateEvalVisitor);
        assertNotNull(reactiveSystemPredicates);
        assertEquals(SubBigraphMatchPredicate.class, reactiveSystemPredicates.getClass());
        assertEquals(2, reactiveSystemPredicates.getBigraph().getNodes().size());

        assertAll(() -> {
            LocalPredicateDeclaration localPredDecl1 = getLocalPredDecl(brsModel, 1);
            ReactiveSystemPredicate reactiveSystemPredicates1 = (ReactiveSystemPredicate) localPredDecl1.interpretStart(predicateEvalVisitor);
        });

        LocalPredicateDeclaration localPredDecl2 = getLocalPredDecl(brsModel, 2);
        ReactiveSystemPredicate reactiveSystemPredicates2 = (ReactiveSystemPredicate) localPredDecl2.interpretStart(predicateEvalVisitor);
        assertNotNull(reactiveSystemPredicates2);

        assertEquals(reactiveSystemPredicates, reactiveSystemPredicates2);


        LocalPredicateDeclaration localPredDecl3 = getLocalPredDecl(brsModel, 3);
        assertEquals("predVar4", localPredDecl3.getName());
        ReactiveSystemPredicate reactiveSystemPredicates3 = (ReactiveSystemPredicate) localPredDecl3.interpretStart(predicateEvalVisitor);
        assertNotNull(reactiveSystemPredicates3);
        assertEquals(BigraphIsoPredicate.class, reactiveSystemPredicates3.getClass());
        assertEquals(3, reactiveSystemPredicates3.getBigraph().getNodes().size());


    }

    @Test
    void bigraph_definition_tests() throws Exception {
        BDSLDocument brsModel = parse("sample-scripts/variables/bigraph_assignments.bdsl");
        validate(brsModel);

        DefaultDynamicSignature dynamicSignature = getDefaultDynamicSignature(brsModel);
        PureBigraphExpressionEvalVisitorImpl bigraphExprEvalVisitor = new PureBigraphExpressionEvalVisitorImpl(dynamicSignature);
        PureReactionRuleEvalVisitorImpl ruleEvalVisitor = new PureReactionRuleEvalVisitorImpl(bigraphExprEvalVisitor);
        MainStatementEvalVisitorImpl statementEvalVisitor = new MainStatementEvalVisitorImpl();

        LocalVarDecl localVarDecl4 = getLocalVarDecl(brsModel, 4);
        Bigraph<?> bigraph4 = (Bigraph<?>) localVarDecl4.interpretStart(new PureBigraphExpressionEvalVisitorImpl());
        BigraphFileModelManagement.Store.exportAsInstanceModel((EcoreBigraph) bigraph4, System.out);

        LocalVarDecl localVarDecl = getLocalVarDecl(brsModel, 0);
        Bigraph<?> bigraph = (Bigraph<?>) localVarDecl.interpretStart(bigraphExprEvalVisitor);
        BigraphFileModelManagement.Store.exportAsInstanceModel((EcoreBigraph) bigraph, System.out);
//
        // load method assignment
        System.out.println("Load method assignment test ...");
        LocalVarDecl localVarDecl1 = getLocalVarDecl(brsModel, 1);
        Bigraph<?> bigraph1 = (Bigraph<?>) localVarDecl1.interpretStart(bigraphExprEvalVisitor);
        assertNotNull(bigraph1);
        BigraphFileModelManagement.Store.exportAsInstanceModel((EcoreBigraph) bigraph1, System.out);
//
        LocalVarDecl localVarDecl2 = getLocalVarDecl(brsModel, 2);
        System.out.println("Bigraph Reference assignment test for " + localVarDecl2.getName());
        Bigraph<?> bigraph2 = (Bigraph<?>) localVarDecl2.interpretStart(bigraphExprEvalVisitor);
        assertNotNull(bigraph2);
        BigraphFileModelManagement.Store.exportAsInstanceModel((EcoreBigraph) bigraph2, System.out);
//

        LocalRuleDecl localRuleDecl = getLocalRuleDecl(brsModel, 0);
        System.out.println("Rule creation test for " + localRuleDecl.getName());
        assertEquals("testReact1", localRuleDecl.getName());
        ReactionRule reactionRule = (ReactionRule) localRuleDecl.interpretStart(ruleEvalVisitor);
        assertNotNull(reactionRule);
        assertNotNull(reactionRule.getRedex());
        assertNotNull(reactionRule.getReactum());
        BigraphFileModelManagement.Store.exportAsInstanceModel((EcoreBigraph) reactionRule.getRedex(), System.out);
        BigraphFileModelManagement.Store.exportAsInstanceModel((EcoreBigraph) reactionRule.getReactum(), System.out);

        LocalRuleDecl localRuleDecl1 = getLocalRuleDecl(brsModel, 1);
        assertEquals("reactRule2", localRuleDecl1.getName());
        ReactionRule reactionRule1 = (ReactionRule) localRuleDecl1.interpretStart(ruleEvalVisitor);
        assertNotNull(reactionRule1);
//
        AbstractMainStatements mainStatement = getMainStatement(brsModel, 0);
        BdslStatementInterpreterResult<Object> result = (BdslStatementInterpreterResult<Object>) mainStatement.interpret(statementEvalVisitor);
        Bigraph mbigraph0 = (Bigraph) result.getBdslExecutableStatement().call().get();

        AbstractMainStatements mainStatement1 = getMainStatement(brsModel, 1);
        BdslStatementInterpreterResult<Object> result1 = (BdslStatementInterpreterResult<Object>) mainStatement1.interpret(statementEvalVisitor);
        Bigraph mbigraph1 = (Bigraph) result1.getBdslExecutableStatement().call().get();

        ByteArrayOutputStream stream0 = new ByteArrayOutputStream();
        BigraphFileModelManagement.Store.exportAsInstanceModel((EcoreBigraph) mbigraph0, stream0);
        ByteArrayOutputStream stream1 = new ByteArrayOutputStream();
        BigraphFileModelManagement.Store.exportAsInstanceModel((EcoreBigraph) mbigraph1, stream1);
        String o0 = new String(stream0.toByteArray());
        String o1 = new String(stream1.toByteArray());
        System.out.println(o0);
        System.out.println(o1);
        assertNotEquals(o0, o1);

        String m0 = "  <bRoots>\n" +
                "    <bChild xsi:type=\"bigraphMetaModel:a\" name=\"v0\"/>\n" +
                "    <bChild xsi:type=\"bigraphMetaModel:b\" name=\"v1\"/>\n" +
                "  </bRoots>";
        String m1 = "<bRoots>\n" +
                "    <bChild xsi:type=\"bigraphMetaModel:b\" name=\"v0\">\n" +
                "      <bChild xsi:type=\"bigraphMetaModel:BSite\" index=\"1\"/>\n" +
                "    </bChild>\n" +
                "    <bChild xsi:type=\"bigraphMetaModel:a\" name=\"v1\">\n" +
                "      <bChild xsi:type=\"bigraphMetaModel:BSite\"/>\n" +
                "    </bChild>\n" +
                "    <bChild xsi:type=\"bigraphMetaModel:b\" name=\"v2\">\n" +
                "      <bChild xsi:type=\"bigraphMetaModel:BSite\" index=\"2\"/>\n" +
                "    </bChild>\n" +
                "  </bRoots>";
        assertTrue(o0.contains(m0));
//        assertTrue(o1.contains(m1));
//
        AbstractMainStatements mainStatement2 = getMainStatement(brsModel, 2);
        BdslStatementInterpreterResult<Object> result2 = (BdslStatementInterpreterResult<Object>) mainStatement2.interpret(statementEvalVisitor);
        Bigraph mbigraph2 = (Bigraph) result2.getBdslExecutableStatement().call().get();
//        // signature should be overwritten now
        Signature sig2 = getSignature(brsModel, 1);
        assertEquals("Sig2", sig2.getName());
        DefaultDynamicSignature signature1 = (DefaultDynamicSignature) sig2.interpretStart(new SignatureEvalVisitorImpl());
        assertEquals(signature1, mbigraph2.getSignature());
    }

    @Override
    public DefaultDynamicSignature getDefaultDynamicSignature(BDSLDocument brsModel) {
        DefaultDynamicSignature defaultDynamicSignature = super.getDefaultDynamicSignature(brsModel);
        checkBasicSignature(defaultDynamicSignature);
        return defaultDynamicSignature;
    }


}
