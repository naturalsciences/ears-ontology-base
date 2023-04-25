/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package be.naturalsciences.bmdc.ontology.writer;

import be.naturalsciences.bmdc.ontology.OntologyConstants;
import be.naturalsciences.bmdc.ontology.entities.AsConcept;
import be.naturalsciences.bmdc.ontology.entities.EarsTermLabel;
import be.naturalsciences.bmdc.ontology.entities.IAction;
import be.naturalsciences.bmdc.ontology.entities.ICountry;
import be.naturalsciences.bmdc.ontology.entities.IEarsTerm;
import be.naturalsciences.bmdc.ontology.entities.IEventDefinition;
import be.naturalsciences.bmdc.ontology.entities.IGenericEventDefinition;
import be.naturalsciences.bmdc.ontology.entities.IHarbour;
import be.naturalsciences.bmdc.ontology.entities.IOrganisation;
import be.naturalsciences.bmdc.ontology.entities.IParameter;
import be.naturalsciences.bmdc.ontology.entities.IProcess;
import be.naturalsciences.bmdc.ontology.entities.IProcessAction;
import be.naturalsciences.bmdc.ontology.entities.IProject;
import be.naturalsciences.bmdc.ontology.entities.IProperty;
import be.naturalsciences.bmdc.ontology.entities.ISeaArea;
import be.naturalsciences.bmdc.ontology.entities.ISpecificEventDefinition;
import be.naturalsciences.bmdc.ontology.entities.ISubject;
import be.naturalsciences.bmdc.ontology.entities.ITool;
import be.naturalsciences.bmdc.ontology.entities.IToolCategory;
import be.naturalsciences.bmdc.ontology.entities.IVessel;
import be.naturalsciences.bmdc.ontology.entities.Term;
import gnu.trove.set.hash.THashSet;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.Instant;
import java.time.ZoneOffset;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collection;
import java.util.Date;
import java.util.List;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import org.semanticweb.owlapi.apibinding.OWLManager;
import org.semanticweb.owlapi.model.AddImport;
import org.semanticweb.owlapi.model.AddOntologyAnnotation;
import org.semanticweb.owlapi.model.IRI;
import org.semanticweb.owlapi.model.OWLAnnotation;
import org.semanticweb.owlapi.model.OWLAnnotationAssertionAxiom;
import org.semanticweb.owlapi.model.OWLAnnotationProperty;
import org.semanticweb.owlapi.model.OWLAnnotationSubject;
import org.semanticweb.owlapi.model.OWLAnonymousIndividual;
import org.semanticweb.owlapi.model.OWLAxiom;
import org.semanticweb.owlapi.model.OWLClass;
import org.semanticweb.owlapi.model.OWLClassAssertionAxiom;
import org.semanticweb.owlapi.model.OWLDataFactory;
import org.semanticweb.owlapi.model.OWLDataProperty;
import org.semanticweb.owlapi.model.OWLDataPropertyAssertionAxiom;
import org.semanticweb.owlapi.model.OWLImportsDeclaration;
import org.semanticweb.owlapi.model.OWLIndividual;
import org.semanticweb.owlapi.model.OWLLiteral;
import org.semanticweb.owlapi.model.OWLNamedIndividual;
import org.semanticweb.owlapi.model.OWLObjectProperty;
import org.semanticweb.owlapi.model.OWLObjectPropertyAssertionAxiom;
import org.semanticweb.owlapi.model.OWLOntology;
import org.semanticweb.owlapi.model.OWLOntologyCreationException;
import org.semanticweb.owlapi.model.OWLOntologyID;
import org.semanticweb.owlapi.model.OWLOntologyManager;
import org.semanticweb.owlapi.model.SetOntologyID;
import org.semanticweb.owlapi.model.parameters.OntologyCopy;
import org.semanticweb.owlapi.util.DefaultPrefixManager;
import org.semanticweb.owlapi.util.OWLOntologyMerger;
import org.semanticweb.owlapi.vocab.OWL2Datatype;
import org.semanticweb.owlapi.vocab.OWLRDFVocabulary;

/**
 *
 * @author Thomas Vandenberghe
 */
public class EARSOntologyCreator {

    private static final Logger log = Logger.getLogger(EARSOntologyCreator.class.getName());

    private OWLOntologyManager manager;
    private DefaultPrefixManager pm;

    public static DefaultPrefixManager DEFAULT_PM;

    static {
        DEFAULT_PM = new DefaultPrefixManager();
        DEFAULT_PM.setDefaultPrefix(OntologyConstants.EARS2_NS);
        DEFAULT_PM.setPrefix("skos:", "http://www.w3.org/2004/02/skos/core#");
        DEFAULT_PM.setPrefix("dc:", "http://purl.org/dc/elements/1.1/");
        DEFAULT_PM.setPrefix("vc:", "http://www.w3.org/2006/vcard/ns#");
        DEFAULT_PM.setPrefix("gn:", "http://www.geonames.org/ontology#");
        DEFAULT_PM.setPrefix("geo:", "http://www.w3.org/2003/01/geo#");
        DEFAULT_PM.setPrefix("dbpedia:", "http://dbpedia.org/ontology/");
        DEFAULT_PM.setPrefix("mailto:", "");
    }

    private final ScopeMap scopeMap;
    private final String fullName;

    private Collection<? extends IToolCategory> toolCategoryCollection;
    private Collection<? extends ITool> toolCollection;
    private Collection<? extends IProcess> processCollection;
    private Collection<? extends IAction> actionCollection;
    private Collection<? extends IProperty> propertyCollection;
    private Collection<? extends IProcessAction> processActionCollection;
    private Collection<? extends ISpecificEventDefinition> sevCollection;
    private Collection<? extends IGenericEventDefinition> gevCollection;
    private Collection<? extends ISubject> subjectCollection;
    private Collection<? extends IVessel> vesselCollection;

    private Collection<? extends IParameter> parameterCollection;
    private Collection<? extends ISeaArea> seaAreaCollection;
    private Collection<? extends IHarbour> harbourCollection;
    private Collection<? extends IOrganisation> organisationCollection;
    private Collection<? extends ICountry> countryCollection;
    private Collection<? extends IProject> projectCollection;
    
    public List<AsConcept> getAllConceptsOfKind(String kindCode) {
        List<AsConcept> result = new ArrayList();
        switch (kindCode) {
            case "PRO":
                result.addAll(getProcessCollection());
                break;
            case "ACT":
                result.addAll(getActionCollection());
                break;
            case "SUJ":
                break;
            case "DEV":
                result.addAll(getToolCollection());
                break;
            case "PRY":
                result.addAll(getPropertyCollection());
                break;
            case "SEV":
                break;
            case "CTG":
                result.addAll(getToolCategoryCollection());
                break;
            default:
                return null;
        }
        return result;
    }

    public Collection<? extends IToolCategory> getToolCategoryCollection() {
        return toolCategoryCollection;
    }

    public void setToolCategoryCollection(Collection<? extends IToolCategory> toolCategoryCollection) {
        this.toolCategoryCollection = toolCategoryCollection;
    }

    public Collection<? extends ITool> getToolCollection() {
        return toolCollection;
    }

    public void setToolCollection(Collection<? extends ITool> toolCollection) {
        this.toolCollection = toolCollection;
    }

    public Collection<? extends IVessel> getVesselCollection() {
        return vesselCollection;
    }

    public void setVesselCollection(Collection<? extends IVessel> vesselCollection) {
        this.vesselCollection = vesselCollection;
    }

    public Collection<? extends IProcess> getProcessCollection() {
        return processCollection;
    }

    public void setProcessCollection(Collection<? extends IProcess> processCollection) {
        this.processCollection = processCollection;
    }

    public Collection<? extends IAction> getActionCollection() {
        return actionCollection;
    }

    public void setActionCollection(Collection<? extends IAction> actionCollection) {
        this.actionCollection = actionCollection;
    }

    public Collection<? extends IProperty> getPropertyCollection() {
        return propertyCollection;
    }

    public void setPropertyCollection(Collection<? extends IProperty> propertyCollection) {
        this.propertyCollection = propertyCollection;
    }

    public Collection<? extends IProcessAction> getProcessActionCollection() {
        return processActionCollection;
    }

    public void setProcessActionCollection(Collection<? extends IProcessAction> processActionCollection) {
        this.processActionCollection = processActionCollection;
    }

    public Collection<? extends ISpecificEventDefinition> getSevCollection() {
        return sevCollection;
    }

    public void setSevCollection(Collection<? extends ISpecificEventDefinition> sevCollection) {
        this.sevCollection = sevCollection;
    }

    public Collection<? extends IGenericEventDefinition> getGevCollection() {
        return gevCollection;
    }

    public void setGevCollection(Collection<? extends IGenericEventDefinition> gevCollection) {
        this.gevCollection = gevCollection;
    }

    public Collection<? extends IParameter> getParameterCollection() {
        return parameterCollection;
    }

    public void setParameterCollection(Collection<? extends IParameter> parameterCollection) {
        this.parameterCollection = parameterCollection;
    }

    public Collection<? extends ISeaArea> getSeaAreaCollection() {
        return seaAreaCollection;
    }

    public void setSeaAreaCollection(Collection<? extends ISeaArea> seaAreaCollection) {
        this.seaAreaCollection = seaAreaCollection;
    }

    public Collection<? extends IHarbour> getHarbourCollection() {
        return harbourCollection;
    }

    public void setHarbourCollection(Collection<? extends IHarbour> harbourCollection) {
        this.harbourCollection = harbourCollection;
    }

    public Collection<? extends IOrganisation> getOrganisationCollection() {
        return organisationCollection;
    }

    public void setOrganisationCollection(Collection<? extends IOrganisation> organisationCollection) {
        this.organisationCollection = organisationCollection;
    }

    public Collection<? extends ICountry> getCountryCollection() {
        return countryCollection;
    }

    public void setCountryCollection(Collection<? extends ICountry> countryCollection) {
        this.countryCollection = countryCollection;
    }

    public Collection<? extends ISubject> getSubjectCollection() {
        return subjectCollection;
    }

    public void setSubjectCollection(Collection<? extends ISubject> subjectCollection) {
        this.subjectCollection = subjectCollection;
    }

    public Collection<? extends IProject> getProjectCollection() {
        return projectCollection;
    }

    public void setProjectCollection(Collection<? extends IProject> projectCollection) {
        this.projectCollection = projectCollection;
    }

    public ScopeMap getScopeMap() {
        return scopeMap;
    }

    public String getFullName() {
        return fullName;
    }

    public OWLOntologyManager getManager() {
        return manager;
    }

    public OWLDataFactory getFactory() {
        return manager.getOWLDataFactory();
    }

    public EARSOntologyCreator(ScopeMap scopeMap, String fullName) {
        if (scopeMap.size() == 0) {
            throw new IllegalArgumentException("The scope is empty");
        }
        if ((scopeMap.sameScope(ScopeMap.VESSEL_SCOPE) || scopeMap.sameScope(ScopeMap.PROGRAM_SCOPE)) && (scopeMap.getScopedTo() == null || scopeMap.getScopedTo().isEmpty())) {
            throw new IllegalArgumentException("Vessel or program scopes must contain an actual vessel or program.");
        }
        if ((scopeMap.sameScope(ScopeMap.BASE_SCOPE) || scopeMap.sameScope(ScopeMap.STATIC_SCOPE) || scopeMap.sameScope(ScopeMap.TEST_SCOPE)) && (scopeMap.getScopedTo() != null && !scopeMap.getScopedTo().isEmpty())) {
            throw new IllegalArgumentException("Base, test or static scopes may not specify an actual scope value");
        }
        if (fullName == null) {
            throw new IllegalArgumentException("The name of the ontology cannot be null. Use an empty string if no name is needed.");
        } else {
            this.fullName = fullName;
        }
        this.manager = OWLManager.createOWLOntologyManager();
        this.scopeMap = scopeMap;

        //write the prefixes
        this.pm = new DefaultPrefixManager();
        pm.setDefaultPrefix(OntologyConstants.EARS2_NS);
        pm.setPrefix("skos:", "http://www.w3.org/2004/02/skos/core#");
        pm.setPrefix("dc:", "http://purl.org/dc/elements/1.1/");
        pm.setPrefix("vc:", "http://www.w3.org/2006/vcard/ns#");
        pm.setPrefix("gn:", "http://www.geonames.org/ontology#");
        pm.setPrefix("geo:", "http://www.w3.org/2003/01/geo#");
        pm.setPrefix("dbpedia:", "http://dbpedia.org/ontology/");
        pm.setPrefix("mailto:", "");
    }

