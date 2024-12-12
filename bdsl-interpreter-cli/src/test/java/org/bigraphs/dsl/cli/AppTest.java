package org.bigraphs.dsl.cli;

import org.bigraphs.dsl.cli.Application;
import org.bigraphs.framework.simulation.modelchecking.ModelCheckingOptions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Dominik Grzelak
 */
@ExtendWith(SpringExtension.class) // for junit5, no RunWith necessary
@TestPropertySource(value = {"classpath:cli-test.properties"}, inheritProperties = true)
public class AppTest {

    @Test
    void name() {
        List<String> args = new ArrayList<>();
//        args.add("-help");
//        args.add("--disableBanner");
//        args.add("-version");
//        args.add("-Dcycles=1");
//        args.add("-Dtime=1337");
//        args.add("-Dimode=SIM");
//        args.add("--verbose=DEBUG");
//        args.add("--disableBanner");
//        args.add("-Bns-uri=org.bar.baz");
//        args.add("-Bns-prefix=foo");
//        args.add("--main=../../src/test/resources/test_bdsl_01.bdsl");
        Application.main(args.toArray(new String[0]));
    }

    @Test
    void multiple_includeFiles_test_01() {
        List<String> args = new ArrayList<>();

        args.add("--main=../../src/test/resources/sample-scripts/includeTest/main.bdsl");
        args.add("--include=../../src/test/resources/sample-scripts/includeTest/include-1.bdsl," +
                "../../src/test/resources/sample-scripts/includeTest/include-2.bdsl," +
                "../../src/test/resources/sample-scripts/includeTest/include-3.bdsl, ");
        Application.main(args.toArray(new String[0]));
    }

    @Test
    void udf_test_01() {
        List<String> args = new ArrayList<>();

        args.add("--main=../../src/test/resources/sample-scripts/udf/udf_01.bdsl");
        args.add("--includeUdf=../../src/test/resources/sample-scripts/udf/bigraph-test-examples-1.0-SNAPSHOT.jar");
        args.add("--outputDir=../../src/test/resources/dump/udf_test_01/");
        Application.main(args.toArray(new String[0]));
    }

    @Test
    void callback_test_01() throws InterruptedException {
        List<String> args = new ArrayList<>();
        args.add("--main=../../src/test/resources/sample-scripts/callback/brs-callback-01.bdsl");
        args.add("--includeUdf=../../src/test/resources/sample-scripts/udf/bigraph-test-examples-1.0-SNAPSHOT.jar");
        Application.main(args.toArray(new String[0]));
    }

    @Test
    void callback_test_02() throws InterruptedException {
        List<String> args = new ArrayList<>();
        args.add("--main=../../src/test/resources/sample-scripts/callback/brs-callback-02.bdsl");
        args.add("--includeUdf=../../src/test/resources/sample-scripts/udf/bigraph-test-examples-1.0-SNAPSHOT.jar");
        Application.main(args.toArray(new String[0]));
    }
}
