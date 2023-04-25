/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package be.naturalsciences.bmdc.ontology.entities;

import java.io.Serializable;
import java.util.Collection;
import java.util.List;

/**
 *
 * @author Thomas Vandenberghe
 */
public interface IProcessAction<E extends IEarsTerm, P extends IProcess, A extends IAction, PA extends IProcessAction, PAPK extends IProcessActionPK, SEV extends ISpecificEventDefinition, GEV extends IGenericEventDefinition> extends Serializable {

    public IAction getAction();

    public Collection<? extends IEventDefinition> getEvents();

    public Collection<? extends IGenericEventDefinition> getGenericEventDefinitionCollection();

    public Boolean getIsDataProvider();

    public IProcess getProcess();

    public IProcessAction getProcessAction();

    public Collection<? extends IProcessAction> getProcessActionCollection();

    public IProcessActionPK getProcessActionPK();

    public Collection<? extends ISpecificEventDefinition> getSpecificEventDefinitionCollection();

    public List<? extends ITool> getTools();

    public void setAction(A action);

    public void setGenericEventDefinitionCollection(Collection<GEV> genericEventDefinitionCollection);

    public void setIsDataProvider(Boolean isDataProvider);

    public void setProcess(P process);

    public void setProcessAction(PA processAction);

    public void setProcessActionCollection(Collection<PA> processActionCollection);

    public void setProcessActionPK(PAPK processActionPK);

    public void setSpecificEventDefinitionCollection(Collection<SEV> specificEventDefinitionCollection);
}
