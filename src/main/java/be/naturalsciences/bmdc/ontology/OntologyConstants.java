/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package be.naturalsciences.bmdc.ontology;

import be.naturalsciences.bmdc.ontology.writer.FileUtils;
import gnu.trove.set.hash.THashSet;
import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.net.URL;
import java.nio.file.Paths;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.apache.commons.collections4.BidiMap;
import org.apache.commons.collections4.bidimap.TreeBidiMap;

/**
 *
 * @author thomas
 */
public class OntologyConstants {

    public static final String EARS2_NS = "http://ontologies.ef-ears.eu/ears2/1#";
    public static final String EARS2_BASE_NS = "http://ontologies.ef-ears.eu/ears2/1";

    public static final String SPARQL_ENDPOINT = "http://ontologies.ef-ears.eu/sparql?query=";
    public static final String EARS2_HOST = "ontologies.ef-ears.eu";
    public static final String EARS2_SCHEME = "http";
    public static final String EARS2_PATH = "/ears2/1/";

    public static final String ONTOLOGY_FILE_LOCATION = "/var/www/ears2/1/current";

    public static final String BASE_ONTOLOGY_FILENAME = "earsv2-onto.rdf";
    public static final String COMPRESSED_BASE_ONTOLOGY_FILENAME = "earsv2-onto.rdf.7z";
    public static final String BASE_ONTOLOGY_FILENAME_NO_EXT = "earsv2-onto";
    public static final String STATIC_ONTOLOGY_FILENAME = "earsv2-static.rdf";
    public static final String STATIC_ONTOLOGY_FILENAME_NO_EXT = "earsv2-static";
    public static final String VESSEL_ONTOLOGY_FILENAME = "earsv2-onto-vessel.rdf";
    public static final String VESSEL_ONTOLOGY_FILENAME_NO_EXT = "earsv2-onto-vessel";
    public static final String TEST_ONTOLOGY_FILENAME = "test-ontology.rdf";
    public static final String TEST_ONTOLOGY_FILENAME_NO_EXT = "test-ontology";
    public static final String NERC_URL_PREFIX = "http://vocab.nerc.ac.uk/collection/";
    public static final SimpleDateFormat YYYYMMDD_FM = new SimpleDateFormat("yyyyMMdd");
    public static final String BODCGOV = "SeaVox (BODC/SDN)";
    public static final String EFGOV = "Eurofleets Governance Team";
    public static final String ONTO_AXIOM_SERVER_PATH = "/var/www/ears2/owl";

    public static final String ONTOLOGY_AXIOM_FILENAME_BASE = "earsv2-schema";
    public static final String ONTOLOGY_AXIOM_FILENAME_FORMAT = ONTOLOGY_AXIOM_FILENAME_BASE + "-%1$tY%1$tm%1$td";
    public static final String ONTOLOGY_AXIOM_URL = "http://ontologies.ef-ears.eu/ears2/owl";
    public static final String CURRENT_ONTOLOGY_PATH = EARS2_BASE_NS + "/current";
    public static final URI CURRENT_ONTOLOGY_URI = URI.create(OntologyConstants.CURRENT_ONTOLOGY_PATH);

    public static final String ONTOLOGY_METADATA_JSON_URL = "http://ontologies.ef-ears.eu/ontology.json";

    public static URL SERVER_ONTOLOGY_AXIOM_URL;

    static {
        File axFile = FileUtils.findLastFileByNameInDir(ONTO_AXIOM_SERVER_PATH, "xml");
        try {
            if (axFile != null) {
                SERVER_ONTOLOGY_AXIOM_URL = Paths.get(axFile.getCanonicalPath()).toUri().toURL();
            }
        } catch (IOException ex) {
            Logger.getLogger(OntologyConstants.class.getName()).log(Level.SEVERE, "An exception occured.", ex);
        }
    }

    public static class ConceptMD {

        public String className;
        public String sparqlWhere;
        public String abbreviation;
        public boolean inTree;
        public List<String> sdn;

        public ConceptMD(String className, String abbreviation, List<String> sdn, String sparqlWhere, boolean inTree) {
            this.className = className;
            this.sparqlWhere = sparqlWhere;
            this.abbreviation = abbreviation;
            this.inTree = inTree;
            this.sdn = sdn;
        }

        public boolean urnMatches(String urn) {
            if (urn.toLowerCase().contains(this.abbreviation.toLowerCase())) {
                return true;
            }
            if (this.sdn != null) {
                for (String s : this.sdn) {
                    if (urn.toLowerCase().contains(s.toLowerCase())) {
                        return true;
                    }
                }
            }
            return false;
        }
    }

