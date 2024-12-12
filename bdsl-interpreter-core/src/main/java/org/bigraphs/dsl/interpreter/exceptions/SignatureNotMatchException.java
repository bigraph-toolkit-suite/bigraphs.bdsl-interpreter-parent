package org.bigraphs.dsl.interpreter.exceptions;

import org.eclipse.emf.ecore.EObject;

/**
 * @author Dominik Grzelak
 */
public class SignatureNotMatchException extends BdslInterpretationException {

    public SignatureNotMatchException(String message, EObject... lastElements) {
        super(message, lastElements);
    }
}
