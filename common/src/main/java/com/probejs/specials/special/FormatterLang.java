package com.probejs.specials.special;

import com.probejs.ProbeJS;
import com.probejs.docs.formatter.formatter.IFormatter;
import net.minecraft.client.Minecraft;
import net.minecraft.client.resources.language.ClientLanguage;
import net.minecraft.client.resources.language.LanguageInfo;
import net.minecraft.client.resources.language.LanguageManager;
import net.minecraft.locale.Language;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class FormatterLang implements IFormatter {
    @Override
    public List<String> format(Integer indent, Integer stepIndent) {
        if (Language.getInstance() instanceof ClientLanguage) {
            return List.of("%stype LangKey = %s".formatted(" ".repeat(indent),
                    getLangKeys(LanguageManager.DEFAULT_LANGUAGE_CODE)
                            .map(Map.Entry::getKey)
                            .map(ProbeJS.GSON::toJson)
                            .collect(Collectors.joining(" | "))
            ));
        }
        return List.of();
    }

    public static Stream<Map.Entry<String, String>> getLangKeys(String language) {
        LanguageManager manager = Minecraft.getInstance().getLanguageManager();
        return getLangKeys(manager.getLanguages().get(language));
    }

    public static Stream<Map.Entry<String, String>> getLangKeys(LanguageInfo language) {
        Minecraft mc = Minecraft.getInstance();
        LanguageManager manager = mc.getLanguageManager();
        LanguageInfo english = manager.getLanguage(LanguageManager.DEFAULT_LANGUAGE_CODE);
        List<LanguageInfo> languages = language.equals(english)
                ? List.of(english)
                : List.of(english, language);

        HashMap<LanguageInfo, String> reversedMap = new HashMap<>();
        manager.getLanguages().forEach((key, value) -> reversedMap.put(value, key));
        List<String> langFiles = languages.stream()
                .map(reversedMap::get)
                .filter(Objects::nonNull)
                .toList();

        ClientLanguage clientLanguage = ClientLanguage.loadFrom(
                mc.getResourceManager(),
                langFiles,
                english.bidirectional()
        );
        return clientLanguage.storage.entrySet().stream();
    }
}
