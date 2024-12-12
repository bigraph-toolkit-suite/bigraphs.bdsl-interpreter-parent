package org.bigraphs.dsl.interpreter.expressions;

import org.bigraphs.dsl.bDSL.BDSLDocument;
import org.eclipse.collections.api.list.ImmutableList;
import org.eclipse.collections.impl.factory.Lists;
import org.eclipse.xtext.nodemodel.impl.RootNode;
import org.eclipse.xtext.nodemodel.util.NodeModelUtils;

import java.io.BufferedReader;
import java.io.StringReader;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * @author Dominik Grzelak
 */
public class BdslMagicComments {
    public final static String SEPARATOR = ":";

    public static List<Comment> parse(BDSLDocument brsModel) {
        RootNode rootNode = (RootNode) NodeModelUtils.getNode(brsModel).getRootNode();
        int offset = rootNode.getTextRegionWithLineInformation().getOffset();
        String substring = rootNode.getCompleteContent().substring(0, offset);
        return new BufferedReader(new StringReader(substring))
                .lines()
                .map(l -> l.replaceFirst("//", "").trim())
                .filter(l -> ALL.stream().anyMatch(l::startsWith))
                .map(l -> {
                    String[] split = l.split(SEPARATOR, 2);
                    if (split.length == 2) {
                        return new Comment(split[0].trim(), split[1].trim());
                    }
                    return null;
                })
                .filter(Objects::nonNull)
                .collect(Collectors.toList());
    }

    public static boolean setValue(List<Comment> comments, String key, String value) {
        int ix = comments.indexOf(key);
        if (ix < 0) {
            return comments.add(new Comment(key, value));
        } else {
            comments.set(ix, new Comment(key, value));
            return true;
        }
    }

    public static Optional<Comment> getValue(List<Comment> comments, String key) {
        return comments.stream().filter(c -> c.KEY.equals(key)).findFirst();
    }

    public static class Comment {
        public final String KEY;
        public final String VALUE;

        public Comment(String KEY, String VALUE) {
            this.KEY = KEY;
            this.VALUE = VALUE;
        }
    }

    private static ImmutableList<String> ALL = Lists.immutable.of(
            Export.ENCODING, Export.SCHEMA_LOCATION,
            ModelData.MODEL_NAME, ModelData.NS_PREFIX, ModelData.NS_URI
    );


    public final static class Export {
        public final static String ENCODING = "encoding";
        public final static String SCHEMA_LOCATION = "schemaLocation";

    }

    public final static class ModelData {
        public final static String NS_URI = "ns-uri";
        public final static String NS_PREFIX = "ns-prefix";
        public final static String MODEL_NAME = "name";
    }

}
