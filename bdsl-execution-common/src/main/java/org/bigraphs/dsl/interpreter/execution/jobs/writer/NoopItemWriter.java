package org.bigraphs.dsl.interpreter.execution.jobs.writer;

import java.util.List;

/**
 * A default implementation of the Spring's batch processing writer, i.e., here a BDSL statement executor,
 * that does nothing.
 *
 * @author Dominik Grzelak
 */
public class NoopItemWriter extends BaseBdslItemWriter<Object> {

    public NoopItemWriter() {
    }

    @Override
    public void write(List<?> list) throws Exception {
        System.out.println("writing...");
        System.out.println(list);
    }
}
