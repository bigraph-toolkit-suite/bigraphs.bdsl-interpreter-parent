package org.bigraphs.dsl.interpreter.expressions.main;

import org.bigraphs.framework.converter.PrettyPrinter;
import org.bigraphs.framework.converter.ReactiveSystemPrettyPrinter;
import org.bigraphs.framework.converter.bigmc.BigMcTransformator;
import org.bigraphs.framework.converter.bigrapher.BigrapherTransformator;
import org.bigraphs.framework.converter.ecore.EcoreConverter;
import org.bigraphs.framework.core.Bigraph;
import org.bigraphs.framework.core.BigraphFileModelManagement;
import org.bigraphs.framework.core.factory.BigraphFactory;
import org.bigraphs.framework.core.impl.pure.PureBigraph;
import org.bigraphs.framework.core.impl.pure.PureBigraphBuilder;
import org.bigraphs.framework.core.impl.signature.DefaultDynamicSignature;
import org.bigraphs.framework.core.reactivesystem.*;
import org.bigraphs.dsl.BDSLLib;
import org.bigraphs.dsl.bDSL.*;
import org.bigraphs.dsl.interpreter.BDSLUtil2;
import org.bigraphs.dsl.interpreter.BdslStatementInterpreterResult;
import org.bigraphs.dsl.interpreter.exceptions.BdslIOException;
import org.bigraphs.dsl.interpreter.exceptions.BdslUdfInterpreterException;
import org.bigraphs.dsl.interpreter.exceptions.BigraphDeclarationInterpreterException;
import org.bigraphs.dsl.interpreter.exceptions.BigraphMethodInterpreterException;
import org.bigraphs.dsl.interpreter.expressions.main.functions.*;
import org.bigraphs.dsl.interpreter.expressions.variables.*;
import org.bigraphs.dsl.interpreter.expressions.main.functions.*;
import org.bigraphs.dsl.interpreter.extensions.main.MainBlockVisitableExtension;
import org.bigraphs.dsl.interpreter.extensions.variables.BigraphExpressionVisitableExtension;
import org.bigraphs.dsl.udf.BDSLUserDefinedConsumer;
import org.bigraphs.dsl.udf.BDSLUserDefinedFunction;
import org.bigraphs.dsl.utils.BDSLUtil;
import org.bigraphs.framework.simulation.matching.pure.PureReactiveSystem;
import org.bigraphs.framework.simulation.modelchecking.BigraphModelChecker;
import org.bigraphs.framework.simulation.modelchecking.PureBigraphModelChecker;
import org.bigraphs.framework.visualization.BigraphGraphvizExporter;
import lombok.experimental.ExtensionMethod;
import org.apache.commons.io.FilenameUtils;
import org.bigraphs.dsl.interpreter.expressions.variables.*;
import org.eclipse.emf.common.util.EList;
import org.eclipse.emf.ecore.EPackage;
import org.eclipse.xtext.EcoreUtil2;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintStream;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.nio.file.Paths;
import java.util.LinkedList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

import static org.bigraphs.framework.core.factory.BigraphFactory.pureRandomBuilder;
import static org.bigraphs.dsl.bDSL.ExportFormat.PNG;

/**
 * @author Dominik Grzelak
 */
@ExtensionMethod({MainBlockVisitableExtension.class, BigraphExpressionVisitableExtension.class})
public class MainStatementEvalVisitorImpl extends AbstractMainEvalVisitor<BdslStatementInterpreterResult<Object>, AbstractMainStatements> {
    //TODO rename to methodvisiext/laterexecutable
    public MainStatementEvalVisitorImpl() {
        super();
    }

    PureBigraphExpressionEvalVisitorImpl bigraphExprEvalVisitor = new PureBigraphExpressionEvalVisitorImpl();
    PureReactiveSystemEvalVisitorImpl brsExprEvalVisitor = new PureReactiveSystemEvalVisitorImpl();
    PureReactionRuleEvalVisitorImpl ruleEvalVisitor = new PureReactionRuleEvalVisitorImpl();
    PurePredicateEvalVisitorImpl predicateEvalVisitor = new PurePredicateEvalVisitorImpl();

