package xylo_datapacks.energy_manipulation.glyph.editor_data;

import com.google.common.collect.Maps;
import net.minecraft.resources.Identifier;
import xylo_datapacks.energy_manipulation.glyph.Glyph;
import xylo_datapacks.energy_manipulation.glyph.pin.InputPinDefinition;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

public class GlyphEditorData {
    
    public Map<String, InputPinEditorData> inputPinsEditorData = new LinkedHashMap<>();
    public boolean bHasTypeDependentTexture = false;
}
