package org.bigraphs.dsl.cli.configuration.v1;

import org.bigraphs.dsl.bDSL.UDFOperation;
import org.bigraphs.dsl.interpreter.BdslStatementInterpreterResult;
import org.bigraphs.dsl.interpreter.execution.jobs.writer.BaseBdslItemWriter;

import java.util.List;
import java.util.Optional;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * @author Dominik Grzelak
 */
public class SimpleSequentialBdslStmtWriter extends BaseBdslItemWriter<BdslStatementInterpreterResult<Object>> {
    ExecutorService executor = Executors.newFixedThreadPool(Runtime.getRuntime().availableProcessors());

    @Override
    public void write(List<? extends BdslStatementInterpreterResult<Object>> list) throws Exception {
        for (BdslStatementInterpreterResult<Object> each : list) {
            //TODO provide sequentialasync and sync writer
            //TODO evaluate here whether statement shall be executed async or sync (?)
            if (each.getStatement() instanceof UDFOperation) {
                executor.submit(each.getBdslExecutableStatement());
            } else {
                // TODO evaluate here whether statement shall be executed async or sync (?)
//                Future<Optional<Object>> submit = executor.submit(each.getBdslExecutableStatement());
//                Optional<Object> call = submit.get();
                Optional<Object> call = each.getBdslExecutableStatement().call();
                if (call.isPresent()) {
                    call.get();
                }
            }
        }
    }
}
