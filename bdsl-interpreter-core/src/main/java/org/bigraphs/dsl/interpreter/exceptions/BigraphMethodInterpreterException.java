package org.bigraphs.dsl.interpreter.exceptions;

import org.eclipse.emf.ecore.EObject;

/**
 * @author Dominik Grzelak
 */
public class BigraphMethodInterpreterException extends BdslInterpretationException {

    public BigraphMethodInterpreterException(Exception originalException, EObject... lastElements) {
        super(originalException, lastElements);
    }

    public BigraphMethodInterpreterException(String message, EObject... lastElements) {
        super(message, lastElements);
    }
}
