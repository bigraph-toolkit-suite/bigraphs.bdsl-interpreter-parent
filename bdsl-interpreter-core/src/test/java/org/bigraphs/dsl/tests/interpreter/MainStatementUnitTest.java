package org.bigraphs.dsl.tests.interpreter;

import org.bigraphs.framework.core.BigraphFileModelManagement;
import org.bigraphs.framework.core.impl.pure.PureBigraph;
import org.bigraphs.dsl.bDSL.*;
import org.bigraphs.dsl.interpreter.BdslStatementInterpreterResult;
import org.bigraphs.dsl.interpreter.expressions.main.MainBlockEvalVisitorImpl;
import org.bigraphs.dsl.interpreter.expressions.main.MainStatementEvalVisitorImpl;
import org.bigraphs.dsl.interpreter.extensions.variables.BigraphExpressionVisitableExtension;
import org.bigraphs.dsl.interpreter.extensions.main.MainBlockVisitableExtension;
import lombok.experimental.ExtensionMethod;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.Iterator;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

/**
 * @author Dominik Grzelak
 */
@ExtensionMethod({MainBlockVisitableExtension.class, BigraphExpressionVisitableExtension.class})
public class MainStatementUnitTest extends BaseUnitTestSupport {

    @Test
    void bigraph_assignment_tests() {
        BDSLDocument brsModel = parse("sample-scripts/main-block/main-assignments.bdsl");
        validate(brsModel);
    }

    @Test
    void executeBRSMethod_test_01() throws Exception {
        BDSLDocument brsModel = parse("sample-scripts/main-block/brs-executeMethod.bdsl");

        MainStatementEvalVisitorImpl evalVisitor = new MainStatementEvalVisitorImpl();

        AbstractMainStatements definition1 = getMainStatement(brsModel, 0);
        assertTrue(definition1 instanceof BDSLVariableDeclaration2);


        AbstractMainStatements method = getMainStatement(brsModel, 1);

        BdslStatementInterpreterResult<Object> result1 = (BdslStatementInterpreterResult<Object>) method.interpret(evalVisitor);
        assertNotNull(result1);
        assertTrue(result1.getBdslExecutableStatement().call().isPresent());
    }

    @Test
    void executeBRSMethod_test_02() {
        BDSLDocument brsModel = parse("sample-scripts/main-block/brs-executeMethod_02.bdsl");

        MainStatementEvalVisitorImpl statementEvalVisitor = new MainStatementEvalVisitorImpl();
        MainBlockEvalVisitorImpl mainEvalVisitor = new MainBlockEvalVisitorImpl(statementEvalVisitor);

        brsModel.getMain().interpret(mainEvalVisitor);
    }

    @Test
    @DisplayName("car pathfinding example")
    void executeBRSMethod_test_03() throws Exception {
        BDSLDocument brsModel = parse("sample-scripts/car/bdsl-pathfinding.bdsl");

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
    }

    @Test
    void load_methods() {
        BDSLDocument brsModel = parse("sample-scripts/main-block/main-load-methods.bdsl");
        validate(brsModel);

        MainElement m = (MainElement) brsModel.getMain();
        MainStatementEvalVisitorImpl mainDeclarationEvalVisitor = new MainStatementEvalVisitorImpl();
//
        AbstractMainStatements mainStatement = getMainStatement(brsModel, 0);
        BdslStatementInterpreterResult<Object> result = (BdslStatementInterpreterResult<Object>) mainStatement.interpret(mainDeclarationEvalVisitor);
        System.out.println(result);
//
        AbstractMainStatements mainStatement1 = getMainStatement(brsModel, 1);
        BdslStatementInterpreterResult<Object> result1 = (BdslStatementInterpreterResult<Object>) mainStatement1.interpret(mainDeclarationEvalVisitor);
        assertAll(() -> {
            PureBigraph o = (PureBigraph) result1.getBdslExecutableStatement().call().get();
            System.out.println(o);
            BigraphFileModelManagement.Store.exportAsInstanceModel(o, System.out);
        });

        //TODO
//        AbstractMainStatements mainStatement2 = getMainStatement(brsModel, 2);
//        BdslStatementInterpreterResult<Object> result2 = (BdslStatementInterpreterResult<Object>) mainStatement2.interpret(mainDeclarationEvalVisitor);
//        System.out.println(result2);
//
//        for (int i = 3; i <= 5; i++) {
//            AbstractMainStatements print = getMainStatement(brsModel, i);
//            print.interpret(mainDeclarationEvalVisitor);
//        }
//
//        AbstractMainStatements mainStatement3 = getMainStatement(brsModel, 2);
//        Optional<Object> result4 = (Optional<Object>) mainStatement3.interpret(mainDeclarationEvalVisitor);
//        System.out.println(result4);
    }

