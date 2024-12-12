package org.bigraphs.dsl.interpreter.expressions.main.functions;

import org.bigraphs.framework.core.Bigraph;
import org.bigraphs.framework.core.Signature;
import org.bigraphs.framework.core.impl.pure.PureBigraph;
import org.bigraphs.framework.simulation.modelchecking.BigraphModelChecker;

/**
 * @author Dominik Grzelak
 */
@FunctionalInterface
public interface OnReactiveSystemStarted<B extends Bigraph<? extends Signature<?>>> extends BigraphModelChecker.ReactiveSystemListener<B> {
    OnReactiveSystemStarted<PureBigraph> EMPTY_PUREBIGRAPH = () -> {
    };

    @Override
    void onReactiveSystemStarted();
}
