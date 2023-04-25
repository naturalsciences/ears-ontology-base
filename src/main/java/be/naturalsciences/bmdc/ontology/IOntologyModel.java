/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package be.naturalsciences.bmdc.ontology;

import be.naturalsciences.bmdc.ontology.entities.AsConcept;
import be.naturalsciences.bmdc.ontology.writer.ScopeMap;
import com.hp.hpl.jena.query.ResultSet;
import gnu.trove.map.hash.THashMap;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.net.ConnectException;
import java.util.ArrayDeque;
import java.util.Date;
import java.util.Map;
import java.util.Scanner;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 *
 * @author Thomas Vandenberghe
 */
public interface IOntologyModel {

    public enum ActionEnum {

        BROWSING, EDITING
    };

    public static final String SCOPE = "SCOPE";

    public static final String SCOPEDTO = "SCOPEDTO";

    public static final String VERSIONINFO = "VERSIONINFO";

    public static final String DATEMODIFIED = "DATEMODIFIED";

    public ResultSet query(String q) throws Exception;

    public void open(ArrayDeque<Class<? extends AsConcept>> classesOrder, ActionEnum operation) throws FileNotFoundException, IOException;

    public IOntologyNodes getNodes();

    public ScopeMap getScopeMap();

    public String getScope();

    public String getScopedTo();

   // public boolean actionIsAllowed(String act);

    public void close(ActionEnum operation);

    public boolean isEditable();
    
    public boolean isPasswordProtected();
    
    public Set<ActionEnum> getCurrentActions();

    public File getFile();

    public Date getVersionInfo();

    /**
     * *
     * The identifying name of the resource. Corresponds to the rdfs:label of
     * the ontology. Not the file name!
     *
     * @return
     */
    public String getName();

    public static String getPreferredName() {
        return null;
    }

    public IIndividuals getIndividuals();

    /**
     * ***
     * Test whether this jenaModel (BASE, VESSEL) is outdated with respect to a
     * source ontology with the same scope (BASE, VESSEL).
     *
     * @param scope
     * @param date
     * @return
     */
    public Boolean isOutdated(Date date) throws ConnectException, EarsException;

    public File downloadLatestVersion(String name);
    
    public void register();

    public static Map<String, String> getStaticStuff(InputStream is) {
        Scanner scanner = null;

        scanner = new Scanner(is);
        //scanner.useDelimiter("\n");
        Pattern ptScope = Pattern.compile("<(?:ears2:)??scope.*?>(.*?)<\\/(?:ears2:)??scope>");
        Pattern ptVersionInfo = Pattern.compile("<(?:owl:)??versionInfo.*?>(.*?)<\\/(?:owl:)??versionInfo>");
        Pattern ptDateModified = Pattern.compile("<(?:dc:)??modified.*?>(.*?)<\\/(?:dc:)??modified>");
        Pattern ptScopedTo = Pattern.compile("<(?:ears2:)??scopedTo.*?>(.*?)<\\/(?:ears2:)??scopedTo>");
        Pattern ptStop = Pattern.compile("<\\/owl:Ontology>");
        String r = null;
        Map<String, String> res = new THashMap<>();
        while (scanner.hasNextLine()) {
            String l = scanner.nextLine();

            Matcher m = ptScope.matcher(l);
            Matcher m2 = ptStop.matcher(l);
            Matcher m3 = ptVersionInfo.matcher(l);
            Matcher m4 = ptScopedTo.matcher(l);
            Matcher m5 = ptDateModified.matcher(l);

            if (m2.find()) {
                scanner.close();
                break;
            }
            if (l.contains("scope")) {
                if (m.find()) {
                    r = m.group(1);
                    res.put(SCOPE, r);
                }
            }
            if (l.contains("scopedTo")) {
                if (m4.find()) {
                    r = m4.group(1);
                    res.put(SCOPEDTO, r);
                }
            }
            if (l.contains("versionInfo")) {
                if (m3.find()) {
                    r = m3.group(1);
                    res.put(VERSIONINFO, r);
                }
            }
            if (l.contains("modified")) {
                if (m5.find()) {
                    r = m5.group(1);
                    res.put(DATEMODIFIED, r);
                }
            }
        }
        if (res.size() > 1) {
            // if (res.size() == 3) {
            return res;
            /*  } else {
                res.put(SCOPEDTO, null);
                return res;
            }*/
        } else {
            return null;
        }
    }

    /**
     * *
     * Retrieve some basic information about an ontology using a file reference.
     *
     * @param file
     * @return
     */
    public static Map<String, String> getStaticStuff(File file) throws FileNotFoundException {
        InputStream is = new FileInputStream(file);
        return getStaticStuff(is);
    }

}
