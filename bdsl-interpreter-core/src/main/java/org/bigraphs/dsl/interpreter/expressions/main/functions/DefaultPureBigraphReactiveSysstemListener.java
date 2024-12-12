package org.bigraphs.dsl.interpreter.expressions.main.functions;

import org.bigraphs.framework.core.impl.pure.PureBigraph;
import org.bigraphs.framework.core.reactivesystem.BigraphMatch;
import org.bigraphs.framework.core.reactivesystem.ReactionRule;
import org.bigraphs.framework.core.reactivesystem.ReactiveSystemPredicate;
import org.bigraphs.framework.simulation.modelchecking.BigraphModelChecker;

/**
 * An implementation of the reactive system listener from the bigraph framework.
 *
 * @author Dominik Grzelak
 */
public class DefaultPureBigraphReactiveSysstemListener implements BigraphModelChecker.ReactiveSystemListener<PureBigraph> {

    OnReactiveSystemStarted<PureBigraph> onReactiveSystemStarted = OnReactiveSystemStarted.EMPTY_PUREBIGRAPH;
    OnReactiveSystemFinished<PureBigraph> onReactiveSystemFinished = OnReactiveSystemFinished.EMPTY_PUREBIGRAPH;
    OnRuleMatch<PureBigraph> onRuleMatch = OnRuleMatch.EMPTY_PUREBIGRAPH;
    OnPredicateMatch<PureBigraph> onPredicateMatch = OnPredicateMatch.EMPTY_PUREBIGRAPH;
    OnAllPredicateMatch<PureBigraph> onAllPredicateMatch = OnAllPredicateMatch.EMPTY_PUREBIGRAPH;

    @Override
    public void onReactiveSystemStarted() {
        onReactiveSystemStarted.onReactiveSystemStarted();
    }

    @Override
    public void onReactiveSystemFinished() {
        onReactiveSystemFinished.onReactiveSystemFinished();
    }

    @Override
    public void onUpdateReactionRuleApplies(PureBigraph agent, ReactionRule<PureBigraph> reactionRule, BigraphMatch<PureBigraph> matchResult) {
        onRuleMatch.onUpdateReactionRuleApplies(agent, reactionRule, matchResult);
    }

    @Override
    public void onAllPredicateMatched(PureBigraph currentAgent, String label) {
        onAllPredicateMatch.onAllPredicateMatched(currentAgent, label);
    }

    @Override
    public void onPredicateMatched(PureBigraph currentAgent, ReactiveSystemPredicate<PureBigraph> predicate) {
        onPredicateMatch.onPredicateMatched(currentAgent, predicate);
    }

    public DefaultPureBigraphReactiveSysstemListener setOnReactiveSystemStarted(OnReactiveSystemStarted<PureBigraph> onReactiveSystemStarted) {
        this.onReactiveSystemStarted = onReactiveSystemStarted;
        return this;
    }

    public DefaultPureBigraphReactiveSysstemListener setOnReactiveSystemFinished(OnReactiveSystemFinished<PureBigraph> onReactiveSystemFinished) {
        this.onReactiveSystemFinished = onReactiveSystemFinished;
        return this;
    }

    public DefaultPureBigraphReactiveSysstemListener setOnRuleMatch(OnRuleMatch<PureBigraph> onRuleMatch) {
        this.onRuleMatch = onRuleMatch;
        return this;
    }

    @SuppressWarnings("UnusedReturnValue")
    public DefaultPureBigraphReactiveSysstemListener setOnPredicateMatch(OnPredicateMatch<PureBigraph> onPredicateMatch) {
        this.onPredicateMatch = onPredicateMatch;
        return this;
    }

    @SuppressWarnings("UnusedReturnValue")
    public DefaultPureBigraphReactiveSysstemListener setOnAllPredicateMatch(OnAllPredicateMatch<PureBigraph> onAllPredicateMatch) {
        this.onAllPredicateMatch = onAllPredicateMatch;
        return this;
    }
}
