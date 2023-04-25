/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package be.naturalsciences.bmdc.ontology;

import be.naturalsciences.bmdc.ontology.entities.AsConcept;
import be.naturalsciences.bmdc.ontology.entities.Term;

/**
 *
 * @author Thomas Vandenberghe
 */
public class OntologyServices {

    public static final String NERC_PREFIX = "http://vocab.nerc.ac.uk/collection/";

    public static String getConceptURI(AsConcept concept) {
        return "http://ontologies.ef-ears.eu/ears2/1#" + concept.getTermRef().getKind().toLowerCase() + "_" + concept.getId();
    }

    public static String getTermURI(boolean bodc, Term term) {
        if (bodc) {
            return getBodcURI(term.getPublisherUrn());
        } else {
            return "http://ontologies.ef-ears.eu/ears2/1#concept_" + term.getId();
        }
    }

    public static String getBodcURI(String sdnCode) {
        String[] tokens = sdnCode.split(":");
        if (tokens.length < 4) {
            return null;
        }
        return NERC_PREFIX + tokens[1] + "/current/" + tokens[3] + "/";
    }

}
