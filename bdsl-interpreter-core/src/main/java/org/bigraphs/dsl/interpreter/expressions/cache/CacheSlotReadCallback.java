package org.bigraphs.dsl.interpreter.expressions.cache;

import org.eclipse.emf.ecore.EObject;

/**
 * @author Dominik Grzelak
 * @see ExpressionCacheService
 */
@FunctionalInterface
public interface CacheSlotReadCallback<T extends EObject, R> {

    ExpressionCacheService.SlotCacheState performRead(ExpressionCacheService<T, R> service);
}
