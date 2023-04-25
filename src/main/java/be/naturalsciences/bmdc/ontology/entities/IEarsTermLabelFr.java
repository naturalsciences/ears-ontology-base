/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package be.naturalsciences.bmdc.ontology.entities;

import java.io.Serializable;
import java.net.URI;

/**
 *
 * @author Thomas Vandenberghe
 */
public interface IEarsTermLabelFr extends EarsTermLabel, Serializable {

    boolean equals(Object object);

    String getAltLabel();

    String getDefinition();

    Term getEarsTerm();

    Long getId();

    String getPrefLabel();

    URI getUri();

    int hashCode();

    void setAltLabel(String altLabel);

    void setDefinition(String definition);

    void setEarsTerm(Term earsTerm);

    void setId(Long id);

    void setPrefLabel(String prefLabel);

    void setUri(URI uri);

    String toString();

}
