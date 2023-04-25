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
public interface IAction<E extends IEarsTerm, PA extends IProcessAction> extends AsConcept<E>, Serializable {

    public Boolean getIsIncident();

    public Collection<? extends IProcessAction> getProcessActionCollection();

    public void setIsIncident(Boolean isIncident);

    public void setProcessActionCollection(Collection<PA> processActionCollection);

}
