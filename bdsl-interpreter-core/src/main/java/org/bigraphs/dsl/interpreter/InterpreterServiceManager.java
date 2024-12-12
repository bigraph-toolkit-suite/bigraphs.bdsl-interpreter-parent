package org.bigraphs.dsl.interpreter;

import com.google.inject.Guice;
import com.google.inject.Injector;
import org.bigraphs.dsl.BDSLStandaloneSetupGenerated;
import org.bigraphs.dsl.interpreter.config.InterpreterModule;
import org.bigraphs.dsl.interpreter.expressions.cache.ExpressionCacheService;
import org.bigraphs.dsl.scoping.BDSLImportedNamespaceAwareLocalScopeProvider;

/**
 * one aggregate with a single entry point
 * <p>
 * returns the standard parser for the CLI
 * returns all evaluators to build the logic in the CLI: different kinds of interpreting modes possible
 *
 * @author Dominik Grzelak
 */
public class InterpreterServiceManager {
    private static final Injector interpreterInjector = Guice.createInjector(new InterpreterModule());
    private static final Injector bdslInjector = new BDSLStandaloneSetupGenerated().createInjectorAndDoEMFRegistration();

    public static ExpressionCacheService<?, ?> getLocalVarCacheService() {
        return interpreterInjector.getInstance(ExpressionCacheService.DefaultBigraphCacheServiceImpl.class);
    }

    public static ExpressionCacheService<?, ?> getLocalRuleCacheService() {
        return interpreterInjector.getInstance(ExpressionCacheService.DefaultRuleCacheServiceImpl.class);
    }

    public static ExpressionCacheService<?, ?> getLocalBRSCacheService() {
        return interpreterInjector.getInstance(ExpressionCacheService.DefaultBRSCacheServiceImpl.class);
    }

    public static ExpressionCacheService<?, ?> getLocalPredCacheService() {
        return interpreterInjector.getInstance(ExpressionCacheService.DefaultPredCacheServiceImpl.class);
    }

    public static ExpressionCacheService<?, ?> getSignatureCache() {
        return interpreterInjector.getInstance(ExpressionCacheService.DefaultServiceCacheServiceImpl.class);
    }

    public static ParserService parser() {
        return interpreterInjector.getInstance(ParserService.class);
    }

    public static BDSLImportedNamespaceAwareLocalScopeProvider scopeProvider() {
        return bdslInjector.getInstance(BDSLImportedNamespaceAwareLocalScopeProvider.class);
    }
}
