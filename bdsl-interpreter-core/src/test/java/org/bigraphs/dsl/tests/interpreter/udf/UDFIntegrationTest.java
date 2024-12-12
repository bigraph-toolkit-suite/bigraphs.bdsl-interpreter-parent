package org.bigraphs.dsl.tests.interpreter.udf;

import org.bigraphs.dsl.BDSLLib;
import org.bigraphs.dsl.bDSL.BDSLDocument;
import org.bigraphs.dsl.interpreter.BdslStatementInterpreterResult;
import org.bigraphs.dsl.interpreter.expressions.main.MainBlockEvalVisitorImpl;
import org.bigraphs.dsl.interpreter.expressions.main.MainStatementEvalVisitorImpl;
import org.bigraphs.dsl.interpreter.extensions.main.MainBlockVisitableExtension;
import org.bigraphs.dsl.interpreter.extensions.variables.BigraphExpressionVisitableExtension;
import org.bigraphs.dsl.tests.interpreter.BaseUnitTestSupport;
import org.bigraphs.dsl.udf.BDSLUserDefinedConsumer;
import lombok.experimental.ExtensionMethod;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Optional;
import java.util.jar.JarFile;

import static org.junit.jupiter.api.Assertions.assertNotNull;


/**
 * @author Dominik Grzelak
 */
@ExtensionMethod({MainBlockVisitableExtension.class, BigraphExpressionVisitableExtension.class})
public class UDFIntegrationTest extends BaseUnitTestSupport {

    @BeforeAll
    static void beforeAll() {
//        BDSLLib.addToClasspath(new File("/home/dominik/eclipse-projects/bdsl/extlibs/bigraph-test-examples-1.0-SNAPSHOT.jar"));
    }

    @Test
    @DisplayName("Find all UDF archives and classes under a concrete package name")
    void findAll_UDF_01() {
        List<String> locations = new ArrayList<>();
        locations.add("./src/test/resources/udf/");
        locations.add("./src/test/resources/udf/config/");

        BDSLLib bdslLib = BDSLLib.getInstance();
        List<JarFile> allJars = bdslLib.findAllUdfJars(locations);
        bdslLib.addToClasspath(allJars.toArray(new JarFile[0]));

        List<Class> allClasses = bdslLib.findAllClasses("de.tudresden.inf.st.bigraphs.examples.interpreter.udf", allJars);
        List<Class> allClasses2 = bdslLib.findAllClasses("de.tudresden.inf.st.bigraphs.examples.interpreter.udf",
                BDSLUserDefinedConsumer.class,
                allJars);
        System.out.println(allClasses);
        System.out.println(allClasses2);
    }

    @Test
    @DisplayName("Calling a UDF that prints something to the console")
    void test_01() throws Exception {
        List<String> locations = new ArrayList<>();
        locations.add("./src/test/resources/udf/");
        locations.add("./src/test/resources/udf/config/");

        BDSLLib bdslLib = BDSLLib.getInstance();
        List<JarFile> allJars = bdslLib.findAllUdfJars(locations);
        bdslLib.addToClasspath(allJars.toArray(new JarFile[0]));

        BDSLDocument brsModel = parse("sample-scripts/udf/udf_01.bdsl");
        validate(brsModel);

        MainStatementEvalVisitorImpl statementEvalVisitor = new MainStatementEvalVisitorImpl();
        MainBlockEvalVisitorImpl mainEvalVisitor = new MainBlockEvalVisitorImpl(statementEvalVisitor);

        List<BdslStatementInterpreterResult> output = (List<BdslStatementInterpreterResult>) brsModel.getMain().interpret(mainEvalVisitor);
        Iterator<BdslStatementInterpreterResult> iterator = output.iterator();
        while (iterator.hasNext()) {
            BdslStatementInterpreterResult next = iterator.next();
            Optional<Object> call = next.getBdslExecutableStatement().call();
            if (call.isPresent()) {
                assertNotNull(call.get());
            }
        }
//        BDSLUdfImport bdslUdaImport = brsModel.getImportDeclarationsUdf().get(0);
//
//        String namespaceUDF = bdslUdaImport.getImportedNamespace();
//
//        BDSLUserDefinedExtensionLoader el = new BDSLUserDefinedExtensionLoader();
//        BDSLUserDefinedConsumer udfHelloBdsl = el.LoadClass("/home/dominik/eclipse-projects/bdsl/bdsl-algebraic-bigraph-interpreter/bdsl-interpreter-cli/src/test/resources/sample-scripts/udf",
//                namespaceUDF,
//                "UDFHelloBdsl",
//                BDSLUserDefinedConsumer.class);
//        System.out.println(udfHelloBdsl);
//        udfHelloBdsl.accept(null);
    }
}
