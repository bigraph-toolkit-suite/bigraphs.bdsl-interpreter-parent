package org.bigraphs.dsl.tests.interpreter;

import org.bigraphs.framework.core.Bigraph;
import org.bigraphs.framework.core.BigraphFileModelManagement;
import org.bigraphs.framework.core.EcoreBigraph;
import org.bigraphs.framework.core.impl.pure.PureBigraph;
import org.bigraphs.framework.core.impl.signature.DefaultDynamicSignature;
import org.bigraphs.dsl.bDSL.BDSLDocument;
import org.bigraphs.dsl.bDSL.LocalVarDecl;
import org.bigraphs.dsl.interpreter.ParserService;
import org.bigraphs.dsl.interpreter.expressions.variables.PureBigraphExpressionEvalVisitorImpl;
import org.bigraphs.dsl.interpreter.extensions.main.MainBlockVisitableExtension;
import org.bigraphs.dsl.interpreter.extensions.variables.BigraphExpressionVisitableExtension;
import lombok.experimental.ExtensionMethod;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.io.IOException;

import static org.junit.jupiter.api.Assertions.*;

@ExtensionMethod({BigraphExpressionVisitableExtension.class, MainBlockVisitableExtension.class})
public class BigraphCompositionUnitTest extends BaseUnitTestSupport {

    @Test
    void substitution_composition_tests() throws IOException {
        BDSLDocument brsModel = parse("sample-scripts/bigraph-composition/bigraphVarDecl_linkingOperations.bdsl");
        System.out.println(brsModel);
        validate(brsModel);

        DefaultDynamicSignature dynamicSignature = getDefaultDynamicSignature(brsModel);
        LocalVarDecl varDecl3 = getLocalVarDecl(brsModel, 3);
        Bigraph<?> bigraph3 = (Bigraph<?>) varDecl3.interpretStart(new PureBigraphExpressionEvalVisitorImpl(dynamicSignature));
        BigraphFileModelManagement.Store.exportAsInstanceModel((EcoreBigraph) bigraph3, System.out);
        assertEquals(1, bigraph3.getOuterNames().size());
        assertEquals(2, bigraph3.getInnerNames().size());
        assertEquals(0, bigraph3.getRoots().size());
        assertEquals(0, bigraph3.getSites().size());
        assertEquals(0, bigraph3.getNodes().size());
        assertEquals(0, bigraph3.getAllPlaces().size());
        assertEquals(0, bigraph3.getEdges().size());
    }

    @Test
    void closure_composition_test_01() throws Exception {
        ParserService parserService = new ParserService();
        BDSLDocument brsModel = parse("sample-scripts/bigraph-composition/bigraphVarDecl_linkingOperations.bdsl");
        System.out.println(brsModel);
        parserService.validate(brsModel);

        DefaultDynamicSignature dynamicSignature = getDefaultDynamicSignature(brsModel);
        LocalVarDecl varDecl0 = getLocalVarDecl(brsModel, 0);
        Bigraph<?> bigraph0 = (Bigraph<?>) varDecl0.interpretStart(new PureBigraphExpressionEvalVisitorImpl(dynamicSignature));
        BigraphFileModelManagement.Store.exportAsInstanceModel((EcoreBigraph) bigraph0, System.out);
        assertEquals(0, bigraph0.getRoots().size());
        assertEquals(0, bigraph0.getSites().size());
        assertEquals(0, bigraph0.getNodes().size());
        assertEquals(0, bigraph0.getAllPlaces().size());
        assertEquals(0, bigraph0.getOuterNames().size());
        assertEquals(0, bigraph0.getEdges().size());
        assertEquals(3, bigraph0.getInnerNames().size());

        LocalVarDecl varDecl1 = getLocalVarDecl(brsModel, 1);
        Bigraph<?> bigraph1 = (Bigraph<?>) varDecl1.interpretStart(new PureBigraphExpressionEvalVisitorImpl(dynamicSignature));
        BigraphFileModelManagement.Store.exportAsInstanceModel((EcoreBigraph) bigraph1, System.out);
        assertEquals(3, bigraph1.getAllPlaces().size());
        assertEquals(0, bigraph1.getSites().size());
        assertEquals(1, bigraph1.getRoots().size());
        assertEquals(2, bigraph1.getNodes().size());
        assertEquals(0, bigraph1.getOuterNames().size());
        assertEquals(1, bigraph1.getEdges().size());
        assertEquals(0, bigraph1.getInnerNames().size());

        LocalVarDecl varDecl2 = getLocalVarDecl(brsModel, 2);
        Bigraph<?> bigraph2 = (Bigraph<?>) varDecl2.interpretStart(new PureBigraphExpressionEvalVisitorImpl(dynamicSignature));
        BigraphFileModelManagement.Store.exportAsInstanceModel((EcoreBigraph) bigraph2, System.out);
        assertEquals(1, bigraph2.getAllPlaces().size());
        assertEquals(0, bigraph2.getSites().size());
        assertEquals(1, bigraph2.getRoots().size());
        assertEquals(0, bigraph2.getNodes().size());
        assertEquals(0, bigraph2.getOuterNames().size());
        assertEquals(0, bigraph2.getEdges().size());
        assertEquals(4, bigraph2.getInnerNames().size());
    }

