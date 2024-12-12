package org.bigraphs.dsl.tests.interpreter;

import com.google.common.io.Resources;
import com.google.inject.Inject;
import org.bigraphs.framework.core.Bigraph;
import org.bigraphs.framework.core.ControlStatus;
import org.bigraphs.framework.core.impl.signature.DefaultDynamicSignature;
import org.bigraphs.dsl.bDSL.*;
import org.bigraphs.dsl.interpreter.InterpreterServiceManager;
import org.bigraphs.dsl.interpreter.ParserService;
import org.bigraphs.dsl.interpreter.expressions.variables.SignatureEvalVisitorImpl;
import org.bigraphs.dsl.interpreter.extensions.variables.BigraphExpressionVisitableExtension;
import org.bigraphs.dsl.scoping.BDSLImportedNamespaceAwareLocalScopeProvider;
import lombok.experimental.ExtensionMethod;
import org.eclipse.emf.common.util.Diagnostic;
import org.eclipse.xtext.testing.extensions.InjectionExtension;
import org.junit.jupiter.api.extension.ExtendWith;

import java.io.File;
import java.io.FileNotFoundException;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

/**
 * @author Dominik Grzelak
 */
//@InjectWith(BDSLInjectorProvider.class)
//@RunWith(XtextRunner.class)
@ExtendWith(InjectionExtension.class)
@ExtensionMethod({BigraphExpressionVisitableExtension.class})
public abstract class BaseUnitTestSupport {
    @Inject
    BDSLImportedNamespaceAwareLocalScopeProvider provider;

    ParserService parser = InterpreterServiceManager.parser();

    String loadScriptAsString(String filename) throws URISyntaxException, FileNotFoundException {
        URL url = Resources.getResource(filename);
        return new java.util.Scanner(new File(url.toURI()), "UTF-8").useDelimiter("\\Z").next();
    }

    //TODO: put this in a service class too
    public BDSLDocument parse(String filename) {
        return parser.parse("src/test/resources/" + filename);
    }

    public BDSLDocument parse(String filename, String[] includes) {
        String[] incs2 = Arrays.stream(includes).map(f -> "src/test/resources/" + f).toArray(String[]::new);
        return parser.parse("src/test/resources/" + filename, incs2);
    }

    public BDSLDocument parse(String filename, boolean resolveAll) {
        return parser.parse("src/test/resources/" + filename, new String[0], resolveAll);
    }

    public Diagnostic validate(BDSLDocument brsModel) {
        return parser.validate(brsModel);
    }

    LocalVarDecl getLocalVarDecl(BDSLDocument brsModel, int index) {
        List<LocalVarDecl> collect = brsModel.getStatements().stream()
                .filter(x -> x instanceof BDSLVariableDeclaration2)
                .map(x -> ((BDSLVariableDeclaration2) x))
                .filter(x -> x.getVariable() instanceof LocalVarDecl)
                .map(x -> ((LocalVarDecl) x.getVariable()))
                .collect(Collectors.toList());
        LocalVarDecl bdslVariableDeclaration = collect.get(index);
        assert bdslVariableDeclaration != null;
        return bdslVariableDeclaration;
    }

    BRSDefinition getBRSVarDecl(BDSLDocument brsModel, int index) {
        List<BRSDefinition> collect = brsModel.getStatements().stream()
                .filter(x -> x instanceof BDSLVariableDeclaration2)
                .map(x -> ((BDSLVariableDeclaration2) x))
                .filter(x -> x.getVariable() instanceof BRSDefinition)
                .map(x -> ((BRSDefinition) x.getVariable()))
                .collect(Collectors.toList());
        BRSDefinition bdslVariableDeclaration = collect.get(index);
        assert bdslVariableDeclaration != null;
        return bdslVariableDeclaration;
    }

