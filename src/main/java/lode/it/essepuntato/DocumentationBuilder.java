/* * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * *      
 * Copyright (c) 2010-2013, Silvio Peroni <essepuntato@gmail.com>
 * 
 * Permission to use, copy, modify, and/or distribute this software for any
 * purpose with or without fee is hereby granted, provided that the above
 * copyright notice and this permission notice appear in all copies.
 * 
 * THE SOFTWARE IS PROVIDED "AS IS" AND THE AUTHOR DISCLAIMS ALL WARRANTIES
 * WITH REGARD TO THIS SOFTWARE INCLUDING ALL IMPLIED WARRANTIES OF
 * MERCHANTABILITY AND FITNESS. IN NO EVENT SHALL THE AUTHOR BE LIABLE FOR
 * ANY SPECIAL, DIRECT, INDIRECT, OR CONSEQUENTIAL DAMAGES OR ANY DAMAGES
 * WHATSOEVER RESULTING FROM LOSS OF USE, DATA OR PROFITS, WHETHER IN AN
 * ACTION OF CONTRACT, NEGLIGENCE OR OTHER TORTIOUS ACTION, ARISING OUT OF
 * OR IN CONNECTION WITH THE USE OR PERFORMANCE OF THIS SOFTWARE.
 * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * */
package lode.it.essepuntato;

import gnu.trove.set.hash.THashSet;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.StringReader;
import java.net.MalformedURLException;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.Map;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.stream.StreamResult;
import javax.xml.transform.stream.StreamSource;
import org.semanticweb.owlapi.apibinding.OWLManager;
import org.semanticweb.owlapi.formats.RDFXMLDocumentFormat;
import org.semanticweb.owlapi.io.StringDocumentTarget;
import org.semanticweb.owlapi.model.IRI;
import org.semanticweb.owlapi.model.OWLAnnotationAssertionAxiom;
import org.semanticweb.owlapi.model.OWLEntity;
import org.semanticweb.owlapi.model.OWLOntology;
import org.semanticweb.owlapi.model.OWLOntologyCreationException;
import org.semanticweb.owlapi.model.OWLOntologyManager;
import org.semanticweb.owlapi.model.OWLOntologyStorageException;

public class DocumentationBuilder {

    /*     
     <script src="http://eelst.cs.unibo.it/apps/LODE/jquery.js"></script>
     <script src="http://eelst.cs.unibo.it/apps/LODE/jquery.scrollTo.js"></script>
            
     <script>
    
     $(document).ready(
     function () {
     var list = $('a[name="http://www.essepuntato.it/tmp/1437047837-ontology"]');
     if (list.size() != 0) {
     var element = list.first();
     $.scrollTo(element);
     }
     });
     </script>*/
    private static final long serialVersionUID = 1L;
    //private String xsltURL2 = "http://ostrea.mumm.ac.be/ears2/1/extraction.xsl";
    //private String xsltURL = "http://lode.sourceforge.net/xslt";
    private String cssLocation = "http://eelst.cs.unibo.it/apps/LODE/";
    private int maxTentative = 3;

    private OWLOntologyManager manager;
    private URL ontologyURL;
    private OWLOntology ontology;
    private boolean useOWLAPI;
    private boolean considerImportedOntologies;
    private boolean considerImportedClosure;
    private boolean useReasoner;

    public DocumentationBuilder(URL ontologyURL,
            boolean useOWLAPI,
            boolean considerImportedOntologies,
            boolean considerImportedClosure,
            boolean useReasoner) {
        manager = OWLManager.createOWLOntologyManager();
        try {
            this.ontology = manager.loadOntology(IRI.create(ontologyURL.toString()));
        } catch (OWLOntologyCreationException ex) {
            Logger.getLogger(DocumentationBuilder.class.getName()).log(Level.SEVERE, null, ex);
        }
        this.ontologyURL = ontologyURL;
        this.useOWLAPI = useOWLAPI;
        this.considerImportedOntologies = considerImportedOntologies;
        this.considerImportedClosure = considerImportedClosure;
        this.useReasoner = useReasoner;
    }

    public DocumentationBuilder(OWLOntology ontology,
            boolean useOWLAPI,
            boolean considerImportedOntologies,
            boolean considerImportedClosure,
            boolean useReasoner) {
        this.ontology = ontology;
        manager = OWLManager.createOWLOntologyManager();
        try {
            this.ontologyURL = ontology.getOntologyID().getOntologyIRI().get().toURI().toURL();
        } catch (MalformedURLException ex) {
            Logger.getLogger(DocumentationBuilder.class.getName()).log(Level.SEVERE, null, ex);
        }
        this.useOWLAPI = useOWLAPI;
        this.considerImportedOntologies = considerImportedOntologies;
        this.considerImportedClosure = considerImportedClosure;
        this.useReasoner = useReasoner;
    }