    @Test
    void closure_composition_test_02() throws IOException {
        ParserService parserService = new ParserService();
        BDSLDocument bdslDocument = parse("sample-scripts/bigraph-composition/closure_composition_test_02.bdsl");
        System.out.println(bdslDocument);
        parserService.validate(bdslDocument);

        DefaultDynamicSignature dynamicSignature = getDefaultDynamicSignature(bdslDocument);
        assertNotNull(dynamicSignature);
        PureBigraphExpressionEvalVisitorImpl bigraphEvalVisitor = new PureBigraphExpressionEvalVisitorImpl(dynamicSignature);

        LocalVarDecl localVarDecl01 = getLocalVarDecl(bdslDocument, 0);
        Bigraph<?> bigraph01 = (Bigraph<?>) localVarDecl01.interpretStart(bigraphEvalVisitor);
        assertNotNull(bigraph01);
        BigraphFileModelManagement.Store.exportAsInstanceModel((EcoreBigraph) bigraph01, System.out);
        assertEquals(1, bigraph01.getRoots().size());
        assertEquals(2, bigraph01.getSites().size());
        assertEquals(2, bigraph01.getNodes().size());
        assertEquals(5, bigraph01.getAllPlaces().size());
        assertEquals(1, bigraph01.getEdges().size());
        assertEquals(0, bigraph01.getOuterNames().size());
        assertEquals(0, bigraph01.getInnerNames().size());
        assertEquals(2, bigraph01.getPointsFromLink(bigraph01.getEdges().iterator().next()).size());

    }

    @Test
    @DisplayName("Testing the creation of sites using the identity function")
    void bigraphExpression_sites_Test() throws Exception {
        BDSLDocument bdslDoc = parse("sample-scripts/bigraph-composition/bigraphVarDecl_sites.bdsl");

        DefaultDynamicSignature dynamicSignature = getDefaultDynamicSignature(bdslDoc);
        PureBigraphExpressionEvalVisitorImpl bigraphEvalVisitor = new PureBigraphExpressionEvalVisitorImpl(dynamicSignature);
        // Two bigraphs are included
        assertLocalVarDeclCount(bdslDoc, 5);

        // one root
        LocalVarDecl varDecl0 = getLocalVarDecl(bdslDoc, 0);
        assertNotNull(varDecl0);
        assertEquals("big1", varDecl0.getName());
        Bigraph<?> bigraph0 = (Bigraph<?>) varDecl0.interpretStart(bigraphEvalVisitor);
        assertNotNull(bigraph0);
        BigraphFileModelManagement.Store.exportAsInstanceModel((EcoreBigraph) bigraph0, System.out);
        assertEquals(1, bigraph0.getRoots().size());
        assertEquals(5, bigraph0.getSites().size());
//
        // one root
        LocalVarDecl varDecl1 = getLocalVarDecl(bdslDoc, 1);
        assertNotNull(varDecl1);
        assertEquals("big2", varDecl1.getName());
        Bigraph<?> bigraph1 = (Bigraph<?>) varDecl1.interpretStart(bigraphEvalVisitor);
        assertNotNull(bigraph1);
        BigraphFileModelManagement.Store.exportAsInstanceModel((EcoreBigraph) bigraph1, System.out);
        assertEquals(1, bigraph1.getRoots().size());
        assertEquals(5, bigraph1.getSites().size());

        // many roots
        LocalVarDecl varDecl2 = getLocalVarDecl(bdslDoc, 2);
        assertNotNull(varDecl2);
        assertEquals("big3", varDecl2.getName());
        Bigraph<?> bigraph2 = (Bigraph<?>) varDecl2.interpretStart(bigraphEvalVisitor);
        assertNotNull(bigraph2);
        BigraphFileModelManagement.Store.exportAsInstanceModel((EcoreBigraph) bigraph2, System.out);
        assertEquals(5, bigraph2.getRoots().size());
        assertEquals(4, bigraph2.getSites().size());
        assertEquals(2, bigraph2.getNodes().size());

        // "join" elementary bigraph function
        LocalVarDecl varDecl3 = getLocalVarDecl(bdslDoc, 3);
        assertNotNull(varDecl3);
        assertEquals("big4", varDecl3.getName());
        Bigraph<?> bigraph3 = (Bigraph<?>) varDecl3.interpretStart(bigraphEvalVisitor);
        assertNotNull(bigraph3);
        BigraphFileModelManagement.Store.exportAsInstanceModel((EcoreBigraph) bigraph3, System.out);
        assertEquals(1, bigraph3.getRoots().size());
        assertEquals(2, bigraph3.getSites().size());
        assertEquals(0, bigraph3.getNodes().size());
        assertEquals(0, bigraph3.getInnerNames().size());
        assertEquals(0, bigraph3.getOuterNames().size());
        assertEquals(0, bigraph3.getEdges().size());

        LocalVarDecl varDecl4 = getLocalVarDecl(bdslDoc, 4);
        assertNotNull(varDecl4);
        assertEquals("big5", varDecl4.getName());
        Bigraph<?> bigraph4 = (Bigraph<?>) varDecl4.interpretStart(bigraphEvalVisitor);
        assertNotNull(bigraph4);
        BigraphFileModelManagement.Store.exportAsInstanceModel((EcoreBigraph) bigraph4, System.out);
        assertEquals(2, bigraph4.getRoots().size());
        assertEquals(4, bigraph4.getSites().size());
    }

