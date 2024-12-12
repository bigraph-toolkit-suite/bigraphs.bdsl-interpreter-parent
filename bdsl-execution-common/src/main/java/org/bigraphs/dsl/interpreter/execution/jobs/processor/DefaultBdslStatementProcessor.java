package org.bigraphs.dsl.interpreter.execution.jobs.processor;

/**
 * Batch processor that simply executes the received (already interpreted) BDSL statements.
 *
 * @author Dominik Grzelak
 */
public class DefaultBdslStatementProcessor<V extends Object, T extends Object> extends BaseBdslStatementProcessor<V, T> {

    public DefaultBdslStatementProcessor() {
    }

    @Override
    public T process(V v) throws Exception {
        return null;
    }
}
