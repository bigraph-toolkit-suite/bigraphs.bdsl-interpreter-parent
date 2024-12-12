package org.bigraphs.dsl.interpreter.expressions.main.functions;

import org.bigraphs.framework.core.Bigraph;
import org.bigraphs.framework.core.Signature;
import org.bigraphs.framework.core.impl.pure.PureBigraph;
import org.bigraphs.framework.core.reactivesystem.BigraphMatch;
import org.bigraphs.framework.core.reactivesystem.ReactionRule;
import org.bigraphs.framework.simulation.modelchecking.BigraphModelChecker;

/**
 * @author Dominik Grzelak
 */
@FunctionalInterface
public interface OnRuleMatch<B extends Bigraph<? extends Signature<?>>> extends BigraphModelChecker.ReactiveSystemListener<B> {
    OnRuleMatch<PureBigraph> EMPTY_PUREBIGRAPH = new OnRuleMatch<PureBigraph>() {
        @Override
        public void onUpdateReactionRuleApplies(PureBigraph agent, ReactionRule<PureBigraph> reactionRule, BigraphMatch<PureBigraph> matchResult) {

        }
    };
    @Override
    void onUpdateReactionRuleApplies(B agent, ReactionRule<B> reactionRule, BigraphMatch<B> matchResult);
}