    @Test
    void bigraphExpression_nestingOperation_Test() throws Exception {
        BDSLDocument brsModel = parse("sample-scripts/bigraph-composition/bigraphVarDecl_nestingOperations.bdsl");

        DefaultDynamicSignature dynamicSignature = getDefaultDynamicSignature(brsModel);
        assertLocalVarDeclCount(brsModel, 2);

        // First bigraph
        LocalVarDecl varDecl0 = getLocalVarDecl(brsModel, 0);
        assertNotNull(varDecl0);
        Bigraph<?> bigraph0 = (Bigraph<?>) varDecl0.interpretStart(new PureBigraphExpressionEvalVisitorImpl(dynamicSignature));
        assertNotNull(bigraph0);
        assertEquals(1, bigraph0.getRoots().size());
        assertEquals(3, bigraph0.getNodes().size());
        assertEquals(4, bigraph0.getAllPlaces().size());
        assertEquals(dynamicSignature, bigraph0.getSignature());

        // Second bigraph
        LocalVarDecl varDecl1 = getLocalVarDecl(brsModel, 1);
        assertNotNull(varDecl1);
        Bigraph<?> bigraph1 = (Bigraph<?>) varDecl1.interpretStart(new PureBigraphExpressionEvalVisitorImpl(dynamicSignature));
        assertNotNull(bigraph1);
        assertEquals(1, bigraph1.getRoots().size());
        assertEquals(4, bigraph1.getNodes().size());
        assertEquals(5, bigraph1.getAllPlaces().size());
        assertEquals(dynamicSignature, bigraph1.getSignature());


        BigraphFileModelManagement.Store.exportAsInstanceModel((PureBigraph) bigraph0, System.out);
//        BigraphArtifacts.exportAsInstanceModel((PureBigraph) bigraph1, System.out);
    }