    public String generateDoc() {
        String result = null;
        try {
            result = parseWithOWLAPI(
                    this.ontologyURL,
                    this.useOWLAPI,
                    this.considerImportedOntologies,
                    this.considerImportedClosure,
                    this.useReasoner);
        } catch (OWLOntologyCreationException ex) {
            Logger.getLogger(DocumentationBuilder.class.getName()).log(Level.SEVERE, null, ex);
        } catch (OWLOntologyStorageException ex) {
            Logger.getLogger(DocumentationBuilder.class.getName()).log(Level.SEVERE, null, ex);
        } catch (URISyntaxException ex) {
            Logger.getLogger(DocumentationBuilder.class.getName()).log(Level.SEVERE, null, ex);
        }
        try {
            return applyXSLTTransformation(result, this.ontologyURL.toString(), "en");
        } catch (TransformerException ex) {
            Logger.getLogger(DocumentationBuilder.class.getName()).log(Level.SEVERE, null, ex);
        }
        return null;
    }

    private String parseWithOWLAPI(
            URL ontologyURL,
            boolean useOWLAPI,
            boolean considerImportedOntologies,
            boolean considerImportedClosure,
            boolean useReasoner)
            throws OWLOntologyCreationException, OWLOntologyStorageException, URISyntaxException {
        String result = "";

        if (useOWLAPI) {

            if (considerImportedClosure || considerImportedOntologies) {
                Set<OWLOntology> setOfImportedOntologies = new THashSet();
                if (considerImportedOntologies) {
                    setOfImportedOntologies.addAll(this.ontology.getDirectImports());
                } else {
                    setOfImportedOntologies.addAll(this.ontology.getImportsClosure());
                }
                for (OWLOntology importedOntology : setOfImportedOntologies) {
                    manager.addAxioms(this.ontology, importedOntology.getAxioms());
                }
            }
            if (useReasoner) {
                //this.ontology = parseWithReasoner(manager, this.ontology);
            }
            StringDocumentTarget parsedOntology = new StringDocumentTarget();
            manager.saveOntology(this.ontology, new RDFXMLDocumentFormat(), parsedOntology);
            result = parsedOntology.toString();
            FileWriter writer;
            File file = new File("/var/tmp/reasoned-onto.rdf");
            try {
                file.createNewFile();
                writer = new FileWriter(file);
                writer.write(result);
                writer.flush();
                writer.close();
            } catch (IOException ex) {
                Logger.getLogger(DocumentationBuilder.class.getName()).log(Level.SEVERE, null, ex);
            }
        }

        return result;
    }

    /*private OWLOntology parseWithReasoner(OWLOntologyManager manager, OWLOntology ontology) {
        try {
            PelletOptions.load(new URL("http://" + cssLocation + "pellet.properties"));
            PelletReasoner reasoner = PelletReasonerFactory.getInstance().createReasoner(ontology);
            reasoner.getKB().prepare();
            List<InferredAxiomGenerator<? extends OWLAxiom>> generators  = new ArrayList();
            generators.add(new InferredSubClassAxiomGenerator());
            generators.add(new InferredClassAssertionAxiomGenerator());
            generators.add(new InferredDisjointClassesAxiomGenerator());
            generators.add(new InferredEquivalentClassAxiomGenerator());
            generators.add(new InferredEquivalentDataPropertiesAxiomGenerator());
            generators.add(new InferredEquivalentObjectPropertyAxiomGenerator());
            generators.add(new InferredInverseObjectPropertiesAxiomGenerator());
            generators.add(new InferredPropertyAssertionGenerator());
            generators.add(new InferredSubDataPropertyAxiomGenerator());
            generators.add(new InferredSubObjectPropertyAxiomGenerator());

            InferredOntologyGenerator iog = new InferredOntologyGenerator(reasoner, generators);

            OWLOntologyID id = ontology.getOntologyID();
            Set<OWLImportsDeclaration> declarations = ontology.getImportsDeclarations();
            Set<OWLAnnotation> annotations = ontology.getAnnotations();

            Map<OWLEntity, Set<OWLAnnotationAssertionAxiom>> entityAnnotations = new THashMap();
            for (OWLClass aEntity : ontology.getClassesInSignature()) {
                entityAnnotations.put(aEntity, aEntity.getAnnotationAssertionAxioms(ontology));
            }
            for (OWLObjectProperty aEntity : ontology.getObjectPropertiesInSignature()) {
                entityAnnotations.put(aEntity, aEntity.getAnnotationAssertionAxioms(ontology));
            }
            for (OWLDataProperty aEntity : ontology.getDataPropertiesInSignature()) {
                entityAnnotations.put(aEntity, aEntity.getAnnotationAssertionAxioms(ontology));
            }
            for (OWLNamedIndividual aEntity : ontology.getIndividualsInSignature()) {
                entityAnnotations.put(aEntity, aEntity.getAnnotationAssertionAxioms(ontology));
            }
            for (OWLAnnotationProperty aEntity : ontology.getAnnotationPropertiesInSignature()) {
                entityAnnotations.put(aEntity, aEntity.getAnnotationAssertionAxioms(ontology));
            }
            for (OWLDatatype aEntity : ontology.getDatatypesInSignature()) {
                entityAnnotations.put(aEntity, aEntity.getAnnotationAssertionAxioms(ontology));
            }

            manager.removeOntology(ontology);
            OWLOntology inferred = manager.createOntology(id);
            iog.fillOntology(manager, inferred);

            for (OWLImportsDeclaration decl : declarations) {
                manager.applyChange(new AddImport(inferred, decl));
            }
            for (OWLAnnotation ann : annotations) {
                manager.applyChange(new AddOntologyAnnotation(inferred, ann));
            }
            for (OWLClass aEntity : inferred.getClassesInSignature()) {
                applyAnnotations(aEntity, entityAnnotations, manager, inferred);
            }
            for (OWLObjectProperty aEntity : inferred.getObjectPropertiesInSignature()) {
                applyAnnotations(aEntity, entityAnnotations, manager, inferred);
            }
            for (OWLDataProperty aEntity : inferred.getDataPropertiesInSignature()) {
                applyAnnotations(aEntity, entityAnnotations, manager, inferred);
            }
            for (OWLNamedIndividual aEntity : inferred.getIndividualsInSignature()) {
                applyAnnotations(aEntity, entityAnnotations, manager, inferred);
            }
            for (OWLAnnotationProperty aEntity : inferred.getAnnotationPropertiesInSignature()) {
                applyAnnotations(aEntity, entityAnnotations, manager, inferred);
            }
            for (OWLDatatype aEntity : inferred.getDatatypesInSignature()) {
                applyAnnotations(aEntity, entityAnnotations, manager, inferred);
            }

            return inferred;
        } catch (MalformedURLException e1) {
            return ontology;
        } catch (IOException e1) {
            return ontology;
        } catch (OWLOntologyCreationException e) {
            return ontology;
        }
    }*/
    private void applyAnnotations(
            OWLEntity aEntity, Map<OWLEntity, Set<OWLAnnotationAssertionAxiom>> entityAnnotations,
            OWLOntologyManager manager, OWLOntology ontology) {
        Set<OWLAnnotationAssertionAxiom> entitySet = entityAnnotations.get(aEntity);
        if (entitySet != null) {
            for (OWLAnnotationAssertionAxiom ann : entitySet) {
                manager.addAxiom(ontology, ann);
            }
        }
    }