    public MainStatementEvalVisitorImpl(String filename) throws Exception {
        super(filename);
    }

    @Override
    public BdslStatementInterpreterResult<Object> beginVisit(AbstractMainStatements dslElement) {
        return (BdslStatementInterpreterResult<Object>) dslElement.interpret(this);
    }

    @Override
    public BdslStatementInterpreterResult<Object> visit(BDSLAssignment assignment) {
//        if (AssignableBigraphExpression.class.isAssignableFrom(assignment.getClass())) {
//            return (Optional) ((AssignableBigraphExpression) assignment).interpret(this);
//        }
        try {
            BDSLExpression left = assignment.getLeft();
            BDSLExpression right = assignment.getRight();
            //TODO: change this, make BDSLExpression interpret method and distribute from there not here: BDSLAssignment
            BdslStatementInterpreterResult<Object> bigraph = null;
            if (AssignableBigraphExpression.class.isAssignableFrom(right.getClass())) {
//            bigraph = (Optional) ((AssignableBigraphExpression) right).interpret(this);
                bigraph = (BdslStatementInterpreterResult<Object>) ((AssignableBigraphExpression) right).interpret(this);
            }
            if (right instanceof BDSLAssignment) {
//            bigraph = (Optional) ((BDSLAssignment) right).interpret(this);
                bigraph = (BdslStatementInterpreterResult<Object>) ((BDSLAssignment) right).interpret(this);
            }
            return bigraph;
        } catch (Exception e) {
            e.printStackTrace();
        }
        throw new IllegalStateException();
    }

