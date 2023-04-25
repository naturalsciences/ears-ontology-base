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
public interface IGenericEventDefinition<EC extends ICompoundEarsTerm, E extends IEarsTerm, TC extends IToolCategory, PA extends IProcessAction, EV extends IEventDefinition, SEV extends ISpecificEventDefinition, PR extends IProperty> extends AsConcept<EC>, Serializable {//, IEventDefinition<E, PR, EV> {

    public void addCompoundEarsTerm();

    public IAction getAction();

    public Long getEventRef();

    public String getLabel();

    public String getPrefLabel();

    public IProcess getProcess();

    public IProcessAction getProcessAction();

    public Collection<? extends ISpecificEventDefinition> getSpecificEventDefinitionCollection();

    public IToolCategory getToolCategoryRef();

    public void setEventRef(Long eventRef);

    public void setLabel();

    public void setProcessAction(PA pa);

    public void setSpecificEventDefinitionCollection(Collection<SEV> specificEventDefinitionCollection);

    public void setToolCategoryRef(TC tc);

    public Collection<?> getPropertyCollection();

    public Boolean getIsDataProvider();

    public Collection<?> getTriggeredCollection();

}
