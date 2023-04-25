package be.naturalsciences.bmdc.ontology.entities;

import java.net.URI;

/**
 *
 *
 */
public interface EarsTermLabel {

    public String getPrefLabel();

    public void setPrefLabel(String prefLabel);

    public String getAltLabel();

    public void setAltLabel(String altLabel);

    public String getDefinition();

    public void setDefinition(String definition);

    public Term getEarsTerm();

    public void setEarsTerm(Term earsTerm);

    public URI getUri();

    public Long getId();

    public void setUri(URI uri);

    public void setId(Long id);
}
