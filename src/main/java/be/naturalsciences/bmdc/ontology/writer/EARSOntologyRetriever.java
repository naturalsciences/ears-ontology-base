/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package be.naturalsciences.bmdc.ontology.writer;

import be.naturalsciences.bmdc.ontology.OntologyConstants;
import gnu.trove.map.hash.THashMap;
import java.io.File;
import java.io.IOException;
import java.net.ConnectException;
import java.net.MalformedURLException;
import java.net.URL;
import java.text.ParseException;
import java.util.Date;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.apache.commons.io.FilenameUtils;

/**
 * A class to retrieve the EARS ontology from a remote system, ie. from
 * http://ontologies.ef-ears.eu. Not intended to be run on the ontology server
 * itself
 *
 * @author Thomas Vandenberghe
 */
public class EARSOntologyRetriever {

    private static final Logger log = Logger.getLogger(EARSOntologyCreator.class.getName());

    private URL currentOntologyFileDirectory;
    private URL currentOntologyFile;
    private URL compressedCurrentOntologyFile;
    private URL latestOntologyFileDirectory;
    private Date latestOntologyDate;
    private Date latestOntologyAxiomDate;
    private URL latestOntologyAxiomUrl;

    private String ontologyMetadataJson;

    public URL getCurrentOntologyFile() {
        return currentOntologyFile;
    }

    public URL getCurrentOntologyFileDirectory() {
        return currentOntologyFileDirectory;
    }

    public URL getLatestOntologyFileDirectory() {
        return latestOntologyFileDirectory;
    }

    public Date getLatestOntologyDate() {
        return latestOntologyDate;
    }

    public Date getLatestOntologyAxiomDate() {
        return latestOntologyAxiomDate;
    }

    public URL getLatestOntologyAxiomUrl() {
        return latestOntologyAxiomUrl;
    }

    public String getLatestOntologyAxiomFileName() {
        return FilenameUtils.getName(latestOntologyAxiomUrl.getPath());
    }

    public EARSOntologyRetriever() throws ConnectException, EARSOntologyRetrievalException, MalformedURLException {
        if (ontologyServiceIsAvailable()) {
            ontologyMetadataJson = FileUtils.getStringFromUrl(OntologyConstants.ONTOLOGY_METADATA_JSON_URL);
            Map<String, String> keys = new THashMap<>();
            keys.put("key", null);
            keys.put("ontologyDefinitions", null);
            keys.put("currentOntologyFileDirectory", null);
            keys.put("latestOntologyFileDirectory", null);
            keys.put("latestOntologyDate", null);
            keys.put("latestOntologyAxiomDate", null);
            keys.put("latestOntologyAxiomUrl", null);
            keys = JSONReader.getJsonKeyVals(keys, ontologyMetadataJson);

            currentOntologyFileDirectory = new URL(keys.get("currentOntologyFileDirectory"));
            compressedCurrentOntologyFile = new URL(currentOntologyFileDirectory, OntologyConstants.COMPRESSED_BASE_ONTOLOGY_FILENAME);
            currentOntologyFile = new URL(currentOntologyFileDirectory, OntologyConstants.BASE_ONTOLOGY_FILENAME);
            latestOntologyFileDirectory = new URL(keys.get("latestOntologyFileDirectory"));
            try {
                latestOntologyDate = StringUtils.SDF_ISO_DATE.parse(keys.get("latestOntologyDate"));
            } catch (ParseException ex) {
                Logger.getLogger(EARSOntologyRetriever.class.getName()).log(Level.SEVERE, "An exception occured.", ex);
                throw new EARSOntologyRetrievalException("The date given in the on-shore ontology server at http://ontologies.ef-ears.eu/sparql could not be processed.", ex);
            }

            try {
                latestOntologyAxiomDate = StringUtils.SDF_ISO_DATE.parse(keys.get("latestOntologyAxiomDate"));
            } catch (ParseException ex) {
                Logger.getLogger(EARSOntologyRetriever.class.getName()).log(Level.SEVERE, "An exception occured.", ex);
                throw new EARSOntologyRetrievalException("The date given in the on-shore ontology server at http://ontologies.ef-ears.eu/sparql could not be processed.", ex);
            }
            latestOntologyAxiomUrl = new URL(keys.get("latestOntologyAxiomUrl"));
        } else {
            throw new ConnectException("Cannot connect to the EARS Ontology server.");
        }
    }

    public static boolean ontologyServiceIsAvailable() {

        try {
            if (FileUtils.websiteIsAvailable(OntologyConstants.ONTOLOGY_METADATA_JSON_URL)) {
                return StringUtils.containsIgnoreCase(FileUtils.readStringFromUrl(OntologyConstants.ONTOLOGY_METADATA_JSON_URL, 40), "ontologyInformation");
            }
            return false;
        } catch (IOException ex) {
            Logger.getLogger(EARSOntologyRetriever.class.getName()).log(Level.SEVERE, "An exception occured.", ex);
            return false;
        }
    }

