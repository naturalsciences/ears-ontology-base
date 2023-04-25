/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package be.naturalsciences.bmdc.ontology;

import be.naturalsciences.bmdc.ontology.entities.AsConcept;
import be.naturalsciences.bmdc.ontology.entities.IFakeConcept;
import java.nio.file.Path;
import java.util.Comparator;
import java.util.Set;

/**
 *
 * @author thomas
 */
public interface IOntologyNodes<T extends AsConcept> {

    public boolean save();

    public boolean saveAs(Path destPath);

    public Set<T> getNodes();

    public void setRoot(IFakeConcept root);

    Set<AsConcept> getIndividuals(boolean sorted);

    <C extends AsConcept> Set< C> getIndividuals(Class< C> cls, Comparator comp);

    <C extends AsConcept> Set< C> getIndividuals(Class< C> cls, boolean sorted);

    public AsConcept findIndividualConcept(String uri);

}