    @Test
    void bigraphExpression_parallelOperation_Test() throws Exception {
//        String s = loadScriptAsString("sample-scripts/bigraph-composition/bigraphVarDecl_parallelOperations.bdsl");
        BDSLDocument brsModel = parse("sample-scripts/bigraph-composition/bigraphVarDecl_parallelOperations.bdsl"); //parseHelper.parse(s);
        DefaultDynamicSignature dynamicSignature = getDefaultDynamicSignature(brsModel);

        assertLocalVarDeclCount(brsModel, 2);

        LocalVarDecl varDecl0 = getLocalVarDecl(brsModel, 0);
        Bigraph<?> bigraph0 = (Bigraph<?>) varDecl0.interpretStart(new PureBigraphExpressionEvalVisitorImpl(dynamicSignature));
        assertNotNull(bigraph0);
        assertEquals(3, bigraph0.getRoots().size());
        assertEquals(3, bigraph0.getNodes().size());
        assertEquals(8, bigraph0.getAllPlaces().size());
        assertEquals(dynamicSignature, bigraph0.getSignature());
        BigraphFileModelManagement.Store.exportAsInstanceModel((PureBigraph) bigraph0, System.out);

        LocalVarDecl varDecl1 = getLocalVarDecl(brsModel, 1);
        Bigraph<?> bigraph1 = (Bigraph<?>) varDecl1.interpretStart(new PureBigraphExpressionEvalVisitorImpl(dynamicSignature));
        assertNotNull(bigraph1);
        assertEquals(1, bigraph1.getRoots().size());
        assertEquals(4, bigraph1.getNodes().size());
        assertEquals(7, bigraph1.getAllPlaces().size());
        assertEquals(dynamicSignature, bigraph1.getSignature());
        BigraphFileModelManagement.Store.exportAsInstanceModel((PureBigraph) bigraph1, System.out);

    }

    /**
     *
     */
    @Test
    void simpleBigraphExpression_Evaluation_Test() throws Exception {
//        String s = loadScriptAsString("sample-scripts/bigraph-composition/bigraphVarDecl.bdsl");
        BDSLDocument brsModel = parse("sample-scripts/bigraph-composition/bigraphVarDecl.bdsl"); //parseHelper.parse(s);
//        valHelper.assertNoErrors(brsModel);
        parser.validate(brsModel);
        DefaultDynamicSignature dynamicSignature = getDefaultDynamicSignature(brsModel);

        assertLocalVarDeclCount(brsModel, 2);

        LocalVarDecl bigExplicitRoot = getLocalVarDecl(brsModel, 0);
        assertTrue(bigExplicitRoot.getControlType() == null && (bigExplicitRoot.getSiteArgs() == null || bigExplicitRoot.getSiteArgs().size() == 0));
        Bigraph<?> bigraph = (Bigraph<?>) bigExplicitRoot.interpretStart(new PureBigraphExpressionEvalVisitorImpl(dynamicSignature));
        PureBigraph pureBigraph = (PureBigraph) bigraph;
        assertNotNull(pureBigraph);
        assertEquals(1, pureBigraph.getRoots().size());
        assertEquals(2, pureBigraph.getSites().size());
        assertEquals(3, pureBigraph.getNodes().size());
        assertEquals(6, pureBigraph.getAllPlaces().size());
        assertEquals(pureBigraph.getSignature(), dynamicSignature);
        BigraphFileModelManagement.Store.exportAsInstanceModel((PureBigraph) bigraph, System.out);

        LocalVarDecl bigNonExplicitRoot = getLocalVarDecl(brsModel, 1);
        assertTrue(bigNonExplicitRoot.getControlType() != null && (bigNonExplicitRoot.getSiteArgs() == null || bigNonExplicitRoot.getSiteArgs().size() == 0));
        Bigraph<?> bigraph2 = (Bigraph<?>) bigNonExplicitRoot.interpretStart(new PureBigraphExpressionEvalVisitorImpl(dynamicSignature));
        assertNotNull(bigraph2);
        assertEquals(1, bigraph2.getRoots().size());
        assertEquals(2, bigraph2.getSites().size());
        assertEquals(4, bigraph2.getNodes().size());
        assertEquals(7, bigraph2.getAllPlaces().size());
        assertEquals(bigraph2.getSignature(), dynamicSignature);
        BigraphFileModelManagement.Store.exportAsInstanceModel((PureBigraph) bigraph2, System.out);
    }

