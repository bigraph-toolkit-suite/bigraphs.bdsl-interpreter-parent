package org.bigraphs.dsl.interpreter.expressions.variables;

import org.bigraphs.framework.core.Bigraph;
import org.bigraphs.framework.core.Signature;
import org.bigraphs.framework.core.exceptions.InvalidReactionRuleException;
import org.bigraphs.framework.core.impl.pure.PureBigraph;
import org.bigraphs.framework.core.impl.signature.DefaultDynamicSignature;
import org.bigraphs.framework.core.reactivesystem.ReactionRule;
import org.bigraphs.framework.core.reactivesystem.ReactiveSystem;
import org.bigraphs.framework.core.reactivesystem.ReactiveSystemPredicate;
import org.bigraphs.dsl.bDSL.*;
import org.bigraphs.dsl.interpreter.BDSLUtil2;
import org.bigraphs.dsl.interpreter.exceptions.BdslInterpretationException;
import org.bigraphs.dsl.interpreter.exceptions.ReactionRuleInterpreterException;
import org.bigraphs.dsl.interpreter.exceptions.SignatureNotMatchException;
import org.bigraphs.dsl.interpreter.extensions.variables.BigraphExpressionVisitableExtension;
import org.bigraphs.dsl.utils.BDSLUtil;
import org.bigraphs.framework.simulation.matching.pure.PureReactiveSystem;
import lombok.experimental.ExtensionMethod;
import org.eclipse.xtext.EcoreUtil2;

import java.util.Objects;

/**
 * @author Dominik Grzelak
 */
@ExtensionMethod({BigraphExpressionVisitableExtension.class})
public class PureReactiveSystemEvalVisitorImpl extends AbstractBigraphExpressionEvalVisitor<ReactiveSystem, BRSDefinition> {

    private final PureBigraphExpressionEvalVisitorImpl bigraphExpressionEvalVisitor;
    private final SignatureEvalVisitorImpl signatureEvalVisitor = new SignatureEvalVisitorImpl();

    public PureReactiveSystemEvalVisitorImpl() {
        super();
        this.bigraphExpressionEvalVisitor = new PureBigraphExpressionEvalVisitorImpl();
    }

    public PureReactiveSystemEvalVisitorImpl(PureBigraphExpressionEvalVisitorImpl bigraphExpressionEvalVisitor) {
        super(bigraphExpressionEvalVisitor.getSignature());
        this.bigraphExpressionEvalVisitor = bigraphExpressionEvalVisitor;
    }