    @Override
    public BdslStatementInterpreterResult<Object> visit(AssignableBigraphExpression assignmentExpression) {
        if (ReferenceClassSymbol.class.isAssignableFrom(assignmentExpression.getClass())) {
            AbstractNamedSignatureElement type = ((ReferenceClassSymbol) assignmentExpression).getType();
            if (type instanceof BRSDefinition) {
                ReactiveSystem reactiveSystem = (ReactiveSystem) ((BRSDefinition) type).interpretStart(brsExprEvalVisitor);
                return BdslStatementInterpreterResult.create(
                        () -> Optional.ofNullable(reactiveSystem),
                        EcoreUtil2.getContainerOfType(assignmentExpression, AbstractMainStatements.class),
                        true
                );
            }
            if (type instanceof LocalVarDecl) {
                LocalVarDecl bigReference = (LocalVarDecl) type;

                Bigraph agent = (Bigraph<?>) ((LocalVarDecl) bigReference).interpretStart(bigraphExprEvalVisitor);
                BDSLUtil.updateSignatureOfAssignment(assignmentExpression, bigReference.getSig());
                return BdslStatementInterpreterResult.create(
                        () -> Optional.ofNullable(agent),
                        EcoreUtil2.getContainerOfType(assignmentExpression, AbstractMainStatements.class),
                        true
                );
            }
            if (type instanceof LocalRuleDecl) {
                LocalRuleDecl bigReference = (LocalRuleDecl) type;
                ReactionRule result = (ReactionRule) bigReference.interpretStart(ruleEvalVisitor);
                BDSLUtil.updateSignatureOfAssignment(assignmentExpression, bigReference.getSig());
                return BdslStatementInterpreterResult.create(
                        () -> Optional.ofNullable(result),
                        EcoreUtil2.getContainerOfType(assignmentExpression, AbstractMainStatements.class),
                        true
                );
            }
            if (type instanceof LocalPredicateDeclaration) {
                LocalPredicateDeclaration bigReference = (LocalPredicateDeclaration) type;
                ReactiveSystemPredicate result = (ReactiveSystemPredicate) bigReference.interpretStart(predicateEvalVisitor);
                BDSLUtil.updateSignatureOfAssignment(assignmentExpression, bigReference.getSig());
                return BdslStatementInterpreterResult.create(
                        () -> Optional.ofNullable(result),
                        EcoreUtil2.getContainerOfType(assignmentExpression, AbstractMainStatements.class),
                        true
                );
            }
        }

        //TODO: dispatch the following to another visitor call via calling interpret(): AssignableBigraphExpressionWithExplicitSig
        if (assignmentExpression instanceof CreateRandomBigraphMethod) {
            CreateRandomBigraphMethod randomBigraphMethod = ((CreateRandomBigraphMethod) assignmentExpression);
            if (Objects.isNull(randomBigraphMethod.getSig())) {
                throw new BigraphMethodInterpreterException("Signature must not be null", assignmentExpression);
            }
            int numOfNodes = randomBigraphMethod.getN();
            int numOfTrees = randomBigraphMethod.getT();
            float linkingFraction = randomBigraphMethod.getP();
            DefaultDynamicSignature signature = (DefaultDynamicSignature) randomBigraphMethod.getSig()
                    .interpretStart(new SignatureEvalVisitorImpl());
            BDSLUtil.updateSignatureOfAssignment(assignmentExpression, randomBigraphMethod.getSig());
            return BdslStatementInterpreterResult.<Object>create(
                    () -> {

                        PureBigraph bigraph = pureRandomBuilder(signature).generate(numOfTrees, numOfNodes, linkingFraction);
                        return Optional.ofNullable(bigraph);
                    },
                    EcoreUtil2.getContainerOfType(assignmentExpression, AbstractMainStatements.class)
            );
        }

        if (assignmentExpression instanceof LoadMethod) {
            String resourcePath = BDSLUtil.Strings.rawStringOf(((LoadMethod) assignmentExpression).getResourcePath());
            Signature sig = ((LoadMethod) assignmentExpression).getSig();

            // update also signature of LocalVarDecl before returning with the bigraph
            BDSLUtil.updateSignatureOfAssignment(assignmentExpression, sig);
            DefaultDynamicSignature dynamicSignature = (DefaultDynamicSignature) sig.interpretStart(new SignatureEvalVisitorImpl());
            switch (BDSLUtil.Resources.getDataSourceFromIdentifier(resourcePath)) {
                case LOCAL_FILE:
                case CLASSPATH:
                    resourcePath = BDSLUtil2.prepareResourcePath(BDSLUtil.Resources.getDataSourceFromIdentifier(resourcePath), resourcePath);
                    String finalResourcePath = resourcePath;
                    return BdslStatementInterpreterResult.<Object>create(
                            () -> {
                                Bigraph bigraph = loadFromFilesystem(finalResourcePath, ((LoadMethod) assignmentExpression).getFormat(), dynamicSignature);
                                return Optional.ofNullable(bigraph);
                            },
                            EcoreUtil2.getContainerOfType(assignmentExpression, AbstractMainStatements.class)
                    );
                case DB:
                    throw new UnsupportedOperationException("not yet implemented");
                default:
            }
        }
        throw new IllegalStateException();
    }

    @Override
    public BdslStatementInterpreterResult<Object> visit(BDSLReferenceDeclaration assignment) {
        assert assignment.getValue() instanceof AssignableBigraphExpression;
        if (Objects.nonNull(assignment.getTarget())) { //=>AbstractNamedSignatureElement
            BdslStatementInterpreterResult<Object> interpret = (BdslStatementInterpreterResult<Object>)
                    ((AssignableBigraphExpression) assignment.getValue()).interpret(this);
            return interpret;
        }
        throw new IllegalStateException();
    }

