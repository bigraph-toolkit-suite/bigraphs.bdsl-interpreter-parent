package org.bigraphs.dsl.cli.configuration.v1;

import org.bigraphs.dsl.bDSL.AbstractMainStatements;
import org.bigraphs.dsl.interpreter.BdslStatementInterpreterResult;
import org.bigraphs.dsl.interpreter.execution.jobs.processor.DefaultBdslStatementProcessor;
import org.bigraphs.dsl.interpreter.expressions.main.MainStatementEvalVisitorImpl;
import org.bigraphs.dsl.interpreter.extensions.main.MainBlockVisitableExtension;
import org.bigraphs.dsl.interpreter.extensions.variables.BigraphExpressionVisitableExtension;
import lombok.experimental.ExtensionMethod;

/**
 * @author Dominik Grzelak
 */
@ExtensionMethod({MainBlockVisitableExtension.class, BigraphExpressionVisitableExtension.class})
public class SimpleSequentialBdslStmtProcessor extends DefaultBdslStatementProcessor<AbstractMainStatements, BdslStatementInterpreterResult<Object>> {

    MainStatementEvalVisitorImpl mainStatementEvalVisitor = new MainStatementEvalVisitorImpl();

    @Override
    public BdslStatementInterpreterResult<Object> process(AbstractMainStatements o) throws Exception {
        BdslStatementInterpreterResult<Object> output = (BdslStatementInterpreterResult<Object>) o.interpret(mainStatementEvalVisitor);
        return output;
    }
}
