package org.bigraphs.dsl.cli;

import java.io.File;
import java.io.UnsupportedEncodingException;
import java.net.URISyntaxException;
import java.net.URLDecoder;
import java.nio.file.Paths;
import java.security.CodeSource;
import java.util.Objects;
import java.util.Optional;

/**
 * @author Dominik Grzelak
 */
public class FileLoadUtil {

    public static Optional<File> load(String filename) {
        if (Objects.isNull(filename)) return Optional.empty();
        filename = filename.trim();
        File file = new File(filename);
        if (file.exists()) {
            return Optional.of(file);
        }
        if (!Paths.get(filename).isAbsolute()) {
            try {
                CodeSource codeSource = Application.class.getProtectionDomain().getCodeSource();
                String decodedPath = URLDecoder.decode(codeSource.getLocation().toURI().getPath(), "UTF-8");
                file = Paths.get(decodedPath, filename).toFile();
                if (file.exists())
                    return Optional.of(file);
            } catch (UnsupportedEncodingException | URISyntaxException e) {
                e.printStackTrace();
            }
        }
        return Optional.empty();
    }
}
