package org.bigraphs.dsl.interpreter.execution.codeexecution;

import java.io.BufferedWriter;
import java.io.Console;
import java.io.IOException;
import java.io.PrintStream;
import java.util.Arrays;

import javax.tools.DiagnosticCollector;
import javax.tools.JavaCompiler;
import javax.tools.JavaFileObject;
import javax.tools.StandardJavaFileManager;
import javax.tools.ToolProvider;

public class DiagnosticCollectorCompile {

  public static void main(String[] args) throws IOException {
    JavaCompiler compiler = ToolProvider.getSystemJavaCompiler();
    DiagnosticCollector<JavaFileObject> diagnostics = new DiagnosticCollector<JavaFileObject>();
    StandardJavaFileManager fileManager = compiler.getStandardFileManager(diagnostics, null, null);
    Iterable<? extends JavaFileObject> compilationUnits = fileManager
        .getJavaFileObjectsFromStrings(Arrays.asList("/home/dominik/eclipse-projects/bdsl/bdsl-algebraic-bigraph-interpreter/bdsl-execution-aggregate/src/test/java/de/tudresden/inf/st/bigraphs/dsl/interpreter/execution/codeexecution/MyClass.java"));
    JavaCompiler.CompilationTask task = compiler.getTask(null, fileManager, diagnostics, null,
        null, compilationUnits);
    boolean success = task.call();
    fileManager.close();
    System.out.println("Success: " + success);
  }
}