    private void makeConceptAssertions(OWLOntology onto, OWLNamedIndividual individualConcept, Term term) throws OWLOntologyCreationException {
        try {
            //OWLOntologyManager manager = OWLManager.createOWLOntologyManager();
            OWLDataFactory factory = manager.getOWLDataFactory();

            Set<OWLAxiom> axioms = new THashSet();
            OWLClass conceptCl = factory.getOWLClass("skos:Concept", pm);
            OWLClassAssertionAxiom isConceptAssertion = factory.getOWLClassAssertionAxiom(conceptCl, individualConcept);
            axioms.add(isConceptAssertion);

            //EarsTerm substitute=term.getSubstituteRef();
            //EarsTerm synonym=term.getSynonymRef();
            /*
             if(term.getStatus() != null){
             ItemStatus status=term.getStatus();
             OWLLiteral isDataProviderLiteral = factory.getOWLLiteral(sev.getIsDataProvider());
             OWLDataProperty isDataProviderProp = factory.getOWLDataProperty(":isDataProvider", pm);
             OWLDataPropertyAssertionAxiom isDataProviderAssertion= factory.getOWLDataPropertyAssertionAxiom(isDataProviderProp, individualSev, isDataProviderLiteral);
             axioms.add(isDataProviderAssertion);
             }*/
            for (IEarsTerm.Language lang : IEarsTerm.Language.values()) {
                if (term.getEarsTermLabel(lang) != null) {
                    EarsTermLabel label = term.getEarsTermLabel(lang);
                    if (label.getPrefLabel() != null) {
                        addAnnotation(onto, manager, individualConcept, "skos:prefLabel", label.getPrefLabel().trim(), lang.name());
                    }
                    if (label.getAltLabel() != null) {
                        addAnnotation(onto, manager, individualConcept, "skos:altLabel", label.getAltLabel().trim(), lang.name());
                    }
                    if (label.getDefinition() != null) {
                        addAnnotation(onto, manager, individualConcept, "skos:definition", label.getDefinition().trim(), lang.name());
                    }
                }
            }

            OWLAnnotationProperty identifierProperty = factory.getOWLAnnotationProperty("dc:identifier", pm);
            OWLLiteral identifierValue = null;
            OWLLiteral dateValue = null;
            if (!term.isPublished()) {
                identifierValue = factory.getOWLLiteral(term.getOrigUrn().trim(), OWL2Datatype.XSD_STRING);
                /*OWLAnnotationProperty creationDateProperty = factory.getOWLAnnotationProperty("dc:created", pm);
                 dateValue = factory.getOWLLiteral(dateToISO8601String(term.getCreationDate()), OWL2Datatype.XSD_DATE_TIME);
                 axioms.add(factory.getOWLAnnotationAssertionAxiom(creationDateProperty, (OWLAnnotationSubject) (individualConcept.getIRI()), dateValue));

                 if (term.getModifDate() != null) {
                 OWLAnnotationProperty modificationDateProperty = factory.getOWLAnnotationProperty("dc:modified", pm);
                 dateValue = factory.getOWLLiteral(dateToISO8601String(term.getModifDate()), OWL2Datatype.XSD_DATE_TIME);
                 axioms.add(factory.getOWLAnnotationAssertionAxiom(modificationDateProperty, (OWLAnnotationSubject) (individualConcept.getIRI()), dateValue));
                 }*/
                OWLLiteral value = null;
                OWLAnnotationProperty annotation = factory.getOWLAnnotationProperty("dc:creator", pm); //replaces submitter
                if (term.getSubmitter() != null) {
                    value = factory.getOWLLiteral(term.getSubmitter(), OWL2Datatype.XSD_STRING);
                } else {
                    value = factory.getOWLLiteral(term.getCreator(), OWL2Datatype.XSD_STRING);
                }

                axioms.add(factory.getOWLAnnotationAssertionAxiom(annotation, (OWLAnnotationSubject) (individualConcept.getIRI()), value));

                annotation = factory.getOWLAnnotationProperty("dc:publisher", pm);
                value = factory.getOWLLiteral(OntologyConstants.EFGOV);
                axioms.add(factory.getOWLAnnotationAssertionAxiom(annotation, (OWLAnnotationSubject) (individualConcept.getIRI()), value));

            } else {
                identifierValue = factory.getOWLLiteral(term.getPublisherUrn().trim(), OWL2Datatype.XSD_STRING);

                OWLAnnotationProperty annotation = factory.getOWLAnnotationProperty("dc:creator", pm);
                OWLLiteral value = factory.getOWLLiteral(OntologyConstants.BODCGOV);
                axioms.add(factory.getOWLAnnotationAssertionAxiom(annotation, (OWLAnnotationSubject) (individualConcept.getIRI()), value));

                annotation = factory.getOWLAnnotationProperty("dc:publisher", pm);
                value = factory.getOWLLiteral(OntologyConstants.BODCGOV);
                axioms.add(factory.getOWLAnnotationAssertionAxiom(annotation, (OWLAnnotationSubject) (individualConcept.getIRI()), value));

                /*OWLAnnotationProperty modificationDateProperty = factory.getOWLAnnotationProperty("dc:modified", pm);
                 dateValue = factory.getOWLLiteral(dateToISO8601String(term.getModifDate()), OWL2Datatype.XSD_DATE_TIME);
                 axioms.add(factory.getOWLAnnotationAssertionAxiom(modificationDateProperty, (OWLAnnotationSubject) (individualConcept.getIRI()), dateValue));*/
            }
            OWLAnnotationProperty creationDateProperty = factory.getOWLAnnotationProperty("dc:created", pm);
            dateValue = factory.getOWLLiteral(dateToISO8601String(term.getCreationDate()), OWL2Datatype.XSD_DATE_TIME);
            axioms.add(factory.getOWLAnnotationAssertionAxiom(creationDateProperty, (OWLAnnotationSubject) (individualConcept.getIRI()), dateValue));

            if (term.getModifDate() != null) {
                OWLAnnotationProperty modificationDateProperty = factory.getOWLAnnotationProperty("dc:modified", pm);
                dateValue = factory.getOWLLiteral(dateToISO8601String(term.getModifDate()), OWL2Datatype.XSD_DATE_TIME);
                axioms.add(factory.getOWLAnnotationAssertionAxiom(modificationDateProperty, (OWLAnnotationSubject) (individualConcept.getIRI()), dateValue));
            }
            axioms.add(factory.getOWLAnnotationAssertionAxiom(identifierProperty, (OWLAnnotationSubject) (individualConcept.getIRI()), identifierValue));

            if (term.getStatus() != null) {
                /*OWLAnnotationProperty hasNoteProperty = factory.getOWLAnnotationProperty(":status", pm);
                 OWLLiteral hasNoteValue = factory.getOWLLiteral(term.getStatus().getName(), "en");
                 axioms.add(factory.getOWLAnnotationAssertionAxiom(hasNoteProperty, (OWLAnnotationSubject) (individualConcept.getIRI()), hasNoteValue));*/
                OWLDataProperty statusProp = factory.getOWLDataProperty(":status", pm);
                OWLLiteral statusLiteral = factory.getOWLLiteral(term.getStatus().getName(), OWL2Datatype.XSD_STRING);
                OWLDataPropertyAssertionAxiom submitterAssertion = factory.getOWLDataPropertyAssertionAxiom(statusProp, individualConcept, statusLiteral);
                axioms.add(submitterAssertion);
            } else {
                if (scopeMap.sameScope(ScopeMap.BASE_SCOPE)) {
                    throw new OWLOntologyCreationException("Cannot build BASE ontology for terms without a status");
                } else {
                    reportError("Warning: no status provided for term " + term.getUri().toString() + "; provisorily set to WaitForApproval", null);
                    term.setStatusName("WA"); //waitforapproval
                }
            }

            OWLAnnotationProperty versionProperty = factory.getOWLAnnotationProperty("owl:versionInfo", pm);
            OWLLiteral versionValue = factory.getOWLLiteral(term.getVersionInfo(), OWL2Datatype.XSD_STRING);
            axioms.add(factory.getOWLAnnotationAssertionAxiom(versionProperty, (OWLAnnotationSubject) (individualConcept.getIRI()), versionValue));

            /*OWLDataProperty submitterProp = factory.getOWLDataProperty(":submitter", pm);
             OWLLiteral submitterLiteral = factory.getOWLLiteral(term.getSubmitter(), OWL2Datatype.XSD_STRING);
             OWLDataPropertyAssertionAxiom submitterAssertion = factory.getOWLDataPropertyAssertionAxiom(submitterProp, individualConcept, submitterLiteral);
             axioms.add(submitterAssertion);*/

 /*OWLDataProperty controllerProp = factory.getOWLDataProperty(":controller", pm);
             OWLLiteral controllerLiteral = factory.getOWLLiteral(term.getController(), OWL2Datatype.XSD_STRING);
             OWLDataPropertyAssertionAxiom controllerAssertion = factory.getOWLDataPropertyAssertionAxiom(controllerProp, individualConcept, controllerLiteral);
             axioms.add(controllerAssertion);*/
 /*somehow here it is important to not store references top terms that are undefined and WILL never be defined*/
            if (term.getSubstituteRef() != null && !term.equals(term.getSubstituteRef())) { //I have a substitute, so I am an old term; but I am not the same as that term
               // List<AsConcept> allConceptsOfKind = getAllConceptsOfKind(term.getKind());
               // boolean 
               // for (AsConcept concept : allConceptsOfKind) {
               //     if (concept.getTermRef().getId().equals(term.getId()))
               // }
               // if (allConcepts.contains(term)) { //the old term that has been replaced is actually present in the list of provided terms during class instantiation. If not we do nothing, else we would have an illegal tree
                    OWLObjectProperty substituteProp = factory.getOWLObjectProperty(":supersededBy", pm);
                    OWLNamedIndividual substituteIndividual = factory.getOWLNamedIndividual(":concept_" + term.getSubstituteRef().getId(), pm);
                    OWLObjectPropertyAssertionAxiom substituteAssertion = factory.getOWLObjectPropertyAssertionAxiom(substituteProp, individualConcept, substituteIndividual);
                    axioms.add(substituteAssertion);
               // }
            }
            /* TODO
             if (term.getSynonymRef() != null) {
             OWLObjectProperty synonymProp = factory.getOWLObjectProperty(":isSynonymOf", pm);
             OWLNamedIndividual synonymIndividual = factory.getOWLNamedIndividual(":concept_" + term.getSynonymRef().getId(), pm);
             OWLObjectPropertyAssertionAxiom synonymAssertion = factory.getOWLObjectPropertyAssertionAxiom(synonymProp, individualConcept, synonymIndividual);
             axioms.add(synonymAssertion);
             }
             */
            if (term.getSameAs() != null) {
                OWLNamedIndividual theSameAs = makeSimpleIndividual(manager, term.getSameAs());
                makeObjectProperty(onto, manager, "owl:sameAs", individualConcept, theSameAs);
            }
            if (term.getBroader() != null) {
                OWLNamedIndividual theBroader = makeSimpleIndividual(manager, term.getBroader());
                makeObjectProperty(onto, manager, "skos:broader", individualConcept, theBroader);
            }
            manager.addAxioms(onto, axioms);
            //return axioms;
        } catch (OWLOntologyCreationException e) {
            reportError("OWLOntologyCreationException with term " + term.getUri().toString() + ". Message:" + e.getMessage(), e);
            throw e;

        } catch (Exception e) {
            reportError("Exception with term " + term.getUri().toString() + ". Message:" + e.getMessage(), e);
            throw e;
        }
    }

    private String dateToISO8601String(Date date) {
        //return new DateTime(date, DateTimeZone.forID("Europe/Paris")).toString();
        if (date == null) {
            return Instant.now().atZone(ZoneOffset.UTC).toString();
        } else {
            Calendar calendar = Calendar.getInstance();
            calendar.setTime(date);
            int seconds = calendar.get(Calendar.SECOND);
            if (seconds == 0) {
                return date.toInstant().plusSeconds(1).atZone(ZoneOffset.UTC).toString();
            } else {
                return date.toInstant().atZone(ZoneOffset.UTC).toString();
            }
        }
    }

    private String getBodcURI(String sdnCode) {
        String[] tokens = sdnCode.split(":");
        if (tokens.length < 4) {
            return null;
        }
        return OntologyConstants.NERC_URL_PREFIX + tokens[1] + "/current/" + tokens[3] + "/";
    }

