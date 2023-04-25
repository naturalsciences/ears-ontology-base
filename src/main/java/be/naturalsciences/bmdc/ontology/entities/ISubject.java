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
public interface ISubject< E extends IEarsTerm, TC extends IToolCategory, P extends IProcess> extends AsConcept<E>, Serializable {

    public Collection<? extends IProcess> getProcessCollection();

    public void setProcessCollection(Collection<P> processCollection);

    public Collection<? extends IToolCategory> getToolCategCollection();

    public void setToolCategCollection(Collection<TC> toolCategCollection);
}
