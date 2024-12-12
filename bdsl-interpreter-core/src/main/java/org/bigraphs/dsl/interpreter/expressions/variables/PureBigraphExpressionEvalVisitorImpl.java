package org.bigraphs.dsl.interpreter.expressions.variables;

import org.bigraphs.framework.core.Bigraph;
import org.bigraphs.framework.core.BigraphComposite;
import org.bigraphs.framework.core.exceptions.IncompatibleSignatureException;
import org.bigraphs.framework.core.exceptions.operations.IncompatibleInterfaceException;
import org.bigraphs.framework.core.impl.signature.DefaultDynamicSignature;
import org.bigraphs.dsl.bDSL.*;
import org.bigraphs.dsl.bDSL.impl.LocalVarDeclImpl;
import org.bigraphs.dsl.interpreter.BDSLUtil2;
import org.bigraphs.dsl.interpreter.BdslStatementInterpreterResult;
import org.bigraphs.dsl.interpreter.exceptions.BigraphDeclarationInterpreterException;
import org.bigraphs.dsl.interpreter.exceptions.BigraphMethodInterpreterException;
import org.bigraphs.dsl.interpreter.exceptions.OperatorInterpreterException;
import org.bigraphs.dsl.interpreter.exceptions.SignatureNotMatchException;
import org.bigraphs.dsl.interpreter.expressions.main.MainStatementEvalVisitorImpl;
import org.bigraphs.dsl.interpreter.extensions.main.MainBlockVisitableExtension;
import org.bigraphs.dsl.interpreter.extensions.variables.BigraphExpressionVisitableExtension;
import org.bigraphs.dsl.utils.BDSLUtil;
import lombok.experimental.ExtensionMethod;
import org.eclipse.xtext.EcoreUtil2;

import java.util.List;
import java.util.Objects;
import java.util.Optional;

import static org.bigraphs.framework.core.factory.BigraphFactory.ops;

/**
 * This interface implementation represents a bigraph expression visitor in order to evaluate bigraph expressions.
 * The processing of the bigraph expressions takes place here.
 * It is used for evaluating {@link LocalVarDecl} definitions.
 * <p>
 * This is a double dispatch mechanism. Only the expression knows which method must be called.
 * We have to include the extension methods for the {@link BigraphExpression} types since the evaluator visitor
 * needs to call them here.
 *
 * @author Dominik Grzelak
 * @see BigraphExpressionVisitableExtension
 */
@ExtensionMethod({BigraphExpressionVisitableExtension.class, MainBlockVisitableExtension.class})
public class PureBigraphExpressionEvalVisitorImpl extends AbstractBigraphExpressionEvalVisitor<Bigraph, LocalVarDecl> {

    public PureBigraphExpressionEvalVisitorImpl() {
        super();
    }

    public PureBigraphExpressionEvalVisitorImpl(DefaultDynamicSignature signature) {
        super(signature);
    }

