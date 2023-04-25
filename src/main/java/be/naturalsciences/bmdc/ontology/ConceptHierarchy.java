/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package be.naturalsciences.bmdc.ontology;

import be.naturalsciences.bmdc.ontology.entities.AsConcept;
import be.naturalsciences.bmdc.ontology.entities.IAction;
import be.naturalsciences.bmdc.ontology.entities.IEventDefinition;
import be.naturalsciences.bmdc.ontology.entities.IFakeConcept;
import be.naturalsciences.bmdc.ontology.entities.IGenericEventDefinition;
import be.naturalsciences.bmdc.ontology.entities.IProcess;
import be.naturalsciences.bmdc.ontology.entities.IProperty;
import be.naturalsciences.bmdc.ontology.entities.ITool;
import be.naturalsciences.bmdc.ontology.entities.IToolCategory;
import gnu.trove.set.hash.THashSet;
import java.util.Collection;
import java.util.List;

/**
 *
 * @author Thomas Vandenberghe
 */
public class ConceptHierarchy {

    private IFakeConcept root;

    private IToolCategory toolCategory;
    private ITool tool;
    private ITool hostedTool;
    private IProcess process;
    private IAction action;
    private IProperty property;

    public IToolCategory getToolCategory() {
        return toolCategory;
    }

    public void setToolCategory(IToolCategory toolCategory) {
        this.toolCategory = toolCategory;
    }

    /**
     * *
     * Get the tool, or if it exists, the hosted tool.
     *
     * @return
     */
    public ITool getTool() {
        return tool;
    }

    public ITool getLowestToolInHierarchy() {
        return (hostedTool == null) ? tool : hostedTool;
    }

    public void setTool(ITool tool) {
        this.tool = tool;
    }

    public ITool getHostedTool() {
        return hostedTool;
    }

    public void setHostedTool(ITool hostedTool) {
        this.hostedTool = hostedTool;
    }

    public IProcess getProcess() {
        return process;
    }

    public void setProcess(IProcess process) {
        this.process = process;
    }

    public IAction getAction() {
        return action;
    }

    public void setAction(IAction action) {
        this.action = action;
    }

    public IProperty getProperty() {
        return property;
    }

    public void setProperty(IProperty property) {
        this.property = property;
    }

    public IFakeConcept getRoot() {
        return root;
    }

    public void setRoot(IFakeConcept root) {
        this.root = root;
    }

    /**
     * *
     * Constructor used for root level
     */
    public ConceptHierarchy() {

    }

    public ConceptHierarchy(IToolCategory toolCategory, ITool tool, ITool hostedTool, IProcess process, IAction action, IProperty property, IFakeConcept root) {
        this.toolCategory = toolCategory;
        this.tool = tool;
        this.hostedTool = hostedTool;
        this.process = process;
        this.action = action;
        this.property = property;
        this.root = root;
    }

    public ConceptHierarchy(Collection<? extends AsConcept> allConcepts) {
        Collection<ITool> hostedTools = new THashSet<>();
        Collection<ITool> gatheredTools = new THashSet<>();
        for (AsConcept a : allConcepts) {
            if (a instanceof ITool) {
                ITool tool = (ITool) a;
                gatheredTools.add(tool);
                if (tool.getHostedCollection().size() > 0) {
                    hostedTools.addAll(tool.getHostedCollection());
                }
            } else {
                add(a);
            }
        }
        for (ITool gatheredTool : gatheredTools) {
            if (hostedTools.contains(gatheredTool)) { //ie the tool in the argument is used as a nested tool of another provided one
                this.hostedTool = gatheredTool;
            } else {
                this.tool = gatheredTool;
            }
        }
    }

    public ConceptHierarchy(ConceptHierarchy other) {
        this.toolCategory = other.getToolCategory();
        this.tool = other.getTool();
        this.hostedTool = other.getHostedTool();
        this.process = other.getProcess();
        this.action = other.getAction();
        this.property = other.getProperty();
        this.root = other.getRoot();
    }

