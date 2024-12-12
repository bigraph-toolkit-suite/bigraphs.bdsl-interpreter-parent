package org.bigraphs.dsl.tests.interpreter;

import org.bigraphs.framework.core.Bigraph;
import org.bigraphs.framework.core.impl.signature.DefaultDynamicSignature;
import org.bigraphs.dsl.bDSL.BDSLDocument;
import org.bigraphs.dsl.bDSL.LocalVarDecl;
import org.bigraphs.dsl.interpreter.exceptions.BdslInterpretationException;
import org.bigraphs.dsl.interpreter.expressions.variables.PureBigraphExpressionEvalVisitorImpl;
import org.bigraphs.dsl.interpreter.extensions.variables.BigraphExpressionVisitableExtension;
import lombok.experimental.ExtensionMethod;
import org.eclipse.emf.common.util.Diagnostic;
import org.eclipse.xtext.resource.XtextSyntaxDiagnostic;
import org.junit.jupiter.api.Test;

import java.util.List;

/**
 * @author Dominik Grzelak
 */
@ExtensionMethod({BigraphExpressionVisitableExtension.class})
public class ErrorDiagnosticUnitTest extends BaseUnitTestSupport {

    @Test
    void name() {
        BDSLDocument brsModel = parse("sample-scripts/errors/bigraphVarDecl-withLinkNames.bdsl");
        validate(brsModel);

        DefaultDynamicSignature signature = getDefaultDynamicSignature(brsModel);
        LocalVarDecl var0 = getLocalVarDecl(brsModel, 0);
        try {
            Bigraph<?> bigraph0 = (Bigraph<?>) var0.interpretStart(new PureBigraphExpressionEvalVisitorImpl(signature));
        } catch (BdslInterpretationException e) {
//            e.printStackTrace();
            Diagnostic diagnostic = e.getDiagnostic();
            System.out.println("diagnostic: " + diagnostic);
            List<XtextSyntaxDiagnostic> syntaxDiagnostics = e.getSyntaxDiagnostics();
            System.out.println(syntaxDiagnostics.get(0).getMessage());
            System.out.println(syntaxDiagnostics.get(0).getCode());
            System.out.println(syntaxDiagnostics.get(0).getData());
            System.out.println(syntaxDiagnostics.get(0).getLine());
            System.out.println(syntaxDiagnostics.get(0).getLineEnd());
            System.out.println(syntaxDiagnostics.get(0).getColumn());
            System.out.println(syntaxDiagnostics.get(0).getLocation());
            System.out.println(syntaxDiagnostics.get(0).getOffset());
        }
    }
}
