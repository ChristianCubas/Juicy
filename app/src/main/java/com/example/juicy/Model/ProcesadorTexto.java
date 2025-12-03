package com.example.juicy.Model;

import org.json.JSONObject;
import java.util.Iterator;

public class ProcesadorTexto {

    public static String formatearPersonalizacion(String jsonString) {
        if (jsonString == null || jsonString.isEmpty() || jsonString.equals("{}") || jsonString.equals("null")) {
            return "Regular (Sin cambios)";
        }

        try {
            JSONObject json = new JSONObject(jsonString);
            StringBuilder sb = new StringBuilder();

            Iterator<String> keys = json.keys();
            while (keys.hasNext()) {
                String key = keys.next();
                String value = json.getString(key);

                sb.append(key).append(": ").append(value);

                if (keys.hasNext()) {
                    sb.append(" | ");
                }
            }
            return sb.toString();

        } catch (Exception e) {
            return "Personalizado";
        }
    }
}