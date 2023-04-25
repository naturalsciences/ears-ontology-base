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
public interface ISpecificEventDefinition<E extends IEarsTerm, T extends ITool, PA extends IProcessAction, EV extends IEventDefinition, GEV extends IGenericEventDefinition, PR extends IProperty> extends AsConcept<E>, Serializable {//, IEventDefinition<E, PR, EV> {

    public IAction getAction();

    public Long getEventRef();

    public String getLabel();

    public IProcess getProcess();

    public IProcessAction getProcessAction();

    public IGenericEventDefinition getRealizesRef();

    public ITool getToolRef();

    public void setEventRef(Long eventRef);

    public void setLabel();

    public void setProcessAction(PA processAction);

    public void setRealizesRef(GEV realizesRef);

    public void setToolRef(T toolRef);

    public Collection<?> getTriggeredCollection();

    public Collection<?> getTriggerCollection();

    public Boolean getIsDataProvider();

    public Collection<?> getPropertyCollection();
}
