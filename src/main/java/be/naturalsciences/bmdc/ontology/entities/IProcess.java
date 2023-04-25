/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package be.naturalsciences.bmdc.ontology.entities;

import java.io.Serializable;
import java.util.Collection;
import java.util.List;
import java.util.Set;

/**
 *
 * @author Thomas Vandenberghe
 */
public interface IProcess<E extends IEarsTerm, T extends ITool, P extends IProcess, A extends IAction, PA extends IProcessAction, EV extends IEventDefinition, S extends ISubject> extends AsConcept<E>, Serializable {

    /**
     * *
     * Get all the Actions of this Process
     *
     * @return
     */
    Collection<? extends IAction> getActionCollection();

    /**
     * *
     * Get all the actions of this Process and limit them by tool
     *
     * @param tool
     * @return
     */
    Set<? extends IAction> getActionCollection(T tool);

    Collection<IEventDefinition> getEventDefinition();

    /**
     * *
     * Get all the EventDefinition of this Process and limit them by tool and
     * action
     *
     * @param tool
     * @return
     */
    List<? extends IEventDefinition> getEventDefinitionCollection(T tool, A action);

    public IProcess getNextProcessRef();

    public IProcess getParentRef();

    public IProcess getProcess();

    public Collection<? extends IAction> getActionCollectionFromEvent();

    public Collection<? extends IProcess> getProcessCollection();

    public void setActionCollection(Collection<A> actionCollection);

    public void setEventDefinition(Collection<IEventDefinition> eventDefinition);

    public void setNextProcessRef(P nextProcessRef);

    public void setParentRef(P parentRef);

    public void setProcess(P process);

    public void setProcessActionCollection(Collection<PA> processActionCollection);

    public void setProcessCollection(Collection<P> processCollection);

}
