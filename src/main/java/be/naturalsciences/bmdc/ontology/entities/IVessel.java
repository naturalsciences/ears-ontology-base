/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package be.naturalsciences.bmdc.ontology.entities;

import java.io.Serializable;

/**
 *
 * @author Thomas Vandenberghe
 */
public interface IVessel< E extends IEarsTerm, TC extends IToolCategory, T extends ITool, P extends IProcess, SEV extends ISpecificEventDefinition, GEV extends IGenericEventDefinition> extends ITool<E, TC, T, P, SEV, GEV>, Serializable {

    /*public TC getToolCategoryRef();

    public void setToolCategoryRef(TC toolCategoryRef);*/
}
