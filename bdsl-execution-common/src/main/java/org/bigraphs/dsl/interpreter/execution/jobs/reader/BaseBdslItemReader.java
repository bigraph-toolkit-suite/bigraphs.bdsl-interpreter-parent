package org.bigraphs.dsl.interpreter.execution.jobs.reader;

import org.bigraphs.dsl.bDSL.BDSLDocument;
import org.springframework.batch.item.ItemReader;
import org.springframework.batch.item.NonTransientResourceException;
import org.springframework.batch.item.ParseException;
import org.springframework.batch.item.UnexpectedInputException;

/**
 * The class is supposed to read the statements of a BDSL program.
 *
 * @author Dominik Grzelak
 */
public abstract class BaseBdslItemReader<V extends Object> implements ItemReader<V> {
    protected BDSLDocument bdslDocument;

    public void setBdslDocument(BDSLDocument bdslDocument) {
        this.bdslDocument = bdslDocument;
    }

    public BDSLDocument getBdslDocument() {
        return bdslDocument;
    }

    public abstract void prepare();

    /**
     * Return {@code null} to indicate the end of the reading process.
     *
     * @return the statement
     * @throws Exception
     * @throws UnexpectedInputException
     * @throws ParseException
     * @throws NonTransientResourceException
     */
    @Override
    public abstract V read() throws Exception, UnexpectedInputException, ParseException, NonTransientResourceException;
}