    private OWLOntology loadOntology(IRI iri) throws OWLOntologyCreationException {
        OWLOntology onto = null;
        try {
            onto = manager.loadOntologyFromOntologyDocument(iri);

            if (onto == null) {
                throw new OWLOntologyCreationException("Failed in loading ontology from URI: " + iri.toURI().toString(), null);
            }
        } catch (OWLOntologyCreationException e) {
            throw new OWLOntologyCreationException("Failed in loading ontology from URI: " + iri.toURI().toString(), e);
        }
        return onto;
    }

    private static IRI getIRIfromURL(URL url) throws URISyntaxException {
        URI uri;
        if (url == null) {
            return null;
        }
        uri = url.toURI();
        return getIRIfromURI(uri);

    }

    private static IRI getIRIfromURI(URI uri) {
        return IRI.create(uri);

    }

    private static IRI getIRIfromPath(Path path) {
        URI uri = path.toUri();
        return getIRIfromURI(uri);
    }

    public enum LoadOnto {

        IMPORT, PASTE
    }

    public void writeOntologyMetadata(OWLOntologyManager manager, OWLOntology earsOnto, String fullName, ScopeMap scopeMap, int newVersion) {
        /*DefaultPrefixManager pm = new DefaultPrefixManager();
         pm.setDefaultPrefix(OntologyConstants.EARS2_NS);
         pm.setPrefix("skos:", "http://www.w3.org/2004/02/skos/core#");
         pm.setPrefix("dc:", "http://purl.org/dc/elements/1.1/");
         pm.setPrefix("vc:", "http://www.w3.org/2006/vcard/ns#");
         pm.setPrefix("gn:", "http://www.geonames.org/ontology#");
         pm.setPrefix("geo:", "http://www.w3.org/2003/01/geo#");
         pm.setPrefix("mailto:", "");*/
        OWLDataFactory factory = manager.getOWLDataFactory();
        DefaultPrefixManager pm = DEFAULT_PM;
        Date now = new Date();
        String today = OntologyConstants.YYYYMMDD_FM.format(now);
        String baseIRIString = OntologyConstants.EARS2_BASE_NS;
        //String scope;

        //write the base iri
        /*if (scopeMap.getScope() == ScopeMap.Scope.BASE) {
         baseIRIString = baseOnto.getOntologyID().getOntologyIRI().get().toString(); //http://ontologies.ef-ears.eu/ears2/1
         } else {
         baseIRIString = baseOnto.getOntologyID().getOntologyIRI().get().toString(); //http://ontologies.ef-ears.eu/ears2/1
         //baseIRIString = baseOnto.getOntologyID().getOntologyIRI().get().toString() + "/" + scopeMap.getScopedTo(); //http://ontologies.ef-ears.eu/ears2/1/SDN:C17::11BE
         }*/
        String versionIRIString = baseIRIString + "/" + scopeMap.getScope().name() + "/" + today + "#";
        IRI baseIRI = IRI.create(baseIRIString + "#");
        IRI versionIRI = IRI.create(versionIRIString);

        OWLOntologyID newOntologyID = new OWLOntologyID(baseIRI, versionIRI);
        SetOntologyID set = new SetOntologyID(earsOnto, newOntologyID);
        //SetOntologyID set = new SetOntologyID(earsOnto, versionIRI);
        manager.applyChange(set);

        //write the versionInfo
        OWL2Datatype string = OWL2Datatype.valueOf("XSD_STRING");
        OWLLiteral lit = factory.getOWLLiteral(Integer.toString(newVersion), string);
        OWLAnnotationProperty versionInfoAnnotationProperty = factory.getOWLAnnotationProperty(OWLRDFVocabulary.OWL_VERSION_INFO.getIRI());
        OWLAnnotation versionInfoAnnotation = factory.getOWLAnnotation(versionInfoAnnotationProperty, lit);

        manager.applyChange(new AddOntologyAnnotation(earsOnto, versionInfoAnnotation));

        //write the dc:modified
        OWLAnnotation modifiedAnnotation = createAnnotation(manager, "dc:modified", dateToISO8601String(now), OWL2Datatype.XSD_DATE_TIME);
        manager.applyChange(new AddOntologyAnnotation(earsOnto, modifiedAnnotation));

        //write the ontology label <rdfs:label>EarsV2 Ontology</rdfs:label>
        lit = factory.getOWLLiteral(fullName, string);
        OWLAnnotationProperty labelAnnotationProperty = factory.getOWLAnnotationProperty(OWLRDFVocabulary.RDFS_LABEL.getIRI());
        OWLAnnotation labelInfoAnnotation = factory.getOWLAnnotation(labelAnnotationProperty, lit);

        manager.applyChange(new AddOntologyAnnotation(earsOnto, labelInfoAnnotation));

        //write the scope
        OWLLiteral lit2 = factory.getOWLLiteral((String) scopeMap.getScope().name(), string);
        OWLAnnotationProperty scopeAnnotationProperty = factory.getOWLAnnotationProperty("scope", pm);
        OWLAnnotation scopeAnnotation = factory.getOWLAnnotation(scopeAnnotationProperty, lit2);

        manager.applyChange(new AddOntologyAnnotation(earsOnto, scopeAnnotation));

        if ((scopeMap.sameScope(ScopeMap.VESSEL_SCOPE) || scopeMap.sameScope(ScopeMap.PROGRAM_SCOPE)) && (scopeMap.getScopedTo() == null || scopeMap.getScopedTo().equals(""))) {
            throw new IllegalArgumentException("Vessel or program scopes must contain an actual vessel or program.");
        } else {
            OWLLiteral lit3 = factory.getOWLLiteral((String) scopeMap.getScopedTo(), string);
            OWLAnnotationProperty scopedToAnnotationProperty = factory.getOWLAnnotationProperty("scopedTo", pm);
            OWLAnnotation scopedToAnnotation = factory.getOWLAnnotation(scopedToAnnotationProperty, lit3);

            manager.applyChange(new AddOntologyAnnotation(earsOnto, scopedToAnnotation));
        }

    }

    /**
     * *
     * Creates an OWLOntology. Fills in all base characteristics of an EARS
     * ontology such as prefix, imports, versionInfo and scope. Adds all
     * individuals set by the setXxxxList() methods. Doesn't add these if they
     * are null/empty and creates an ontology with just the axioms.
     *
     * @param importOrPaste Whether the axiomatic ontology should be imported or
     * written out (true=use owl:imports; false=copy)
     * @param axiomaUrl
     * @return
     * @throws OWLOntologyCreationException
     */
    public OWLOntology createOntology(LoadOnto importOrPaste, URL axiomaUrl, int newVersion) throws OWLOntologyCreationException {
        //File axFile = null;
        //URL axUrl = null;
        if (importOrPaste == LoadOnto.PASTE && axiomaUrl == null) {
            throw new IllegalArgumentException("The ontology axiom's url must be provided when pasting the axioms into an existing ontology.");
        }
        OWLOntology earsOnto = null;
        /*if (importOrPaste == LoadOnto.PASTE && (scopeMap.containsKey(ScopeMap.Scope.BASE) || scopeMap.containsKey(ScopeMap.Scope.STATIC))) { //if the axioms should be pasted and I'm being run on the ontology server (http://ontologies.ef-ears.eu).
         axFile = FileUtils.lastFileName(OntologyConstants.ONTO_AXIOM_SERVER_PATH, "xml");
         try {
         //axiomaticOntologyFileurl = Thread.currentThread().getContextClassLoader().getResource(ONTO_AXIOM_PATH);

         axUrl = Paths.get(axFile.getCanonicalPath()).toUri().toURL();

         } catch (IOException ex) {
         throw new OWLOntologyCreationException("Base ontology axiom owl file not found on local filesystem", ex);
         }
         /*} else if (importOrPaste == LoadOnto.PASTE && !scopeMap.containsKey(ScopeMap.Scope.BASE)) { //if the axioms should be pasted and I'm being run away from the ontology server (http://ontologies.ef-ears.eu).
         throw new IllegalArgumentException("Ontology axioms must be imported when not run on the ontology server (http://ontologies.ef-ears.eu).");*/
 /* } else { //If the axioms should be imported && I'm being run away from or on the ontology server http://ontologies.ef-ears.eu

         axUrl = retriever.getLatestOntologyAxiomUrl();
         // axFile = new File(axUrl.getPath());
         //File axiomaticOntologyFile= EARSOntologyRetriever.
         }*/
        if (axiomaUrl != null) {
            IRI axiomaticOntologyIri;
            try {
                axiomaticOntologyIri = getIRIfromURL(axiomaUrl);
            } catch (URISyntaxException ex) {
                throw new OWLOntologyCreationException("Couldn't retrieve the ontology axiom owl file from the ears ontology server.", ex);
            }
            //EARSOntologyRetriever retriever = new EARSOntologyRetriever();

            //OWLOntology baseOnto = manager.loadOntology(axiomaticOntologyIri);
            OWLDataFactory factory = this.manager.getOWLDataFactory();
            if (importOrPaste == LoadOnto.PASTE) { //paste
                earsOnto = manager.loadOntology(axiomaticOntologyIri);
            } else { //import
                earsOnto = manager.createOntology();
                //write the owl:imports
                OWLImportsDeclaration importDeclaraton = factory.getOWLImportsDeclaration(IRI.create(axiomaUrl));
                this.manager.applyChange(new AddImport(earsOnto, importDeclaraton));
            }

            writeOntologyMetadata(this.manager, earsOnto, this.fullName, this.scopeMap, newVersion);

            //write the individuals
            try {
                saveParameters(earsOnto);
            } catch (Exception e) {
                reportError("Failed in saving parameters: " + e.getMessage(), e);
                throw new OWLOntologyCreationException("Failed in saving parameters: ", e);
            }

            try {
                saveToolCategories(earsOnto);
            } catch (Exception e) {
                reportError("Failed in saving tool categories: " + e.getMessage(), e);
                throw new OWLOntologyCreationException("Failed in saving tool categories: ", e);
            }

            try {
                saveTools(earsOnto);
            } catch (Exception e) {
                reportError("Failed in saving tools: " + e.getMessage(), e);
                throw new OWLOntologyCreationException("Failed in saving tools: ", e);
            }

            try {
                saveVessels(earsOnto);
            } catch (Exception e) {
                reportError("Failed in saving vessels: " + e.getMessage(), e);
                throw new OWLOntologyCreationException("Failed in saving vessels: ", e);
            }

            try {
                saveProcesses(earsOnto);
            } catch (Exception e) {
                reportError("Failed in saving processes: " + e.getMessage(), e);
                throw new OWLOntologyCreationException("Failed in saving processes: ", e);
            }

            try {
                saveActions(earsOnto);
            } catch (Exception e) {
                reportError("Failed in saving actions: " + e.getMessage(), e);
                throw new OWLOntologyCreationException("Failed in saving actions: ", e);
            }

            try {
                saveProperties(earsOnto);
            } catch (Exception e) {
                reportError("Failed in saving properties: " + e.getMessage(), e);
                throw new OWLOntologyCreationException("Failed in saving properties: ", e);
            }

            try {
                saveProcessActions(earsOnto);
            } catch (Exception e) {
                reportError("Failed in saving process-actions: " + e.getMessage(), e);
                throw new OWLOntologyCreationException("Failed in saving process-actions: ", e);
            }

            try {
                saveGenericEvents(earsOnto);
            } catch (Exception e) {
                reportError("Failed in saving generic events: " + e.getMessage(), e);
                throw new OWLOntologyCreationException("Failed in saving generic events: ", e);
            }

            try {
                saveSpecificEvents(earsOnto);
            } catch (Exception e) {
                reportError("Failed in saving specific events: " + e.getMessage(), e);
                throw new OWLOntologyCreationException("Failed in saving specific events: ", e);
            }

            try {
                saveSubjects(earsOnto);
            } catch (Exception e) {
                reportError("Failed in saving subjects: " + e.getMessage(), e);
                throw new OWLOntologyCreationException("Failed in saving subjects: ", e);
            }

            try {
                saveHarbours(earsOnto);
            } catch (Exception e) {
                reportError("Failed in saving harbours: " + e.getMessage(), e);
                throw new OWLOntologyCreationException("Failed in saving harbours: ", e);
            }
            try {
                saveSeaAreas(earsOnto);
            } catch (Exception e) {
                reportError("Failed in saving sea areas: " + e.getMessage(), e);
                throw new OWLOntologyCreationException("Failed in saving sea areas: ", e);
            }
            try {
                saveOrganisations(earsOnto);
            } catch (Exception e) {
                reportError("Failed in saving organisations: " + e.getMessage(), e);
                throw new OWLOntologyCreationException("Failed in saving organisations: ", e);
            }
            try {
                saveCountries(earsOnto);
            } catch (Exception e) {
                reportError("Failed in saving countries: " + e.getMessage(), e);
                throw new OWLOntologyCreationException("Failed in saving countries: ", e);
            }
            try {
                saveProjects(earsOnto);
            } catch (Exception e) {
                reportError("Failed in saving projects: " + e.getMessage(), e);
                throw new OWLOntologyCreationException("Failed in saving countries: ", e);
            }
        }
        return earsOnto;
    }

