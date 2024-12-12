package org.bigraphs.dsl.tests.interpreter.performance;

import com.google.inject.Injector;
import org.bigraphs.framework.core.ControlStatus;
import org.bigraphs.framework.core.datatypes.FiniteOrdinal;
import org.bigraphs.framework.core.datatypes.StringTypedName;
import org.bigraphs.framework.core.impl.signature.DefaultDynamicControl;
import org.bigraphs.framework.core.impl.signature.DefaultDynamicSignature;
import org.bigraphs.framework.core.impl.signature.DynamicSignatureBuilder;
import org.bigraphs.dsl.BDSLStandaloneSetup;
import org.bigraphs.dsl.bDSL.BDSLDocument;
import org.bigraphs.dsl.tests.interpreter.BaseUnitTestSupport;
import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.xtext.resource.XtextResource;
import org.eclipse.xtext.resource.XtextResourceSet;
import org.junit.jupiter.api.Test;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashSet;
import java.util.function.Supplier;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import static org.bigraphs.framework.core.factory.BigraphFactory.pureSignatureBuilder;

public class BDSLPerformanceUnitTests extends BaseUnitTestSupport {

    @Test
    void numberOf_bigraphVariableDeclarations_increases() throws IOException {
        System.out.println("numberOf_bigraphVariableDeclarations_increases");

        StringBuilder sb = new StringBuilder("testcase,numOfVars,time,numOfNodesBigraph,flatOrDeep").append("\r\n");
//        String testCase = "vb";
//        String testCase = "vr";
        String testCase = "vp";
//        String hierarchyStyle = "deep";
        String hierarchyStyle = "flat";
//        int numOfVars = 1;
        int numOfVars = 100;
//        int[] numOfNodesBigraph = new int[]{10};
        int[] numOfNodesBigraph = new int[]{25, 100, 500};
        for (int i = 0; i < numOfVars; i++) {
            for (int j = 0; j < numOfNodesBigraph.length; j++) {
                long time = testMethod(
                        false,
                        false,
                        true,
                        numOfNodesBigraph[j],
                        0,
                        0,
                        (i+1),
                        hierarchyStyle,
                        "/home/dominik/eclipse-projects/bdsl/bdsl-algebraic-bigraph-interpreter/bdsl-interpreter-core/src/test/resources/sample-scripts/performance/",
                        testCase
                );
                System.out.println("Time: " + time);
                sb.append(testCase).append(",")
                        .append((i+1)).append(",")
                        .append(time).append(",")
                        .append(numOfNodesBigraph[j])
                        .append(",").append(hierarchyStyle).append("\r\n");
            }
        }
        System.out.println(sb.toString());
        FileWriter fileWriter = new FileWriter("src/test/resources/sample-scripts/performance/");
        fileWriter.write(sb.toString(), testCase + "_" + hierarchyStyle + ".csv");
    }