    /**
     * *
     * Get a String representing the latest ontology axiom owl file as on the
     * ontology server.
     *
     * @return
     */
    /*private static String retrieveLatestOntologyAxiomFileName() {
        Document doc;
        try {
            doc = Jsoup.connect(OntologyConstants.ONTOLOGY_AXIOM_URL).get();
        } catch (IOException ex) {
            log.log(Level.SEVERE, "ontologyAxioms url malformed.");
            return null;
        }
        String lastName = "";

        for (Element file : doc.select("a[href*=\"" + OntologyConstants.ONTOLOGY_AXIOM_FILENAME_BASE + "\"]")) {
            if (file.attr("href").compareTo(lastName) > 0) {
                lastName = file.attr("href");
            }
        }
        if (!"".equals(lastName)) {
            /*if (targetDir != null) {
             return new File(targetDir.toFile(), lastName);
             } else {
             return new File(lastName);
             }*/
    //        return lastName;
    //    } else {
    //        return null;
    //    }
    //}
    /**
     * *
     * Download the latest rdf version of the base ontology to a specified
     * directory path and return it as a file if succesful.
     *
     * @param targetDir
     * @return
     */
    public File downloadLatestOntology(File targetDir) throws IOException {
        if (currentOntologyFile != null && compressedCurrentOntologyFile != null) {
            String fileName = FilenameUtils.getName(currentOntologyFile.getPath());
            String compressedFileName = FilenameUtils.getName(compressedCurrentOntologyFile.getPath());
            try {
                File downloadedCompressedOntologyFile = FileUtils.downloadFromUrl(compressedCurrentOntologyFile, targetDir, compressedFileName);
                if (downloadedCompressedOntologyFile != null) {
                    File downloadedOntologyFile = null;
                    downloadedOntologyFile = FileUtils.decompress7zFile(downloadedCompressedOntologyFile, targetDir);
                    if (downloadedOntologyFile != null) {
                        return downloadedOntologyFile;
                    }
                }

            } catch (IOException ex) {
                return FileUtils.downloadFromUrl(currentOntologyFile, targetDir, fileName);
            }

        }
        return null;
    }

    /**
     * Download the axiom ontology file, save it as RDF in the specified
     * location and return it as a BufferedOutputStream.
     *
     * @param targetDir
     * @return
     */
    public File downloadLatestOntologyAxiom(File targetDir) throws IOException {
        if (latestOntologyAxiomUrl != null) {
            String fileName = FilenameUtils.getName(latestOntologyAxiomUrl.getPath());
            return FileUtils.downloadFromUrl(latestOntologyAxiomUrl, targetDir, fileName);
        } else {
            return null;
        }
    }

    /**
     * *
     * Get the versioninfo (=Date) of the latest base ontology
     *
     * @return
     * @throws
     * be.naturalsciences.bmdc.ontology.writer.EARSOntologyRetrievalException
     */
    /*public static Date retrieveLatestOntologyDate() throws EARSOntologyRetrievalException {
        String url = "http://ontologies.ef-ears.eu/sparql?query=PREFIX+owl%3A+%3Chttp%3A%2F%2Fwww.w3.org%2F2002%2F07%2Fowl%23%3E%0D%0APREFIX+ears2%3A+%3Chttp%3A%2F%2Fontologies.ef-ears.eu%2Fears2%2F1%23%3E%0D%0ASELECT+%3FversionInfo%0D%0AWHERE+{%0D%0A++++%3Fontology+owl%3AversionInfo+%3FversionInfo+.%0D%0AFILTER+%28%3Fontology%3Dears2%3A%29%0D%0A}";
        String json = null;
        try {
            json = JSONReader.getStringFromUrl(url);
        } catch (JSONException | IOException ex) {
            Logger.getLogger(EARSOntologyRetriever.class.getName()).log(Level.SEVERE, null, ex);
            throw new EARSOntologyRetrievalException("The message received from the on-shore ontology server at http://ontologies.ef-ears.eu/sparql could not be processed.", ex);
        }
        Map<String, String> keyVal = new HashMap<>();
        keyVal.put("value", "");
        keyVal = JSONReader.getJsonKeyVals(keyVal, json);
        String vi = keyVal.get("value");
        try {
            return YYYYMMDD_FM.parse(vi);
        } catch (ParseException ex) {
            Logger.getLogger(EARSOntologyRetriever.class.getName()).log(Level.SEVERE, null, ex);
            throw new EARSOntologyRetrievalException("The date given in the on-shore ontology server at http://ontologies.ef-ears.eu/sparql could not be processed.", ex);
        }
    }*/

 /* public static Date retrieveLatestOntologyAxiomDate() {
        String date = retrieveLatestOntologyAxiomFileName().replaceAll(OntologyConstants.ONTOLOGY_AXIOM_FILENAME_BASE, "").replaceAll("-", "").replaceAll(".xml", "");

        try {
            return OntologyConstants.YYYYMMDD_FM.parse(date);
        } catch (ParseException ex) {
            return null;
        }

    }*/
}