    @Override
    public BdslStatementInterpreterResult<Object> visit(BDSLVariableDeclaration2 variableDeclaration) {
        BDSLExpression value = variableDeclaration.getValue();
        AbstractNamedSignatureElement variable = variableDeclaration.getVariable();
        // the following will interpret the right side of the declaration
        if (Objects.nonNull(variableDeclaration.getValue()) && Objects.nonNull(variable)) {

            if (BDSLUtil.bdslExpressionIsAssignableBigraphMethodExpression(value)) {
                BdslStatementInterpreterResult<Object> interpret = (BdslStatementInterpreterResult<Object>)
                        ((AbstractMainStatements) value).interpret(this);
                return interpret;
            }

            if (BDSLUtil.bdslExpressionIsBigraphDefinition(value)) {
                LocalVarDecl varDecl = variable instanceof LocalVarDecl ? (LocalVarDecl) variable : ((BigraphVarReference) variable).getValue();
                Bigraph result = (Bigraph) varDecl.interpretStart(bigraphExprEvalVisitor);
                return BdslStatementInterpreterResult.create(
                        () -> Optional.ofNullable(result),
                        variableDeclaration,
                        true
                );
            }

            if (BDSLUtil.bdslExpressionIsPredicateDefinition(value)) {
                LocalPredicateDeclaration varDecl = variable instanceof LocalPredicateDeclaration ? (LocalPredicateDeclaration) variable : ((PredicateVarReference) variable).getValue();
                ReactiveSystemPredicate result = (ReactiveSystemPredicate) varDecl.interpretStart(predicateEvalVisitor);
                return BdslStatementInterpreterResult.create(
                        () -> Optional.ofNullable(result),
                        variableDeclaration,
                        true
                );
            }

            if (BDSLUtil.bdslExpressionIsRuleDefinition(value)) {
                LocalRuleDecl varDecl = variable instanceof LocalRuleDecl ? (LocalRuleDecl) variable : ((RuleVarReference) variable).getValue();
                ReactionRule result = (ReactionRule) varDecl.interpretStart(ruleEvalVisitor);
                return BdslStatementInterpreterResult.create(
                        () -> Optional.ofNullable(result),
                        variableDeclaration,
                        true
                );
            }

            if (BDSLUtil.bdslExpressionIsBRSDefinition(value)) {
                BRSDefinition brsDefinition = variable instanceof BRSDefinition ? (BRSDefinition) variable : ((BRSVarReference) variable).getValue();
                ReactiveSystem reactiveSystem = (ReactiveSystem) brsDefinition.interpretStart(brsExprEvalVisitor);
                return BdslStatementInterpreterResult.create(
                        () -> Optional.ofNullable(reactiveSystem),
                        variableDeclaration,
                        true
                );
            }

            if (AssignableBigraphExpression.class.isAssignableFrom(value.getClass())) {
                BdslStatementInterpreterResult<Object> interpret = (BdslStatementInterpreterResult<Object>) ((AssignableBigraphExpression) variableDeclaration.getValue())
                        .interpret(this);
                return interpret;
            }
        }
        throw new IllegalStateException();
    }


    private Bigraph loadFromFilesystem(String resourcePath, LoadFormat loadFormat, DefaultDynamicSignature dynamicSignature) {
        //TODO: extract metamodel from arguments in XMI file.... means that we have to parse it first...
        String metaModelFilePath = BDSLUtil2.inferMetaModelResourcePath(resourcePath);
        try {
            switch (loadFormat) {
                case ECORE:
                    BigraphFileModelManagement.Load.bigraphMetaModel(resourcePath);
                    break;
                case XMI:
                    if (metaModelFilePath.isEmpty()) {
                        EPackage orGetBigraphMetaModel = BigraphFactory.createOrGetBigraphMetaModel(dynamicSignature);
                        PureBigraphBuilder<DefaultDynamicSignature> builder =
                                PureBigraphBuilder.create(dynamicSignature, orGetBigraphMetaModel, resourcePath);
                        return builder.createBigraph();
                    } else {
                        PureBigraphBuilder<DefaultDynamicSignature> builder =
                                PureBigraphBuilder.create(dynamicSignature, metaModelFilePath, resourcePath);
                        return builder.createBigraph();
                    }
            }
        } catch (IOException e) {
            throw new BdslIOException(e);
        }
        return null;
    }