    public long testMethod(boolean withBigraphVars,
                           boolean withRulesVars,
                           boolean withPredVars,
                           int numOfNodesPerBigraph,
                           int numOfBigraphs,
                           int numOfRules,
                           int numOfPreds,
                           String bigraphTypeHierarchy,
                           String baseDumpDir,
                           String testCaseFileName
    ) throws IOException {
//        FileWriter fileWriter = new FileWriter(baseDumpDir);
        String controlName = "A";
        DefaultDynamicSignature sig = createSingletonSignature(controlName, 1);
        String scriptFormat = (provideMainBDSLBody());

        StringBuilder collectVars = new StringBuilder();
        StringBuilder collectPrintLn = new StringBuilder();

        if (withBigraphVars) {
            StringBuilder collectBigraphVars = new StringBuilder();
            StringBuilder collectBigraphPrintLnVars = new StringBuilder();
            Supplier<String> bigSup = createNameSupplier("big");
            for (int i = 0; i < numOfBigraphs; i++) {
                String id = bigSup.get();
                String format = bigraphVarToBDSL(id, "Sig1");
                String bigVarDecl = bigraphTypeHierarchy.equalsIgnoreCase("flat") ?
                        String.format(format, printFlatMergeBigraphControls(controlName, numOfNodesPerBigraph)) :
                        String.format(format, printDeepMergeBigraphControls(controlName, numOfNodesPerBigraph));
                collectBigraphVars.append(bigVarDecl);
                collectBigraphPrintLnVars.append(printBigraphVarToBDSL(id));
            }
            collectVars.append(collectBigraphVars);
            collectPrintLn.append(collectBigraphPrintLnVars);
        }
        if (withRulesVars) {
            StringBuilder collect = new StringBuilder();
            StringBuilder collect2 = new StringBuilder();
            Supplier<String> bigSup = createNameSupplier("ruleVar");
            for (int i = 0; i < numOfRules; i++) {
                String id = bigSup.get();
                String format = ruleVarToBDSL(id, "Sig1");
                String redex = bigraphTypeHierarchy.equalsIgnoreCase("flat") ?
                        printFlatMergeBigraphControls(controlName, numOfNodesPerBigraph) :
                        printDeepMergeBigraphControls(controlName, numOfNodesPerBigraph);
                String bigVarDecl = String.format(format, redex, redex);
                collect.append(bigVarDecl);
                collect2.append(printBigraphVarToBDSL(id));
            }
            collectVars.append(collect);
            collectPrintLn.append(collect2);
        }
        if (withPredVars) {
            StringBuilder collect = new StringBuilder();
            StringBuilder collect2 = new StringBuilder();
            Supplier<String> bigSup = createNameSupplier("predVar");
            for (int i = 0; i < numOfPreds; i++) {
                String id = bigSup.get();
                String format = predVarToBDSL(id, "Sig1");
                String bigVarDecl = bigraphTypeHierarchy.equalsIgnoreCase("flat") ?
                        String.format(format, printFlatMergeBigraphControls(controlName, numOfNodesPerBigraph)) :
                        String.format(format, printDeepMergeBigraphControls(controlName, numOfNodesPerBigraph));
                collect.append(bigVarDecl);
                collect2.append(printBigraphVarToBDSL(id));
            }
            collectVars.append(collect);
            collectPrintLn.append(collect2);
        }

        String format = String.format(scriptFormat,
                sigToBDSL(sig, "Sig1"),
                collectPrintLn.toString(),
                collectVars.toString()
        );
//        System.out.println(format);
//        fileWriter.write(format, testCaseFileName);
        InputStream stream = new ByteArrayInputStream(format.getBytes(StandardCharsets.UTF_8));
        long start = System.nanoTime();
//        BDSLDocument parsed = parse("sample-scripts/performance/" + testCaseFileName + ".bdsl"); //fileWriter.getBaseDir()
        BDSLDocument parsed = this.parse(stream); //fileWriter.getBaseDir()
        long end = System.nanoTime();
        long diff = (end - start) / 1000000;
//        System.out.println(parsed);
//        System.out.println(parsed.getStatements());
//        System.out.println(parsed.getMain());
        return diff;
    }

    public BDSLDocument parse(InputStream inputStream) throws IOException {
        Injector injector = new BDSLStandaloneSetup().createInjectorAndDoEMFRegistration();
        XtextResourceSet resourceSet = injector.getInstance(XtextResourceSet.class);
        resourceSet.addLoadOption(XtextResource.OPTION_RESOLVE_ALL, Boolean.TRUE);
        Resource resource = resourceSet.createResource(URI.createURI("*.bdsl"));
        resource.load(inputStream, Collections.EMPTY_MAP);
        BDSLDocument model = (BDSLDocument) resource.getContents().get(0);
        assert resource.getErrors().size() == 0;
        return model;
    }

    // "String.format"-formatted string to insert other statements.
    static String provideMainBDSLBody() {
        String signatures = "%s\n";
        String main = "main = {\n" +
                "%s\n" +
                "}\n";
        String variables = "%s\n";
        return signatures + main + variables;
    }

