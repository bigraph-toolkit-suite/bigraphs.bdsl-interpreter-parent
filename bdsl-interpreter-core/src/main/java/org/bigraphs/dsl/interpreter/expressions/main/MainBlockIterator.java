package org.bigraphs.dsl.interpreter.expressions.main;

import org.bigraphs.dsl.bDSL.AbstractMainStatements;
import org.bigraphs.dsl.bDSL.BDSLBlock;
import org.bigraphs.dsl.bDSL.MainElement;

import java.util.Iterator;
import java.util.NoSuchElementException;
import java.util.Objects;

/**
 * @author Dominik Grzelak
 */
public class MainBlockIterator implements Iterator<AbstractMainStatements> {
    BDSLBlock mainBlock;
    int cursorPos = 0;

    public MainBlockIterator(MainElement mainElement) {
        this(mainElement.getBody());
    }

    public MainBlockIterator(BDSLBlock mainBlock) {
        this.mainBlock = mainBlock;
    }

    @Override
    public boolean hasNext() {
        if (mainBlock.getStatements().size() == 0) return false;
        return cursorPos < mainBlock.getStatements().size() && Objects.nonNull(mainBlock.getStatements().get(cursorPos));
    }

    @Override
    public AbstractMainStatements next() {
        try {
            return mainBlock.getStatements().get(cursorPos++);
        } catch (Exception e) {
            throw new NoSuchElementException(e.getMessage());
        }
    }
}
