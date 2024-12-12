package org.bigraphs.dsl.interpreter.expressions.variables;

import org.bigraphs.framework.core.Bigraph;
import org.bigraphs.framework.core.Signature;
import org.bigraphs.framework.core.impl.pure.PureBigraph;
import org.bigraphs.framework.core.reactivesystem.ReactiveSystemPredicate;
import org.bigraphs.dsl.bDSL.*;
import org.bigraphs.dsl.interpreter.BDSLUtil2;
import org.bigraphs.dsl.interpreter.BdslStatementInterpreterResult;
import org.bigraphs.dsl.interpreter.exceptions.BdslIOException;
import org.bigraphs.dsl.interpreter.exceptions.BigraphMethodInterpreterException;
import org.bigraphs.dsl.interpreter.expressions.main.MainStatementEvalVisitorImpl;
import org.bigraphs.dsl.interpreter.extensions.variables.BigraphExpressionVisitableExtension;
import org.bigraphs.dsl.interpreter.extensions.main.MainBlockVisitableExtension;
import org.bigraphs.dsl.utils.BDSLUtil;
import org.bigraphs.framework.simulation.modelchecking.predicates.BigraphIsoPredicate;
import org.bigraphs.framework.simulation.modelchecking.predicates.SubBigraphMatchPredicate;
import lombok.experimental.ExtensionMethod;
import org.eclipse.xtext.EcoreUtil2;

import java.util.List;
import java.util.Optional;

/**
 * @author Dominik Grzelak
 */
@ExtensionMethod({BigraphExpressionVisitableExtension.class, MainBlockVisitableExtension.class})
public class PurePredicateEvalVisitorImpl extends AbstractBigraphExpressionEvalVisitor<ReactiveSystemPredicate, LocalPredicateDeclaration> {

    private final PureBigraphExpressionEvalVisitorImpl bigraphExpressionEvalVisitor;

    public PurePredicateEvalVisitorImpl(PureBigraphExpressionEvalVisitorImpl bigraphExpressionEvalVisitor) {
        super(bigraphExpressionEvalVisitor.getSignature());
        this.bigraphExpressionEvalVisitor = bigraphExpressionEvalVisitor;
    }

    public PurePredicateEvalVisitorImpl() {
        super();
        this.bigraphExpressionEvalVisitor = new PureBigraphExpressionEvalVisitorImpl();
    }

    @Override
    public ReactiveSystemPredicate beginVisit(LocalPredicateDeclaration predicateDeclaration) {
        setObservedObject(predicateDeclaration);
        BDSLVariableDeclaration2 variableDeclaration = EcoreUtil2.getContainerOfType(predicateDeclaration, BDSLVariableDeclaration2.class);
        assert variableDeclaration != null;
        boolean isBdslAssignment = BDSLUtil2.isBDSLAssignment(variableDeclaration); //e.g., a method assignment
        if (isBdslAssignment) { // evaluate possible reference or method assignment
            BDSLExpression expression = variableDeclaration.getValue();
            MainStatementEvalVisitorImpl mainDeclarationEvalVisitor = new MainStatementEvalVisitorImpl();
            if (expression instanceof ReferenceClassSymbol && BDSLUtil.checkReferenceSymbolType(expression, LocalPredicateDeclaration.class)) {
                return (ReactiveSystemPredicate)
                        ((LocalPredicateDeclaration) ((ReferenceClassSymbol) expression).getType()).interpretStart(this);
            }
////                // an assignable method ...
            if (expression instanceof AssignableBigraphExpressionWithExplicitSig) {
                AbstractMainStatements abstractMainStatement = ((AbstractMainStatements) expression);
                BdslStatementInterpreterResult<Object> result = (BdslStatementInterpreterResult<Object>)
                        abstractMainStatement.interpret(mainDeclarationEvalVisitor);
                try {
//                    return (ReactiveSystemPredicates)
                    Optional<Object> call = result.getBdslExecutableStatement().call();
                    if (call.isPresent() && call.get() instanceof PureBigraph) {
                        switch (predicateDeclaration.getType()) {
                            case ISO:
                                return BigraphIsoPredicate.create((PureBigraph) call.get());
                            case PARTIAL:
                            default:
                                return SubBigraphMatchPredicate.create((PureBigraph) call.get());
                        }
                    } else {
                        throw new BdslIOException("The predicate could not be loaded", abstractMainStatement);
                    }
                } catch (Exception e) {
                    throw new BigraphMethodInterpreterException(e, abstractMainStatement);
                }
            }
        }

        if (BDSLUtil.bdslExpressionIsBigraphDefinition(variableDeclaration.getValue())) {
            List<BigraphExpression> definition = variableDeclaration.getValue().getDefinition();
            Optional<Bigraph> bigraph = computeBigraphFromExpressionList(definition, bigraphExpressionEvalVisitor);
            if (bigraph.isPresent()) {
//            if (redex1 instanceof BigraphExpression) {
//                variableDeclaration.getValue().getRedex()
//                redex = (Bigraph) BigraphUtil.copy((EcoreBigraph) ((BigraphExpression) redex1).interpret(this.bigraphExpressionEvalVisitor));
//            } else if (redex1 instanceof AssignableBigraphExpressionWithExplicitSig) {
//                throw new RuntimeException("Not implemented yet"); //TODO: see above: extract method
//            }
//            BigraphExpression bigraphExpression = predicateDeclaration.getDefinition();
//            Bigraph bigraph = (Bigraph) bigraphExpression.interpret(bigraphExpressionEvalVisitor);
                switch (predicateDeclaration.getType()) {
                    case ISO:
                        return BigraphIsoPredicate.create(bigraph.get());
                    case PARTIAL:
                    default:
                        return SubBigraphMatchPredicate.create(bigraph.get());
                }
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
