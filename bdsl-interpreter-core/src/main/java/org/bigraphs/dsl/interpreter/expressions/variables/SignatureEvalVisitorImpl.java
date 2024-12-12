package org.bigraphs.dsl.interpreter.expressions.variables;

import org.bigraphs.framework.core.ControlStatus;
import org.bigraphs.framework.core.impl.signature.DefaultDynamicSignature;
import org.bigraphs.framework.core.impl.signature.DynamicSignatureBuilder;
import org.bigraphs.dsl.bDSL.ControlVariable;
import org.bigraphs.dsl.bDSL.Signature;
import org.bigraphs.dsl.interpreter.BRSModelVisitor;

import static org.bigraphs.framework.core.factory.BigraphFactory.pureSignatureBuilder;

/**
 * @author Dominik Grzelak
 */
public class SignatureEvalVisitorImpl implements BRSModelVisitor<org.bigraphs.framework.core.Signature<?>, Signature> {

    @Override
    public DefaultDynamicSignature beginVisit(Signature dslElement) {
        DynamicSignatureBuilder builder = pureSignatureBuilder();
        for (ControlVariable eachControlVar : dslElement.getControls()) {
            builder.newControl(eachControlVar.getName(), eachControlVar.getArity())
                    .status(ControlStatus.fromString(eachControlVar.getType().getName())).assign();
        }
        return builder.create();
    }
}
