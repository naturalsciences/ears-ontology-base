/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package be.naturalsciences.bmdc.ontology.writer;

import java.io.BufferedOutputStream;
import java.io.File;
import java.nio.file.Path;
import org.semanticweb.owlapi.apibinding.OWLManager;
import org.semanticweb.owlapi.model.OWLDocumentFormat;
import org.semanticweb.owlapi.model.OWLOntology;
import org.semanticweb.owlapi.model.OWLOntologyCreationException;
import org.semanticweb.owlapi.model.OWLOntologyManager;

/**
 *
 * @author Thomas Vandenberghe
 */
public class OntologyFileWriter {

    protected OWLOntologyManager manager;
    protected File result;

    public OntologyFileWriter(OWLOntologyManager manager) {
        this.manager = manager;
    }

    public OntologyFileWriter() {
        this.manager = OWLManager.createOWLOntologyManager();
    }

    public File getResult() {
        return result;
    }

    public OWLOntologyManager getManager() {
        return manager;
    }

    /**
     * Create (and replace) an ontology file of the specified OWL ontology in
     * the specified format and location.
     *
     * @param outputFormat
     * @param fullPath The path to the actual file
     * @param onto
     * @param perm
     * @param group
     * @return
     */
    public BufferedOutputStream createOntoFile(OWLOntology onto, OntologyFileFormat outputFormat, Path fullPath, String owner, String perm, String group, boolean overwriteIfExists) throws org.semanticweb.owlapi.model.OWLOntologyCreationException {
        if (onto == null) {
            throw new IllegalArgumentException("The provided ontology is null.");
        }

        if (!fullPath.toFile().exists() || (fullPath.toFile().exists() && overwriteIfExists)) {
            OWLDocumentFormat format = outputFormat.getOntologyFormat();
            int bufferSize = 8 * 1024;
            BufferedOutputStream output = FileUtils.createFile(fullPath, outputFormat.getExtension(), owner, perm, group, bufferSize);
            result = fullPath.toFile();
            if (output == null) {
                throw new OWLOntologyCreationException("Failed in writing BufferedOutputStream to " + fullPath.toString());
            }
            try {
                long t1 = java.lang.System.currentTimeMillis();

                manager.saveOntology(onto, format, output);
                long t2 = java.lang.System.currentTimeMillis();
                System.out.println("Saving ontology took: " + String.valueOf(t2 - t1));
                output.flush();
                output.close();
                long t3 = java.lang.System.currentTimeMillis();
                System.out.println("Writing file took: " + String.valueOf(t3 - t2));
                return output;
            } catch (Exception e) {
                throw new OWLOntologyCreationException("Failed in saving ontology to folder " + fullPath.toString(), e);
            }
        } else {
            throw new OWLOntologyCreationException("Overwriting an existing ontology is not allowed.");
        }
    }

}
