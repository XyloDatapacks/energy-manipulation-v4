package xylo_datapacks.energy_manipulation.utils;

import net.minecraft.network.chat.Component;

import java.util.ArrayList;
import java.util.List;

public class LoreProcessor {
    
    public static List<Component> processLore(String rawTranslation, int maxLineLength, int indent) {
        List<Component> finalLore = new ArrayList<>();

        // Unescape JSON newline characters
        String unescaped = rawTranslation.replace("\\n", "\n");

        // Split by explicit new lines first
        String[] explicitLines = unescaped.split("\n");

        // Apply word wrap to each explicit line
        for (int i = 0; i < explicitLines.length; i++) {
            String line = explicitLines[i];
            int lineIndent = i ==  0 ? indent : 0; // Apply indent only to the first line.
            finalLore.addAll(wrapText(line, maxLineLength, lineIndent));
        }

        return finalLore;
    }


    public static List<Component> processLore(String rawTranslation, int maxLineLength) {
        return processLore(rawTranslation, maxLineLength, 0);
    }

    private static List<Component> wrapText(String text, int maxChars, int indent) {
        List<Component> wrapped = new ArrayList<>();
        String[] words = text.split(" ");
        StringBuilder currentLine = new StringBuilder();

        for (String word : words) {
            if (indent + currentLine.length() + word.length() > maxChars) {
                wrapped.add(Component.literal(currentLine.toString().trim()));
                currentLine = new StringBuilder();
                indent = 0; // Reset indent after applying it to the first line.
            }
            currentLine.append(word).append(" ");
        }

        if (!currentLine.isEmpty()) {
            wrapped.add(Component.literal(currentLine.toString().trim()));
        }

        return wrapped;
    }
}
