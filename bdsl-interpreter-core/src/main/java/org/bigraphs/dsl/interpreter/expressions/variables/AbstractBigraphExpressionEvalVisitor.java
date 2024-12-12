package org.bigraphs.dsl.interpreter.expressions.variables;

import org.bigraphs.framework.core.Bigraph;
import org.bigraphs.framework.core.BigraphComposite;
import org.bigraphs.framework.core.ControlStatus;
import org.bigraphs.framework.core.Signature;
import org.bigraphs.framework.core.datatypes.EMetaModelData;
import org.bigraphs.framework.core.datatypes.StringTypedName;
import org.bigraphs.framework.core.exceptions.IncompatibleSignatureException;
import org.bigraphs.framework.core.exceptions.InvalidConnectionException;
import org.bigraphs.framework.core.exceptions.builder.TypeNotExistsException;
import org.bigraphs.framework.core.exceptions.operations.IncompatibleInterfaceException;
import org.bigraphs.framework.core.factory.BigraphFactory;
import org.bigraphs.framework.core.impl.BigraphEntity;
import org.bigraphs.framework.core.impl.elementary.Linkings;
import org.bigraphs.framework.core.impl.elementary.Placings;
import org.bigraphs.framework.core.impl.pure.PureBigraphBuilder;
import org.bigraphs.framework.core.impl.signature.DefaultDynamicControl;
import org.bigraphs.framework.core.impl.signature.DefaultDynamicSignature;
import org.bigraphs.dsl.bDSL.*;
import org.bigraphs.dsl.interpreter.AbstractBRSModelVisitor;
import org.bigraphs.dsl.interpreter.BDSLUtil2;
import org.bigraphs.dsl.interpreter.exceptions.BdslInterpretationException;
import org.bigraphs.dsl.interpreter.exceptions.BigraphDeclarationInterpreterException;
import org.bigraphs.dsl.interpreter.exceptions.OperatorInterpreterException;
import org.bigraphs.dsl.interpreter.exceptions.SignatureNotMatchException;
import org.bigraphs.dsl.interpreter.expressions.BdslMagicComments;
import org.bigraphs.dsl.interpreter.extensions.variables.BigraphExpressionVisitableExtension;
import org.bigraphs.dsl.utils.BDSLUtil;
import lombok.experimental.ExtensionMethod;
import org.eclipse.emf.common.util.EList;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.xtext.EcoreUtil2;

import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import static com.google.common.base.Preconditions.checkNotNull;
import static org.bigraphs.framework.core.factory.BigraphFactory.*;
import static org.bigraphs.dsl.utils.BDSLUtil.Strings.rawStringOf;

/**
 * This abstract class is the base class for all bigraph-related expression visitors in order to
 * evaluate bigraph-related expressions.
 * It is used for evaluating {@link LocalVarDecl} definitions.
 * <p>
 * This is a double dispatch mechanism. Only the expression knows which method must be called.
 *
 * @author Dominik Grzelak
 */
@ExtensionMethod({BigraphExpressionVisitableExtension.class}) //extends Bigraph & ReactionRule
public abstract class AbstractBigraphExpressionEvalVisitor<ReturnType, ParamType extends EObject> extends AbstractBRSModelVisitor<ReturnType, ParamType> {

    //    private final PureBigraphFactory bFactory = pure();
    protected EMetaModelData metaModelData = EMetaModelData.builder()
            .setNsPrefix("bigraphMetaModel")
            .setName("default")
            .setNsUri("de.tud.inf.st.bigraphs")
            .create();
    @Deprecated
    protected final Stack<Object> expressionStack = new Stack<>();
    protected DefaultDynamicSignature signature;
    EObject observedObject;

    public AbstractBigraphExpressionEvalVisitor() {
        super();
    }

    public AbstractBigraphExpressionEvalVisitor(DefaultDynamicSignature signature) {
        super();
        checkNotNull(signature, "Signature must not be null");
        this.signature = signature;
    }

    public DefaultDynamicSignature getSignature() {
        return signature;
    }

    @Override
    public AbstractBRSModelVisitor<ReturnType, ParamType> withMagicComments(List<BdslMagicComments.Comment> comments) {
        super.withMagicComments(comments);
        EMetaModelData.MetaModelDataBuilder builder = EMetaModelData.builder();
        BdslMagicComments.getValue(magicComments(), BdslMagicComments.ModelData.MODEL_NAME).ifPresent(c -> builder.setName(c.VALUE));
        BdslMagicComments.getValue(magicComments(), BdslMagicComments.ModelData.NS_PREFIX).ifPresent(c -> builder.setNsPrefix(c.VALUE));
        BdslMagicComments.getValue(magicComments(), BdslMagicComments.ModelData.NS_URI).ifPresent(c -> builder.setNsUri(c.VALUE));
        this.metaModelData = builder.create();
        return this;
    }