    /**
     * *
     * Create an OWLNamedIndividual within the ears2 namespace given the
     * AsConcept ac.
     *
     * @param ac
     * @param factory
     * @param pm
     * @return
     */
    public OWLNamedIndividual makeEARSIndividual(AsConcept ac, OWLDataFactory factory, DefaultPrefixManager pm) {
        OWLNamedIndividual individual = null;
        if (ac.getUri() == null && ac.getId() != null) {
            individual = factory.getOWLNamedIndividual(":" + buildUrlFragment(ac), pm);
        } else {
            individual = factory.getOWLNamedIndividual(IRI.create(ac.getUri()));
        }
        return individual;
    }

    /**
     * *
     * Create a literal data property of a literal datatype for an OWLIndividual
     * individual, of OWL2Datatype type. If type is null,
     * http://www.w3.org/2001/XMLSchema#string is assumed.
     *
     * @param onto
     * @param manager
     * @param key
     * @param value
     * @param individual
     * @param type
     * @return
     */
    private OWLOntologyManager makeLiteralDataProperty(OWLOntology onto, OWLOntologyManager manager, String key, String value, OWLIndividual individual, OWL2Datatype type) {
        OWLDataFactory factory = manager.getOWLDataFactory();
        OWLLiteral literal = null;
        if (type == null) {
            literal = factory.getOWLLiteral(value);
        } else {
            literal = factory.getOWLLiteral(value, type);
        }

        OWLDataProperty property = factory.getOWLDataProperty(key, pm);
        OWLDataPropertyAssertionAxiom assertion = factory.getOWLDataPropertyAssertionAxiom(property, individual, literal);
        manager.addAxiom(onto, assertion);
        return manager;
    }

    /**
     * *
     * Create an OWLNamedIndividual with IRI given in abbreviatedIRI (of form
     * namespace:property).
     *
     * @param ac
     * @param factory
     * @param pm
     * @return
     */
    private OWLNamedIndividual makeIndividual(OWLOntologyManager manager, String abbreviatedIRI) {
        OWLDataFactory factory = manager.getOWLDataFactory();
        OWLNamedIndividual individual = factory.getOWLNamedIndividual(abbreviatedIRI, pm);
        return individual;
    }

    /**
     * *
     * Create an OWLAnonymousIndividual (without an url).
     *
     * @param manager
     * @return
     */
    private OWLAnonymousIndividual makeAnonymousIndividual(OWLOntologyManager manager) {
        OWLDataFactory factory = manager.getOWLDataFactory();
        OWLAnonymousIndividual individual = factory.getOWLAnonymousIndividual();
        return individual;
    }

    /**
     * *
     * Create an OWLNamedIndividual with a free-form url
     *
     * @param manager
     * @param uri
     * @return
     */
    private OWLNamedIndividual makeSimpleIndividual(OWLOntologyManager manager, String uri) {
        OWLDataFactory factory = manager.getOWLDataFactory();
        IRI iri = IRI.create(uri);
        OWLNamedIndividual individual = factory.getOWLNamedIndividual(iri);
        return individual;
    }

    /**
     * *
     * Link 2 OWLIndividuals individual1, individual2 together via an
     * ObjectProperty abbreviatedIRI (of form namespace:property).
     *
     * @param manager
     * @param uri
     * @return
     */
    private OWLOntologyManager makeObjectProperty(OWLOntology onto, OWLOntologyManager manager, String abbreviatedIRI, OWLIndividual individual1, OWLIndividual individual2) {
        OWLDataFactory factory = manager.getOWLDataFactory();
        OWLObjectProperty property = factory.getOWLObjectProperty(abbreviatedIRI, pm);
        OWLObjectPropertyAssertionAxiom assertion = factory.getOWLObjectPropertyAssertionAxiom(property, individual1, individual2);
        manager.addAxiom(onto, assertion);
        return manager;
    }

    private OWLAnnotation createAnnotation(OWLOntologyManager manager, String key, String strValue, OWL2Datatype type) {
        OWLDataFactory factory = manager.getOWLDataFactory();
        OWLAnnotationProperty annotation = factory.getOWLAnnotationProperty(key, pm);
        OWLLiteral lit = factory.getOWLLiteral(strValue, type);
        return factory.getOWLAnnotation(annotation, lit);

    }

    private OWLOntologyManager addAnnotation(OWLOntology onto, OWLOntologyManager manager, OWLNamedIndividual individualConcept, String key, String strValue, String lang) {
        OWLDataFactory factory = manager.getOWLDataFactory();
        OWLAnnotationProperty annotation = factory.getOWLAnnotationProperty(key, pm);
        OWLLiteral value = factory.getOWLLiteral(strValue, lang);
        OWLAnnotationAssertionAxiom assertion = factory.getOWLAnnotationAssertionAxiom(annotation, (OWLAnnotationSubject) (individualConcept.getIRI()), value);
        manager.addAxiom(onto, assertion);
        return manager;
    }

    private void saveConcept(AsConcept ac, OWLNamedIndividual individual, OWLOntology onto, DefaultPrefixManager pm) throws OWLOntologyCreationException /*throws Exception*/ {
        // ===============================================================
        // Create the associated individual concept
        // ===============================================================
        //OWLOntologyManager manager = OWLManager.createOWLOntologyManager();
        OWLDataFactory factory = manager.getOWLDataFactory();

        IEarsTerm term = (IEarsTerm) ac.getTermRef();
        Long termId = ac.getId();
        //Set<OWLAxiom> axioms = null;
        OWLObjectProperty asConcept = factory.getOWLObjectProperty(":asConcept", pm);
        String individualURI = "";
        if (term.isBodcTerm()) {
            if (term.getPublisherUrn() == null) {
                throw new IllegalArgumentException("Missing URN for BODC concept associated to ToolCategory " + termId);
            }
            individualURI = "<" + getBodcURI(term.getPublisherUrn()) + ">";
            if (individualURI == null) {
                throw new IllegalArgumentException("Unable to retrieve BODC URI for concept associated to ToolCategory " + termId);
            }
            //from a bodc concept
            OWLNamedIndividual individualConcept = factory.getOWLNamedIndividual(individualURI, pm);
            OWLObjectPropertyAssertionAxiom asConceptAssertion = factory.getOWLObjectPropertyAssertionAxiom(asConcept, individual, individualConcept);
            manager.addAxiom(onto, asConceptAssertion);

            /*axioms = */ makeConceptAssertions(onto, individualConcept, term);
            //manager.addAxioms(onto, axioms);

        } else {
            OWLNamedIndividual individualConcept = null;
            if (term.getUri() == null && term.getId() != null) {
                individualURI = ":concept_" + term.getId();
                individualConcept = factory.getOWLNamedIndividual(individualURI, pm);
            } else {
                individualConcept = factory.getOWLNamedIndividual(IRI.create(term.getUri()));
            }

            //OWLNamedIndividual individualConcept = factory.getOWLNamedIndividual(individualURI, pm);
            OWLObjectPropertyAssertionAxiom asConceptAssertion = factory.getOWLObjectPropertyAssertionAxiom(asConcept, individual, individualConcept);
            manager.addAxiom(onto, asConceptAssertion);

            //axioms = null;
            //try {
            /*axioms = */
            makeConceptAssertions(onto, individualConcept, term);
            //manager.addAxioms(onto, axioms);
            // } catch (Exception e) {
            //throw new Exception("Concept assertions failed");
            // }
        }
        //return axioms;
    }

    private void addToCollection(OWLNamedIndividual ind, String collectionString, OWLOntology onto, DefaultPrefixManager pm) {
        // ===============================================================
        // Set the  individual as a member of :collectionString
        // ===============================================================
        OWLOntologyManager manager = OWLManager.createOWLOntologyManager();
        OWLDataFactory factory = manager.getOWLDataFactory();

        OWLObjectProperty hasMember = factory.getOWLObjectProperty("skos:member", pm);
        OWLNamedIndividual collection = factory.getOWLNamedIndividual(collectionString, pm);
        OWLObjectPropertyAssertionAxiom isMemberAssertion = factory.getOWLObjectPropertyAssertionAxiom(hasMember, collection, ind);
        manager.addAxiom(onto, isMemberAssertion);
    }

    private void saveToolCategories(OWLOntology onto) throws OWLOntologyCreationException {
        if (onto != null) {
            if (toolCategoryCollection != null) {
                for (AsConcept ac : toolCategoryCollection) {
                    //EarsTerm term = ac.getTermRef();
                    //IEarsTerm term = (IEarsTerm) ac.getTermRef();
                    //OWLOntologyManager manager = OWLManager.createOWLOntologyManager();
                    OWLDataFactory factory = manager.getOWLDataFactory();
                    // =============================
                    // Create the individual process
                    // =============================
                    OWLNamedIndividual individualTC = makeEARSIndividual(ac, factory, pm);//factory.getOWLNamedIndividual(":" + buildUrlFragment(ac), pm);
                    OWLClass processCl = factory.getOWLClass(":ToolCategory", pm);
                    OWLClassAssertionAxiom isProcAssertion = factory.getOWLClassAssertionAxiom(processCl, individualTC);
                    manager.addAxiom(onto, isProcAssertion);
                    // ===============================================================
                    // Create the associated individual concept
                    // ===============================================================
                    /*OWLObjectProperty asConcept = factory.getOWLObjectProperty(":asConcept", pm);

                     String indivudualURI = ":concept_" + term.getId();
                     if (term.getController() != null && term.getController().equalsIgnoreCase("bodc")) {
                     if (term.getPublisherUrn() == null) {
                     throw new Exception("Missing URN for BODC concept associated to ToolCategory " + ac.getId());
                     }
                     indivudualURI = "<" + getBodcURI(term.getPublisherUrn()) + ">";
                     if (indivudualURI == null) {
                     throw new Exception("Unable to retrieve BODC URI for concept associated to ToolCategory " + ac.getId());
                     }
                     }
                     OWLNamedIndividual individualConcept = factory.getOWLNamedIndividual(indivudualURI, pm);
                     OWLObjectPropertyAssertionAxiom asConceptAssertion =  factory.getOWLObjectPropertyAssertionAxiom(asConcept, individualProc, individualConcept);
                     manager.addAxiom(onto, asConceptAssertion);
                     if (term.getController() == null || !term.getController().equalsIgnoreCase("bodc")) {
                     Set<OWLAxiom> axioms = makeConceptAssertions(individualConcept, term, pm);
                     manager.addAxioms(onto, axioms);
                     }*/
                    saveConcept(ac, individualTC, onto, pm);
                    //addToCollection(individualTC, ":toolCategoriesCollection", onto, pm);
                }
            }
        }
    }