    /**
     * Same simple bigraph expression as the other test cases here, but some part of a bigraph is a reference to
     * another.
     */
    @Test
    void simpleBigraphExpression_Evaluation_With_Reference_Test() throws Exception {
        BDSLDocument brsModel = parse("sample-scripts/bigraph-composition/bigraphVarDecl-with-reference.bdsl"); //parseHelper.parse(s);
        System.out.println(brsModel);
        DefaultDynamicSignature signature1 = getDefaultDynamicSignature(brsModel);

        assertLocalVarDeclCount(brsModel, 2);

        LocalVarDecl bigExplicitRoot = getLocalVarDecl(brsModel, 0);
        assertTrue(bigExplicitRoot.getControlType() == null && (bigExplicitRoot.getSiteArgs() == null || bigExplicitRoot.getSiteArgs().size() == 0));

        Bigraph<?> bigraph = (Bigraph<?>) bigExplicitRoot.interpretStart(new PureBigraphExpressionEvalVisitorImpl(signature1));
        PureBigraph pureBigraph = (PureBigraph) bigraph;
        BigraphFileModelManagement.Store.exportAsInstanceModel((PureBigraph) bigraph, System.out);
        assertNotNull(pureBigraph);
        assertEquals(1, pureBigraph.getRoots().size());
        assertEquals(2, pureBigraph.getSites().size());
        assertEquals(3, pureBigraph.getNodes().size());
        assertEquals(6, pureBigraph.getAllPlaces().size());
        assertEquals(pureBigraph.getSignature(), signature1);
    }

    /**
     * This test evaluates inner bigraph variable declarations of an outer bigraph declaration.
     * The user can refer to them within the correct scope of course.
     */
    @Test
    void BigraphVariableWithLinkNames_Declaration_Test() throws Exception {
        BDSLDocument brsModel = parse("sample-scripts/bigraph-composition/bigraphVarDecl-withLinkNames.bdsl"); //parseHelper.parse(s);
        System.out.println(brsModel);

        DefaultDynamicSignature signature = getDefaultDynamicSignature(brsModel);

        assertLocalVarDeclCount(brsModel, 2);

        LocalVarDecl bigraphDeclaration = getLocalVarDecl(brsModel, 0);
        Bigraph<?> big1 = (Bigraph<?>) bigraphDeclaration.interpretStart(new PureBigraphExpressionEvalVisitorImpl(signature));

        BigraphFileModelManagement.Store.exportAsInstanceModel((PureBigraph) big1, System.out);
//        assertEquals(3, big1.getRoots().size());
//        assertEquals(3, big1.getSites().size());
//        assertEquals(8, big1.getNodes().size());
//        assertEquals(14, big1.getAllPlaces().size());

        LocalVarDecl bigraphDeclaration2 = getLocalVarDecl(brsModel, 1);
        Bigraph<?> big2 = (Bigraph<?>) bigraphDeclaration2.interpretStart(new PureBigraphExpressionEvalVisitorImpl(signature));
        BigraphFileModelManagement.Store.exportAsInstanceModel((PureBigraph) big2, System.out);
//        assertEquals(1, big2.getRoots().size());
//        assertEquals(3, big2.getSites().size());
//        assertEquals(9, big2.getNodes().size());
//        assertEquals(13, big2.getAllPlaces().size());

    }


    /**
     * This test evaluates inner bigraph variable declarations of an outer bigraph declaration.
     * The user can refer to them within the correct scope of course.
     */
    @Test
    void innerBigraphVariable_Declaration_Test() throws Exception {
        BDSLDocument brsModel = parse("sample-scripts/bigraph-composition/bigraphVarDecl-innerVariables.bdsl"); //parseHelper.parse(s);
        System.out.println(brsModel);

        DefaultDynamicSignature signature = getDefaultDynamicSignature(brsModel);
        assertLocalVarDeclCount(brsModel, 2);
//
        LocalVarDecl bigraphDeclaration = getLocalVarDecl(brsModel, 0);
        Bigraph<?> big1 = (Bigraph<?>) bigraphDeclaration.interpretStart(new PureBigraphExpressionEvalVisitorImpl(signature));
//
        BigraphFileModelManagement.Store.exportAsInstanceModel((PureBigraph) big1, System.out);
        assertEquals(3, big1.getRoots().size());
        assertEquals(4, big1.getSites().size());
        assertEquals(13, big1.getNodes().size());
        assertEquals(20, big1.getAllPlaces().size());

        LocalVarDecl bigraphDeclaration2 = getLocalVarDecl(brsModel, 1);
        Bigraph<?> big2 = (Bigraph<?>) bigraphDeclaration2.interpretStart(new PureBigraphExpressionEvalVisitorImpl(signature));
        BigraphFileModelManagement.Store.exportAsInstanceModel((PureBigraph) big2, System.out);
        assertEquals(1, big2.getRoots().size());
        assertEquals(3, big2.getSites().size());
        assertEquals(9, big2.getNodes().size());
        assertEquals(13, big2.getAllPlaces().size());
    }
}
