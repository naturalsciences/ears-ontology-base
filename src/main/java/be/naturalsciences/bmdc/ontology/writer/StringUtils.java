/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package be.naturalsciences.bmdc.ontology.writer;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeFormatterBuilder;
import java.time.temporal.ChronoField;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

/**
 *
 * @author Thomas Vandenberghe
 */
public class StringUtils extends org.apache.commons.lang3.StringUtils {

    public static final SimpleDateFormat SDF_ISO_DATETIME = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

    public static final SimpleDateFormat SDF_SIMPLE_DATE = new SimpleDateFormat("yyyyMMdd");

    public static final SimpleDateFormat SDF_ISO_DATE = new SimpleDateFormat("yyyy-MM-dd");

    public static final SimpleDateFormat SDF_FULL_ISO_DATETIME = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSX");

    public static final DateTimeFormatter DTF_TIME_FORMAT_HOURS_MINS = DateTimeFormatter.ofPattern("HH:mm", Locale.FRANCE);

    public static final DateTimeFormatter DTF_TIME_FORMAT_HOURS_MINS_SECS = DateTimeFormatter.ofPattern("HH:mm:ss", Locale.FRANCE);

    public static final DateTimeFormatter DTF_TIME_FORMAT_HOURS_MINS_SECS_ZONE = DateTimeFormatter.ofPattern("HH:mm:ssX", Locale.FRANCE);

    public static final DateTimeFormatter DTF_FULL_ISO_DATETIME = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss.SSSX");

    public static final DateTimeFormatter DTF_ISO_DATETIME_ZONE = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ssX");

    public static final DateTimeFormatter DTF_ISO_DATETIME = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss");

    public static final DateTimeFormatter DTF_ISO_DATETIME_FLEX;

    static {
        DateTimeFormatter DTF_ISO_DATETIME_ZONE_INT = new DateTimeFormatterBuilder()
                .appendPattern("yyyy-MM-dd'T'HH:mm:ss")
                .appendFraction(ChronoField.MICRO_OF_SECOND, 0, 6, true)
                .appendZoneId()
                .toFormatter();
        DTF_ISO_DATETIME_FLEX = DTF_ISO_DATETIME_ZONE_INT;
    }

    public static String concatString(Collection<String> strings, String separator) {
        StringBuilder sb = new StringBuilder();
        int i = 0;
        int l = strings.size();
        for (String s : strings) {
            sb.append(s);
            if (l > 1 && i < l - 1) {
                sb.append(separator);
            }
            i++;
        }
        return sb.toString();
    }

    public static int countOccurences(String line, String of) {
        return line.length() - line.replace(of, "").length();
    }

    public static String[] parseUrn(String urn) {
        return urn.split(":");
    }

    public static String getLastUrnPart(String urn) {
        String[] parts = urn.split(":");
        return parts[parts.length - 1];
    }

    public static Map<String, ArrayList<String>> mappifyPropertyString(String propString, String KEYVAL_DELIM, String PROP_DELIM, String REGEX_PROP_DELIM, String REGEX_KEYVAL_DELIM) {
        Map r = new HashMap<String, String>();
        if (propString.contains(KEYVAL_DELIM)) {//must contain at least one key-value pair
            if (StringUtils.countOccurences(propString, "|") > 1 && propString.contains(PROP_DELIM)) { //if has more than one key-value pair, then must be delimited correctly
                propString = propString.replace("\\", "");
                String properties[] = propString.split(REGEX_PROP_DELIM);
                for (int i = 0; i < properties.length; i++) {
                    String property = properties[i];
                    String keyVal[] = property.split(REGEX_KEYVAL_DELIM);
                    if (!r.containsKey(keyVal[0])) {
                        List l = new ArrayList();
                        l.add(keyVal[1]);
                        r.put(keyVal[0], l);
                    } else {
                        List vals = (ArrayList) r.get(keyVal[0]);
                        vals.add(keyVal[1]);
                        r.put(keyVal[0], vals);
                    }
                }
            }
        }
        return r;
    }

    public static Date returnMostSpecificDate(Map<SimpleDateFormat, String> dates) {
        SimpleDateFormat mostSpecificFormat = StringUtils.SDF_SIMPLE_DATE;//new ArrayList<SimpleDateFormat>(dates.keySet()).get(0);
        for (Map.Entry<SimpleDateFormat, String> entry : dates.entrySet()) {
            String dateString = entry.getValue();
            SimpleDateFormat format = entry.getKey();

            if (dateString != null && format.toPattern().length() > mostSpecificFormat.toPattern().length()) {
                mostSpecificFormat = format;
            }
        }
        Date date = null;
        String dateString = dates.get(mostSpecificFormat);
        if (dateString != null) {
            try {
                mostSpecificFormat.setLenient(false);
                date = mostSpecificFormat.parse(dateString);
            } catch (ParseException ex) {

            }
        }
        return date;
    }

    public static Date parse(String date, SimpleDateFormat[] formats) {
        Date result = null;
        if (date != null) {
            for (SimpleDateFormat format : formats) {
                format.setLenient(false);
                try {
                    result = format.parse(date);
                } catch (ParseException ex) {
                }
            }
        }
        return result;

    }
}