    private void saveTools(OWLOntology onto) throws OWLOntologyCreationException {
        if (onto != null) {
            if (toolCollection != null) {
                for (AsConcept ac : toolCollection) {

                    //EarsTerm term = ac.getTermRef();
                    //IEarsTerm term = (IEarsTerm) ac.getTermRef();
                    //OWLOntologyManager manager = OWLManager.createOWLOntologyManager();
                    OWLDataFactory factory = manager.getOWLDataFactory();
                    // =============================
                    // Create the individual process
                    // =============================
                    OWLNamedIndividual individualTool = makeEARSIndividual(ac, factory, pm);//factory.getOWLNamedIndividual(":" + buildUrlFragment(ac), pm);
                    OWLClass processCl = factory.getOWLClass(":Tool", pm);
                    OWLClassAssertionAxiom isProcAssertion = factory.getOWLClassAssertionAxiom(processCl, individualTool);
                    manager.addAxiom(onto, isProcAssertion);
                    // ===============================================================
                    // Create the associated individual concept
                    // ===============================================================
                    /*OWLObjectProperty asConcept = factory.getOWLObjectProperty(":asConcept", pm);
                     String indivudualURI = ":concept_" + term.getId();
                     if (term.getController() != null && term.getController().equalsIgnoreCase("bodc")) {
                     if (term.getPublisherUrn() == null) {
                     throw new Exception("Missing URN for BODC concept associated to ToolCategory " + ac.getId());
                     }
                     indivudualURI = "<" + getBodcURI(term.getPublisherUrn()) + ">";
                     if (indivudualURI == null) {
                     throw new Exception("Unable to retrieve BODC URI for concept associated to ToolCategory " + ac.getId());
                     }
                     }
                     OWLNamedIndividual individualConcept = factory.getOWLNamedIndividual(indivudualURI, pm);
                     OWLObjectPropertyAssertionAxiom asConceptAssertion =  factory.getOWLObjectPropertyAssertionAxiom(asConcept, individualTool, individualConcept);
                     manager.addAxiom(onto, asConceptAssertion);
                     if (term.getController() == null || !term.getController().equalsIgnoreCase("bodc")) {
                     Set<OWLAxiom> axioms = makeConceptAssertions(individualConcept, term, pm);
                     manager.addAxioms(onto, axioms);
                     }*/
                    saveConcept(ac, individualTool, onto, pm);
                    // ===============================================================
                    // Create the relations of compositions
                    // ===============================================================
                    ITool hostTool = (ITool) ac;
                    //if (vessel.getIsComposite() == true) {
                    if (hostTool.getHostedCollection() != null && hostTool.getHostedCollection().size() > 0) {
                        Set<OWLAxiom> axioms = new THashSet();
                        OWLObjectProperty canHost = factory.getOWLObjectProperty(":canHost", pm);
                        for (Object object : hostTool.getHostedCollection()) {////CONVERT TO INTERFACE for (ITool hostedTool : vessel.getHostedCollection()) 
                            ITool hostedTool = (ITool) object;
                            //ISpecificEventDefinition sev = (ISpecificEventDefinition) object;
                            OWLNamedIndividual individualHostedTool = makeEARSIndividual(hostedTool, factory, pm);//factory.getOWLNamedIndividual(":" + buildUrlFragment(hostedTool), pm);
                            axioms.add(factory.getOWLObjectPropertyAssertionAxiom(canHost, individualTool, individualHostedTool));
                        }
                        manager.addAxioms(onto, axioms);
                    }
                    // ===============================================================
                    // Create the relations of categories
                    // ===============================================================
                    Set<OWLAxiom> axioms = new THashSet();
                    if(hostTool.getTermRef().getEarsTermLabel().getPrefLabel().contains("PumpProbe")){
                        int a =5;
                    }
                    OWLObjectProperty isMemberOf = factory.getOWLObjectProperty(":isMemberOf", pm);
                    if (hostTool.getToolCategoryCollection() != null) {
                        for (Object object : hostTool.getToolCategoryCollection()) {  //CONVERT TO INTERFACE  for (IToolCategory tc) {
                            IToolCategory tc = (IToolCategory) object;
                            OWLNamedIndividual owlTc = makeEARSIndividual(tc, factory, pm);//factory.getOWLNamedIndividual(":" + buildUrlFragment(tc), pm);
                            axioms.add(factory.getOWLObjectPropertyAssertionAxiom(isMemberOf, individualTool, owlTc));
                        }
                    }

                    if (hostTool.getToolIdentifier() != null) {
                        makeLiteralDataProperty(onto, manager, ":toolIdentifier", hostTool.getToolIdentifier(), individualTool, OWL2Datatype.XSD_STRING);
                    }
                    if (hostTool.getSerialNumber() != null) {
                        makeLiteralDataProperty(onto, manager, ":serialNumber", hostTool.getSerialNumber(), individualTool, OWL2Datatype.XSD_STRING);
                    }
                    manager.addAxioms(onto, axioms);
                    //addToCollection(individualTool, ":toolsCollection", onto, pm);

                }
            }
        }
    }

    private void saveVessels(OWLOntology onto) throws OWLOntologyCreationException {
        if (onto != null) {
            if (vesselCollection != null) {
                for (AsConcept ac : vesselCollection) {
                    OWLDataFactory factory = manager.getOWLDataFactory();
                    // =============================
                    // Create the individual vessel
                    // =============================
                    OWLNamedIndividual individualTool = makeEARSIndividual(ac, factory, pm);
                    OWLClass processCl = factory.getOWLClass(":Vessel", pm);
                    OWLClassAssertionAxiom isProcAssertion = factory.getOWLClassAssertionAxiom(processCl, individualTool);
                    manager.addAxiom(onto, isProcAssertion);
                    // ===============================================================
                    // Create the associated individual concept
                    // ===============================================================
                    saveConcept(ac, individualTool, onto, pm);
                    // ===============================================================
                    // Create the relations of categories
                    // ===============================================================
                    IVessel vessel = (IVessel) ac;
                    Set<OWLAxiom> axioms = new THashSet();
                    OWLObjectProperty isMemberOf = factory.getOWLObjectProperty(":isMemberOf", pm);
                    if (vessel.getToolCategoryCollection() != null) {
                        for (Object object : vessel.getToolCategoryCollection()) {  //CONVERT TO INTERFACE  for (IToolCategory tc) {
                            IToolCategory tc = (IToolCategory) object;
                            OWLNamedIndividual owlTc = makeEARSIndividual(tc, factory, pm);//factory.getOWLNamedIndividual(":" + buildUrlFragment(tc), pm);
                            axioms.add(factory.getOWLObjectPropertyAssertionAxiom(isMemberOf, individualTool, owlTc));
                        }
                    }
                    manager.addAxioms(onto, axioms);
                }
            }
        }
    }

    private void saveProcesses(OWLOntology onto) throws OWLOntologyCreationException {
        if (onto != null) {
            if (processCollection != null) {
                for (AsConcept ac : processCollection) {
                    OWLDataFactory factory = manager.getOWLDataFactory();
                    // =============================
                    // Create the individual process
                    // =============================
                    OWLNamedIndividual individualProc = makeEARSIndividual(ac, factory, pm);//factory.getOWLNamedIndividual(":" + buildUrlFragment(ac), pm);
                    OWLClass processCl = factory.getOWLClass(":Process", pm);
                    OWLClassAssertionAxiom isProcAssertion = factory.getOWLClassAssertionAxiom(processCl, individualProc);
                    manager.addAxiom(onto, isProcAssertion);
                    // ===============================================================
                    // Create the associated individual concept
                    // ===============================================================
                    saveConcept(ac, individualProc, onto, pm);
                    // ===============================================================
                    // Set the  individual process as a member of :processCollection
                    // ===============================================================
                    //addToCollection(individualProc, ":processCollection", onto, pm);
                }
            }
        }
    }

    private void saveActions(OWLOntology onto) throws OWLOntologyCreationException {
        if (onto != null) {
            if (actionCollection != null) {
                for (AsConcept ac : actionCollection) {
                    OWLDataFactory factory = manager.getOWLDataFactory();
                    // ===============================================================
                    // Create the individual
                    // ===============================================================

                    OWLNamedIndividual individualAction = makeEARSIndividual(ac, factory, pm);//factory.getOWLNamedIndividual(":" + buildUrlFragment(ac), pm);
                    OWLClass processCl = factory.getOWLClass(":ProcessStep", pm);
                    OWLClassAssertionAxiom isProcAssertion = factory.getOWLClassAssertionAxiom(processCl, individualAction);
                    manager.addAxiom(onto, isProcAssertion);
                    // ===============================================================
                    // Create the associated individual concept
                    // ===============================================================
                    saveConcept(ac, individualAction, onto, pm);
                    // ===============================================================
                    // Set the  individual process as a memeber of :actionsCollection
                    // ===============================================================
                    //addToCollection(individualAction, ":actionsCollection", onto, pm);
                }
            }
        }
    }

    private void saveProperties(OWLOntology onto) throws OWLOntologyCreationException {
        if (onto != null) {
            if (propertyCollection != null) {
                for (AsConcept ac : propertyCollection) {
                    /*
                     IProperty prop = (IProperty) ac;
                     Collection<EventDefinition> relatedEvents = prop.getEventDefinitionCollection();
                     for (EventDefinition ev : relatedEvents) {
                     if (ev instanceof GenericEventDefinition) {
                     GenericEventDefinition gev = (GenericEventDefinition) ev;
                     System.out.println("GEV:" + gev.getLabel());
                     } else {
                     SpecificEventDefinition sev = (SpecificEventDefinition) ev;
                     System.out.println("SEV:" + sev.getLabel());
                     }
                     } 
                     */
                    OWLDataFactory factory = manager.getOWLDataFactory();
                    // ====================================================================
                    // Create the individual property
                    // ====================================================================
                    OWLNamedIndividual individualProp = makeEARSIndividual(ac, factory, pm);//factory.getOWLNamedIndividual(":" + buildUrlFragment(ac), pm);
                    OWLClass processCl = factory.getOWLClass(":EventProperty", pm);
                    OWLClassAssertionAxiom isProcAssertion = factory.getOWLClassAssertionAxiom(processCl, individualProp);
                    manager.addAxiom(onto, isProcAssertion);
                    // ====================================================================
                    // Create the associated individual concept
                    // ====================================================================
                    saveConcept(ac, individualProp, onto, pm);
                    // ====================================================================
                    // Set the  individual process as a memeber of :eventPropertiesCollection
                    // ====================================================================
                    //addToCollection(individualProp, ":eventPropertiesCollection", onto, pm);
                    // ====================================================================
                    // Save the mandatory and multiple fields
                    // ====================================================================
                    IProperty prop = (IProperty) ac;

                    OWLLiteral isMandatoryLiteral = factory.getOWLLiteral(prop.isMandatory());
                    OWLDataProperty isMandatoryProp = factory.getOWLDataProperty(":mandatory", pm);
                    OWLDataPropertyAssertionAxiom isMandatoryAssertion = factory.getOWLDataPropertyAssertionAxiom(isMandatoryProp, individualProp, isMandatoryLiteral);
                    //axioms.add(isMandatoryAssertion);
                    manager.addAxiom(onto, isMandatoryAssertion);

                    OWLLiteral isMultipleLiteral = factory.getOWLLiteral(prop.isMultiple());
                    OWLDataProperty isMultipleProp = factory.getOWLDataProperty(":multiple", pm);
                    OWLDataPropertyAssertionAxiom isMultipleAssertion = factory.getOWLDataPropertyAssertionAxiom(isMultipleProp, individualProp, isMultipleLiteral);
                    //axioms.add(isMultipleAssertion);
                    manager.addAxiom(onto, isMultipleAssertion);

                    if (prop.getId().equals(12L)) { //subject
                        prop.setValueClass("Subject");
                    }
                    if (prop.getId().equals(13L)) { //parameter
                        prop.setValueClass("Parameter");
                    }
                    if (prop.getId().equals(22L)) { //program
                        prop.setValueClass("Program");
                    }

                    if (prop.getValueClass() != null) {
                        /*OWLNamedIndividual makeSimpleIndividual = makeSimpleIndividual(manager,"");
                        makeObjectProperty(onto, manager, "gn:valueClass", individual, countryIndividual);*/

                        OWLLiteral valueClassLiteral = factory.getOWLLiteral(prop.getValueClass());
                        OWLDataProperty valueClassProp = factory.getOWLDataProperty(":valueClass", pm);
                        OWLDataPropertyAssertionAxiom valueClassAssertion = factory.getOWLDataPropertyAssertionAxiom(valueClassProp, individualProp, valueClassLiteral);
                        //axioms.add(isMultipleAssertion);
                        manager.addAxiom(onto, valueClassAssertion);
                    }
                    // ====================================================================
                    // Create the propertySubjectsCollection of this property
                    // ====================================================================
//                    Collection<?> ovCollection = prop.getObjectValueCollection(); //CONVERT TO INTERFACE Collection<IObjectValue>
//
//                    Collection<IObjectValue> subjectCollection = new ArrayList();
//                    Collection<IObjectValue> parameterCollection = new ArrayList();
//                    if (ovCollection != null && !ovCollection.isEmpty()) {
//                        subjectCollection = CollectionUtils.select(ovCollection, new Predicate() {
//                            @Override
//                            public boolean evaluate(Object object) {
//                                return ((IObjectValue) object).getValuedTermRef().getKind().equals("SUJ");
//                            }
//                        });
//
//                        parameterCollection = CollectionUtils.select(ovCollection, new Predicate() {
//                            @Override
//                            public boolean evaluate(Object object) {
//                                return ((IObjectValue) object).getValuedTermRef().getKind().equals("PAR");
//                            }
//                        });
//                        if (subjectCollection.size() > 0) {
//                            OWLNamedIndividual propertySubjectsCollection = factory.getOWLNamedIndividual(":" + buildUrlFragment(prop) + "_subjectsCollection", pm);
//                            // ====================================================================
//                            // Tell that it is a propertySubjectsCollection
//                            // ====================================================================
//                            OWLClass propertySubjectsCollectionClass = factory.getOWLClass(":propertySubjectsCollection", pm);
//                            OWLClassAssertionAxiom isSubjectsCollectionAssertion = factory.getOWLClassAssertionAxiom(propertySubjectsCollectionClass, propertySubjectsCollection);
//                            manager.addAxiom(onto, isSubjectsCollectionAssertion);
//                            // ====================================================================
//                            // Add it to the property
//                            // ====================================================================
//                            OWLObjectProperty limitedToSubjects = factory.getOWLObjectProperty(":limitedToSubjects", pm);
//                            OWLObjectPropertyAssertionAxiom limitedToSubjectsAssertion = factory.getOWLObjectPropertyAssertionAxiom(limitedToSubjects, individualProp, propertySubjectsCollection);
//                            manager.addAxiom(onto, limitedToSubjectsAssertion);
//                            // ====================================================================
//                            // Get the ObjectValues (subjects) of this property and assign them to the collection
//                            // ====================================================================
//                            /*for (IObjectValue ov : subjectCollection) {
//                             IEarsTerm etm = (IEarsTerm) ov.getValuedTermRef();
//                             ISubject subj = etm.getSubject();
//                             OWLNamedIndividual individualSubj = factory.getOWLNamedIndividual(":"+buildUrlFragment(subj), pm);
//
//                             OWLObjectProperty isSubjectCollectionMember = factory.getOWLObjectProperty("skos:member", pm);
//                             OWLObjectPropertyAssertionAxiom isSubjectCollectionMemberAssertion =  factory.getOWLObjectPropertyAssertionAxiom(isSubjectCollectionMember, propertySubjectsCollection, individualSubj);
//                             manager.addAxiom(onto, isSubjectCollectionMemberAssertion);
//                             }*/
//                        }
//                        if (parameterCollection.size() > 0) {
//                            OWLNamedIndividual propertyParametersCollection = factory.getOWLNamedIndividual(":" + buildUrlFragment(prop) + "_parametersCollection", pm);
//                            // ====================================================================
//                            // Tell that it is a propertyParametersCollection
//                            // ====================================================================
//                            OWLClass propertyParametersCollectionClass = factory.getOWLClass(":propertyParametersCollection", pm);
//                            OWLClassAssertionAxiom isParametersCollectionAssertion = factory.getOWLClassAssertionAxiom(propertyParametersCollectionClass, propertyParametersCollection);
//                            manager.addAxiom(onto, isParametersCollectionAssertion);
//                            // ====================================================================
//                            // Add it to the property
//                            // ====================================================================
//                            OWLObjectProperty limitedToParameters = factory.getOWLObjectProperty(":limitedToParameters", pm);
//                            OWLObjectPropertyAssertionAxiom limitedToParametersAssertion = factory.getOWLObjectPropertyAssertionAxiom(limitedToParameters, individualProp, propertyParametersCollection);
//                            manager.addAxiom(onto, limitedToParametersAssertion);
//                            // ====================================================================
//                            // Get the ObjectValues (parameters) of this property and assign them to the collection
//                            // ====================================================================
//                            /*for (IObjectValue ov : parameterCollection) {
//                             IEarsTerm etm = (IEarsTerm) ov.getValuedTermRef();
//                             IParameter param = etm.getParameter();
//                             OWLNamedIndividual individualParam = factory.getOWLNamedIndividual(":"+buildUrlFragment(param), pm);
//
//                             OWLObjectProperty isParameterCollectionMember = factory.getOWLObjectProperty("skos:member", pm);
//                             OWLObjectPropertyAssertionAxiom isParameterCollectionMemberAssertion =  factory.getOWLObjectPropertyAssertionAxiom(isParameterCollectionMember, propertyParametersCollection, individualParam);
//                             manager.addAxiom(onto, isParameterCollectionMemberAssertion);
//                             }*/
//                        }
//                    }
                }
            }
        }
    }

