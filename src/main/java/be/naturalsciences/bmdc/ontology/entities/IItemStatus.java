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
public interface IItemStatus<E extends IEarsTerm> extends Serializable {

    public Collection<E> getEarsTermCollection();

    public String getName();

    public String getShortName();

    public void setEarsTermCollection(Collection<E> earsTermCollection);

    public void setName(String name);

    public void setShortName(String shortName);
}
