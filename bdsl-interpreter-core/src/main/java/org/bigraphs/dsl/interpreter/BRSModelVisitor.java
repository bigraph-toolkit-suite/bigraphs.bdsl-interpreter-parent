package org.bigraphs.dsl.interpreter;

import org.bigraphs.dsl.interpreter.expressions.BdslMagicComments;
import org.bigraphs.dsl.interpreter.expressions.cache.ExpressionCacheService;
import org.eclipse.emf.ecore.EObject;

import java.util.Collections;
import java.util.List;

/**
 * Base visitor interface for the underlying {@link org.bigraphs.dsl.bDSL.BDSLDocument} of the
 * bigraph DSL (BDSL).
 *
 * @author Dominik Grzelak
 * @see org.bigraphs.dsl.bDSL.BDSLDocument
 */
public interface BRSModelVisitor<ReturnType, ParamType extends EObject> {

    /**
     * The starting process of any BDSL definition to be evaluated.
     * This "entry point" must be implemented by the concrete subclass and represents the entry point of a
     * specific visitor implementation.
     *
     * @param dslElement any element of {@link org.bigraphs.dsl.bDSL.BDSLDocument} to process
     * @return the result of a BDSL element interpretation; specific to the concrete implementation
     */
    ReturnType beginVisit(ParamType dslElement) throws Exception;

    default ExpressionCacheService getSignatureCache() {
        return InterpreterServiceManager.getSignatureCache();
    }

    default ExpressionCacheService getLocalVarCache() {
        return InterpreterServiceManager.getLocalVarCacheService();
    }

    default ExpressionCacheService getLocalRuleCache() {
        return InterpreterServiceManager.getLocalRuleCacheService();
    }

    default ExpressionCacheService getLocalPredCache() {
        return InterpreterServiceManager.getLocalPredCacheService();
    }

    default ExpressionCacheService getLocalBRSCache() {
        return InterpreterServiceManager.getLocalBRSCacheService();
    }

    default List<BdslMagicComments.Comment> magicComments() {
        return Collections.emptyList();
    }
}