    protected void setObservedObject(EObject observedObject) {
        this.observedObject = observedObject;
        if (observedObject instanceof AbstractNamedSignatureElement) {
            resolveSignatureIfNotSet((AbstractNamedSignatureElement) observedObject);
        }
    }

    protected void resolveSignatureIfNotSet(AbstractNamedSignatureElement element) {
        if (this.signature == null) {
            Optional.ofNullable(BDSLUtil.tryInferSignature(element))
                    .ifPresent(sigContainer -> {
                        this.signature = (DefaultDynamicSignature) sigContainer.interpretStart(new SignatureEvalVisitorImpl());
                    });
        }
        if (this.signature == null) {
            org.bigraphs.dsl.bDSL.Signature sigInferred = BDSLUtil2.createEmptySignatureElement();
            this.signature = new SignatureEvalVisitorImpl().beginVisit(sigInferred);
        }
    }

    protected void resolveSignatureIfNotSet(AbstractBigraphDeclaration element) {
        if (this.signature == null) {
            Optional.ofNullable(EcoreUtil2.getContainerOfType(element, BDSLVariableDeclaration2.class))
                    .ifPresent(bdslVarDecl -> {
                        if (Objects.nonNull(bdslVarDecl.getVariable())) {
                            resolveSignatureIfNotSet(bdslVarDecl.getVariable());
                        }
                    });
        }
    }

    protected Optional<Bigraph> computeBigraphFromExpressionList(List<BigraphExpression> localVarDeclDefinitions, PureBigraphExpressionEvalVisitorImpl visitor) {
        Optional<Bigraph> reduce = IntStream.range(0, localVarDeclDefinitions.size())
                .mapToObj(
                        ix -> (Bigraph) localVarDeclDefinitions.get(ix)
                                .interpret(visitor)
                )
                .reduce((prev, curr) -> {
                    try {
                        return ops(prev).parallelProduct(curr).getOuterBigraph();
                    } catch (IncompatibleSignatureException | IncompatibleInterfaceException e) {
                        throw new OperatorInterpreterException(e, localVarDeclDefinitions.toArray(new EObject[0]));
                    }
                });
        return reduce;
    }

    protected PureBigraphBuilder<DefaultDynamicSignature> createBigraphBuilder() {
        createOrGetBigraphMetaModel(signature, metaModelData);
        return pureBuilder(signature);
    }

    protected Placings<DefaultDynamicSignature> createPlacingsBuilder() {
        createOrGetBigraphMetaModel(signature, metaModelData);
        return purePlacings(signature);
    }

    protected Linkings<DefaultDynamicSignature> createLinkingssBuilder() {
        createOrGetBigraphMetaModel(signature, metaModelData);
        return pureLinkings(signature);
    }

//    public void beginVisit(BDSLVariableDeclaration2 variableDeclaration) throws BdslInterpretationException {
//        AbstractNamedSignatureElement variable = variableDeclaration.getVariable(); //LHS
//        BDSLExpression value = variableDeclaration.getValue(); //RHS
//        if (Objects.nonNull(variableDeclaration.getValue()) && Objects.nonNull(variable)) {
//
//        }
//        throw new IllegalStateException();
//    }

    @Override
    public abstract ReturnType beginVisit(ParamType bigraphVariable) throws BdslInterpretationException;

    public abstract Bigraph<? extends Signature<?>> visit(AbstractBigraphDeclaration bigraphDeclaration);

    public abstract Bigraph<? extends Signature<?>> visit(ElementaryBigraphs elementaryBigraph);

    public abstract Bigraph<? extends Signature<?>> visit(Plus plus);

    public abstract Bigraph<? extends Signature<?>> visit(Multi multi);

    protected Bigraph createAtomFrom(ControlVariable ctrlVar) {
        return createAtomFrom(ctrlVar, ctrlVar.getType() != ControlType.ATOMIC, Collections.emptyList());
    }

    protected Bigraph createAtomFrom(ControlVariable ctrlVar, List<NameConstant> names) {
        return createAtomFrom(ctrlVar, ctrlVar.getType() != ControlType.ATOMIC, names);
    }

