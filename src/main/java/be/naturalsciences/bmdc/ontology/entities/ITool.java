/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package be.naturalsciences.bmdc.ontology.entities;

import java.io.Serializable;
import java.util.Collection;
import java.util.Set;

/**
 *
 * @author Thomas Vandenberghe
 */
public interface ITool< E extends IEarsTerm, TC extends IToolCategory, T extends ITool, P extends IProcess, SEV extends ISpecificEventDefinition, GEV extends IGenericEventDefinition> extends AsConcept<E>, Serializable {

    /**
     * Adds the given category to the tool.
     *
     */
    public void addToCategory(TC tc);

    /**
     * Get all the Actions of a tool.
     *
     * @return
     */
    public Set<? extends IAction> getActionCollection();

    /**
     * Get all the Actions of a tool that have the given Process
     *
     * @param p
     * @return
     */
    public Set<? extends IAction> getActionCollection(P p);

    public Collection<? extends IEventDefinition> getEvents();

    public Collection<T> getHostedCollection();

    public Collection<? extends ITool> getHostsCollection();

    /**
     * *
     * Whether this tool is hosts other tools.
     *
     * @return
     */
    public boolean isHostingTool();

    /**
     * *
     * Whether this tool is hosted (=nested) on another
     *
     * @return
     */
    public boolean isHostedTool();

    public String getSerialNumber();

    public void setSerialNumber(String serialNumber);

    public String getToolIdentifier();

    public void setToolIdentifier(String toolIdentifier);

    /**
     * Get all the Processes of a tool.
     *
     * @return
     */
    public Set<? extends IProcess> getProcessCollection();

    public Collection<? extends ISpecificEventDefinition> getSpecificEventDefinitionCollection();

    public Collection<? extends IToolCategory> getToolCategoryCollection();

    public void setHostedCollection(Collection<T> hostedCollection);

    public void setHostsCollection(Collection<T> hostsCollection);

    /*public void setIsComposite(boolean isComposite);

    public void setIsSensor(boolean isSensor);*/
    public void setSpecificEventDefinitionCollection(Collection<SEV> specificEventDefinitionCollection);

    public void setToolCategoryCollection(Collection<TC> toolCategoryCollection);
}
