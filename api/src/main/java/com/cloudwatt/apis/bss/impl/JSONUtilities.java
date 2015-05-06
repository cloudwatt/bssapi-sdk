/**
 * 
 */
package com.cloudwatt.apis.bss.impl;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import com.fasterxml.jackson.databind.JsonNode;

/**
 * @author pierre
 *
 */
public class JSONUtilities {

    /**
     * Convert a JSON Node into a Map
     * 
     * @param nodes the object to convert
     * @return a map of String, String
     */
    public static Map<String, String> jsonNodeToMap(JsonNode nodes) {
        final Iterator<Map.Entry<String, JsonNode>> it = nodes.fields();
        final HashMap<String, String> ret = new HashMap<String, String>();
        while (it.hasNext()) {
            final Map.Entry<String, JsonNode> en = it.next();
            ret.put(en.getKey(), en.getValue().asText());
        }
        return ret;
    }

}