    static String sigToBDSL(DefaultDynamicSignature signature, String name) {
        StringBuilder sb = new StringBuilder("signature " + name + "{\n");
        for (DefaultDynamicControl each : signature.getControls()) {
//            active ctrl a arity 1
            sb.append(each.getControlKind().name().toLowerCase()).append(" ctrl ")
                    .append(each.getNamedType().stringValue()).append(" arity ")
                    .append(each.getArity().getValue()).append("\n");
        }
        sb.append("}\n");
        return sb.toString();
    }

    // val name(signame) = { %s }
    static String bigraphVarToBDSL(String name, String sigName) {
        return "val " + name + "(" + sigName + ") = {\n%s\n}\n";
    }

    static String ruleVarToBDSL(String name, String sigName) {
        return "react " + name + "(" + sigName + ") = {\n%s\n}, {\n%s\n}\n";
    }

    static String predVarToBDSL(String name, String sigName) {
        return "pred " + name + "(" + sigName + "):iso = {\n%s\n}\n";
    }

    // println($big1)
    static String printBigraphVarToBDSL(String bigName) {
        return "println($" + bigName + ")\n";
    }

    static Supplier<String> createNameSupplier(final String prefix) {
        return new Supplier<>() {
            private int id = 0;

            @Override
            public String get() {
                return prefix + id++;
            }
        };
    }

    static String printFlatMergeBigraphControls(String control, int count) {
        if (count <= 1) {
            return "(" + control + ")";
        }
        String collect = IntStream.range(0, count)
                .mapToObj(value -> control).collect(Collectors.joining(" | "));
        return "(" + collect + ")";
    }

    static String printDeepMergeBigraphControls(String control, int count) {
        if (count <= 1) {
            return "(" + control + ")";
        }
        String paranthesisEnd = "";
        StringBuilder sb = new StringBuilder("(");
        for (int i = 0; i < count; i++) {
            sb.append(control).append(" | (");
            paranthesisEnd += ")";
        }
        sb.deleteCharAt(sb.length() - 1);
        sb.append(control);
        sb.append(paranthesisEnd);
        return sb.toString();
    }

    private DefaultDynamicSignature createRandomSignature(int n, float probOfPositiveArity) {
        DynamicSignatureBuilder signatureBuilder = pureSignatureBuilder();

        char[] chars = IntStream.rangeClosed('A', 'Z')
                .mapToObj(c -> "" + (char) c).collect(Collectors.joining()).toCharArray();

        int floorNum = (int) Math.ceil(n * probOfPositiveArity);
        for (int i = 0; i < floorNum; i++) {
            signatureBuilder = signatureBuilder.newControl().identifier(StringTypedName.of(String.valueOf(chars[i]))).arity(FiniteOrdinal.ofInteger(1)).assign();
        }
        for (int i = floorNum; i < n; i++) {
            signatureBuilder = (DynamicSignatureBuilder) signatureBuilder.newControl().identifier(StringTypedName.of(String.valueOf(chars[i]))).arity(FiniteOrdinal.ofInteger(0)).assign();
        }
        DefaultDynamicSignature s = signatureBuilder.create();
        ArrayList<DefaultDynamicControl> cs = new ArrayList<>(s.getControls());
        Collections.shuffle(cs);
        return signatureBuilder.createWith(new LinkedHashSet<>(cs));
    }

    private DefaultDynamicSignature createSingletonSignature(String control, int arity) {
        DynamicSignatureBuilder signatureBuilder = pureSignatureBuilder();
        signatureBuilder = signatureBuilder
                .newControl().identifier(StringTypedName.of(String.valueOf(control)))
                .arity(FiniteOrdinal.ofInteger(arity))
                .status(ControlStatus.ACTIVE)
                .assign();
        return signatureBuilder.create();
    }
}
