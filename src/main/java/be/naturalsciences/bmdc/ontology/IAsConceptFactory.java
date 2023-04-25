/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package be.naturalsciences.bmdc.ontology;

import be.naturalsciences.bmdc.ontology.entities.AsConcept;

/**
 *
 * @author Thomas Vandenberghe
 */
public interface IAsConceptFactory {

    public IOntologyModel getModel();

    public IIndividuals getIndividuals();

    public <C extends AsConcept> AsConcept buildChild(C parent) throws EarsException;

    public <C extends AsConcept> C build(Class<C> cls) throws EarsException;

}
