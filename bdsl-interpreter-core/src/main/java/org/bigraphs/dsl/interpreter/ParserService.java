package org.bigraphs.dsl.interpreter;

import com.google.inject.Injector;
import org.bigraphs.dsl.BDSLLib;
import org.bigraphs.dsl.BDSLStandaloneSetup;
import org.bigraphs.dsl.bDSL.BDSLDocument;

import org.bigraphs.dsl.interpreter.exceptions.BdslIOException;
import org.eclipse.emf.common.util.Diagnostic;
import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.emf.ecore.util.Diagnostician;
import org.eclipse.xtext.resource.XtextResource;
import org.eclipse.xtext.resource.XtextResourceSet;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.inject.Singleton;
import java.io.IOException;
import java.io.InputStream;
import java.util.Collections;
import java.util.List;

/**
 * @author Dominik Grzelak
 */
@Singleton
public class ParserService implements InterpreterService {
    private static final Logger LOG = LoggerFactory.getLogger(ParserService.class);
    BDSLLib bdslLib;

    public ParserService() {
        bdslLib = BDSLLib.getInstance();
    }

    public BDSLDocument parse(InputStream inputStream) throws IOException {
        Injector injector = new BDSLStandaloneSetup().createInjectorAndDoEMFRegistration();
        XtextResourceSet resourceSet = injector.getInstance(XtextResourceSet.class);
        resourceSet.addLoadOption(XtextResource.OPTION_RESOLVE_ALL, Boolean.TRUE);
        Resource resource = resourceSet.createResource(URI.createURI("*.bdsl"));
        resource.load(inputStream, Collections.EMPTY_MAP);
        BDSLDocument model = (BDSLDocument) resource.getContents().get(0);
        LOG.debug(resource.getErrors().toString());
//        System.out.println(resource.getErrors());
        assert resource.getErrors().size() == 0;
        return model;
    }


    /**
     * Standalone parser for BDSL files underlying the {@code BDSL.xtext} grammar.
     *
     * @param mainProgramFilename the filename of the main {@literal *.bdsl} file
     * @param includeFilenames    additional include files to load before the main program
     * @param resolveAll          option that indicates whether to resolve references
     * @return the loaded model(s) from the BDSL file
     * @see <a href="https://wiki.eclipse.org/Xtext/FAQ#How_do_I_load_my_model_in_a_standalone_Java_application.C2.A0.3F">How do I load my model in a standalone Java application ?</a>
     */
    //TODO: return new class BdslParseResult with resource and model
    public BDSLDocument parse(String mainProgramFilename, String[] includeFilenames, boolean resolveAll) {
        Injector injector = new BDSLStandaloneSetup().createInjectorAndDoEMFRegistration();
        XtextResourceSet resourceSet = injector.getInstance(XtextResourceSet.class);
//        resourceSet.addLoadOption(XtextResource.OPTION_RESOLVE_ALL, Boolean.TRUE);
        resourceSet.addLoadOption(XtextResource.OPTION_RESOLVE_ALL, resolveAll);

        // load additional include libraries if available
        try {
            List<Resource> includeResources = bdslLib.loadIncludeLibraries(resourceSet, includeFilenames);
            includeResources.forEach(r -> {
//                System.out.println(r.getErrors());
                LOG.debug(r.getErrors().toString());
                assert r.getErrors().size() == 0;
            });
        } catch (IOException e) {
            throw new BdslIOException(e);
        }

        // load the main BDSL program
        Resource resource = resourceSet.getResource(
                URI.createURI(mainProgramFilename), true);
        BDSLDocument model = (BDSLDocument) resource.getContents().get(0);
        System.out.println(resource.getErrors());
        LOG.debug(resource.getErrors().toString());
        assert resource.getErrors().size() == 0;
        return model;
    }

    public BDSLDocument parse(String filename) {
        return parse(filename, new String[0]);
    }

    public BDSLDocument parse(String filename, String[] includeFilenames) {
        return parse(filename, includeFilenames, Boolean.TRUE);
    }


    //see: assertNoErrors(final Resource resource) ValidationTestHelper
    public Diagnostic validate(BDSLDocument brsModel) {
        Diagnostic diagnostic = Diagnostician.INSTANCE.validate(brsModel);
        LOG.debug("Severity: " + diagnostic.getSeverity());
        LOG.debug(diagnostic.toString());
//        System.out.println("Severity: " + diagnostic.getSeverity());
//        System.out.println(diagnostic.toString());
        if (diagnostic.getSeverity() != 0) throw new RuntimeException("to bad!");
        return diagnostic;
    }
}
