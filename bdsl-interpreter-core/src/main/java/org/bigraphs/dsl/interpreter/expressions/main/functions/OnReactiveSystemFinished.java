package org.bigraphs.dsl.interpreter.expressions.main.functions;

import org.bigraphs.framework.core.Bigraph;
import org.bigraphs.framework.core.Signature;
import org.bigraphs.framework.core.impl.pure.PureBigraph;
import org.bigraphs.framework.simulation.modelchecking.BigraphModelChecker;

/**
 * @author Dominik Grzelak
 */
@FunctionalInterface
public interface OnReactiveSystemFinished<B extends Bigraph<? extends Signature<?>>> extends BigraphModelChecker.ReactiveSystemListener<B> {
    OnReactiveSystemFinished<PureBigraph> EMPTY_PUREBIGRAPH = () -> {
    };

    @Override
    void onReactiveSystemFinished();
}
