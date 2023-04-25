/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package be.naturalsciences.bmdc.ontology.entities;

import java.io.Serializable;
import java.net.URI;
import java.util.Collection;

/**
 *
 * @author Thomas Vandenberghe
 */
public interface IEventDefinition<E extends IEarsTerm, PR extends IProperty, EV extends IEventDefinition, S extends ISubject> extends Serializable, EARSThing {

    public Long getId();

    public void setId(Long id);

    public URI getUri();

    public void setUri(URI uri);

    public void addProperty(PR prop);

    public Boolean getIsDataProvider();

    public String getLabel();

    public Collection<? extends IProperty> getPropertyCollection();

    public Collection<? extends IEventDefinition> getTriggerCollection();

    public Collection<? extends IEventDefinition> getTriggeredCollection();

    public Collection<? extends ISubject> getSubjectCollection();

    public void setIsDataProvider(Boolean isDataProvider);

    public void setLabel();

    public void setPropertyCollection(Collection<PR> propertyCollection);

    public void setTriggerCollection(Collection<EV> triggererCollection);

    public void setTriggeredCollection(Collection<EV> triggeredCollection);

    public void setSubjectCollection(Collection<S> subjectCollection);
    
    // public E getTermRef();

}
