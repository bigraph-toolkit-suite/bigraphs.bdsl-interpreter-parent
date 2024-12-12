package org.bigraphs.dsl.cli.configuration.v1;

import org.bigraphs.dsl.bDSL.AbstractMainStatements;
import org.bigraphs.dsl.interpreter.execution.jobs.reader.BaseBdslItemReader;
import org.bigraphs.dsl.interpreter.expressions.main.MainBlockIterator;
import org.springframework.batch.item.NonTransientResourceException;
import org.springframework.batch.item.ParseException;
import org.springframework.batch.item.UnexpectedInputException;

/**
 * @author Dominik Grzelak
 */
public class SimpleSequentialBdslDocumentReader extends BaseBdslItemReader<AbstractMainStatements> {
    MainBlockIterator iterator;

    @Override
    public void prepare() {
        iterator = new MainBlockIterator(bdslDocument.getMain());
    }

    @Override
    public AbstractMainStatements read() throws Exception, UnexpectedInputException, ParseException, NonTransientResourceException {
        if (iterator.hasNext()) {
            return iterator.next();
        }
        return null;
    }
}
