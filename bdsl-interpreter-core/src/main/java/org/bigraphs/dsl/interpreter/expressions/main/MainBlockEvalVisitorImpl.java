package org.bigraphs.dsl.interpreter.expressions.main;

import org.bigraphs.dsl.bDSL.AbstractMainStatements;
import org.bigraphs.dsl.bDSL.MainElement;
import org.bigraphs.dsl.interpreter.BdslStatementInterpreterResult;
import org.bigraphs.dsl.interpreter.exceptions.BdslIOException;
import org.bigraphs.dsl.interpreter.extensions.main.MainBlockVisitableExtension;
import lombok.experimental.ExtensionMethod;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.Objects;

/**
 * @author Dominik Grzelak
 */
@ExtensionMethod({MainBlockVisitableExtension.class})
public class MainBlockEvalVisitorImpl extends AbstractMainEvalVisitor<List<BdslStatementInterpreterResult>, MainElement> {

    MainStatementEvalVisitorImpl mainDeclarationEvalVisitor;

    public MainBlockEvalVisitorImpl(MainStatementEvalVisitorImpl mainDeclarationEvalVisitor) {
        super();
        this.mainDeclarationEvalVisitor = mainDeclarationEvalVisitor;
    }

    public MainBlockEvalVisitorImpl(String filename, MainStatementEvalVisitorImpl mainDeclarationEvalVisitor) throws BdslIOException {
        super(filename);
        this.mainDeclarationEvalVisitor = mainDeclarationEvalVisitor;
    }

    @Override
    public List<BdslStatementInterpreterResult> beginVisit(MainElement dslElement) {
        if (Objects.isNull(dslElement) || dslElement.getBody().getStatements().size() == 0)
            return Collections.emptyList();
//        SimpleBdslCallableStatementContainer container = new SimpleBdslCallableStatementContainer();
        List<BdslStatementInterpreterResult> results = new LinkedList<>();
        for (AbstractMainStatements each : dslElement.getBody().getStatements()) {
            BdslStatementInterpreterResult<Object> val = (BdslStatementInterpreterResult<Object>) mainDeclarationEvalVisitor.beginVisit(each);
            results.add(val);
        }
        return results;
    }
}
