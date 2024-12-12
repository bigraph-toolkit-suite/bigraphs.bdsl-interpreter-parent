package org.bigraphs.dsl.interpreter.expressions.main.functions;

import org.bigraphs.framework.core.Bigraph;
import org.bigraphs.framework.core.Signature;
import org.bigraphs.framework.core.impl.pure.PureBigraph;
import org.bigraphs.framework.core.reactivesystem.ReactiveSystemPredicate;
import org.bigraphs.framework.simulation.modelchecking.BigraphModelChecker;

/**
 * @author Dominik Grzelak
 */
@FunctionalInterface
public interface OnPredicateMatch<B extends Bigraph<? extends Signature<?>>> extends BigraphModelChecker.ReactiveSystemListener<B> {
    OnPredicateMatch<PureBigraph> EMPTY_PUREBIGRAPH = new OnPredicateMatch<PureBigraph>() {
        @Override
        public void onPredicateMatched(PureBigraph currentAgent, ReactiveSystemPredicate<PureBigraph> predicate) {

        }
    };

    @Override
    void onPredicateMatched(B currentAgent, ReactiveSystemPredicate<B> predicate);
}