    protected Bigraph<DefaultDynamicSignature> createElementaryBigraph(ElementaryBigraphs elementaryBigraph) {
        if (elementaryBigraph instanceof Barren) {
            return createPlacingsBuilder().barren();
        }
        if (elementaryBigraph instanceof Join) {
            return createPlacingsBuilder().join();
        }
        if (elementaryBigraph instanceof Site) { // is rather an identity
            Site site = (Site) elementaryBigraph;
            int count = site.getIndex();
            if (count <= 0) {
                throw new BigraphDeclarationInterpreterException(
                        new IllegalArgumentException("Argument of site id(x) must be positive."),
                        elementaryBigraph);
            }
            return createSites(count, true);
        }
        if (elementaryBigraph instanceof Merge) {
            return createPlacingsBuilder().merge(((Merge) elementaryBigraph).getSites());
        }
        if (elementaryBigraph instanceof Closure) {
            EList<String> value = ((Closure) elementaryBigraph).getValue();
            return createLinkingssBuilder().closure(value.stream().map(x -> rawStringOf(x)).map(x -> StringTypedName.of(x)).collect(Collectors.toSet()));
        }
        if (elementaryBigraph instanceof Substitution) {
            Substitution substitution = (Substitution) elementaryBigraph;
            StringTypedName from = StringTypedName.of(rawStringOf(substitution.getFrom()));
            StringTypedName[] objects = substitution.getTo().stream().map(x -> StringTypedName.of(rawStringOf(x)))
                    .distinct().toArray(StringTypedName[]::new);
            return createLinkingssBuilder().substitution(from, objects);
        }
        throw new UnsupportedOperationException("elementary bigraph not supported yet=" + elementaryBigraph);
    }

    protected Bigraph createSites(int numberOfSites, boolean parallel) {
        PureBigraphBuilder<DefaultDynamicSignature> bigraphBuilder = createBigraphBuilder();
        if (parallel) {
            for (int i = 0; i < numberOfSites; i++) {
                PureBigraphBuilder<DefaultDynamicSignature>.Hierarchy root = bigraphBuilder.createRoot();
                root.addSite();
            }
            return bigraphBuilder.createBigraph();
        } else {
            PureBigraphBuilder<DefaultDynamicSignature>.Hierarchy root = bigraphBuilder.createRoot();
            for (int i = 0; i < numberOfSites; i++) {
                root.addSite();
            }
            return root.createBigraph();
        }
    }

    protected Bigraph createAtomFrom(ControlVariable ctrlVar, boolean withSite, List<NameConstant> names) {
        DefaultDynamicControl control = signature.getControl(ctrlVar.getName(), ctrlVar.getArity(),
                ControlStatus.fromString(ctrlVar.getType().getName()));
        if (Objects.nonNull(control)) {
            PureBigraphBuilder<DefaultDynamicSignature> builder = createBigraphBuilder();
            PureBigraphBuilder<DefaultDynamicSignature>.Hierarchy hierarchy = builder.createRoot()
                    .addChild(control);
            if (!names.isEmpty()) {
                for (NameConstant eachName : names) {
//                    assert eachName instanceof NameConstant;
                    String value = rawStringOf(eachName.getValue());
                    BigraphEntity.OuterName outerName = builder.createOuterName(value);
                    try {
                        hierarchy = hierarchy.linkToOuter(outerName);
                    } catch (TypeNotExistsException | InvalidConnectionException e) {
                        throw new BigraphDeclarationInterpreterException(
                                e,
                                Objects.nonNull(observedObject) ? observedObject : ctrlVar);
                    }
                }
            }
            if (withSite) {
                hierarchy.down().addSite();
            }
            Bigraph bigraph = hierarchy.createBigraph();
            return bigraph;
        }
        return null;
    }

    /**
     * Same as nestBigraphUnderSingleNode, except the the outer bigraph will be created first based on the provided
     * {@code controlVariable}.
     *
     * @param b               the bigraph to nest under the controlVariable
     * @param controlVariable the control of the bigraph to create
     * @return a new bigraph
     * @throws IncompatibleSignatureException if an error happes
     * @throws IncompatibleInterfaceException if an error happes
     */
    protected Bigraph nestBigraphUnderControl(Bigraph b, ControlVariable controlVariable) throws IncompatibleSignatureException, IncompatibleInterfaceException {
        Bigraph singleNode = createAtomFrom(controlVariable);
        return nestBigraphUnderSingleNode(b, singleNode);
    }

    protected Bigraph nestBigraphUnderSingleNode(Bigraph b, Bigraph singleNode) throws IncompatibleSignatureException, IncompatibleInterfaceException {
        Placings<DefaultDynamicSignature>.Merge merge = createPlacingsBuilder().merge(b.getRoots().size());
//        BigraphComposite<DefaultDynamicSignature> x = BigraphFactory.ops(merge).compose(b);
//        BigraphComposite<DefaultDynamicSignature> y = BigraphFactory.ops(singleNode).compose(x);
        BigraphComposite<DefaultDynamicSignature> x = BigraphFactory.ops(merge).nesting(b);
        BigraphComposite<DefaultDynamicSignature> y = BigraphFactory.ops(singleNode).nesting(x);
        return y.getOuterBigraph();
    }

    protected void assertSignaturesMatch(org.bigraphs.dsl.bDSL.Signature signature) {
        DefaultDynamicSignature cachedOrEval = (DefaultDynamicSignature) signature.interpretStart(new SignatureEvalVisitorImpl());
        if (Objects.nonNull(this.signature) && !cachedOrEval.equals(this.signature)) {
            throw new SignatureNotMatchException("Signatures do not match.", signature);
        }
    }
}