    @Test
    void print_with_modes() throws Exception {
        BDSLDocument brsModel = parse("sample-scripts/main-block/main-println-with-modes.bdsl"); // parseHelper.parse(s);
        MainElement m = (MainElement) brsModel.getMain();

        PrintLn definition = (PrintLn) getMainStatement(brsModel, 0);
        BigraphVarReference reference = (BigraphVarReference) definition.getText();
        assertNotNull(reference);

//        List<BdslStatementInterpreterResult> output = (List<BdslStatementInterpreterResult>) m.interpret(new MainBlockEvalVisitorImpl(new MainStatementEvalVisitorImpl()));
//        assertNotNull(output);
    }

    @Test
    void print_bigraph() throws Exception {
//        String s = loadScriptAsString("sample-scripts/main-block/main-with-bigraphReference.bdsl");
//        System.out.println(s);
        BDSLDocument brsModel = parse("sample-scripts/main-block/main-with-bigraphReference.bdsl"); // parseHelper.parse(s);
        MainElement m = (MainElement) brsModel.getMain();
        PrintLn definition = (PrintLn) getMainStatement(brsModel, 0);
        BigraphVarReference reference = (BigraphVarReference) definition.getText();
//        IScope scope = provider.getScope(m, BDSLPackage.Literals.BIGRAPH_VAR_REFERENCE.getEAllReferences().get(0));
//        IEObjectDescription singleElement = scope.getSingleElement(reference.getValue());
//        System.out.println(singleElement);
        List<BdslStatementInterpreterResult> output = (List<BdslStatementInterpreterResult>) m.interpret(new MainBlockEvalVisitorImpl(new MainStatementEvalVisitorImpl()));
    }

    @Test
    void exportBigraph_Test() throws Exception {
        BDSLDocument brsModel = parse("sample-scripts/main-block/main-export-brs.bdsl");
//        valHelper.validate(brsModel);
        validate(brsModel);
        MainStatementEvalVisitorImpl mainDeclarationEvalVisitor = new MainStatementEvalVisitorImpl();

        ExportMethod exportMethod2 = (ExportMethod) getMainStatement(brsModel, 2);
        BdslStatementInterpreterResult<Object> output2 = (BdslStatementInterpreterResult<Object>) exportMethod2.interpret(mainDeclarationEvalVisitor);
        assertNotNull(output2);
        output2.getBdslExecutableStatement().call();

        ExportMethod exportMethod3 = (ExportMethod) getMainStatement(brsModel, 3);
        BdslStatementInterpreterResult<Object> output3 = (BdslStatementInterpreterResult<Object>) exportMethod3.interpret(mainDeclarationEvalVisitor);
        assertNotNull(output3);
        output3.getBdslExecutableStatement().call();

//        assertThrows(BigraphMethodInterpreterException.class, () -> {
        ExportMethod exportMethod4 = (ExportMethod) getMainStatement(brsModel, 4);
        BdslStatementInterpreterResult<Object> output4 = (BdslStatementInterpreterResult<Object>) exportMethod4.interpret(mainDeclarationEvalVisitor);
        assertNotNull(output4);
        output4.getBdslExecutableStatement().call();
//        });
//
        ExportMethod exportMethod5 = (ExportMethod) getMainStatement(brsModel, 5);
        BdslStatementInterpreterResult<Object> output5 = (BdslStatementInterpreterResult<Object>) exportMethod5.interpret(mainDeclarationEvalVisitor);
        assertNotNull(output5);
        output5.getBdslExecutableStatement().call();
//
//        ExportMethod exportMethod6 = (ExportMethod) getMainStatement(brsModel, 6);
//        Optional<Object> output6 = (Optional<Object>) exportMethod6.interpret(mainDeclarationEvalVisitor);
//        System.out.println(output6);
//
        ExportMethod exportMethod7 = (ExportMethod) getMainStatement(brsModel, 7);
        BdslStatementInterpreterResult<Object> output7 = (BdslStatementInterpreterResult<Object>) exportMethod7.interpret(mainDeclarationEvalVisitor);
        output7.getBdslExecutableStatement().call();
//        assertTrue(true);
    }

    @Test
    @SuppressWarnings("all")
    void parse_SimplePrintStatement() throws Exception {
        BDSLDocument brsModel = parse("sample-scripts/main-block/simple-main.bdsl");

        MainElement m = (MainElement) brsModel.getMain();

        BdslStatementInterpreterResult<Object> r1 = (BdslStatementInterpreterResult<Object>) ((AbstractMainStatements) getMainStatement(brsModel, 0)).interpret(new MainStatementEvalVisitorImpl());

        List<BdslStatementInterpreterResult> output = (List<BdslStatementInterpreterResult>) ((MainElement) m).interpret(new MainBlockEvalVisitorImpl(new MainStatementEvalVisitorImpl()));
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
