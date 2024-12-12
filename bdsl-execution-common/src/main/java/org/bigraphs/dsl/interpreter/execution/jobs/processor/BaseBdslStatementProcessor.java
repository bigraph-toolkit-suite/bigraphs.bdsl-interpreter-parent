package org.bigraphs.dsl.interpreter.execution.jobs.processor;

import org.bigraphs.dsl.interpreter.execution.jobs.reader.BaseBdslItemReader;
import org.springframework.batch.item.ItemProcessor;

/**
 * The BDSL-centric implementation of Spring's batch processing {@link ItemProcessor} interface.
 * Here, it accounts for interpreting BDSL statements (to clarify, not executing them explicitly).
 * The BDSL statements are provided by the {@link BaseBdslItemReader}.
 *
 * @author Dominik Grzelak
 * @see BaseBdslItemReader
 */
public abstract class BaseBdslStatementProcessor<V, T> implements ItemProcessor<V, T> {

    @Override
    public abstract T process(V v) throws Exception;
}
