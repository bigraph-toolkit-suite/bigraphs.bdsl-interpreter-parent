package org.bigraphs.dsl.interpreter.extensions.main;

import org.bigraphs.dsl.bDSL.*;
import org.bigraphs.dsl.interpreter.BdslStatementInterpreterResult;
import org.bigraphs.dsl.interpreter.expressions.main.AbstractMainEvalVisitor;
import org.bigraphs.dsl.services.BDSLGrammarAccess;

import java.util.List;

/**
 * Extension methods for the main block of the BDSL grammar; because we cannot / don't want to add a {@code interpret()} method to
 * each BDSL grammar element.
 * <p>
 * Variations of the interpretation logic are given by the passed evaluation visitor.
 * Needs to be added to a visitor as extension method: {@code @ExtensionMethod()}
 * <p>
 * This is a double dispatch mechanism. Only the expression knows which case/method must be called.
 *
 * @author Dominik Grzelak
 */
public class MainBlockVisitableExtension { //TODO rename to methodvisiext/laterexecutable

    public static List<BdslStatementInterpreterResult> interpret(MainElement mainElement, AbstractMainEvalVisitor<List<BdslStatementInterpreterResult>, MainElement> visitor) {
        return (List<BdslStatementInterpreterResult>) visitor.beginVisit(mainElement);
    }

    public static BdslStatementInterpreterResult<Object> interpret(AbstractMainStatements stmts, AbstractMainEvalVisitor<BdslStatementInterpreterResult<Object>, AbstractMainStatements> visitor) {
        if (stmts instanceof PrintLn) {
            return interpret((PrintLn) stmts, visitor);
        }
        if (stmts instanceof ExportMethod) {
            return interpret((ExportMethod) stmts, visitor);
        }
        if(stmts instanceof UDFOperation) {
            return interpret((UDFOperation)stmts, visitor);
        }
        if(stmts instanceof UdfCallExpression) {
            return interpret((UdfCallExpression)stmts, visitor);
        }
        if (stmts instanceof ExecuteBRSMethod) {
            return interpret((ExecuteBRSMethod) stmts, visitor);
        }
        if (stmts instanceof BDSLVariableDeclaration2) { //entry
            return interpret((BDSLVariableDeclaration2) stmts, visitor);
        }
        if (stmts instanceof BDSLReferenceDeclaration) { //entry
            return interpret(((BDSLReferenceDeclaration) stmts), visitor);
        }
        if (stmts instanceof BDSLAssignment) { //sub interpret
            return interpret((BDSLAssignment) stmts, visitor);
        }
        if (stmts instanceof AssignableBigraphExpression) { //sub interpret //change to AssignableBigraphExpressionWithExplicitSig?
            return interpret((AssignableBigraphExpression) stmts, visitor);
        }

        throw new IllegalStateException();
    }

    public static BdslStatementInterpreterResult<Object> interpret(PrintLn stmts, AbstractMainEvalVisitor<BdslStatementInterpreterResult<Object>, AbstractMainStatements> visitor) {
        return visitor.visit(stmts);
    }

    public static BdslStatementInterpreterResult<Object> interpret(UDFOperation stmts, AbstractMainEvalVisitor<BdslStatementInterpreterResult<Object>, AbstractMainStatements> visitor) {
        return visitor.visit(stmts);
    }

    public static BdslStatementInterpreterResult<Object> interpret(UdfCallExpression stmts, AbstractMainEvalVisitor<BdslStatementInterpreterResult<Object>, AbstractMainStatements> visitor) {
        return visitor.visit(stmts);
    }

    public static BdslStatementInterpreterResult<Object> interpret(BDSLReferenceDeclaration stmts, AbstractMainEvalVisitor<BdslStatementInterpreterResult<Object>, AbstractMainStatements> visitor) {
        BdslStatementInterpreterResult<Object> result = visitor.visit(stmts);
        return result;
    }

    public static BdslStatementInterpreterResult<Object> interpret(BDSLVariableDeclaration2 stmts, AbstractMainEvalVisitor<BdslStatementInterpreterResult<Object>, AbstractMainStatements> visitor) {
        BdslStatementInterpreterResult<Object> result = visitor.visit(stmts);
        return result;
    }

    public static BdslStatementInterpreterResult<Object> interpret(BDSLAssignment stmts, AbstractMainEvalVisitor<BdslStatementInterpreterResult<Object>, AbstractMainStatements> visitor) {
        return visitor.visit(stmts);
    }

    public static BdslStatementInterpreterResult<Object> interpret(AssignableBigraphExpression stmts, AbstractMainEvalVisitor<BdslStatementInterpreterResult<Object>, AbstractMainStatements> visitor) {
        return visitor.visit(stmts);
    }

    public static BdslStatementInterpreterResult<Object> interpret(ExportMethod stmts, AbstractMainEvalVisitor<BdslStatementInterpreterResult<Object>, AbstractMainStatements> visitor) {
        return visitor.visit(stmts);
    }

    public static BdslStatementInterpreterResult<Object> interpret(ExecuteBRSMethod stmts, AbstractMainEvalVisitor<BdslStatementInterpreterResult<Object>, AbstractMainStatements> visitor) {
        return visitor.visit(stmts);
    }

}