    private void saveProcessActions(OWLOntology onto) throws OWLOntologyCreationException {
        if (onto != null) {
            if (processActionCollection != null) {
                OWLDataFactory factory = manager.getOWLDataFactory();
                Set<OWLAxiom> axioms = new THashSet<>();
                OWLObjectProperty hasAction = factory.getOWLObjectProperty(":involvesStep", pm);
                for (IProcessAction pa : processActionCollection) {
                    //for (AsConcept concept : processActionList) {
                    //ProcessAction pa = (ProcessAction) concept;
                    IAction action = pa.getAction();
                    IProcess process = pa.getProcess();
                    OWLNamedIndividual individualProc = makeEARSIndividual(process, factory, pm);//factory.getOWLNamedIndividual(":" + buildUrlFragment(process), pm);
                    OWLNamedIndividual individualAct = makeEARSIndividual(action, factory, pm);//factory.getOWLNamedIndividual(":" + buildUrlFragment(action), pm);
                    axioms.add(factory.getOWLObjectPropertyAssertionAxiom(hasAction, individualProc, individualAct));
                }
                manager.addAxioms(onto, axioms);
            }
        }
    }

    private void saveSpecificEvents(OWLOntology onto) throws OWLOntologyCreationException {
        if (onto != null) {
            if (sevCollection != null) {
                for (AsConcept ac : sevCollection) {
                    OWLDataFactory factory = manager.getOWLDataFactory();
                    // ====================================================================
                    // Create the individual specific event
                    // ====================================================================
                    OWLNamedIndividual individualSev = makeEARSIndividual(ac, factory, pm);//factory.getOWLNamedIndividual(":" + buildUrlFragment(ac), pm);
                    OWLClass processCl = factory.getOWLClass(":SpecificEventDefinition", pm);
                    OWLClassAssertionAxiom isProcAssertion = factory.getOWLClassAssertionAxiom(processCl, individualSev);
                    manager.addAxiom(onto, isProcAssertion);
                    // ====================================================================
                    // Create the individual associated concept
                    // ====================================================================
                    Set<OWLAxiom> axioms = new THashSet<>();
                    saveConcept(ac, individualSev, onto, pm);
                    // ====================================================================
                    // Set the  individual process as a member of :specificEvDefinitionsCollection
                    // ====================================================================
                    //addToCollection(individualSev, ":specificEvDefinitionsCollection", onto, pm);
                    // ====================================================================
                    // Create the association with objects proc, action, categ
                    // ====================================================================
                    ISpecificEventDefinition sev = (ISpecificEventDefinition) ac;
                    IProcess proc = sev.getProcessAction().getProcess();
                    IAction action = sev.getProcessAction().getAction();
                    ITool tool = sev.getToolRef();
                    OWLNamedIndividual individualProc = makeEARSIndividual(proc, factory, pm);//factory.getOWLNamedIndividual(":" + buildUrlFragment(proc), pm);
                    OWLNamedIndividual individualAct = makeEARSIndividual(action, factory, pm);//factory.getOWLNamedIndividual(":" + buildUrlFragment(action), pm);
                    OWLNamedIndividual individualTool = makeEARSIndividual(tool, factory, pm);//factory.getOWLNamedIndividual(":" + buildUrlFragment(tool), pm);

                    OWLObjectProperty hasAction = factory.getOWLObjectProperty(":hasAction", pm);
                    OWLObjectPropertyAssertionAxiom hasActionAssertion = factory.getOWLObjectPropertyAssertionAxiom(hasAction, individualSev, individualAct);
                    OWLObjectProperty hasProcess = factory.getOWLObjectProperty(":hasProcess", pm);
                    OWLObjectPropertyAssertionAxiom hasProcessAssertion = factory.getOWLObjectPropertyAssertionAxiom(hasProcess, individualSev, individualProc);
                    OWLObjectProperty withTool = factory.getOWLObjectProperty(":withTool", pm);
                    OWLObjectPropertyAssertionAxiom withToolAssertion = factory.getOWLObjectPropertyAssertionAxiom(withTool, individualSev, individualTool);

                    axioms.add(hasActionAssertion);
                    axioms.add(hasProcessAssertion);
                    axioms.add(withToolAssertion);

                    // ====================================================================
                    // Relation :triggersHostedEvent
                    // ====================================================================
                    Collection<?> triggeredList = sev.getTriggeredCollection(); //CONVERT TO INTERFACE Collection<IEventDefinition>
                    Collection<?> triggerList = sev.getTriggerCollection();//CONVERT TO INTERFACE Collection<IEventDefinition>
                    if (triggeredList != null && triggeredList.size() > 0) {
                        OWLObjectProperty triggersHostedEvent = factory.getOWLObjectProperty(":triggersHostedEvent", pm);
                        for (Object object : triggeredList) { //CONVERT TO INTERFACE for (IEventDefinition triggeredEvent : triggeredList) {
                            IEventDefinition triggeredEvent = (IEventDefinition) object;//CONVERT TO INTERFACE
                            OWLNamedIndividual individualTriggered = null;
                            if (triggeredEvent instanceof ISpecificEventDefinition) {
                                ISpecificEventDefinition triggeredEventS = (ISpecificEventDefinition) triggeredEvent;
                                individualTriggered = makeEARSIndividual(triggeredEventS, factory, pm);//factory.getOWLNamedIndividual(":" + buildUrlFragment(triggeredEventS), pm);
                            } else if (triggeredEvent instanceof IGenericEventDefinition) {
                                IGenericEventDefinition triggeredEventG = (IGenericEventDefinition) triggeredEvent;
                                individualTriggered = makeEARSIndividual(triggeredEventG, factory, pm);//factory.getOWLNamedIndividual(":" + buildUrlFragment(triggeredEventG), pm);
                            }
                            OWLObjectPropertyAssertionAxiom individualTriggeredAssert = factory.getOWLObjectPropertyAssertionAxiom(triggersHostedEvent, individualSev, individualTriggered);
                            axioms.add(individualTriggeredAssert);
                        }
                    }
                    // ====================================================================
                    // Create the datatypeproperty isDataProvider
                    // ====================================================================
                    if (sev.getIsDataProvider() != null) {
                        OWLLiteral isDataProviderLiteral = factory.getOWLLiteral(sev.getIsDataProvider());
                        OWLDataProperty isDataProviderProp = factory.getOWLDataProperty(":isDataProvider", pm);
                        OWLDataPropertyAssertionAxiom isDataProviderAssertion = factory.getOWLDataPropertyAssertionAxiom(isDataProviderProp, individualSev, isDataProviderLiteral);
                        axioms.add(isDataProviderAssertion);
                    }

                    // ========================================================================================
                    // Add properties that are defined at specific level AND are also defined at generic level ONLY to the generic level, ie. remove them from the specific level.
                    // ========================================================================================
                    IGenericEventDefinition gev = sev.getRealizesRef();
                    Collection<?> ownProps = sev.getPropertyCollection(); //CONVERT TO INTERFACE Collection<IProperty>
                    boolean hasGenericProps = false;
                    if (gev != null) {
                        Collection<?> gevProps = gev.getPropertyCollection(); //CONVERT TO INTERFACE Collection<IProperty>
                        if (gevProps != null && gevProps.size() > 0) {
//look at generic properties: 
                            //remove all gev properties not in specific properties
                            //set the specific properties to the remainder
                            hasGenericProps = true;
                            ownProps.removeAll(gevProps);
                            for (Object object : ownProps) { //CONVERT TO INTERFACE for (IProperty prop : ownProps) {
                                IProperty prop = (IProperty) object; //.
                                if (prop.getId() == 10) {
                                    int a = 5;
                                }
                                OWLNamedIndividual individualProp = makeEARSIndividual(prop, factory, pm);//factory.getOWLNamedIndividual(":" + buildUrlFragment(prop), pm);
                                OWLObjectProperty hasProperty = factory.getOWLObjectProperty(":hasProperty", pm);
                                OWLObjectPropertyAssertionAxiom hasPropertyAssertion = factory.getOWLObjectPropertyAssertionAxiom(hasProperty, individualSev, individualProp);
                                axioms.add(hasPropertyAssertion);
                            }
                        }
                    }
                    // ========================================================================================
                    // Add properties that are only defined at specific level to the specific level
                    // ======================================================================================
                    if (!hasGenericProps) {
                        for (Object object : ownProps) { //CONVERT TO INTERFACE for (IProperty prop : ownProps) {
                            IProperty prop = (IProperty) object;

                            OWLNamedIndividual individualProp = makeEARSIndividual(prop, factory, pm);//factory.getOWLNamedIndividual(":" + buildUrlFragment(prop), pm);
                            OWLObjectProperty hasProperty = factory.getOWLObjectProperty(":hasProperty", pm);
                            OWLObjectPropertyAssertionAxiom hasPropertyAssertion = factory.getOWLObjectPropertyAssertionAxiom(hasProperty, individualSev, individualProp);
                            axioms.add(hasPropertyAssertion);
                        }
                    }
                    manager.addAxioms(onto, axioms);
                }
            }
        }
    }

