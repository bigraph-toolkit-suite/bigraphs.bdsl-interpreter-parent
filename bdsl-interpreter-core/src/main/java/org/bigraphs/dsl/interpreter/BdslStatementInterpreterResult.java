package org.bigraphs.dsl.interpreter;

import org.bigraphs.dsl.bDSL.AbstractMainStatements;
import org.eclipse.xtext.nodemodel.INode;

/**
 * The flag {@link BdslStatementInterpreterResult#isResultAlreadyComputed()} indicates whether
 * the result of {@code bdslPredefinedExecutableStatement} is already computed.
 *
 * @author Dominik Grzelak
 */
public class BdslStatementInterpreterResult<V> {

    private AbstractMainStatements statement;
    private INode iNode;
    private BdslExecutableStatement<V> bdslExecutableStatement;
    private Object evaluatedResult;
    private boolean resultAlreadyComputed;

    public static <V> BdslStatementInterpreterResult<V> create(BdslExecutableStatement<V> bdslExecutableStatement, AbstractMainStatements statement, INode iNode, Object evaluatedResult) {
        return new BdslStatementInterpreterResult<>(bdslExecutableStatement, statement, iNode, evaluatedResult, false);
    }

    public static <V> BdslStatementInterpreterResult<V> create(BdslExecutableStatement<V> bdslExecutableStatement, AbstractMainStatements statement) {
        return new BdslStatementInterpreterResult<>(bdslExecutableStatement, statement, null, null, false);
    }

    public static <V> BdslStatementInterpreterResult<V> create(BdslExecutableStatement<V> bdslExecutableStatement, AbstractMainStatements statement, boolean resultAlreadyComputed) {
        return new BdslStatementInterpreterResult<>(bdslExecutableStatement, statement, null, null, resultAlreadyComputed);
    }

    public static <V> BdslStatementInterpreterResult<V> create(BdslExecutableStatement<V> bdslExecutableStatement) {
        return new BdslStatementInterpreterResult<>(bdslExecutableStatement, null, null, null, false);
    }

    BdslStatementInterpreterResult(BdslExecutableStatement<V> bdslExecutableStatement,
                                   AbstractMainStatements statement,
                                   INode iNode,
                                   Object evaluatedResult,
                                   boolean resultAlreadyComputed) {
        this.bdslExecutableStatement = bdslExecutableStatement;
        this.statement = statement;
        this.iNode = iNode;
        this.evaluatedResult = evaluatedResult;
        this.resultAlreadyComputed = resultAlreadyComputed;
    }

    public BdslExecutableStatement<V> getBdslExecutableStatement() {
        return bdslExecutableStatement;
    }

    public AbstractMainStatements getStatement() {
        return statement;
    }

    public INode getiNode() {
        return iNode;
    }

    public Object getEvaluatedResult() {
        return evaluatedResult;
    }

    public boolean isResultAlreadyComputed() {
        return resultAlreadyComputed;
    }
}

