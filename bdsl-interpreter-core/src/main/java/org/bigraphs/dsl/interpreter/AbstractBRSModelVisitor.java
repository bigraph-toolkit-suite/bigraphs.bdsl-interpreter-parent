package org.bigraphs.dsl.interpreter;

import org.bigraphs.dsl.interpreter.expressions.BdslMagicComments;
import org.eclipse.emf.ecore.EObject;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * @author Dominik Grzelak
 */
public abstract class AbstractBRSModelVisitor<ReturnType, ParamType extends EObject> implements BRSModelVisitor<ReturnType, ParamType> {

    protected final List<BdslMagicComments.Comment> magicComments = new ArrayList<>();

    public AbstractBRSModelVisitor() {
    }

    public AbstractBRSModelVisitor<ReturnType, ParamType> withMagicComments(List<BdslMagicComments.Comment> comments) {
        if (Objects.nonNull(comments) && comments.size() != 0) {
            //TODO: dont clear but replace instead
            this.magicComments.clear();
            this.magicComments.addAll(comments);
        }
        return this;
    }

    @Override
    public List<BdslMagicComments.Comment> magicComments() {
        return this.magicComments;
    }
}