    private String buildUrlFragment(AsConcept concept) {
        if (concept.getId() != null) { //todo ALLOW FOR TOOL REFERENCE-ID TO BE ADDED IN THE URL
            return concept.getKind().toLowerCase() + "_" + concept.getId();
        } else {
            return concept.getUri().getFragment();
        }
    }

    private void saveGenericEvents(OWLOntology onto) throws OWLOntologyCreationException {
        if (onto != null) {
            if (gevCollection != null) {
                for (AsConcept concept : gevCollection) {
                    IToolCategory categ = null;
                    IProcess proc = null;
                    IAction action = null;
                    OWLDataFactory factory = null;
                    Set<OWLAxiom> axioms = null;
                    OWLNamedIndividual individualEv = null;
                    IGenericEventDefinition gev = null;
                    try {
                        gev = (IGenericEventDefinition) concept;
                        factory = manager.getOWLDataFactory();
                        // ====================================================================
                        // Create the individual generic event
                        // ====================================================================
                        individualEv = makeEARSIndividual(gev, factory, pm);//factory.getOWLNamedIndividual(":" + buildUrlFragment(gev), pm);
                        OWLClass processCl = factory.getOWLClass(":GenericEventDefinition", pm);
                        OWLClassAssertionAxiom isProcAssertion = factory.getOWLClassAssertionAxiom(processCl, individualEv);
                        manager.addAxiom(onto, isProcAssertion);

                        axioms = new THashSet();

                        // ====================================================================
                        // Create the association with objects proc, action, categ
                        // ====================================================================
                        proc = gev.getProcessAction().getProcess();
                        action = gev.getProcessAction().getAction();
                        categ = gev.getToolCategoryRef();
                        OWLNamedIndividual individualProc = makeEARSIndividual(proc, factory, pm);//factory.getOWLNamedIndividual(":" + buildUrlFragment(proc), pm);
                        OWLNamedIndividual individualAct = makeEARSIndividual(action, factory, pm);//factory.getOWLNamedIndividual(":" + buildUrlFragment(action), pm);
                        OWLNamedIndividual individualCateg = makeEARSIndividual(categ, factory, pm);//factory.getOWLNamedIndividual(":" + buildUrlFragment(categ), pm);

                        OWLObjectProperty hasAction = factory.getOWLObjectProperty(":hasAction", pm);
                        OWLObjectPropertyAssertionAxiom hasActionAssertion = factory.getOWLObjectPropertyAssertionAxiom(hasAction, individualEv, individualAct);
                        OWLObjectProperty hasProcess = factory.getOWLObjectProperty(":hasProcess", pm);
                        OWLObjectPropertyAssertionAxiom hasProcessAssertion = factory.getOWLObjectPropertyAssertionAxiom(hasProcess, individualEv, individualProc);
                        OWLObjectProperty withTool = factory.getOWLObjectProperty(":withTool", pm);
                        OWLObjectPropertyAssertionAxiom withToolAssertion = factory.getOWLObjectPropertyAssertionAxiom(withTool, individualEv, individualCateg);

                        axioms.add(hasActionAssertion);
                        axioms.add(hasProcessAssertion);
                        axioms.add(withToolAssertion);
                    } catch (Exception e) {
                        int a = 5;
                    }
                    // ====================================================================
                    // Create the rdfs:label of the category
                    // ====================================================================
                    try {
                        String enLabel = categ.getTermRef().getEarsTermLabel().getPrefLabel() + ": " + proc.getTermRef().getEarsTermLabel().getPrefLabel()
                                + " " + action.getTermRef().getEarsTermLabel().getPrefLabel();
                        OWLAnnotationProperty annotation = factory.getOWLAnnotationProperty("rdfs:label", pm);
                        OWLLiteral value = factory.getOWLLiteral(enLabel, "en");
                        axioms.add(factory.getOWLAnnotationAssertionAxiom(annotation, (OWLAnnotationSubject) (individualEv.getIRI()), value));
                    } catch (Exception e) {
                        int a = 5;
                    }
                    // ====================================================================
                    // Create the association with event properties
                    // ====================================================================
                    Collection<?> props = gev.getPropertyCollection(); //CONVERT TO INTERFACE Collection<IProperty> props
                    if (props != null && props.size() > 0) {
                        for (Object object : props) { //CONVERT TO INTERFACE  for (IProperty prop : props) {
                            try {
                                IProperty prop = (IProperty) object;  //CONVERT TO INTERFACE
                                OWLNamedIndividual individualProp = makeEARSIndividual(prop, factory, pm);//factory.getOWLNamedIndividual(":" + buildUrlFragment(prop), pm);
                                OWLObjectProperty hasProperty = factory.getOWLObjectProperty(":hasProperty", pm);
                                OWLObjectPropertyAssertionAxiom hasPropertyAssertion = factory.getOWLObjectPropertyAssertionAxiom(hasProperty, individualEv, individualProp);
                                axioms.add(hasPropertyAssertion);
                            } catch (Exception e) {
                                int a = 5;
                            }
                        }
                    }

                    // ====================================================================
                    // Create the datatypeproperty isDataProvider
                    // ====================================================================
                    try {
                        OWLLiteral isDataProviderLiteral = factory.getOWLLiteral(gev.getIsDataProvider());
                        OWLDataProperty isDataProviderProp = factory.getOWLDataProperty(":isDataProvider", pm);
                        OWLDataPropertyAssertionAxiom isDataProviderAssertion = factory.getOWLDataPropertyAssertionAxiom(isDataProviderProp, individualEv, isDataProviderLiteral);
                        axioms.add(isDataProviderAssertion);

                    } catch (Exception e) {
                        int a = 5;
                    }
                    // ====================================================================
                    // Create the association with specific event def
                    // ====================================================================
                    Collection<?> realisations = gev.getSpecificEventDefinitionCollection(); //CONVERT TO INTERFACE Collection<ISpecificEventDefinition>
                    if (realisations != null && realisations.size() > 0) {
                        OWLObjectProperty realizedBy = factory.getOWLObjectProperty(":realizedBy", pm);
                        for (Object object : realisations) {//CONVERT TO INTERFACE for (ISpecificEventDefinition sev : realisations) {
                            try {
                                ISpecificEventDefinition sev = (ISpecificEventDefinition) object;
                                OWLNamedIndividual individualSpec = makeEARSIndividual(sev, factory, pm);//factory.getOWLNamedIndividual(":" + buildUrlFragment(sev), pm);

                                OWLObjectPropertyAssertionAxiom realizedByAssertion = factory.getOWLObjectPropertyAssertionAxiom(realizedBy, individualEv, individualSpec);
                                axioms.add(realizedByAssertion);
                                // ========================================================================================
                                // Create properties that are not defined at generic level to the specific realization->> moved to sev
                                // ========================================================================================
                                /*Collection<Property> subProps = gev.getPropertyCollection();
                                 if (subProps != null && subProps.size() > 0) {
                                 for (Property subProp : subProps) {
                                 if (props == null || !props.contains(subProp)) {
                                 OWLNamedIndividual individualSubProp = factory.getOWLNamedIndividual(":"+buildUrlFragment(subProp), pm);
                                 OWLObjectProperty hasSubProperty = factory.getOWLObjectProperty(":hasProperty", pm);
                                 OWLObjectPropertyAssertionAxiom hasSubPropertyAssertion =  factory.getOWLObjectPropertyAssertionAxiom(hasSubProperty, individualSpec, individualSubProp);
                                 axioms.add(hasSubPropertyAssertion);
                                 }
                                 }
                                 }*/

                            } catch (Exception e) {
                                int a = 5;
                            }
                        }
                    }
                    // ====================================================================
                    // Relation :triggersHostedEvent
                    // ====================================================================

                    Collection<?> triggeredList = gev.getTriggeredCollection(); //CONVERT TO INTERFACE  Collection<IEventDefinition> triggeredList
                    if (triggeredList != null && triggeredList.size() > 0) {
                        OWLObjectProperty triggersHostedEvent = factory.getOWLObjectProperty(":triggersHostedEvent", pm);
                        for (Object object : triggeredList) {  //CONVERT TO INTERFACE for (IEventDefinition triggeredEvent : triggeredList) {
                            try {
                                IEventDefinition triggeredEvent = (IEventDefinition) object;
                                OWLNamedIndividual individualTriggered = null;
                                if (triggeredEvent instanceof ISpecificEventDefinition) {
                                    ISpecificEventDefinition triggeredEventS = (ISpecificEventDefinition) triggeredEvent;
                                    individualTriggered = makeEARSIndividual(triggeredEventS, factory, pm);//factory.getOWLNamedIndividual(":" + buildUrlFragment(triggeredEventS), pm);
                                } else if (triggeredEvent instanceof IGenericEventDefinition) {
                                    IGenericEventDefinition triggeredEventG = (IGenericEventDefinition) triggeredEvent;
                                    individualTriggered = makeEARSIndividual(triggeredEventG, factory, pm);//factory.getOWLNamedIndividual(":" + buildUrlFragment(triggeredEventG), pm);
                                }
                                OWLObjectPropertyAssertionAxiom individualTriggeredAssert = factory.getOWLObjectPropertyAssertionAxiom(triggersHostedEvent, individualEv, individualTriggered);
                                axioms.add(individualTriggeredAssert);
                            } catch (Exception e) {
                                int a = 5;
                            }
                        }
                    }
                    manager.addAxioms(onto, axioms);
                    // ====================================================================
                    // Set the  individual process as a member of :genericEvDefinitionsCollection
                    // ====================================================================
                    //addToCollection(individualSev, ":genericEvDefinitionsCollection", onto, pm);

                }
            }
        }
    }

    private void saveSubjects(OWLOntology onto) throws OWLOntologyCreationException {
        if (onto != null) {
            if (subjectCollection != null) {
                for (AsConcept ac : subjectCollection) {
                    OWLDataFactory factory = manager.getOWLDataFactory();
                    // ===============================================================
                    // Create the individual
                    // ===============================================================
                    OWLNamedIndividual individual = makeEARSIndividual(ac, factory, pm);
                    OWLClass processCl = factory.getOWLClass(":Subject", pm);
                    OWLClassAssertionAxiom isProcAssertion = factory.getOWLClassAssertionAxiom(processCl, individual);
                    manager.addAxiom(onto, isProcAssertion);
                    // ===============================================================
                    // Create the associated individual concept
                    // ===============================================================
                    saveConcept(ac, individual, onto, pm);
                }
            }
        }
    }

    private void saveParameters(OWLOntology onto) throws OWLOntologyCreationException {
        if (onto != null) {
            if (parameterCollection != null) {
                for (AsConcept ac : parameterCollection) {
                    OWLDataFactory factory = manager.getOWLDataFactory();
                    // ===============================================================
                    // Create the individual
                    // ===============================================================
                    OWLNamedIndividual individual = makeEARSIndividual(ac, factory, pm);//factory.getOWLNamedIndividual(":" + buildUrlFragment(ac), pm);
                    OWLClass processCl = factory.getOWLClass(":Parameter", pm);
                    OWLClassAssertionAxiom isProcAssertion = factory.getOWLClassAssertionAxiom(processCl, individual);
                    manager.addAxiom(onto, isProcAssertion);
                    // ===============================================================
                    // Create the associated individual concept
                    // ===============================================================
                    saveConcept(ac, individual, onto, pm);
                    //addToCollection(individual, ":parametersCollection", onto, pm);
                }
            }
        }
    }

    private void saveSeaAreas(OWLOntology onto) throws OWLOntologyCreationException {
        if (onto != null) {
            if (seaAreaCollection != null) {
                for (AsConcept ac : seaAreaCollection) {
                    OWLDataFactory factory = manager.getOWLDataFactory();
                    // ===============================================================
                    // Create the individual
                    // ===============================================================
                    OWLNamedIndividual individual = makeEARSIndividual(ac, factory, pm);//factory.getOWLNamedIndividual(":" + buildUrlFragment(ac), pm);
                    OWLClass processCl = factory.getOWLClass(":SeaArea", pm);
                    OWLClassAssertionAxiom isProcAssertion = factory.getOWLClassAssertionAxiom(processCl, individual);
                    manager.addAxiom(onto, isProcAssertion);
                    // ===============================================================
                    // Create the associated individual concept
                    // ===============================================================
                    saveConcept(ac, individual, onto, pm);
                    //addToCollection(individual, ":seaAreaCollection", onto, pm);
                }
            }
        }
    }

