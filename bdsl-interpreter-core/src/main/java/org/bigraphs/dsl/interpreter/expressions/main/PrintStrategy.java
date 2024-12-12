package org.bigraphs.dsl.interpreter.expressions.main;

import org.bigraphs.framework.core.Bigraph;
import org.bigraphs.framework.core.BigraphFileModelManagement;
import org.bigraphs.framework.core.EcoreBigraph;
import org.bigraphs.framework.core.impl.signature.DefaultDynamicSignature;
import org.bigraphs.dsl.bDSL.*;
import org.bigraphs.dsl.interpreter.AbstractBRSModelVisitor;
import org.bigraphs.dsl.interpreter.exceptions.BdslIOException;
import org.bigraphs.dsl.interpreter.exceptions.BigraphMethodInterpreterException;
import org.bigraphs.dsl.interpreter.expressions.BdslMagicComments;
import org.bigraphs.dsl.interpreter.expressions.variables.PureBigraphExpressionEvalVisitorImpl;
import org.bigraphs.dsl.interpreter.expressions.variables.SignatureEvalVisitorImpl;
import org.bigraphs.dsl.interpreter.extensions.variables.BigraphExpressionVisitableExtension;
import lombok.experimental.ExtensionMethod;
import org.bigraphs.dsl.interpreter.BRSModelVisitor;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintStream;
import java.util.Objects;
import java.util.Optional;

import static org.bigraphs.dsl.utils.BDSLUtil.Strings.rawStringOf;

/**
 * Strategy class with implementations that handles the outputting of BDSL elements, may they be bigraphs, strings, etc.
 * <p>
 * The strategy is called by a {@link BRSModelVisitor}, for example,
 * {@link AbstractMainEvalVisitor} uses it.
 * <p>
 * It allows easy configuration and adaption of new outputting strategies.
 *
 * @author Dominik Grzelak
 */
@ExtensionMethod({BigraphExpressionVisitableExtension.class})
public abstract class PrintStrategy {
    protected PrintStream printStream;
    protected AbstractBRSModelVisitor<?, ?> brsModelVisitor;

    public PrintStrategy(AbstractBRSModelVisitor<?, ?> brsModelVisitor) {
        this.brsModelVisitor = brsModelVisitor;
    }

    protected abstract void initPrintStream() throws BdslIOException;

    public void execute(PrintableExpression printableExpression) {
        if (printableExpression instanceof StringLiteral) {
            printStream.println(rawStringOf(((StringLiteral) printableExpression).getValue()));
        } else if (printableExpression instanceof BigraphVarReference) {
            BigraphVarReference bigraphVarReference = (BigraphVarReference) printableExpression;
            if (Objects.nonNull(bigraphVarReference.getValue())) {
                LocalVarDecl localVarDecl = bigraphVarReference.getValue();
                assert localVarDecl.getSig() != null;
                DefaultDynamicSignature signature = (DefaultDynamicSignature) localVarDecl.getSig().interpretStart(new SignatureEvalVisitorImpl());
                Bigraph<?> result = (Bigraph) brsModelVisitor.getLocalVarCache()
                        .getObject(localVarDecl).orElseGet(() -> (Bigraph) localVarDecl.interpretStart(new PureBigraphExpressionEvalVisitorImpl(signature)));
                if (!(result instanceof EcoreBigraph)) {
                    throw new BigraphMethodInterpreterException(
                            String.format("Bigraph can not be outputted because it is not of type %s", EcoreBigraph.class.getCanonicalName()),
                            bigraphVarReference
                    );
                }
                try {
                    PrintLn printLn = (PrintLn) printableExpression.eContainer();
                    switch (printLn.getMode()) {
                        case META_MODEL:
                            BigraphFileModelManagement.Store.exportAsMetaModel((EcoreBigraph) result, printStream);
                            break;
                        default:
                        case INSTANCE_MODEL:
                            Optional<BdslMagicComments.Comment> value = BdslMagicComments.getValue(brsModelVisitor.magicComments(), BdslMagicComments.Export.SCHEMA_LOCATION);
                            if (value.isPresent()) {
                                BigraphFileModelManagement.Store.exportAsInstanceModel((EcoreBigraph) result, printStream, value.get().VALUE);
                            } else {
                                BigraphFileModelManagement.Store.exportAsInstanceModel((EcoreBigraph) result, printStream);
                            }
                            break;
                    }
                } catch (IOException e) {
                    printStream.println(e.getMessage());
                }
            } else {
                throw new BigraphMethodInterpreterException("Bigraph variable reference couldn't be resolved", bigraphVarReference);
            }
        } else {
            throw new BigraphMethodInterpreterException("Method expression isn't supported", printableExpression);
        }
    }

    /**
     * To a file.
     */
    public static class FilePrintStrategy extends PrintStrategy {
        String filename;

        public FilePrintStrategy(String filename, AbstractBRSModelVisitor<?, ?> brsModelVisitor) throws BdslIOException {
            super(brsModelVisitor);
            this.filename = filename;
            this.initPrintStream();
        }

        @Override
        protected void initPrintStream() throws BdslIOException {
            try {
                printStream = new PrintStream(new FileOutputStream(filename, false));
            } catch (FileNotFoundException e) {
                throw new BdslIOException(e);
            }
        }
    }

    /**
     * On the standard output for the terminal.
     */
    public static class StdOutPrintStrategy extends PrintStrategy {

        public StdOutPrintStrategy(AbstractBRSModelVisitor<?, ?> brsModelVisitor) {
            super(brsModelVisitor);
            this.initPrintStream();
        }

        @Override
        protected void initPrintStream() {
            printStream = new PrintStream(System.out);
        }
    }
}