    /*
     private String getErrorPage(Exception e) {
     return "<html>"
     + "<head><title>LODE error</title></head>"
     + "<body>"
     + "<h2>"
     + "LODE error"
     + "</h2>"
     + "<p><strong>Reason: </strong>"
     + e.getMessage()
     + "</p>"
     + "</body>"
     + "</html>";
     }*/
    private String applyXSLTTransformation(String source, String ontologyUrl, String lang)
            throws TransformerException {
        TransformerFactory tfactory = new net.sf.saxon.TransformerFactoryImpl();
        ByteArrayOutputStream output = new ByteArrayOutputStream();
        ClassLoader classloader = this.getClass().getClassLoader();// Thread.currentThread().getContextClassLoader();
        InputStream is = classloader.getResourceAsStream("lode/extraction.xsl");
        StreamSource ss = new StreamSource(is, "UTF-8");
        Transformer transformer = tfactory.newTransformer(ss);
//ss.setSystemId(xsltFile.toURI().toString());        

//Transformer transformer = tfactory.newTransformer(new StreamSource(xsltURL2));

        /*DocumentBuilderFactory domFact = DocumentBuilderFactory.newInstance();
         DocumentBuilder builder=null;
         try {
         builder = domFact.newDocumentBuilder();
         } catch (ParserConfigurationException ex) {
         Logger.getLogger(DocumentationBuilder.class.getName()).log(Level.SEVERE, null, ex);
         }
         Document doc=null;
         try {
         doc = builder.parse(is);
         } catch (SAXException ex) {
         Logger.getLogger(DocumentationBuilder.class.getName()).log(Level.SEVERE, null, ex);
         } catch (IOException ex) {
         Logger.getLogger(DocumentationBuilder.class.getName()).log(Level.SEVERE, null, ex);
         }
         DOMSource domSource = new DOMSource(doc);*/
        //Transformer transformer = tfactory.newTransformer(new StreamSource(xsltURL2));
        //Transformer transformer = tfactory.newTransformer(domSource);
        /*transformer.setURIResolver(new URIResolver() {
            @Override
            public Source resolve(String href, String base) throws TransformerException {
                final InputStream s = this.getClass().getClassLoader().getResourceAsStream("xslt/" + href);
                return new StreamSource(s);
            }
        });*/

        transformer.setParameter("css-location", cssLocation);
        transformer.setParameter("lang", lang);
        transformer.setParameter("ontology-url", ontologyUrl);
        transformer.setParameter("source", cssLocation + "source");

        StreamSource inputSource = new StreamSource(new StringReader(source));

        transformer.transform(inputSource, new StreamResult(output));

        return output.toString();
    }

}