    @Override
    public BdslStatementInterpreterResult<Object> visit(ExecuteBRSMethod executeBRSMethod) {
        super.visit(executeBRSMethod);
        BRSDefinition value = executeBRSMethod.getBrs().getValue();

        ReactiveSystem reactiveSystem = (ReactiveSystem) value.interpretStart(brsExprEvalVisitor);
        if (Objects.isNull(reactiveSystem))
            throw new BigraphDeclarationInterpreterException(new NullPointerException("BRS variable definition couldn't be interpreted"), executeBRSMethod.getBrs());

        PureBigraphModelChecker modelChecker = new PureBigraphModelChecker(
                (ReactiveSystem<PureBigraph>) reactiveSystem,
                BigraphModelChecker.SimulationStrategy.Type.BFS,
                opts);

        // attach predicate listener
        BDSLDocument bdslDoc = (BDSLDocument) EcoreUtil2.getRootContainer(executeBRSMethod);
        DefaultPureBigraphReactiveSysstemListener reactiveSystemListener = initReactiveSystemListener(
                value,
                bdslDoc
        );
        modelChecker.setReactiveSystemListener(reactiveSystemListener);
        return BdslStatementInterpreterResult.create(
                () -> {
                    modelChecker.execute();
                    return Optional.of(modelChecker);
                },
                executeBRSMethod
        );
    }

    private DefaultPureBigraphReactiveSysstemListener initReactiveSystemListener(BRSDefinition value,
                                                                                 BDSLDocument bdslDoc) {
        BDSLVariableDeclaration2 variableDeclaration = EcoreUtil2.getContainerOfType(value, BDSLVariableDeclaration2.class);
        BDSLExpression brsExpression = variableDeclaration.getValue();

        DefaultPureBigraphReactiveSysstemListener reactiveSystemListener = new DefaultPureBigraphReactiveSysstemListener();
        if (brsExpression.getPredicates().size() >= 1) {
            List<PredicateMatchCallback> allPredicateMatchCallbacksFor = BDSLUtil.getAllPredicateMatchCallbacksFor(value, bdslDoc);
            if (brsExpression.getPredicates().size() == 1) {
                OnAllPredicateMatch<PureBigraph> onAllPredicateMatch = (currentAgent, label) -> {
                    allPredicateMatchCallbacksFor.forEach(MainStatementEvalVisitorImpl::processBRSCallbacks);
                };
                reactiveSystemListener.setOnAllPredicateMatch(onAllPredicateMatch);
            } else {
                OnPredicateMatch<PureBigraph> onPredicateMatch = (currentAgent, predicate) -> {
                    allPredicateMatchCallbacksFor.forEach(x -> {
                        //filter by predicate
                        PurePredicateEvalVisitorImpl evalVisitor = new PurePredicateEvalVisitorImpl();
                        for (PredicateVarReference eachPredVarRef : x.getParams()) {
                            ReactiveSystemPredicate<PureBigraph> predicatesInterpreted =
                                    (ReactiveSystemPredicate<PureBigraph>) eachPredVarRef.getValue().interpretStart(evalVisitor);
                            if (predicatesInterpreted.equals(predicate)) {
                                processBRSCallbacks(x);
                            }
                        }
                    });
                };
                reactiveSystemListener.setOnPredicateMatch(onPredicateMatch);
            }
        }
        List<RuleMatchCallback> allRuleMatchCallbacksFor = BDSLUtil.getAllRuleMatchCallbacksFor(value, bdslDoc);
        if (allRuleMatchCallbacksFor.size() > 0) {
            PureReactionRuleEvalVisitorImpl evalVisitor = new PureReactionRuleEvalVisitorImpl();
            OnRuleMatch<PureBigraph> onRuleMatch =
                    (PureBigraph agent, ReactionRule<PureBigraph> reactionRule, BigraphMatch<PureBigraph> matchResult) ->
                            allRuleMatchCallbacksFor.forEach(x -> {
                                //filter by reaction rule first before calling callback method
                                for (RuleVarReference eachRuleVarRef : x.getParams()) {
                                    ParametricReactionRule<PureBigraph> ruleInterpreted =
                                            (ParametricReactionRule<PureBigraph>) eachRuleVarRef.getValue().interpretStart(evalVisitor);
                                    if (ruleInterpreted.equals(reactionRule)) {
                                        processBRSCallbacks(x);
                                    }
                                }
                            });
            reactiveSystemListener.setOnRuleMatch(onRuleMatch);
        }
        List<BRSStartedCallback> reactiveSystemstartedCallbackFor = BDSLUtil.getReactiveSystemStartedCallbackFor(value, bdslDoc);
        if (reactiveSystemstartedCallbackFor.size() > 0) {
            OnReactiveSystemStarted<PureBigraph> onReactiveSystemStarted = () -> reactiveSystemstartedCallbackFor
                    .forEach(MainStatementEvalVisitorImpl::processBRSCallbacks);
            reactiveSystemListener.setOnReactiveSystemStarted(onReactiveSystemStarted);
        }
        List<BRSFinishedCallback> reactiveSystemFinishedCallbackFor = BDSLUtil.getReactiveSystemFinishedCallbackFor(value, bdslDoc);
        if (reactiveSystemFinishedCallbackFor.size() > 0) {
            OnReactiveSystemFinished<PureBigraph> onReactiveSystemFinished = () -> reactiveSystemFinishedCallbackFor.forEach(MainStatementEvalVisitorImpl::processBRSCallbacks);
            reactiveSystemListener.setOnReactiveSystemFinished(onReactiveSystemFinished);
        }

        return reactiveSystemListener;
    }