    @Override
    public Bigraph beginVisit(LocalVarDecl localVarDecl) {
        setObservedObject(localVarDecl);
        Bigraph b = null;
        BDSLVariableDeclaration2 container = EcoreUtil2.getContainerOfType(localVarDecl, BDSLVariableDeclaration2.class);
        boolean isBdslAssignment = BDSLUtil2.isBDSLAssignment(container); //e.g., a method assignment
        if (isBdslAssignment) { // evaluate possible reference or method assignment
            BDSLExpression expression = container.getValue();
            assert expression instanceof AssignableBigraphExpression;

            if (expression instanceof ReferenceClassSymbol && BDSLUtil.checkReferenceSymbolType(expression, LocalVarDecl.class)) {
                Bigraph result = (Bigraph)
                        ((LocalVarDecl) ((ReferenceClassSymbol) expression).getType()).interpretStart(this);
                if (Objects.isNull(localVarDecl.getSig())) {
                    BDSLUtil.updateSignatureOfAssignment(container, ((ReferenceClassSymbol) expression).getType().getSig());
                }
                return result;
            }
            // an assignable method ...
            if (expression instanceof AssignableBigraphExpressionWithExplicitSig) {
                MainStatementEvalVisitorImpl mainDeclarationEvalVisitor = new MainStatementEvalVisitorImpl();
                AbstractMainStatements abstractMainStatement = ((AbstractMainStatements) expression);
                BdslStatementInterpreterResult<Object> result = (BdslStatementInterpreterResult<Object>)
                        abstractMainStatement.interpret(mainDeclarationEvalVisitor);
                try {
                    b = (Bigraph) result.getBdslExecutableStatement().call().orElseThrow(NullPointerException::new);
                } catch (Exception e) {
                    throw new BigraphMethodInterpreterException(e, abstractMainStatement);
                }
            }
//            // a
//            if (expression instanceof BigraphExpression) {
//                BigraphExpression expression1 = (BigraphExpression) expression;
//                Optional<Bigraph> reduce = computeBigraphFromExpressionList(Collections.singletonList(expression1));
//                b = reduce.get();
////                EcoreUtil2.getContainerOfType(localVarDecl, BDSLVariableDeclaration.class);
//                if (expression1 instanceof BigraphVarReference) {
//                    BDSLUtil.updateSignatureOfAssignment(container, ((BigraphVarReference) expression1).getValue().getSig());
//                }
//            }

        } else { // evaluate algebraic bigraph expression
            List<BigraphExpression> localVarDeclDefinitions = BDSLUtil2.getBigraphExpressionsOf(localVarDecl);
            Optional<Bigraph> reduce = computeBigraphFromExpressionList(localVarDeclDefinitions, this);
            b = reduce.get();
            if (Objects.isNull(localVarDecl.getSig()) && Objects.nonNull(container)) {
                Signature sigInferred = BDSLUtil.tryInferSignature(localVarDecl);
                if (sigInferred == null) {
                    sigInferred = BDSLUtil2.createEmptySignatureElement();
                }
                BDSLUtil.updateSignatureOfAssignment(container, sigInferred);
            }
        }

        if (Objects.nonNull(localVarDecl.getControlType())) {
            if (localVarDecl.getControlType().getType() == ControlType.ATOMIC) {
                throw new OperatorInterpreterException(
                        String.format("Control is atomic. Cannot nest further expression under control %s", localVarDecl.getControlType().getType()),
                        localVarDecl
                );
            }
            try {
                b = nestBigraphUnderControl(b, localVarDecl.getControlType());
            } catch (IncompatibleSignatureException | IncompatibleInterfaceException e) {
                throw new OperatorInterpreterException(e, localVarDecl);
            }
        }
        return b;
    }


    @Override
    public Bigraph<? extends org.bigraphs.framework.core.Signature<?>> visit(ElementaryBigraphs elementaryBigraph) {
        return createElementaryBigraph(elementaryBigraph);
    }

