package org.bigraphs.dsl.interpreter;

import org.bigraphs.dsl.bDSL.*;
import org.bigraphs.dsl.bDSL.impl.BDSLFactoryImpl;
import org.apache.commons.io.FilenameUtils;
import org.eclipse.xtext.EcoreUtil2;

import java.io.File;
import java.net.URL;
import java.nio.file.Paths;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

/**
 * Some utility methods for BDSl statements.
 *
 * @author Dominik Grzelak
 */
public class BDSLUtil2 {

    public static String prepareResourcePath(DataSource dataSource, String resourcePath) {
        switch (dataSource) {
            case CLASSPATH:
                String substring = resourcePath.substring("classpath:".length());
                URL resourceURL = BDSLUtil2.class.getClassLoader().getResource(substring);
                if (resourceURL == null) {
                    return substring;
                }
                return resourceURL.getPath();
//                URL url = Resources.getResource(substring);
//                return url.getPath();
            case LOCAL_FILE:
                return resourcePath.substring("file:".length());
            default:
                return resourcePath;
        }
    }

    public static String inferMetaModelResourcePath(String resourcePath) {
        String s = Paths.get(resourcePath).getFileName().toString();
        String s1 = FilenameUtils.removeExtension(s);
        String metaModelFilePath = resourcePath.substring(0, resourcePath.indexOf(s));
        metaModelFilePath = Paths.get(metaModelFilePath, s1 + ".ecore").toAbsolutePath().toString();
        if (!new File(metaModelFilePath).exists()) {
            return "";
        }
        return metaModelFilePath;
    }

    /**
     * @param bdslVariableDeclaration the variable declaration to check
     * @return if {@code true} the variable declaration is an assignment expression (the user could expect a load
     * method assignment).
     */
    public static boolean isBDSLAssignment(BDSLVariableDeclaration2 bdslVariableDeclaration) {
        if (Objects.nonNull(bdslVariableDeclaration) &&
                bdslVariableDeclaration.getValue() instanceof AssignableBigraphExpression) {
            return true;
        }
//        if (bdslVariableDeclaration.getDefinition().size() == 0 && bdslVariableDeclaration.getExpression() != null) {
//            return true;
//        }
        return false;
    }

    public static boolean isBDSLAssignment(AbstractNamedSignatureElement bdslVariableDeclaration) {
        BDSLVariableDeclaration2 container = EcoreUtil2.getContainerOfType(bdslVariableDeclaration, BDSLVariableDeclaration2.class);
        if (container != null) {
            return BDSLUtil2.isBDSLAssignment(container);
        }
        return false;
    }

    public static List<BigraphExpression> getBigraphExpressionsOf(LocalVarDecl localVarDecl) {
        if (localVarDecl instanceof LVD2) {
            return ((LVD2) localVarDecl).getDefinition();
        }
        BDSLVariableDeclaration2 parent;
        if ((parent = EcoreUtil2.getContainerOfType(localVarDecl, BDSLVariableDeclaration2.class)) != null) {
            if (parent.getValue() != null) {
                return parent.getValue().getDefinition();
            }
        }
        return Collections.emptyList();
    }

    public static LocalVarDecl getLocalVarDecl(AbstractNamedSignatureElement typeLeftHandSide) {
        if (typeLeftHandSide instanceof BigraphVarDeclOrReference) {
            if (typeLeftHandSide instanceof LocalVarDecl) {
                return (LocalVarDecl) typeLeftHandSide;
            }
            if (typeLeftHandSide instanceof BigraphVarReference) {
                return ((BigraphVarReference) typeLeftHandSide).getValue();
            }
        }
//        if (typeLeftHandSide instanceof BDSLAssignment) {
//            return getLocalVarDecl(((BDSLAssignment) typeLeftHandSide).getRight());
//        }
        return null;
//        throw new BigraphDeclarationInterpreterException();
    }

    public static Signature createEmptySignatureElement() {
        org.bigraphs.dsl.bDSL.Signature sigInferred = BDSLFactoryImpl.eINSTANCE.createSignature();
        sigInferred.setName("empty");
        return sigInferred;
    }
}
