/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package be.naturalsciences.bmdc.ontology.entities;

import java.io.Serializable;
import java.util.Collection;

/**
 *
 * @author Thomas Vandenberghe
 */
public interface IToolCategory< E extends IEarsTerm, T extends ITool, V extends IVessel, GEV extends IGenericEventDefinition, S extends ISubject> extends AsConcept<E>, Serializable {

    /**
     * Adds the given tool to this collection. Note that this method does not
     * add this category to the tool as well!
     *
     *
     */
    public void addTool(T tool);

    public Collection<GEV> getGenericEventDefinitionCollection();

    //public Collection<? extends ISubject> getSubjectCollection();

    public Collection<? extends ITool> getToolCollection();

    //public Collection<? extends IVessel> getVesselCollection();
    public void setGenericEventDefinitionCollection(Collection<GEV> genericEventDefinitionCollection);

    //public void setSubjectCollection(Collection<S> subjectCollection);

    public void setToolCollection(Collection<T> toolCollection);

    //public void setVesselCollection(Collection<V> vesselCollection);
}
