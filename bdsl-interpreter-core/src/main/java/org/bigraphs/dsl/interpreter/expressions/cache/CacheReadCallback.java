package org.bigraphs.dsl.interpreter.expressions.cache;

import org.eclipse.emf.ecore.EObject;

import java.util.Optional;

/**
 * @author Dominik Grzelak
 * @see ExpressionCacheService
 */
@FunctionalInterface
public interface CacheReadCallback<T extends EObject, R> {

    Optional<R> performRead(ExpressionCacheService<T, R> service);
}
