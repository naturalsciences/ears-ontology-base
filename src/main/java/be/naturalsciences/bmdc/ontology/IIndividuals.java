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
public interface IIndividuals {

    public AsConcept getByPrefLabel(String s);

    public AsConcept getByUri(String s);

    public AsConcept getByUrn(String s);

    public IOntologyModel getModel();

    <C extends AsConcept> void add(C c/*, Long conceptId, Long termId*/);

    public void add(EventObject ev);

    public void remove(EventObject ev);

    public void change(EventObject ev);

    public <C extends AsConcept> void remove(C c);

    //public <C extends AsConcept> void change(C c);
    public void refresh();

    public <C extends AsConcept> long getGlobalHighestIdOfClass(Class<C> cls);

    public long getGlobalHighestEarsTermId();

}
