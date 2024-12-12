package org.bigraphs.dsl.interpreter.execution.jobs.writer;

import org.bigraphs.dsl.interpreter.execution.jobs.processor.BaseBdslStatementProcessor;
import org.springframework.batch.item.ItemWriter;

import java.util.List;

/**
 * In BDSL, Spring's batch processing writer is here the actual "executor" for BDSL statements.
 * They are provided by the {@link BaseBdslStatementProcessor}.
 * <p>
 * The actual execution is handled by a concrete implementation of {@link BaseBdslItemWriter}.
 *
 * @author Dominik Grzelak
 * @see BaseBdslItemWriter
 * @see BaseBdslStatementProcessor
 */
public abstract class BaseBdslItemWriter<V extends Object> implements ItemWriter<V> {

    @Override
    public abstract void write(List<? extends V> list) throws Exception;
}
