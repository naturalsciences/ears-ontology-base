/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package be.naturalsciences.bmdc.ontology.writer;

import org.semanticweb.owlapi.formats.ManchesterSyntaxDocumentFormat;
import org.semanticweb.owlapi.formats.RDFXMLDocumentFormat;
import org.semanticweb.owlapi.model.OWLDocumentFormat;

/**
 *
 * @author Thomas Vandenberghe
 */
public class OntologyFileFormat {

    private String format;

    private String contentType;

    private String extension;

    private OWLDocumentFormat ontologyFormat;

    public String getFormat() {
        return format;
    }

    public String getContentType() {
        return contentType;
    }

    public String getExtension() {
        return extension;
    }

    public OWLDocumentFormat getOntologyFormat() {
        return ontologyFormat;
    }

    public static final String JSON_LD_FORMAT = "JSON-LD";

    public static final OntologyFileFormat OWL_FORMAT = new OntologyFileFormat("OWL", "application/txt", ".owl", new ManchesterSyntaxDocumentFormat());
    public static final OntologyFileFormat RDF_FORMAT = new OntologyFileFormat("RDF/XML", "application/rdf+XML", ".rdf", new RDFXMLDocumentFormat());

    public OntologyFileFormat(String format, String contentType, String extension, OWLDocumentFormat ontologyFormat) {
        this.format = format;
        this.contentType = contentType;
        this.extension = extension;
        this.ontologyFormat = ontologyFormat;
    }

    /*public static boolean formatIsLegal(String format) {
     String f=new String(format);
     return f.equals(OntologyFileFormatFactory.JSON_LD_FORMAT) || f.equals(OntologyFileFormatFactory.OWL_FORMAT) || f.equals(OntologyFileFormatFactory.RDF_FORMAT);
     }*/
}
