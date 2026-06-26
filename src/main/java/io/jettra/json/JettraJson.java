package io.jettra.json;

import java.lang.reflect.Array;
import java.lang.reflect.Field;
import java.lang.reflect.Type;
import java.util.Collection;
import java.util.Iterator;
import java.util.Map;

public class JettraJson {

    private final JettraJsonBuilder builder;

    public JettraJson() {
        this.builder = new JettraJsonBuilder();
    }

    public JettraJson(JettraJsonBuilder builder) {
        this.builder = builder;
    }

    public String toJson(Object src) {
        if (src == null) {
            return "null";
        }
        if (src instanceof String) {
            return "\"" + escapeString((String) src) + "\"";
        }
        if (src instanceof Number || src instanceof Boolean) {
            return src.toString();
        }
        if (src instanceof JsonObject) {
            return serializeJsonObject((JsonObject) src);
        }
        if (src instanceof JsonArray) {
            return serializeJsonArray((JsonArray) src);
        }
        if (src instanceof Map) {
            return serializeMap((Map<?, ?>) src);
        }
        if (src instanceof Collection) {
            return serializeCollection((Collection<?>) src);
        }
        if (src.getClass().isArray()) {
            return serializeArray(src);
        }

        return serializeObject(src);
    }

    public <T> T fromJson(String json, Class<T> classOfT) {
        // Simplified deserialization stub
        // In a real implementation this would fully parse JSON and use reflection
        if (json == null || json.trim().isEmpty() || json.equals("null")) return null;
        try {
            return classOfT.getDeclaredConstructor().newInstance();
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }
    
    public <T> T fromJson(String json, Type typeOfT) {
        // Stub
        return null;
    }

    private String serializeJsonObject(JsonObject obj) {
        return serializeMap(obj.getMap());
    }

    private String serializeJsonArray(JsonArray arr) {
        return serializeCollection(arr.getList());
    }

    private String serializeMap(Map<?, ?> map) {
        StringBuilder sb = new StringBuilder("{");
        boolean first = true;
        for (Map.Entry<?, ?> entry : map.entrySet()) {
            if (!first) sb.append(",");
            sb.append("\"").append(entry.getKey()).append("\":").append(toJson(entry.getValue()));
            first = false;
        }
        sb.append("}");
        return sb.toString();
    }

    private String serializeCollection(Collection<?> col) {
        StringBuilder sb = new StringBuilder("[");
        boolean first = true;
        for (Object item : col) {
            if (!first) sb.append(",");
            sb.append(toJson(item));
            first = false;
        }
        sb.append("]");
        return sb.toString();
    }

    private String serializeArray(Object array) {
        StringBuilder sb = new StringBuilder("[");
        int length = Array.getLength(array);
        for (int i = 0; i < length; i++) {
            if (i > 0) sb.append(",");
            sb.append(toJson(Array.get(array, i)));
        }
        sb.append("]");
        return sb.toString();
    }

    private String serializeObject(Object obj) {
        StringBuilder sb = new StringBuilder("{");
        boolean first = true;
        Class<?> clazz = obj.getClass();
        Field[] fields = clazz.getDeclaredFields();
        for (Field field : fields) {
            field.setAccessible(true);
            try {
                Object value = field.get(obj);
                if (!first) sb.append(",");
                sb.append("\"").append(field.getName()).append("\":").append(toJson(value));
                first = false;
            } catch (IllegalAccessException e) {
                // Ignore
            }
        }
        sb.append("}");
        return sb.toString();
    }

    private String escapeString(String str) {
        return str.replace("\\", "\\\\")
                  .replace("\"", "\\\"")
                  .replace("\b", "\\b")
                  .replace("\f", "\\f")
                  .replace("\n", "\\n")
                  .replace("\r", "\\r")
                  .replace("\t", "\\t");
    }
}
