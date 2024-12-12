package org.bigraphs.dsl.interpreter.exceptions;

import org.eclipse.xtext.nodemodel.INode;
import org.eclipse.xtext.nodemodel.SyntaxErrorMessage;
import org.eclipse.xtext.resource.XtextSyntaxDiagnostic;

/**
 * @author Dominik Grzelak
 */
public class XtextSyntaxDiagnostic2 extends XtextSyntaxDiagnostic {
    SyntaxErrorMessage syntaxErrorMessage;

    public XtextSyntaxDiagnostic2(INode errorNode, SyntaxErrorMessage syntaxErrorMessage) {
        super(errorNode);
        this.syntaxErrorMessage = syntaxErrorMessage;
    }

    @Override
    public String getMessage() {
        return this.syntaxErrorMessage.getMessage();
    }

    @Override
    public String getCode() {
        return this.syntaxErrorMessage.getIssueCode();
    }

    @Override
    public String[] getData() {
        return this.syntaxErrorMessage.getIssueData();
    }
}