    //TODO: evaluate whether these shall be called async or sync, Default is sync due to the RSListener of MChecker
    private static void processBRSCallbacks(BdslCallbackMethods bdslCallbackMethods) {
        EList<CallbackStatements> statements = bdslCallbackMethods.getStatements();
        MainStatementEvalVisitorImpl mainStatementEvalVisitor = new MainStatementEvalVisitorImpl();
        for (CallbackStatements stmt : statements) {
            if (stmt instanceof AbstractMainStatements) {
                try {
                    BdslStatementInterpreterResult<Object> result = (BdslStatementInterpreterResult<Object>) ((AbstractMainStatements) stmt).interpret(mainStatementEvalVisitor);
                    Optional<Object> call = result.getBdslExecutableStatement().call();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
    }

    @Override
    public BdslStatementInterpreterResult<Object> visit(ExportMethod exportMethod) {
        super.visit(exportMethod);
        ReferenceClassSymbol referenceClassSymbol = exportMethod.getVariable();
        try {

            PrettyPrinter printer;
            switch (exportMethod.getFormat()) {
                case BIGMC:
                    printer = new BigMcTransformator();
                    break;
                case BIGRAPHER:
                    printer = new BigrapherTransformator();
                    break;
                case ECORE:
                    printer = new EcoreConverter().withExportFormat(BigraphFileModelManagement.Format.ECORE);
                    break;
                case PNG:
                    printer = null;
                    break;
                case XMI:
                default:
                    printer = new EcoreConverter().withExportFormat(BigraphFileModelManagement.Format.XMI);
                    break;
            }
            PrintStream printStream;
            DataSink dataSinkFromIdentifier = BDSLUtil.Resources.getDataSinkFromIdentifier(BDSLUtil.Strings.rawStringOf(exportMethod.getResourcePath()));
            File exportFile;
            switch (dataSinkFromIdentifier) {
                case LOCAL_FILE:
                    exportFile = getExportFile(exportMethod);
                    printStream = new PrintStream(new FileOutputStream(exportFile), false);
                    break;
                case STDOUT:
                default:
                    exportFile = null;
                    printStream = new PrintStream(System.out);
                    break;
            }

            if (referenceClassSymbol.getType() instanceof BRSDefinition) {
                BRSDefinition value = (BRSDefinition) referenceClassSymbol.getType();
                ReactiveSystem<?> reactiveSystem = (ReactiveSystem<?>) value.interpretStart(brsExprEvalVisitor);
                if (Objects.isNull(reactiveSystem))
                    throw new BigraphDeclarationInterpreterException(new NullPointerException("BRS variable definition couldn't be interpreted"), referenceClassSymbol);
                if (exportMethod.getFormat() == ExportFormat.PNG) {
                    throw new UnsupportedOperationException("PNG export is not available for BRS currently.");
                }
                return BdslStatementInterpreterResult.create(
                        () -> {
                            ReactiveSystemPrettyPrinter systemPrettyPrinter = ((ReactiveSystemPrettyPrinter<?, ?>) printer);
                            systemPrettyPrinter.toOutputStream(reactiveSystem, printStream);
                            return Optional.of(reactiveSystem);
                        },
                        exportMethod
                );
            }
            if (referenceClassSymbol.getType() instanceof LocalVarDecl || referenceClassSymbol.getType() instanceof LocalPredicateDeclaration) {
                Bigraph<?> bigraph;
                if (referenceClassSymbol.getType() instanceof LocalPredicateDeclaration) {
                    LocalPredicateDeclaration predicateDeclaration = (LocalPredicateDeclaration) referenceClassSymbol.getType();
                    ReactiveSystemPredicate<?> predicates = (ReactiveSystemPredicate<?>) predicateDeclaration.interpretStart(predicateEvalVisitor);
                    bigraph = predicates.getBigraph();
                } else {
                    LocalVarDecl value = (LocalVarDecl) referenceClassSymbol.getType();
                    bigraph = (Bigraph<?>) value.interpretStart(bigraphExprEvalVisitor);
                }
                if (exportMethod.getFormat() == PNG) {
                    if (dataSinkFromIdentifier == DataSink.STDOUT) {
                        throw new IllegalStateException("No resource path specified for exporting the variable as graphics file.");
                    }
                    return BdslStatementInterpreterResult.create(
                            () -> {
                                BigraphGraphvizExporter.toPNG(bigraph, true, exportFile);
                                return Optional.of(bigraph);
                            },
                            exportMethod
                    );
                } else {
                    return BdslStatementInterpreterResult.create(
                            () -> {
                                ReactiveSystemPrettyPrinter systemPrettyPrinter = ((ReactiveSystemPrettyPrinter<?, ?>) printer);
                                PureReactiveSystem tmp = new PureReactiveSystem();
                                tmp.setAgent((PureBigraph) bigraph);
                                systemPrettyPrinter.toOutputStream(tmp, printStream);
                                return Optional.of(bigraph);
                            },
                            exportMethod
                    );
                }
            }

            throw new IllegalStateException("Undefined type of variable reference in export() method call found.");
        } catch (Exception e) {
            throw new BigraphMethodInterpreterException(e, exportMethod);
        }
    }

    private File getExportFile(ExportMethod exportMethod) throws IOException {
        String resourcePath = exportMethod.getResourcePath();
        if (Objects.nonNull(resourcePath)) {
            String resId = BDSLUtil.Resources.getDataSinkFromIdentifier(resourcePath).getLiteral();
            resourcePath = resourcePath.substring((resId + ":").length());
        }
        File f = Paths.get(getBasePath(), BDSLUtil.Strings.rawStringOf(resourcePath)).toFile();
        String filename = FilenameUtils.getName(f.getCanonicalPath());
        if (!f.isAbsolute()) {
            if (filename.isEmpty()) {
                throw new BigraphMethodInterpreterException("No filename specified to export", exportMethod);
            }
            f = new File(FilenameUtils.getFullPath(f.getCanonicalPath()));
        }
        if (!f.exists()) {
            f.mkdirs();
        }
        return Paths.get(f.getCanonicalPath(), filename).toFile();
    }

    @Override
    public BdslStatementInterpreterResult<Object> visit(UDFOperation udfOperation) {
        final List<BdslStatementInterpreterResult<Object>> expressionResults = new LinkedList<>();
        for (XExpression eachExpression : udfOperation.getExpression()) {
            if (eachExpression instanceof UdfCallExpression) {
                BdslStatementInterpreterResult<Object> result = (BdslStatementInterpreterResult<Object>) ((UdfCallExpression) eachExpression).interpret(this);
                expressionResults.add(result);
            }
        }
        return BdslStatementInterpreterResult.create(
                () -> {
                    List<Optional<Object>> returnValues = new LinkedList<>();
                    for (BdslStatementInterpreterResult<Object> each : expressionResults) {
                        Optional<Object> callResult = each.getBdslExecutableStatement().call();
                        returnValues.add(callResult);
                    }
                    return Optional.of(returnValues);
                },
                udfOperation
        );
    }

    @Override
    public BdslStatementInterpreterResult<Object> visit(UdfCallExpression udfCallExpression) {
        try {
            String qualifiedName = udfCallExpression.getType().getQualifiedName();
//            List<File> loadedJarFiles = bdslLib.getLoadedJarFiles();
            Class<?> aClass = Class.forName(qualifiedName, false, BDSLLib.DCL);
            if (BDSLUserDefinedConsumer.class.isAssignableFrom(aClass)) {
                Constructor<?>[] constructors = aClass.getConstructors();
                Constructor<BDSLUserDefinedConsumer<?>> constructor = (Constructor<BDSLUserDefinedConsumer<?>>) constructors[0];
                BDSLUserDefinedConsumer<?> bdslUserDefinedConsumer = constructor.newInstance();
                return BdslStatementInterpreterResult.create(
                        () -> {
                            bdslUserDefinedConsumer.accept(null);
                            return Optional.of(bdslUserDefinedConsumer);
                        },
                        (UDFOperation) udfCallExpression.eContainer()
                );
            } else if (BDSLUserDefinedFunction.class.isAssignableFrom(aClass)) {
                UDFArgumentTypes firstParam = null;
                if (udfCallExpression.getParams() != null && udfCallExpression.getParams().size() >= 1) {
                    firstParam = udfCallExpression.getParams().get(0);
                }
                Constructor<?>[] constructors = aClass.getConstructors();
                Constructor<BDSLUserDefinedFunction> constructor = (Constructor<BDSLUserDefinedFunction>) constructors[0];
                BDSLUserDefinedFunction bdslUserDefinedFunction = constructor.newInstance();
                UDFArgumentTypes finalFirstParam = firstParam;
                return BdslStatementInterpreterResult.create(
                        () -> {
                            bdslUserDefinedFunction.apply(finalFirstParam);
                            return Optional.of(bdslUserDefinedFunction);
                        },
                        (UDFOperation) udfCallExpression.eContainer()
                );
            }
            throw new ClassNotFoundException(aClass + " is not a valid UDF function.");
        } catch (ClassNotFoundException | InstantiationException | IllegalAccessException |
                 InvocationTargetException e) {
            throw new BdslUdfInterpreterException(e);
        }
    }

    @Override
    public BdslStatementInterpreterResult<Object> visit(PrintLn printLn) {
        PrintableExpression expression = printLn.getText();
        return BdslStatementInterpreterResult.create(
                () -> {
                    getPrintStrategy().execute(expression);
                    return Optional.ofNullable(expression);
                },
                printLn
        );
    }
}
