package org.bigraphs.dsl.interpreter.exceptions;

import org.eclipse.emf.ecore.EObject;

/**
 * @author Dominik Grzelak
 */
public class ReactionRuleInterpreterException extends BdslInterpretationException {

    private ReactionRuleInterpreterException() {
        super();
    }

    public ReactionRuleInterpreterException(Exception originalException, EObject lastElement) {
        super(originalException, lastElement);
    }
}
