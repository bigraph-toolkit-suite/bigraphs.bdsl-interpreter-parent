package org.bigraphs.dsl.interpreter;

import java.util.Optional;
import java.util.concurrent.Callable;

/**
 * Interface for returning results produced by the interpreted BDSL statements.
 * This allows to unify the return type of all the {@code interpret()} <emph>extension methods</emph>.
 * <p>
 * The results are actually (or most of them) executable Java expressions related to the concrete (pre-determined) BDSL
 * semantic, such as print a bigraph to the console, for example.
 * <p>
 * This class is usually contained within {@link BdslStatementInterpreterResult}.
 * There, the flag {@link BdslStatementInterpreterResult#isResultAlreadyComputed()} is present which indicates whether
 * the result is already computed.
 *
 * @author Dominik Grzelak
 */
@FunctionalInterface
public interface BdslExecutableStatement<V> extends Callable<Optional<V>> {

    @Override
    Optional<V> call() throws Exception;
}
