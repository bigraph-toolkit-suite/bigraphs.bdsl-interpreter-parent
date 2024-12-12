package org.bigraphs.dsl.interpreter.exceptions;

import org.eclipse.emf.common.util.BasicDiagnostic;
import org.eclipse.emf.common.util.Diagnostic;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.xtext.diagnostics.ExceptionDiagnostic;
import org.eclipse.xtext.nodemodel.ICompositeNode;
import org.eclipse.xtext.nodemodel.SyntaxErrorMessage;
import org.eclipse.xtext.nodemodel.util.NodeModelUtils;
import org.eclipse.xtext.resource.XtextSyntaxDiagnostic;

import java.util.*;

/**
 * @author Dominik Grzelak
 */
public abstract class BdslInterpretationException extends RuntimeException {
    List<XtextSyntaxDiagnostic> syntaxDiagnostics = new ArrayList<>();

    String message;

    final List<EObject> lastElements = new ArrayList<>();
    BasicDiagnostic diagnostic;

    public BdslInterpretationException() {
        super("An interpretation exception occurred.");
    }

    public BdslInterpretationException(Throwable cause) {
        super(cause);
    }

    public BdslInterpretationException(Exception originalException, EObject lastElement) {
        this(originalException, Collections.singleton(lastElement).toArray(new EObject[0]));
    }

    public BdslInterpretationException(Exception originalException, EObject... lastElements) {
        super(originalException);
        this.lastElements.addAll(Arrays.asList(lastElements));
    }

    public BdslInterpretationException(String message, EObject... lastElements) {
        super(message);
        this.message = Objects.nonNull(message) ? message : "";
        this.lastElements.addAll(Arrays.asList(lastElements));
    }

    public void computeErrorFeedback() {
        new ExceptionDiagnostic(new RuntimeException("a"));
//        new XtextSyntaxDiagnostic(null);
        diagnostic = (BasicDiagnostic) BasicDiagnostic.toDiagnostic(this);
//        diagnostic.add(diag);
        for (EObject eachElement : lastElements) {
//            EcoreUtil.computeDiagnostic(eachElement, false);
//            org.eclipse.emf.common.util.Diagnostic diagnostic = this.diagnostician.validate(eachElement);

//            if (!diagnostic.getChildren().isEmpty()) {
//                Iterator var8 = diagnostic.getChildren().iterator();
//
//                while(var8.hasNext()) {
//                    org.eclipse.emf.common.util.Diagnostic childDiagnostic = (org.eclipse.emf.common.util.Diagnostic)var8.next();
//                    this.issueFromEValidatorDiagnostic(childDiagnostic, acceptor);
//                }
//            } else {
//                this.issueFromEValidatorDiagnostic(diagnostic, acceptor);
//            }

            System.out.println(eachElement);
            System.out.println(eachElement.eContainer());
            ICompositeNode node = NodeModelUtils.getNode(eachElement);

            XtextSyntaxDiagnostic2 xtextSyntaxDiagnostic = new XtextSyntaxDiagnostic2(node, new SyntaxErrorMessage("message", "issue_code"));
            syntaxDiagnostics.add(xtextSyntaxDiagnostic);

//            diagnostic.add((org.eclipse.emf.common.util.Diagnostic) xtextSyntaxDiagnostic);
//            System.out.println(xtextSyntaxDiagnostic);
        }
//        BasicDiagnostic diagnostic = Diagnostician.INSTANCE.createDefaultDiagnostic(null);
//        diagnostic.add(new );
    }

    public Diagnostic getDiagnostic() {
        if (Objects.isNull(diagnostic)) computeErrorFeedback();
        return diagnostic;
    }

    public List<XtextSyntaxDiagnostic> getSyntaxDiagnostics() {
        return syntaxDiagnostics;
    }
}
