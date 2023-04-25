/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package be.naturalsciences.bmdc.ontology.entities;

import be.naturalsciences.bmdc.ontology.ConceptHierarchy;
import be.naturalsciences.bmdc.ontology.IAsConceptFactory;
import eu.eurofleets.ears3.AbstractConcept;
import java.net.URI;
import java.util.IdentityHashMap;
import java.util.List;
import java.util.Set;

public interface AsConcept<T extends Term> extends EARSThing, AbstractConcept {

    public Long getId();

    public void setId(Long id);

    public URI getUri();

    public void setUri(URI uri);

    public String getUrn();

    public T getTermRef();

    public void setTermRef(T termRef);

    public String getKind();

    @Override
    public String toString();

    public AsConcept clone(IdentityHashMap<Object, Object> clonedObjects) throws CloneNotSupportedException;

    public void isolate();

    @Override
    public int hashCode();

    @Override
    public boolean equals(Object object);

    public boolean hasChildren();

    public Set<? extends AsConcept> getChildren(ConceptHierarchy parents);

    public List<? extends AsConcept> getParents();

    public void addToChildren(ConceptHierarchy ownParents, AsConcept newChildConcept, boolean removePreviousBottomUpAssociations, ConceptHierarchy newChildParents, IAsConceptFactory factory);

    public Class getParentType();

    public Class getChildType();

    public void delete(ConceptHierarchy parents);

    public int getLastId();

    public void init();

    /**
     * *
     * Get the Uri as a URI of the given Concept.
     *
     * @param concept
     * @return
     * @throws IllegalArgumentException
     */
    public static URI getConceptUri(AsConcept concept) throws IllegalArgumentException {
        if (concept != null) {
            if (concept.getUri() != null) {
                return concept.getUri();
            } else {
                throw new IllegalArgumentException("Concept" + concept.toString() + " has no Uri");
            }
        } else {
            throw new IllegalArgumentException("Provided concept is null");
        }
    }

    public static String getConceptUriString(AsConcept concept) throws IllegalArgumentException {
        return getConceptUri(concept).toASCIIString();
    }

    public static String getConceptName(AsConcept concept) {
        if (concept != null) {
            return concept.getTermRef().getEarsTermLabel().getPrefLabel();
        } else {
            throw new IllegalArgumentException("Provided concept is null");
        }
    }
}
