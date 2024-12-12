package org.bigraphs.dsl.interpreter.expressions.main.functions;

import org.bigraphs.framework.core.Bigraph;
import org.bigraphs.framework.core.Signature;
import org.bigraphs.framework.core.impl.pure.PureBigraph;
import org.bigraphs.framework.simulation.modelchecking.BigraphModelChecker;

/**
 * @author Dominik Grzelak
 */
@FunctionalInterface
public interface OnAllPredicateMatch<B extends Bigraph<? extends Signature<?>>> extends BigraphModelChecker.ReactiveSystemListener<B> {
    OnAllPredicateMatch<PureBigraph> EMPTY_PUREBIGRAPH = new OnAllPredicateMatch<PureBigraph>() {
        @Override
        public void onAllPredicateMatched(PureBigraph currentAgent, String label) {

        }
    };
    @Override
    void onAllPredicateMatched(B currentAgent, String label);
}