    public static final Collection<ConceptMD> CLASSES;
    public static final BidiMap STATUSES;

    public static final String VALIDATED = "VL";
    public static final String WAITFORAPPROVAL = "WA";
    public static final String REJECTED = "RJ";
    public static final String DEPRECATED = "DP";

    public static final ConceptMD EARSTERM = new ConceptMD("EarsTerm", "concept", null, "?s a skos:Concept", false);

    public static final ConceptMD TOOLCATEGORY = new ConceptMD("ToolCategory", "CTG", Arrays.asList("L05", "L06"), "?s a ears2:ToolCategory", true);
    public static final ConceptMD TOOL = new ConceptMD("Tool", "DEV", Arrays.asList("L22"), "?s a ears2:Tool", true);
    public static final ConceptMD PROCESS = new ConceptMD("Process", "PRO", null, "?s a ears2:Process", true);
    public static final ConceptMD ACTION = new ConceptMD("Action", "ACT", null, "{?s a ears2:ProcessStep} UNION {?s a ears2:Incident}", true);
    public static final ConceptMD PROPERTY = new ConceptMD("Property", "PRY", null, "?s a ears2:EventProperty", true);

    public static final ConceptMD INCIDENT = new ConceptMD("Incident", "ACT", null, "?s a ears2:Incident", false);
    public static final ConceptMD PROCESSSTEP = new ConceptMD("ProcessStep", "ACT", null, "?s a ears2:ProcessStep", false);

    public static final ConceptMD SUBJECT = new ConceptMD("Subject", "SUJ", Arrays.asList("C77"), "?s a ears2:Subject", false);
    public static final ConceptMD GENERICEVENTDEFINITION = new ConceptMD("GenericEventDefinition", "GEV", null, "?s a ears2:GenericEventDefinition", false);
    public static final ConceptMD SPECIFICEVENTDEFINITION = new ConceptMD("SpecificEventDefinition", "SEV", null, "?s a ears2:SpecificEventDefinition", false);
    public static final ConceptMD OBJECTVALUE = new ConceptMD("ObjectValue", "OBV", null, "?s a ears2:ObjectValue", false);

    public static final ConceptMD PARAMETER = new ConceptMD("Parameter", "PAR", Arrays.asList("P02"), "?s a ears2:Parameter", false);
    public static final ConceptMD HARBOUR = new ConceptMD("Harbour", "HAR", Arrays.asList("C38"), "?s a ears2:Harbour", false);
    public static final ConceptMD SEAAREA = new ConceptMD("SeaArea", "SEA", Arrays.asList("C16"), "?s a ears2:SeaArea", false);
    public static final ConceptMD ORGANISATION = new ConceptMD("Organisation", "ORG", Arrays.asList("EDMO"), "?s a ears2:Organisation", false);
    public static final ConceptMD COUNTRY = new ConceptMD("Country", "COU", Arrays.asList("C32"), "?s a ears2:Country", false);
    public static final ConceptMD VESSEL = new ConceptMD("Vessel", "VES", Arrays.asList("C17"), "?s a ears2:Vessel", false);//"?s a ears2:Tool . ?s ears2:isMemberOf ?tc"

    static {
        CLASSES = new THashSet();
        CLASSES.add(TOOLCATEGORY);
        CLASSES.add(TOOL);
        CLASSES.add(VESSEL);
        CLASSES.add(PROCESS);
        CLASSES.add(ACTION);
        CLASSES.add(PROPERTY);
        CLASSES.add(INCIDENT);
        CLASSES.add(PROCESSSTEP);
        CLASSES.add(SUBJECT);
        CLASSES.add(GENERICEVENTDEFINITION);
        CLASSES.add(SPECIFICEVENTDEFINITION);
        CLASSES.add(OBJECTVALUE);
        CLASSES.add(PARAMETER);
        CLASSES.add(HARBOUR);
        CLASSES.add(SEAAREA);
        CLASSES.add(ORGANISATION);
        CLASSES.add(COUNTRY);
    }

    static {
        STATUSES = new TreeBidiMap();
        STATUSES.put(OntologyConstants.DEPRECATED, "Deprecated");
        STATUSES.put(OntologyConstants.REJECTED, "Rejected");
        STATUSES.put(OntologyConstants.VALIDATED, "Validated");
        STATUSES.put(OntologyConstants.WAITFORAPPROVAL, "WaitForApproval");
    }
}
