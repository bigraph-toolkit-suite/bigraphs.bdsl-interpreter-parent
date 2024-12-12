package org.bigraphs.dsl.interpreter.expressions.main;

import org.bigraphs.dsl.BDSLLib;
import org.bigraphs.dsl.bDSL.*;
import org.bigraphs.dsl.interpreter.AbstractBRSModelVisitor;
import org.bigraphs.dsl.interpreter.BdslStatementInterpreterResult;
import org.bigraphs.dsl.interpreter.exceptions.BdslIOException;
import org.bigraphs.dsl.interpreter.exceptions.BigraphMethodInterpreterException;
import org.bigraphs.framework.simulation.modelchecking.ModelCheckingOptions;
import org.eclipse.emf.ecore.EObject;

import java.io.File;
import java.util.Objects;

import static org.bigraphs.framework.simulation.modelchecking.ModelCheckingOptions.exportOpts;
import static org.bigraphs.framework.simulation.modelchecking.ModelCheckingOptions.transitionOpts;


/**
 * Abstract evaluation visitor class for statements that may eventual executed later.
 *
 * @author Dominik Grzelak
 */
public abstract class AbstractMainEvalVisitor<ReturnType, ParamType extends EObject> extends AbstractBRSModelVisitor<ReturnType, ParamType> {

    protected PrintStrategy printStrategyEval;
    protected String basePath = ""; // TODO: should be set from "properties"
    protected ModelCheckingOptions opts;
    protected String filename;
    protected BDSLLib bdslLib = BDSLLib.getInstance();

    public AbstractMainEvalVisitor() {
        super();
        this.printStrategyEval = new PrintStrategy.StdOutPrintStrategy(this);
        this.opts = ModelCheckingOptions.create().and(
                transitionOpts().setMaximumTransitions(50).setMaximumTime(60).allowReducibleClasses(true).create())
                .and(exportOpts().setReactionGraphFile(new File("transition-graph.png")).create());
    }

    public AbstractMainEvalVisitor(String filename) throws BdslIOException {
        this.filename = filename;
        this.printStrategyEval = new PrintStrategy.FilePrintStrategy(filename, this);
    }

    PrintStrategy getPrintStrategy() {
        return this.printStrategyEval;
    }

    public final String getBasePath() {
        return basePath;
    }

    public AbstractMainEvalVisitor<ReturnType, ParamType> withModelCheckingOptions(ModelCheckingOptions opts) {
        this.opts = opts;
        return this;
    }

    @Override
    public abstract ReturnType beginVisit(ParamType dslElement);

    public BdslStatementInterpreterResult<Object> visit(PrintLn printLn) {
        return null;
    }

    public BdslStatementInterpreterResult<Object> visit(UDFOperation udfOperation) {
        return null;
    }

    public BdslStatementInterpreterResult<Object> visit(UdfCallExpression udfCallExpression) {
        return null;
    }

    public BdslStatementInterpreterResult<Object> visit(ExportMethod exportMethod) {
        if (Objects.isNull(exportMethod.getVariable()))
            throw new BigraphMethodInterpreterException("BRS variable must not be null", exportMethod);
        return null;
    }

    public BdslStatementInterpreterResult<Object> visit(ExecuteBRSMethod executeBRSMethod) {
        if (Objects.isNull(executeBRSMethod.getBrs()))
            throw new BigraphMethodInterpreterException("BRS variable must not be null", executeBRSMethod);
        return null;
    }

    public BdslStatementInterpreterResult<Object> visit(AssignableBigraphExpression assignmentExpression) {
        return null;
    }

    public BdslStatementInterpreterResult<Object> visit(BDSLVariableDeclaration2 assignment) {
        return null;
    }

    public BdslStatementInterpreterResult<Object> visit(BDSLReferenceDeclaration assignment) {
        return null;
    }

    public BdslStatementInterpreterResult<Object> visit(BDSLAssignment assignment) {
        return null;
    }

}
