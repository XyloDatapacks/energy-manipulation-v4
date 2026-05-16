package xylo_datapacks.energy_manipulation.utils;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import xylo_datapacks.energy_manipulation.EnergyManipulation;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;

public class ServerTranslator {
    private static final Gson GSON = new Gson();
    private static final Map<String, Map<String, String>> CACHE = new HashMap<>();

    public static String getTranslation(String locale, String key) {
        return getTranslation(locale, key, key);
    }
    
    public static String getTranslation(String locale, String key, String defaultText) {
        // Fallback to en_us if the player's language file doesn't exist in your mod
        Map<String, String> langMap = CACHE.computeIfAbsent(locale, ServerTranslator::loadLang);
        if (!langMap.containsKey(key) && !locale.equals("en_us")) {
            langMap = CACHE.computeIfAbsent("en_us", ServerTranslator::loadLang);
        }
        return langMap.getOrDefault(key, defaultText);
    }

    private static Map<String, String> loadLang(String locale) {
        Map<String, String> translations = new HashMap<>();
        String path = "/assets/" + EnergyManipulation.MOD_ID + "/lang/" + locale + ".json";

        try (InputStream stream = ServerTranslator.class.getResourceAsStream(path)) {
            if (stream != null) {
                JsonObject json = GSON.fromJson(new InputStreamReader(stream, StandardCharsets.UTF_8), JsonObject.class);
                json.entrySet().forEach(entry -> translations.put(entry.getKey(), entry.getValue().getAsString()));
            }
        } catch (Exception e) {
            EnergyManipulation.LOGGER.error("Failed to load language file: {}", path, e);
        }
        return translations;
    }
}
