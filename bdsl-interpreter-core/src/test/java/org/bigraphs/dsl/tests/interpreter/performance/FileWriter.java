package org.bigraphs.dsl.tests.interpreter.performance;

import lombok.Data;
import org.apache.commons.io.FileUtils;

import java.io.File;
import java.io.IOException;
import java.nio.file.Paths;

@Data
public class FileWriter {

    private String baseDir;

    public FileWriter(String baseDir) {
        this.baseDir = baseDir;
    }

    public boolean write(String content, String filename) throws IOException {
        File file = Paths.get(
                this.baseDir,
                 filename
        ).toFile();
        FileUtils.writeStringToFile(file, content);
        return true;
    }
}
