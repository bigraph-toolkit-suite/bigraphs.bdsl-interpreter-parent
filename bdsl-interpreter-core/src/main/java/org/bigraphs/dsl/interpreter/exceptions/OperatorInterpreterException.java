package org.bigraphs.dsl.interpreter.exceptions;

import org.eclipse.emf.ecore.EObject;

/**
 * @author Dominik Grzelak
 */
public class OperatorInterpreterException extends BdslInterpretationException {
    private OperatorInterpreterException() {
        super();
    }

    public OperatorInterpreterException(Exception originalException, EObject... lastElements) {
        super(originalException, lastElements);
    }

    public OperatorInterpreterException(String message, EObject... lastElements) {
        super(message, lastElements);
    }
}
