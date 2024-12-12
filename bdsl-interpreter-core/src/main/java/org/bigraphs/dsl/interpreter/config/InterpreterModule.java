package org.bigraphs.dsl.interpreter.config;

import com.google.inject.AbstractModule;
import org.bigraphs.dsl.interpreter.expressions.cache.ExpressionCacheService;

/**
 * For guice.
 *
 * @author Dominik Grzelak
 */
@Deprecated
public class InterpreterModule extends AbstractModule {
    protected void configure() {

        bind(ExpressionCacheService.class)
                .to(ExpressionCacheService.DefaultBigraphCacheServiceImpl.class);
    }
}