    @Override
    public Bigraph visit(AbstractBigraphDeclaration bigraphDeclaration) {
        resolveSignatureIfNotSet(bigraphDeclaration);
        if (bigraphDeclaration instanceof NodeExpressionCall) {
            ControlVariable ctrlVar = ((NodeExpressionCall) bigraphDeclaration).getValue();
            return createAtomFrom(ctrlVar, ((NodeExpressionCall) bigraphDeclaration).getLinks());
        }
        if (bigraphDeclaration instanceof BigraphVarReference) {
            BigraphVarReference bigraphVarReference = (BigraphVarReference) bigraphDeclaration;

            LocalVarDecl localVarDecl = bigraphVarReference.getValue();
            // Signatures should already match here with the surrounding bigraph, when we do _validation_ before
            // Nevertheless, we check again
            if (Objects.nonNull(localVarDecl.getSig()) && !getSignatureCache().getObject(localVarDecl.getSig()).isPresent()) { //if its not in the cache it should not have the same signature ...
//                // or because of an error, ... so we try again
                assertSignaturesMatch(localVarDecl.getSig());
            }

            Bigraph result = (Bigraph) localVarDecl.interpretStart(this);
            return (Bigraph) result;
        }
        if (bigraphDeclaration instanceof LVD2) {
            LVD2 lvd = (LVD2) bigraphDeclaration;
            Signature sigContainer = EcoreUtil2.getContainerOfType(((LocalVarDeclImpl) lvd).getControlType(), Signature.class);
            // check if
            if (Objects.isNull(sigContainer)) {
                throw new SignatureNotMatchException("Signature of inner bigraph declaration does not match with Signature of outer bigraph declaration", lvd);
            }
            assertSignaturesMatch(sigContainer);
            ControlVariable ctrlVar = ((LocalVarDeclImpl) lvd).getControlType();
            Bigraph singleNode = createAtomFrom(ctrlVar);

            if (lvd.getDefinition().size() > 0) {
                if (ctrlVar.getType() == ControlType.ATOMIC) {
                    throw new OperatorInterpreterException(
                            String.format("Control is atomic. Cannot nest further expression under control %s", ctrlVar),
                            lvd
                    );
                }
                try {
                    Bigraph composedBigraph = (Bigraph) ((LocalVarDecl) lvd).interpretStart(new PureBigraphExpressionEvalVisitorImpl(signature));
                    singleNode = nestBigraphUnderSingleNode(composedBigraph, singleNode);
                } catch (IncompatibleInterfaceException | IncompatibleSignatureException e) {
                    throw new BigraphDeclarationInterpreterException(e, lvd);
                }
            }
            return singleNode;
        }

        throw new BigraphDeclarationInterpreterException(
                new UnsupportedOperationException("The bigraph declaration is not supported"),
                bigraphDeclaration);
    }

    @Override
    public Bigraph visit(Plus plus) {
        if (plus.getOperator().equals(BinaryParallelOperator.UNSET)) {
            throw new OperatorInterpreterException(
                    String.format("Invalid Parallel Operator=%s", BinaryParallelOperator.UNSET),
                    plus);
        }

        Bigraph<DefaultDynamicSignature> b1 = (Bigraph<DefaultDynamicSignature>) plus.getLeft().interpret(this);
        Bigraph<DefaultDynamicSignature> b2 = (Bigraph<DefaultDynamicSignature>) plus.getRight().interpret(this);
        try {
            if (plus.getOperator().equals(BinaryParallelOperator.MERGE)) {
                BigraphComposite<DefaultDynamicSignature> result = ops(b1).merge(b2);
                return result.getOuterBigraph();
            }
            if (plus.getOperator().equals(BinaryParallelOperator.PARALLEL)) {
                BigraphComposite bigraphComposite = ops(b1).parallelProduct(b2);
                return (Bigraph) bigraphComposite.getOuterBigraph();
            }
        } catch (IncompatibleSignatureException | IncompatibleInterfaceException e) {
            throw new OperatorInterpreterException(e, plus.getLeft(), plus.getRight());
        }

        throw new OperatorInterpreterException(
                new UnsupportedOperationException("Unknown Parallel Operator"),
                plus);
    }

    @Override
    public Bigraph visit(Multi multi) {
        if (multi.getOperator().equals(BinaryNestingOperator.UNSET2)) {
            throw new OperatorInterpreterException(
                    String.format("Invalid Nesting Operator=%s", BinaryNestingOperator.UNSET2),
                    multi);
        }

        Bigraph b1 = (Bigraph) multi.getLeft().interpret(this);
        Bigraph b2 = (Bigraph) multi.getRight().interpret(this);
        try {
            if (multi.getOperator().equals(BinaryNestingOperator.NESTING)) {
                BigraphComposite nesting = ops(b1).nesting(b2);
                return (Bigraph) nesting.getOuterBigraph();

            }
            if (multi.getOperator().equals(BinaryNestingOperator.COMPOSITION)) {
                BigraphComposite nesting = ops(b1).compose(b2);
                return (Bigraph) nesting.getOuterBigraph();
            }
        } catch (IncompatibleSignatureException | IncompatibleInterfaceException e) {
            throw new OperatorInterpreterException(e, multi.getLeft(), multi.getRight());
        }

        throw new OperatorInterpreterException(
                new UnsupportedOperationException("Unknown Nesting Operator"),
                multi);
    }
}
