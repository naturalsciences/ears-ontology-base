/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package be.naturalsciences.bmdc.ontology.writer;

import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.time.OffsetDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.Map;
import org.junit.Ignore;
import org.junit.Test;
import static org.junit.jupiter.api.Assertions.*;

/**
 *
 * @author thomas
 */
public class StringUtilsTest {

    public StringUtilsTest() {
    }

    @Test
    public void testFields() {
        LocalDateTime ld2 = LocalDateTime.parse("2021-11-24T14:00:18.823Z", StringUtils.DTF_FULL_ISO_DATETIME); //
        LocalDateTime ld = LocalDateTime.parse("2021-11-24T14:00:18Z", StringUtils.DTF_ISO_DATETIME_ZONE); //
        LocalDateTime ld4 = LocalDateTime.parse("2021-11-24T14:00:18.823Z", StringUtils.DTF_ISO_DATETIME_FLEX); //
        LocalDateTime ld5 = LocalDateTime.parse("2021-11-24T14:00:18Z", StringUtils.DTF_ISO_DATETIME_FLEX); //
        assertEquals(ld4, ld2);
        assertEquals(ld, ld5);


    }

    /**
     * Test of concatString method, of class StringUtils.
     */
    @Test
    @Ignore
    public void testConcatString() {
        System.out.println("concatString");
        Collection<String> strings = null;
        String separator = "";
        String expResult = "";
        String result = StringUtils.concatString(strings, separator);
        assertEquals(expResult, result);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    /**
     * Test of countOccurences method, of class StringUtils.
     */
    @Test
    @Ignore
    public void testCountOccurences() {
        System.out.println("countOccurences");
        String line = "";
        String of = "";
        int expResult = 0;
        int result = StringUtils.countOccurences(line, of);
        assertEquals(expResult, result);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    /**
     * Test of parseUrn method, of class StringUtils.
     */
    @Test
    @Ignore
    public void testParseUrn() {
        System.out.println("parseUrn");
        String urn = "";
        String[] expResult = null;
        String[] result = StringUtils.parseUrn(urn);
        assertArrayEquals(expResult, result);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    /**
     * Test of getLastUrnPart method, of class StringUtils.
     */
    @Test
    @Ignore
    public void testGetLastUrnPart() {
        System.out.println("getLastUrnPart");
        String urn = "";
        String expResult = "";
        String result = StringUtils.getLastUrnPart(urn);
        assertEquals(expResult, result);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    /**
     * Test of mappifyPropertyString method, of class StringUtils.
     */
    @Test
    @Ignore
    public void testMappifyPropertyString() {
        System.out.println("mappifyPropertyString");
        String propString = "";
        String KEYVAL_DELIM = "";
        String PROP_DELIM = "";
        String REGEX_PROP_DELIM = "";
        String REGEX_KEYVAL_DELIM = "";
        Map<String, ArrayList<String>> expResult = null;
        Map<String, ArrayList<String>> result = StringUtils.mappifyPropertyString(propString, KEYVAL_DELIM, PROP_DELIM, REGEX_PROP_DELIM, REGEX_KEYVAL_DELIM);
        assertEquals(expResult, result);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    /**
     * Test of returnMostSpecificDate method, of class StringUtils.
     */
    @Test
    @Ignore
    public void testReturnMostSpecificDate() {
        System.out.println("returnMostSpecificDate");
        Map<SimpleDateFormat, String> dates = null;
        Date expResult = null;
        Date result = StringUtils.returnMostSpecificDate(dates);
        assertEquals(expResult, result);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    /**
     * Test of parse method, of class StringUtils.
     */
    @Test
    @Ignore
    public void testParse() {
        System.out.println("parse");
        String date = "";
        SimpleDateFormat[] formats = null;
        Date expResult = null;
        Date result = StringUtils.parse(date, formats);
        assertEquals(expResult, result);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

}
