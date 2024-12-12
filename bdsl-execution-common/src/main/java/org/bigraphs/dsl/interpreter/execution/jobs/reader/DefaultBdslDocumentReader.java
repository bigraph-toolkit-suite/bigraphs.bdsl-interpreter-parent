package org.bigraphs.dsl.interpreter.execution.jobs.reader;

import org.springframework.batch.item.NonTransientResourceException;
import org.springframework.batch.item.ParseException;
import org.springframework.batch.item.UnexpectedInputException;

/**
 * @author Dominik Grzelak
 */
public class DefaultBdslDocumentReader extends BaseBdslItemReader<Object> {

    public DefaultBdslDocumentReader() {

    }

    /**
     * Return {@code null} to indicate the end of the reading process.
     *
     * @return the object or {@code null} if no more elements are available
     * @throws Exception when the read fails
     */
    @Override
    public Object read() throws Exception, UnexpectedInputException, ParseException, NonTransientResourceException {
        return null;
    }

    @Override
    public void prepare() {

    }
}
