package org.bigraphs.dsl.interpreter.exceptions;

import org.eclipse.emf.ecore.EObject;

/**
 * @author Dominik Grzelak
 */
public class BigraphDeclarationInterpreterException extends BdslInterpretationException {

    public BigraphDeclarationInterpreterException() {
        super();
    }

    public BigraphDeclarationInterpreterException(Exception originalException, EObject lastElement) {
        super(originalException, lastElement);
    }
}