    private void saveHarbours(OWLOntology onto) throws OWLOntologyCreationException {
        if (onto != null) {
            if (harbourCollection != null) {
                for (AsConcept ac : harbourCollection) {
                    IHarbour harbour = (IHarbour) ac;
                    OWLDataFactory factory = manager.getOWLDataFactory();
                    // ===============================================================
                    // Create the individual
                    // ===============================================================
                    OWLNamedIndividual individual = makeEARSIndividual(ac, factory, pm);//factory.getOWLNamedIndividual(":" + buildUrlFragment(ac), pm);
                    OWLClass processCl = factory.getOWLClass(":Harbour", pm);
                    OWLClassAssertionAxiom isProcAssertion = factory.getOWLClassAssertionAxiom(processCl, individual);
                    manager.addAxiom(onto, isProcAssertion);
                    // ===============================================================
                    // Save the metadata (gn, geo)
                    // ===============================================================
                    String countryString = harbour.getCountry();
                    ICountry country = null;
                    List<ICountry> results = ((Stream<ICountry>) countryCollection.stream()).filter(c -> c.getTermRef().getEarsTermLabel().getPrefLabel().equalsIgnoreCase(countryString)).collect(Collectors.toList());
                    if (results != null && !results.isEmpty()) {
                        country = results.get(0);
                    }
                    if (country != null) {
                        OWLNamedIndividual countryIndividual = makeEARSIndividual(country, factory, pm);
                        makeObjectProperty(onto, manager, "gn:parentCountry", individual, countryIndividual);
                    }
                    if (harbour.getLatDec() != null && harbour.getLonDec() != null) {
                        makeLiteralDataProperty(onto, manager, "geo:lat", harbour.getLatDec().toString(), individual, OWL2Datatype.XSD_DOUBLE);
                        makeLiteralDataProperty(onto, manager, "geo:lon", harbour.getLonDec().toString(), individual, OWL2Datatype.XSD_DOUBLE);
                    }
                    // ===============================================================
                    // Create the associated individual concept
                    // ===============================================================
                    saveConcept(ac, individual, onto, pm);
                }
            }
        }
    }

    private void saveOrganisations(OWLOntology onto) throws OWLOntologyCreationException {
        if (onto != null) {
            if (organisationCollection != null) {
                for (AsConcept ac : organisationCollection) {
                    IOrganisation organisation = (IOrganisation) ac;
                    OWLDataFactory factory = manager.getOWLDataFactory();
                    // ===============================================================
                    // Create the individual
                    // ===============================================================
                    OWLNamedIndividual individual = makeEARSIndividual(ac, factory, pm);//factory.getOWLNamedIndividual(":" + buildUrlFragment(ac), pm);
                    OWLClass processCl = factory.getOWLClass(":Organisation", pm);
                    OWLClassAssertionAxiom isProcAssertion = factory.getOWLClassAssertionAxiom(processCl, individual);
                    manager.addAxiom(onto, isProcAssertion);
                    // ===============================================================
                    // Save the metadata (vcard)
                    // ===============================================================
                    OWLAnonymousIndividual address = makeAnonymousIndividual(manager);

                    if (organisation.getDeliveryPoint() != null) {
                        makeLiteralDataProperty(onto, manager, "vc:street-address", organisation.getDeliveryPoint().trim(), address, null);
                    }
                    if (organisation.getDeliveryPoint() != null) {
                        makeLiteralDataProperty(onto, manager, "vc:locality", organisation.getCity().trim(), address, null);
                    }
                    if (organisation.getDeliveryPoint() != null) {
                        makeLiteralDataProperty(onto, manager, "vc:postal-code", organisation.getPostalCode().trim(), address, null);
                    }
                    if (organisation.getDeliveryPoint() != null) {
                        makeLiteralDataProperty(onto, manager, "vc:region", organisation.getAdministrativeArea().trim(), address, null);
                    }
                    if (organisation.getDeliveryPoint() != null) {
                        makeLiteralDataProperty(onto, manager, "vc:country-name", organisation.getCountry().trim(), address, null);
                    }
                    makeObjectProperty(onto, manager, "vc:hasAddress", individual, address);
                    // ===============================================================
                    // Save the metadata (vcard:mail)
                    // ===============================================================
                    if (organisation.getElectronicMailAddress() != null && !organisation.getElectronicMailAddress().startsWith("http") && !organisation.getElectronicMailAddress().equals("")) {
                        OWLNamedIndividual email = makeSimpleIndividual(manager, "mailto:" + organisation.getElectronicMailAddress().trim());
                        makeObjectProperty(onto, manager, "vc:hasEmail", individual, email);

                    }
                    String countryString = organisation.getCountry();
                    ICountry country = null;
                    List<ICountry> results = ((Stream<ICountry>) countryCollection.stream()).filter(c -> c.getTermRef().getEarsTermLabel().getPrefLabel().equalsIgnoreCase(countryString)).collect(Collectors.toList());
                    if (results != null && !results.isEmpty()) {
                        country = results.get(0);
                    }
                    if (country != null) {
                        OWLNamedIndividual countryIndividual = makeEARSIndividual(country, factory, pm);
                        makeObjectProperty(onto, manager, "gn:parentCountry", individual, countryIndividual);
                    }
                    // ===============================================================
                    // Create the associated individual concept
                    // ===============================================================
                    saveConcept(ac, individual, onto, pm);
                    //addToCollection(individual, ":organisationCollection", onto, pm);
                }
            }
        }
    }

    private void saveCountries(OWLOntology onto) throws OWLOntologyCreationException {
        if (onto != null) {
            if (countryCollection != null) {
                for (AsConcept ac : countryCollection) {
                    OWLDataFactory factory = manager.getOWLDataFactory();
                    // ===============================================================
                    // Create the individual
                    // ===============================================================
                    OWLNamedIndividual individualParameter = makeEARSIndividual(ac, factory, pm);//factory.getOWLNamedIndividual(":" + buildUrlFragment(ac), pm);
                    OWLClass processCl = factory.getOWLClass(":Country", pm);
                    OWLClassAssertionAxiom isProcAssertion = factory.getOWLClassAssertionAxiom(processCl, individualParameter);
                    manager.addAxiom(onto, isProcAssertion);
                    // ===============================================================
                    // Create the associated individual concept
                    // ===============================================================
                    saveConcept(ac, individualParameter, onto, pm);
                    //addToCollection(individual, ":countryCollection", onto, pm);
                }
            }
        }
    }

    /**
     * *
     * Store all the EDMERP projects in the ontology
     *
     * @param onto
     * @throws OWLOntologyCreationException
     */
    private void saveProjects(OWLOntology onto) throws OWLOntologyCreationException {
        if (onto != null) {
            if (projectCollection != null) {
                for (AsConcept ac : projectCollection) {
                    IProject project = (IProject) ac;
                    OWLDataFactory factory = manager.getOWLDataFactory();
                    // ===============================================================
                    // Create the individual
                    // ===============================================================
                    OWLNamedIndividual individual = makeEARSIndividual(ac, factory, pm);//factory.getOWLNamedIndividual(":" + buildUrlFragment(ac), pm);
                    OWLClass processCl = factory.getOWLClass(":Project", pm);
                    OWLClassAssertionAxiom isProcAssertion = factory.getOWLClassAssertionAxiom(processCl, individual);
                    manager.addAxiom(onto, isProcAssertion);
                    // ===============================================================
                    // Save the dates
                    // ===============================================================
                    if (project.getStartDate() != null) {
                        makeLiteralDataProperty(onto, manager, "dbpedia:projectStartDate", dateToISO8601String(project.getStartDate()), individual, OWL2Datatype.XSD_DATE_TIME);
                    }
                    if (project.getEndDate() != null) {
                        makeLiteralDataProperty(onto, manager, "dbpedia:projectEndDate", dateToISO8601String(project.getEndDate()), individual, OWL2Datatype.XSD_DATE_TIME);
                    }
                    //makeLiteralDataProperty(onto, manager, "ears2:mdAuthor", project.getAuthor().trim(), responsibleParty, null);
                    //makeLiteralDataProperty(onto, manager, "ears2:originator", project.getOriginator().trim(), address, null);
                    //makeLiteralDataProperty(onto, manager, "ears2:pointOfContact", project.getPointOfContact().trim(), address, null););
                    // ===============================================================
                    // Save the project coordinator
                    // ===============================================================
                    String coordinatorString = project.getOriginator();
                    IOrganisation coordinator = null;
                    List<IOrganisation> organisations = ((Stream<IOrganisation>) organisationCollection.stream()).filter(c -> c.getTermRef().getOrigUrn().equalsIgnoreCase(coordinatorString)).collect(Collectors.toList());
                    if (organisations != null && !organisations.isEmpty()) {
                        coordinator = organisations.get(0);
                    }
                    if (coordinator != null) {
                        OWLNamedIndividual countryIndividual = makeEARSIndividual(coordinator, factory, pm);
                        makeObjectProperty(onto, manager, "dbpedia:projectCoordinator", individual, countryIndividual);
                    }
                    // ===============================================================
                    // Save the country
                    // ===============================================================
                    String countryString = project.getCountry();
                    ICountry country = null;
                    List<ICountry> results = ((Stream<ICountry>) countryCollection.stream()).filter(c -> c.getTermRef().getEarsTermLabel().getPrefLabel().equalsIgnoreCase(countryString)).collect(Collectors.toList());
                    if (results != null && !results.isEmpty()) {
                        country = results.get(0);
                    }
                    if (country != null) {
                        OWLNamedIndividual countryIndividual = makeEARSIndividual(country, factory, pm);
                        makeObjectProperty(onto, manager, "gn:parentCountry", individual, countryIndividual);
                    }
                    // ===============================================================
                    // Create the associated individual concept
                    // ===============================================================
                    saveConcept(ac, individual, onto, pm);
                    //addToCollection(individual, ":organisationCollection", onto, pm);
                }
            }
        }
    }

    private static void reportError(String message, Exception e) {
        if (e == null) {
            log.log(Level.INFO, message);
        } else {
            log.log(Level.SEVERE, message);
        }
    }

    /**
     * *
     * Null-config method. Create an rdf-based ontology file for the path, group
     * and permissions. Uses RDF serialization.
     *
     * @param importOrPaste whether to paste the owl individuals or to inlude
     * them as an owl:imports header
     * @param axiomaFile the ontology that contains the rdf schema without any
     * individuals
     * @param owner the unix owner
     * @param fullPath the full path where the ontology will be stored
     * @param perm the unix rwx permissions
     * @param group the unix group
     * @return
     * @throws OWLOntologyCreationException
     */
    public BufferedOutputStream createOntoFile(LoadOnto importOrPaste, File axiomaFile, int newVersion, Path fullPath, String owner, String perm, String group, boolean overwriteIfExists) throws OWLOntologyCreationException {
        URL axiomaUrl = null;
        try {
            axiomaUrl = Paths.get(axiomaFile.getCanonicalPath()).toUri().toURL();
        } catch (IOException ex) {
            throw new OWLOntologyCreationException("The owl ontology could not be created because the provided File axiomaFile cannot be found.");
        }
        OntologyFileWriter owlFileCreator = new OntologyFileWriter(this.getManager());
        OntologyFileFormat outputFormat = OntologyFileFormat.RDF_FORMAT;
        OWLOntology earsOnto = createOntology(importOrPaste, axiomaUrl, newVersion);
        if (earsOnto != null) {
            return owlFileCreator.createOntoFile(earsOnto, outputFormat, fullPath, owner, perm, group, overwriteIfExists);
        } else {
            throw new OWLOntologyCreationException("The owl ontology could not be created from the given inputs.");
        }
    }

    /**
     * Combine two owl ontologies and provide them using a new name
     *
     * @param oo1 The first ontology
     * @param oo2 The second ontology
     * @param name The new name of the new ontology
     * @return
     * @throws OWLOntologyCreationException
     */
    public OWLOntology mergeOntology(OWLOntology oo1, OWLOntology oo2, String name) throws OWLOntologyCreationException {
        OWLOntologyManager m = OWLManager.createOWLOntologyManager();
        OWLOntology o1 = m.copyOntology(oo1, OntologyCopy.DEEP);
        OWLOntology o2 = m.copyOntology(oo2, OntologyCopy.DEEP);

        OWLOntologyMerger merger = new OWLOntologyMerger(m);
        // Merge all of the loaded ontologies, specifying an IRI for the
        IRI mergedOntologyIRI = IRI.create(OntologyConstants.EARS2_NS);
        //OWLOntology merged = null;

        OWLOntology merged = merger.createMergedOntology(m, mergedOntologyIRI);
        OWLOntologyManager m2 = OWLManager.createOWLOntologyManager();
        writeOntologyMetadata(m2, merged, name, ScopeMap.BASE_SCOPE, 0);

        return merged;
    }
}