    LocalRuleDecl getLocalRuleDecl(BDSLDocument brsModel, int index) {
        List<LocalRuleDecl> collect = brsModel.getStatements().stream()
                .filter(x -> x instanceof BDSLVariableDeclaration2)
                .map(x -> ((BDSLVariableDeclaration2) x))
                .filter(x -> x.getVariable() instanceof LocalRuleDecl)
                .map(x -> ((LocalRuleDecl) x.getVariable()))
                .collect(Collectors.toList());
        LocalRuleDecl bdslVariableDeclaration = collect.get(index);
        assert bdslVariableDeclaration != null;
        return bdslVariableDeclaration;
    }

    LocalPredicateDeclaration getLocalPredDecl(BDSLDocument brsModel, int index) {
        List<LocalPredicateDeclaration> collect = brsModel.getStatements().stream()
                .filter(x -> x instanceof BDSLVariableDeclaration2)
                .map(x -> ((BDSLVariableDeclaration2) x))
                .filter(x -> x.getVariable() instanceof LocalPredicateDeclaration)
                .map(x -> ((LocalPredicateDeclaration) x.getVariable()))
                .collect(Collectors.toList());
        LocalPredicateDeclaration bdslVariableDeclaration = collect.get(index);
        assert bdslVariableDeclaration != null;
        return bdslVariableDeclaration;
    }

    AbstractMainStatements getMainStatement(BDSLDocument brsModel, int index) {
        List<AbstractMainStatements> collect = brsModel.getMain().getBody().getStatements().stream()
                .filter(Objects::nonNull).collect(Collectors.toList());
        return collect.get(index);
    }

    public void assertLocalVarDeclCount(BDSLDocument brsModel, int expected) {
        List<BDSLVariableDeclaration2> collect = brsModel.getStatements().stream()
                .filter(x -> x instanceof BDSLVariableDeclaration2)
                .filter(x -> ((BDSLVariableDeclaration2) x).getVariable() instanceof LocalVarDecl)
                .map(x -> ((BDSLVariableDeclaration2) x))
                .collect(Collectors.toList());
        assertEquals(expected, collect.size());
    }

    public DefaultDynamicSignature getDefaultDynamicSignature(BDSLDocument brsModel) {
        assertNotNull(brsModel.getSignature());
//        assertEquals(1, brsModel.getSignature().size());
        Signature signature = brsModel.getSignature().get(0);
        DefaultDynamicSignature signature1 = (DefaultDynamicSignature) signature.interpretStart(new SignatureEvalVisitorImpl());
        return signature1;
    }

    public Signature getSignature(BDSLDocument brsModel, int ix) {
        assertNotNull(brsModel.getSignature());
        Signature signature = brsModel.getSignature().get(ix);
//        DefaultDynamicSignature signature1 = (DefaultDynamicSignature) signature.interpretStart(new SignatureEvalVisitorImpl());
        return signature;
    }

    public void assertBigraphPlaceGraph(Bigraph bigraphToInspect, int numOfRoots, int numOfSites, int numOfNodes) {
        assertEquals(numOfRoots, bigraphToInspect.getRoots().size());
        assertEquals(numOfNodes, bigraphToInspect.getNodes().size());
        assertEquals(numOfSites, bigraphToInspect.getSites().size());
    }

    public void assertBigraphLinkGraph(Bigraph bigraphToInspect, int numOfOuter, int numOfInner, int numOfEdges) {
        assertEquals(numOfOuter, bigraphToInspect.getOuterNames().size());
        assertEquals(numOfInner, bigraphToInspect.getInnerNames().size());
        assertEquals(numOfEdges, bigraphToInspect.getEdges().size());
    }

    protected void checkBasicSignature(DefaultDynamicSignature signature1) {
        assertNotNull(signature1);
        assertEquals(3, signature1.getControls().size());
        assertEquals(ControlStatus.ACTIVE, signature1.getControlByName("a").getControlKind());
        assertEquals(ControlStatus.PASSIVE, signature1.getControlByName("b").getControlKind());
        assertEquals(ControlStatus.ATOMIC, signature1.getControlByName("c").getControlKind());
    }
}
