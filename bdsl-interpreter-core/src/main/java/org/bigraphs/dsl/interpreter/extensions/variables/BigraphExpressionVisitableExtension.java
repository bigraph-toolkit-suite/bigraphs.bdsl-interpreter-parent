package org.bigraphs.dsl.interpreter.extensions.variables;

import org.bigraphs.framework.core.Bigraph;
import org.bigraphs.framework.core.impl.signature.DefaultDynamicSignature;
import org.bigraphs.framework.core.reactivesystem.ReactionRule;
import org.bigraphs.framework.core.reactivesystem.ReactiveSystem;
import org.bigraphs.framework.core.reactivesystem.ReactiveSystemPredicate;
import org.bigraphs.dsl.bDSL.*;
import org.bigraphs.dsl.interpreter.exceptions.BdslInterpretationException;
import org.bigraphs.dsl.interpreter.expressions.variables.*;
import org.bigraphs.dsl.interpreter.expressions.variables.*;
import org.eclipse.emf.ecore.EObject;

/**
 * Extension methods for generic grammar elements of BDSL that must be immediately executed.
 * Also important for caching.
 * <p>
 * These extension methods add a {@code interpret()} method to each BDSL grammar element.
 * Variations of the interpretation logic are given by the passed evaluation visitor.
 * Needs to be added to a visitor as extension method: {@code @ExtensionMethod()}.
 * <p>
 * This is a double dispatch mechanism. Only the expression knows which case/method must be called.
 *
 * @author Dominik Grzelak
 */
public class BigraphExpressionVisitableExtension {

    public static DefaultDynamicSignature interpretStart(Signature signatureDeclaration, SignatureEvalVisitorImpl visitor) throws BdslInterpretationException {
        DefaultDynamicSignature cachedOrEval = (DefaultDynamicSignature) visitor.getSignatureCache().getObject(signatureDeclaration)
                .orElseGet(() -> visitor.beginVisit(signatureDeclaration));
        visitor.getSignatureCache().synchronize(signatureDeclaration, cachedOrEval);
        return cachedOrEval;
    }

    public static Bigraph<?> interpretStart(LocalVarDecl localVarDecl, AbstractBigraphExpressionEvalVisitor<Bigraph, LocalVarDecl> visitor) throws BdslInterpretationException {
        Bigraph<?> bigraph = (Bigraph<?>) visitor.getLocalVarCache().getObject(localVarDecl)
                .orElseGet(() -> visitor.beginVisit(localVarDecl));
        visitor.getLocalVarCache().synchronize(localVarDecl, bigraph);
        return bigraph;
    }

    public static ReactionRule<?> interpretStart(LocalRuleDecl localRuleDecl, PureReactionRuleEvalVisitorImpl visitor) throws BdslInterpretationException {
        ReactionRule<?> result = (ReactionRule<?>) visitor.getLocalRuleCache().getObject(localRuleDecl)
                .orElseGet(() -> visitor.beginVisit(localRuleDecl));
        visitor.getLocalRuleCache().synchronize(localRuleDecl, result);
        return result;
    }

    public static ReactiveSystemPredicate<?> interpretStart(LocalPredicateDeclaration localPredDecl, PurePredicateEvalVisitorImpl visitor) {
        ReactiveSystemPredicate<?> result = (ReactiveSystemPredicate<?>) visitor.getLocalPredCache().getObject(localPredDecl)
                .orElseGet(() -> visitor.beginVisit(localPredDecl));
        visitor.getLocalPredCache().synchronize(localPredDecl, result);
        return result;
    }

    public static ReactiveSystem<?> interpretStart(BRSDefinition brsDefinition, PureReactiveSystemEvalVisitorImpl visitor) throws BdslInterpretationException {
        ReactiveSystem<?> result = (ReactiveSystem<?>) visitor.getLocalBRSCache().getObject(brsDefinition)
                .orElseGet(() -> visitor.beginVisit(brsDefinition));
        visitor.getLocalBRSCache().synchronize(brsDefinition, result);
        return result;
    }

    public static <T extends EObject> Bigraph interpret(BigraphExpression bigraphExpression, AbstractBigraphExpressionEvalVisitor<Bigraph, T> visitor) {
//        //TODO or dispatch to other interpreter methods here, better here than in the visitor
//        //use reflection approach ...
        if (bigraphExpression instanceof Plus) {
            return interpret((Plus) bigraphExpression, visitor);
        }
        if (bigraphExpression instanceof Multi) {
            return interpret((Multi) bigraphExpression, visitor);
        }
        if (bigraphExpression instanceof NodeExpressionCall ||
                bigraphExpression instanceof BigraphVarReference ||
                bigraphExpression instanceof LVD2) {
            return interpret((AbstractBigraphDeclaration) bigraphExpression, visitor);
        }
        if (bigraphExpression instanceof ElementaryBigraphs) {
            return interpret((ElementaryBigraphs) bigraphExpression, visitor);
        }
        throw new UnsupportedOperationException("interpret extension not available for element=" + bigraphExpression);
    }

    public static <T extends EObject> Bigraph interpret(ElementaryBigraphs elementaryBigraph, AbstractBigraphExpressionEvalVisitor<Bigraph, T> visitor) {
        return visitor.visit(elementaryBigraph);
    }

    //TODO: add generic return types
    public static <T extends EObject> Bigraph interpret(Plus plus, AbstractBigraphExpressionEvalVisitor<Bigraph, T> visitor) {
        return (Bigraph) visitor.visit(plus);
    }

    public static <T extends EObject> Bigraph interpret(Multi multi, AbstractBigraphExpressionEvalVisitor<Bigraph, T> visitor) {
        return (Bigraph) visitor.visit(multi);
    }

    public static <T extends EObject> Bigraph interpret(AbstractBigraphDeclaration nodeExpression, AbstractBigraphExpressionEvalVisitor<Bigraph, T> visitor) {
        return (Bigraph) visitor.visit(nodeExpression);
    }
}
