package org.bigraphs.dsl.interpreter.expressions.cache;

import org.eclipse.emf.ecore.EObject;

/**
 * @author Dominik Grzelak
 * @see ExpressionCacheService
 */
@FunctionalInterface
public interface CacheWriteCallback<T extends EObject, R> {

    void performWrite(ExpressionCacheService<T, R> service);
}
