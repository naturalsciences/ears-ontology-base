/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package be.naturalsciences.bmdc.ontology.entities;

import java.net.URI;
import java.util.Date;

public interface Term<E extends IEarsTerm, IS extends IItemStatus> extends EARSThing {

    public EarsTermLabel getEarsTermLabel();

    public EarsTermLabel getEarsTermLabel(IEarsTerm.Language language);

    public void setTermLabel(EarsTermLabel label);

    public void setTermLabel(EarsTermLabel label, IEarsTerm.Language language);

    public Long getId();

    public String getKind();

    public IItemStatus getStatus();

    public void setStatus(IS status);

    public String getStatusName();

    public void setStatusName(String status);

    public void setOrigUrn(String urn);

    public String getOrigUrn();

    public void setPublisherUrn(String urn);

    public String getPublisherUrn();

    public IEarsTerm getSubstituteRef();

    public void setSubstituteRef(E substitute);

    public IEarsTerm getSubstituteBackRef();

    public void setSubstituteBackRef(E substitute);

    public void setCreationDate(Date creationDate);

    public Date getCreationDate();

    public void setModifDate(Date creationDate);

    public Date getModifDate();

    public boolean getIsMeta();

    public void setIsMeta(boolean isMeta);

    public boolean isLabelEditable();

    public boolean isBodcTerm();

    public boolean isOwnTerm(String vesselIdentifier);

    public boolean isPublished();

    public boolean isDeprecated();

    public int getLastId();

    public String getSubmitter();

    public void setSubmitter(String submitter);

    public String getController();

    public void setController(String controller);

    public String getCreator();

    public void setCreator(String creator);

    public URI getUri();

    public void setUri(URI uri);

    public void fixSubstitutes();

    public String getVersionInfo();

    public void setVersionInfo(String string);

    public String getName();

    public String getSameAs();

    public void setSameAs(String sameAs);

    public String getBroader();

    public void setBroader(String broader);

}
