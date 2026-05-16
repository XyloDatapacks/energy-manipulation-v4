package xylo_datapacks.energy_manipulation.utils;

import net.minecraft.network.chat.Component;

import java.util.ArrayList;
import java.util.List;

public class LoreProcessor {
    
    public static List<Component> processLore(String rawTranslation, int maxLineLength) {
        List<Component> finalLore = new ArrayList<>();

        // Unescape JSON newline characters
        String unescaped = rawTranslation.replace("\\n", "\n");

        // Split by explicit new lines first
        String[] explicitLines = unescaped.split("\n");

        // Apply word wrap to each explicit line
        for (String line : explicitLines) {
            finalLore.addAll(wrapText(line, maxLineLength));
        }

        return finalLore;
    }

    private static List<Component> wrapText(String text, int maxChars) {
        List<Component> wrapped = new ArrayList<>();
        String[] words = text.split(" ");
        StringBuilder currentLine = new StringBuilder();

        for (String word : words) {
            if (currentLine.length() + word.length() > maxChars) {
                wrapped.add(Component.literal(currentLine.toString().trim()));
                currentLine = new StringBuilder();
            }
            currentLine.append(word).append(" ");
        }

        if (!currentLine.isEmpty()) {
            wrapped.add(Component.literal(currentLine.toString().trim()));
        }

        return wrapped;
    }
}