    /**
     * Test if the hierarchy is built from a generic event definition in the toolcategory
     * @return 
     */
    public boolean isGeneric() {
        for (Object gevO : this.toolCategory.getGenericEventDefinitionCollection()) {
            IGenericEventDefinition gev = (IGenericEventDefinition) gevO;
            if (gev != null && this.process != null && this.process.equals(gev.getProcess())) {
                return true;
            }
        }
        return false;
    }

    /**
     * *
     * Returns the true direct parent of the given concept.
     *
     * @param concept
     * @return
     */
    public AsConcept getParent(AsConcept concept) {
        if (concept instanceof IToolCategory) {
            return root;
        } else if (concept instanceof ITool) {
            return this.toolCategory;
        } else if (concept instanceof IProcess) {
            return this.tool;
        } else if (concept instanceof IAction) {
            return this.process;
        } else if (concept instanceof IProperty) {
            return this.action;
        } else {
            return null;
        }
    }

    /**
     * *
     * Adds a concept to the node group if the slot of its type is empty. If the
     * slot is not null, concept is not added. For nested tools: do not use to
     * add nested tools.
     *
     * @param concept
     */
    public ConceptHierarchy add(AsConcept concept) {
        if (concept instanceof IToolCategory && this.toolCategory == null) {
            this.toolCategory = (IToolCategory) concept;
        } else if (concept instanceof ITool && this.tool == null) {
            ITool tool = (ITool) concept;
            if (!tool.isHostedTool()) { //if I am not part of a larger tool
                this.tool = tool;
                //this.hostedTool = null;
            }
        } else if (concept instanceof ITool && this.hostedTool == null) {
            ITool tool = (ITool) concept;
            if (tool.isHostedTool()) {
                this.hostedTool = tool;
                // this.tool = null;
            }
        } else if (concept instanceof IProcess && this.process == null) {
            this.process = (IProcess) concept;
        } else if (concept instanceof IAction && this.action == null) {
            this.action = (IAction) concept;
        } else if (concept instanceof IProperty && this.property == null) {
            this.property = (IProperty) concept;
        } else if (concept instanceof IFakeConcept && this.root == null) {
            this.root = (IFakeConcept) concept;
        }
        return this;
    }

    /**
     * *
     * Set the slot (free it) of the type of @param concept to null.
     *
     * @param concept
     */
    public ConceptHierarchy removeOfType(AsConcept concept) {
        if (concept instanceof IToolCategory) {
            this.toolCategory = null;
        } else if (concept instanceof ITool) {
            ITool tool = (ITool) concept;
            if (!tool.isHostedTool()) {
                this.tool = null;
                this.hostedTool = null; //if I am not hosted, i'm either a parent or I have no children. In either case clear the hostedTool.
            } else if (tool.isHostedTool()) {
                this.hostedTool = null; 
            }
        } else if (concept instanceof IProcess) {
            this.process = null;
        } else if (concept instanceof IAction) {
            this.action = null;
        } else if (concept instanceof IProperty) {
            this.property = null;
        } else if (concept instanceof IFakeConcept) {
            this.root = null;
        }
        return this;
    }

    public ConceptHierarchy removeAll() {
        this.toolCategory = null;
        this.tool = null;
        this.process = null;
        this.action = null;
        this.property = null;
        this.root = null;
        return this;
    }

    @Override
    public ConceptHierarchy clone() {
        return new ConceptHierarchy(toolCategory, tool, hostedTool, process, action, property, root);
    }

    public IEventDefinition getEvent() {
        List<IEventDefinition> eventDefinitionCollection;
        if (hostedTool == null) {
            eventDefinitionCollection = process.getEventDefinitionCollection(tool, action);
        } else {
            eventDefinitionCollection = process.getEventDefinitionCollection(hostedTool, action);
        }
        return eventDefinitionCollection.size() > 0 ? eventDefinitionCollection.get(0) : null;
    }

}
