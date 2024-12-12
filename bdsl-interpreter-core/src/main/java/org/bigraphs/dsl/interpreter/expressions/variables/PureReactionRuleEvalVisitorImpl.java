package org.bigraphs.dsl.interpreter.expressions.variables;

import org.bigraphs.framework.core.Bigraph;
import org.bigraphs.framework.core.Signature;
import org.bigraphs.framework.core.exceptions.InvalidReactionRuleException;
import org.bigraphs.framework.core.EcoreBigraph;
import org.bigraphs.framework.core.reactivesystem.ParametricReactionRule;
import org.bigraphs.framework.core.reactivesystem.ReactionRule;
import org.bigraphs.framework.core.utils.BigraphUtil;
import org.bigraphs.dsl.bDSL.*;
import org.bigraphs.dsl.interpreter.BDSLUtil2;
import org.bigraphs.dsl.interpreter.BdslStatementInterpreterResult;
import org.bigraphs.dsl.interpreter.exceptions.BdslInterpretationException;
import org.bigraphs.dsl.interpreter.exceptions.BigraphMethodInterpreterException;
import org.bigraphs.dsl.interpreter.exceptions.ReactionRuleInterpreterException;
import org.bigraphs.dsl.interpreter.expressions.main.MainStatementEvalVisitorImpl;
import org.bigraphs.dsl.interpreter.extensions.variables.BigraphExpressionVisitableExtension;
import org.bigraphs.dsl.interpreter.extensions.main.MainBlockVisitableExtension;
import org.bigraphs.dsl.utils.BDSLUtil;
import lombok.experimental.ExtensionMethod;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.xtext.EcoreUtil2;

import java.util.Collections;
import java.util.Optional;

/**
 * @author Dominik Grzelak
 */
@ExtensionMethod({BigraphExpressionVisitableExtension.class, MainBlockVisitableExtension.class})
public class PureReactionRuleEvalVisitorImpl extends AbstractBigraphExpressionEvalVisitor<ReactionRule, LocalRuleDecl> {

    private final PureBigraphExpressionEvalVisitorImpl bigraphExpressionEvalVisitor;

    public PureReactionRuleEvalVisitorImpl() {
        super();
        this.bigraphExpressionEvalVisitor = new PureBigraphExpressionEvalVisitorImpl();
    }

    public PureReactionRuleEvalVisitorImpl(PureBigraphExpressionEvalVisitorImpl bigraphExpressionEvalVisitor) {
        super(bigraphExpressionEvalVisitor.getSignature());
        this.bigraphExpressionEvalVisitor = bigraphExpressionEvalVisitor;
    }

    @Override
    public ParametricReactionRule beginVisit(LocalRuleDecl bigraphVariable) throws BdslInterpretationException {
        setObservedObject(bigraphVariable);
        try {
            BDSLVariableDeclaration2 variableDeclaration = EcoreUtil2.getContainerOfType(bigraphVariable, BDSLVariableDeclaration2.class);
            assert variableDeclaration != null;
            boolean isBdslAssignment = BDSLUtil2.isBDSLAssignment(variableDeclaration); //e.g., a method assignment
            if (isBdslAssignment) { // evaluate possible reference or method assignment
                BDSLExpression expression = variableDeclaration.getValue();
                MainStatementEvalVisitorImpl mainDeclarationEvalVisitor = new MainStatementEvalVisitorImpl();
                if (expression instanceof ReferenceClassSymbol && BDSLUtil.checkReferenceSymbolType(expression, LocalRuleDecl.class)) {
                    return (ParametricReactionRule)
                            ((LocalRuleDecl) ((ReferenceClassSymbol) expression).getType()).interpretStart(this);
                }
//                // an assignable method ...
                if (expression instanceof AssignableBigraphExpressionWithExplicitSig) {
                    AbstractMainStatements abstractMainStatement = ((AbstractMainStatements) expression);
                    BdslStatementInterpreterResult<Object> result = (BdslStatementInterpreterResult<Object>)
                            abstractMainStatement.interpret(mainDeclarationEvalVisitor);
                    try {
                        return (ParametricReactionRule) result.getBdslExecutableStatement().call()
                                .orElseThrow(NullPointerException::new);
                    } catch (Exception e) {
                        throw new BigraphMethodInterpreterException(e, abstractMainStatement);
                    }
                }
            }

            if (BDSLUtil.bdslExpressionIsRuleDefinition(variableDeclaration.getValue())) {
                Optional<Bigraph> redex = null;
                Optional<Bigraph> reactum = null;
                EObject redex1 = variableDeclaration.getValue().getRedex();
                EObject reactum1 = variableDeclaration.getValue().getReactum();
                if (redex1 instanceof BigraphExpression) {
                    redex = computeBigraphFromExpressionList(Collections.singletonList((BigraphExpression) redex1), bigraphExpressionEvalVisitor);
                } else if (redex1 instanceof AssignableBigraphExpressionWithExplicitSig) {
                    MainStatementEvalVisitorImpl mainDeclarationEvalVisitor = new MainStatementEvalVisitorImpl();
                    AbstractMainStatements abstractMainStatement = ((AbstractMainStatements) redex1);
                    BdslStatementInterpreterResult<Object> result = (BdslStatementInterpreterResult<Object>)
                            abstractMainStatement.interpret(mainDeclarationEvalVisitor);
                    try {
                        redex = Optional.ofNullable((Bigraph) result.getBdslExecutableStatement().call().orElseThrow(NullPointerException::new));
                    } catch (Exception e) {
                        throw new BigraphMethodInterpreterException(e, abstractMainStatement);
                    }
                }
                if (reactum1 instanceof BigraphExpression) {
                    reactum = computeBigraphFromExpressionList(Collections.singletonList((BigraphExpression) reactum1), bigraphExpressionEvalVisitor);
                } else if (variableDeclaration.getValue().getReactum() instanceof AssignableBigraphExpressionWithExplicitSig) {
                    MainStatementEvalVisitorImpl mainDeclarationEvalVisitor = new MainStatementEvalVisitorImpl();
                    AbstractMainStatements abstractMainStatement = ((AbstractMainStatements) reactum1);
                    BdslStatementInterpreterResult<Object> result = (BdslStatementInterpreterResult<Object>)
                            abstractMainStatement.interpret(mainDeclarationEvalVisitor);
                    try {
                        reactum = Optional.ofNullable((Bigraph) result.getBdslExecutableStatement().call().orElseThrow(NullPointerException::new));
                    } catch (Exception e) {
                        throw new BigraphMethodInterpreterException(e, abstractMainStatement);
                    }
                }
                if (redex.isPresent() && reactum.isPresent()) {
                    return new ParametricReactionRule<>(
                            (Bigraph) BigraphUtil.copy((EcoreBigraph) redex.get()),
                            (Bigraph) BigraphUtil.copy((EcoreBigraph) reactum.get())
                    );
                }
                throw new NullPointerException();
            }
        } catch (InvalidReactionRuleException | CloneNotSupportedException e) {
            throw new ReactionRuleInterpreterException(e, bigraphVariable);
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