    @Override
    public ReactiveSystem beginVisit(BRSDefinition brsDefinition) throws BdslInterpretationException {
        setObservedObject(brsDefinition);
        PureReactiveSystem reactiveSystem = new PureReactiveSystem();
        BDSLVariableDeclaration2 variableDeclaration = EcoreUtil2.getContainerOfType(brsDefinition, BDSLVariableDeclaration2.class);
        boolean isBdslAssignment = BDSLUtil2.isBDSLAssignment(variableDeclaration);

        BDSLExpression brsExpression = variableDeclaration.getValue();
        if (BDSLUtil.bdslExpressionIsBRSDefinition(brsExpression) && brsExpression.getAgents().size() > 0) {
            BigraphVarReference bigraphVarReference = (BigraphVarReference) brsExpression.getAgents().get(0);
            LocalVarDecl localVarDecl = bigraphVarReference.getValue();
            PureBigraphExpressionEvalVisitorImpl pureBigraphExpressionEvalVisitor = new PureBigraphExpressionEvalVisitorImpl();

            if (Objects.nonNull(localVarDecl.getSig())) {
                if (Objects.isNull(brsDefinition.getSig())) {
                    org.bigraphs.dsl.bDSL.Signature signature = BDSLUtil.tryInferSignature(brsDefinition);
                    BDSLUtil.updateSignatureOfAssignment(variableDeclaration, signature);
                }
                assert brsDefinition.getSig() != null;
                DefaultDynamicSignature brsSignature = (DefaultDynamicSignature) brsDefinition.getSig().interpretStart(signatureEvalVisitor);
                DefaultDynamicSignature dynamicSignature = (DefaultDynamicSignature) localVarDecl.getSig().interpretStart(signatureEvalVisitor);
                if (!brsSignature.equals(dynamicSignature)) {
                    throw new SignatureNotMatchException("The  signature of the BRS and agent do not match", brsDefinition, localVarDecl);
                }
                pureBigraphExpressionEvalVisitor = new PureBigraphExpressionEvalVisitorImpl(dynamicSignature);
            }

            Bigraph<?> agent = (Bigraph<?>) ((LocalVarDecl) localVarDecl).interpretStart(pureBigraphExpressionEvalVisitor);
            reactiveSystem.setAgent((PureBigraph) agent);
            if (brsExpression.getRules().size() > 0) {
                PureReactionRuleEvalVisitorImpl evalVisitor = new PureReactionRuleEvalVisitorImpl(pureBigraphExpressionEvalVisitor);
                for (RuleVarReference ruleVarReference : brsExpression.getRules()) {
                    LocalRuleDecl ruleDecl = ruleVarReference.getValue();
                    try {
                        ReactionRule<PureBigraph> ruleInterpreted = (ReactionRule<PureBigraph>) ruleDecl.interpretStart(evalVisitor);
                        reactiveSystem.addReactionRule(ruleInterpreted);
                    } catch (InvalidReactionRuleException e) {
                        throw new ReactionRuleInterpreterException(e, ruleDecl);
                    }
                }
            }

            if (Objects.nonNull(brsExpression.getPredicates()) &&
                    brsExpression.getPredicates().size() > 0) {
                PurePredicateEvalVisitorImpl evalVisitor = new PurePredicateEvalVisitorImpl(pureBigraphExpressionEvalVisitor);
                for (PredicateVarReference eachRef : brsExpression.getPredicates()) {
                    LocalPredicateDeclaration value = eachRef.getValue();
                    ReactiveSystemPredicate<PureBigraph> predicatesInterpreted = (ReactiveSystemPredicate<PureBigraph>) value.interpretStart(evalVisitor);
                    assert predicatesInterpreted != null;
                    reactiveSystem.addPredicate(predicatesInterpreted);
                }
            }

            return reactiveSystem;
        }

        // Cover the rest of the cases by dispatching it again
        // E.g., assignments via other BRS references or method calls
        if (isBdslAssignment) {
            BDSLExpression expression = variableDeclaration.getValue();
            if (expression instanceof ReferenceClassSymbol && BDSLUtil.checkReferenceSymbolType(expression, BRSDefinition.class)) {
                ReactiveSystem result = (ReactiveSystem)
                        ((BRSDefinition) ((ReferenceClassSymbol) expression).getType()).interpretStart(this);
                return result;
            }
//                // an assignable method ...
            if (expression instanceof AssignableBigraphExpressionWithExplicitSig) {
                AbstractMainStatements abstractMainStatement = ((AbstractMainStatements) expression);
                throw new IllegalStateException("not implemented");
//                BdslStatementInterpreterResult<Object> result = (BdslStatementInterpreterResult<Object>)
//                        abstractMainStatement.interpret(this);
//                return result;
            }
        }
        throw new IllegalStateException();
    }

    @Override
    public Bigraph<? extends Signature<?>> visit(AbstractBigraphDeclaration bigraphDeclaration) {
        return this.bigraphExpressionEvalVisitor.visit(bigraphDeclaration);
    }

    @Override
    public Bigraph<? extends Signature<?>> visit(ElementaryBigraphs elementaryBigraph) {
        return bigraphExpressionEvalVisitor.visit(elementaryBigraph);
    }

    @Override
    public Bigraph<? extends Signature<?>> visit(Plus plus) {
        return this.bigraphExpressionEvalVisitor.visit(plus);
    }

    @Override
    public Bigraph<? extends Signature<?>> visit(Multi multi) {
        return this.bigraphExpressionEvalVisitor.visit(multi);
    }
}
