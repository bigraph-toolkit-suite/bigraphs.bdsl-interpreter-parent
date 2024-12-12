package org.bigraphs.dsl.tests.interpreter;

import org.bigraphs.framework.core.Bigraph;
import org.bigraphs.framework.core.BigraphFileModelManagement;
import org.bigraphs.framework.core.impl.pure.PureBigraph;
import org.bigraphs.framework.core.impl.signature.DefaultDynamicSignature;
import org.bigraphs.dsl.bDSL.BDSLDocument;
import org.bigraphs.dsl.bDSL.LocalVarDecl;
import org.bigraphs.dsl.bDSL.Signature;
import org.bigraphs.dsl.interpreter.expressions.BdslMagicComments;
import org.bigraphs.dsl.interpreter.expressions.variables.PureBigraphExpressionEvalVisitorImpl;
import org.bigraphs.dsl.interpreter.expressions.variables.SignatureEvalVisitorImpl;
import org.bigraphs.dsl.interpreter.extensions.variables.BigraphExpressionVisitableExtension;
import lombok.experimental.ExtensionMethod;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;


/**
 * @author Dominik Grzelak
 */
@ExtensionMethod({BigraphExpressionVisitableExtension.class})
public class MagicCommentUnitTest extends BaseUnitTestSupport {

    @Test
    void name() throws Exception {
        BDSLDocument brsModel = parse("sample-scripts/variables/magic-comments.bdsl"); //parseHelper.parse(s);
//        new HiddenTokenSequencer().
//        ICompositeNode node = NodeModelUtils.getNode(brsModel).getRootNode();

        List<BdslMagicComments.Comment> parse = BdslMagicComments.parse(brsModel);
    }

    @Test
    void name2() throws Exception {
        BDSLDocument brsModel = parse("sample-scripts/variables/magic-comments.bdsl");

        assertNotNull(brsModel.getSignature());
        assertEquals(1, brsModel.getSignature().size());
        Signature signature = brsModel.getSignature().get(0);
        DefaultDynamicSignature dynamicSignature = (DefaultDynamicSignature) signature.interpretStart(new SignatureEvalVisitorImpl());

        // Two bigraphs are included
        LocalVarDecl varDecl0 = getLocalVarDecl(brsModel, 0);
        assertNotNull(varDecl0);
        List<BdslMagicComments.Comment> mc = BdslMagicComments.parse(brsModel);
        PureBigraphExpressionEvalVisitorImpl visitor =
                (PureBigraphExpressionEvalVisitorImpl) new PureBigraphExpressionEvalVisitorImpl(dynamicSignature).withMagicComments(mc);
        Bigraph<?> bigraph0 = (Bigraph<?>) varDecl0.interpretStart(visitor);
        assertNotNull(bigraph0);
        assertTrue(BdslMagicComments.getValue(mc, BdslMagicComments.Export.SCHEMA_LOCATION).isPresent());
        //TODO: schemaLocation is not written
        BigraphFileModelManagement.Store.exportAsInstanceModel((PureBigraph) bigraph0,
                System.out, "test-1.ecore");
//                BdslMagicComments.getValue(mc, BdslMagicComments.Export.SCHEMA_LOCATION).get().VALUE);
    }
}
