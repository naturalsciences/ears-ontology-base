/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package be.naturalsciences.bmdc.ontology;

import be.naturalsciences.bmdc.ontology.entities.AsConcept;
import java.util.EventObject;

/**
 *
 * @author thomas
 */
public class AsConceptEvent extends EventObject {

    private AsConcept conceptThatChanged;
    private Long conceptId;
    private Long termId;

    public AsConceptEvent(Object caller, AsConcept conceptThatChanged, Long conceptId, Long termId) {
        super(caller);
        this.conceptThatChanged = conceptThatChanged;
        this.conceptId = conceptId;
        this.termId = termId;
    }

    public AsConcept getConceptThatChanged() {
        return conceptThatChanged;
    }

    public void setConceptThatChanged(AsConcept conceptThatChanged) {
        this.conceptThatChanged = conceptThatChanged;
    }

    public Long getConceptId() {
        return conceptId;
    }

    public Long getTermId() {
        return termId;
    }

}
