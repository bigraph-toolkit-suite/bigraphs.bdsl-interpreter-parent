package org.bigraphs.dsl.interpreter.exceptions;

import org.eclipse.emf.ecore.EObject;

/**
 * @author Dominik Grzelak
 */
public class BdslIOException extends BdslInterpretationException {

    public BdslIOException(Exception originalException, EObject lastElement) {
        super(originalException, lastElement);
    }
    public BdslIOException(String errorMessage, EObject lastElement) {
        super(errorMessage, lastElement);
    }

    public BdslIOException(Throwable cause) {
        super(cause);
    }
}
