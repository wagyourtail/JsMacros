package xyz.wagyourtail.jsmacros.client.mixins.access;


import com.google.common.collect.ImmutableMap;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.google.gson.stream.JsonReader;
import net.minecraft.client.resource.language.LanguageDefinition;
import net.minecraft.client.resource.language.LanguageManager;
import net.minecraft.client.resource.language.TranslationStorage;
import net.minecraft.resource.ResourceManager;
import net.minecraft.util.Language;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import org.spongepowered.asm.mixin.injection.callback.LocalCapture;
import xyz.wagyourtail.jsmacros.client.FakeFabricLoader;

import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

@Mixin(TranslationStorage.class)
public class MixinTranslationStorage {
    
    @Inject(at = @At(value = "INVOKE", target = "Ljava/util/List;iterator()Ljava/util/Iterator;"), method = "load(Lnet/minecraft/resource/ResourceManager;Ljava/util/List;)Lnet/minecraft/client/resource/language/TranslationStorage;", locals = LocalCapture.CAPTURE_FAILHARD)
    private static void insertFabricLanguageData(ResourceManager p_239497_0_, List<LanguageDefinition> p_239497_1_, CallbackInfoReturnable<TranslationStorage> cir, Map<String, String> map) {
        Map<String, String> translations = new LinkedHashMap<>();
        for (LanguageDefinition lang : p_239497_1_) {
            Set<String> res = FakeFabricLoader.instance.getLangResources(lang.getCode());
            for (String r : res) {
                JsonObject ts = null;
                try (Reader reader = new InputStreamReader(FakeFabricLoader.class.getResourceAsStream(r))) {
                    ts = new JsonParser().parse(reader).getAsJsonObject();
                } catch (IOException e) {
                    e.printStackTrace();
                }
                ts.entrySet().forEach((e) -> translations.putIfAbsent(e.getKey(), e.getValue().getAsString()));
            }
        }
        translations.forEach(map::putIfAbsent);
    }
}
