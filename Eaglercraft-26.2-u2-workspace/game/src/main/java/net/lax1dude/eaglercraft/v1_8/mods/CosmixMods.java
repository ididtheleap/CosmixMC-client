package net.lax1dude.eaglercraft.v1_8.mods;

import java.nio.charset.StandardCharsets;
import java.util.ArrayDeque;
import java.util.Iterator;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import net.lax1dude.eaglercraft.v1_8.EagRuntime;
import net.minecraft.util.Util;

public final class CosmixMods {
   private static final String STORAGE_KEY = "cosmix_mod_settings";
   private static final String[] ACCENT_NAMES = {"Teal", "Coral", "Amber", "Sky", "Lime"};
   private static final int[] ACCENT_COLORS = {0xFF48D8CA, 0xFFF17866, 0xFFFFB84F, 0xFF63B8FF, 0xFFA6D96A};
   private static boolean loaded;
   private static boolean coordinatesEnabled;
   private static boolean fpsEnabled;
   private static boolean fullbrightEnabled;
   private static boolean keystrokesEnabled;
   private static boolean directionEnabled;
   private static boolean pingEnabled;
   private static boolean cpsEnabled;
   private static boolean armorStatusEnabled;
   private static boolean saturationEnabled;
   private static boolean arrowsEnabled;
   private static boolean potionStatusEnabled;
   private static boolean minimapEnabled;
      private static boolean chatTimestampsEnabled;
      private static boolean chatAntiSpamEnabled;
      private static int accentIndex;
      private static int infoHudX = 2;
      private static int infoHudY = 2;
      private static int armorHudX = 2;
      private static int armorHudBottom = 40;
      private static int mapHudRight = 8;
      private static int mapHudBottom = 8;
   private static final ArrayDeque<Long> leftClicks = new ArrayDeque<>();

   private CosmixMods() {
   }

   public static boolean isCoordinatesEnabled() {
      load();
      return coordinatesEnabled;
   }

   public static void setCoordinatesEnabled(final boolean enabled) {
      load();
      coordinatesEnabled = enabled;
      save();
   }

   public static boolean isFpsEnabled() {
      load();
      return fpsEnabled;
   }

   public static void setFpsEnabled(final boolean enabled) {
      load();
      fpsEnabled = enabled;
      save();
   }

   public static boolean isFullbrightEnabled() {
      load();
      return fullbrightEnabled;
   }

   public static void setFullbrightEnabled(final boolean enabled) {
      load();
      fullbrightEnabled = enabled;
      save();
   }

   public static boolean isKeystrokesEnabled() {
      load();
      return keystrokesEnabled;
   }

   public static void setKeystrokesEnabled(final boolean enabled) {
      load();
      keystrokesEnabled = enabled;
      save();
   }

   public static boolean isDirectionEnabled() {
      load();
      return directionEnabled;
   }

   public static void setDirectionEnabled(final boolean enabled) {
      load();
      directionEnabled = enabled;
      save();
   }

   public static boolean isPingEnabled() {
      load();
      return pingEnabled;
   }

   public static void setPingEnabled(final boolean enabled) {
      load();
      pingEnabled = enabled;
      save();
   }

   public static boolean isCpsEnabled() {
      load();
      return cpsEnabled;
   }

   public static void setCpsEnabled(final boolean enabled) {
      load();
      cpsEnabled = enabled;
      save();
   }

   public static boolean isArmorStatusEnabled() {
      load();
      return armorStatusEnabled;
   }

   public static void setArmorStatusEnabled(final boolean enabled) {
      load();
      armorStatusEnabled = enabled;
      save();
   }

   public static boolean isSaturationEnabled() {
      load();
      return saturationEnabled;
   }

   public static void setSaturationEnabled(final boolean enabled) {
      load();
      saturationEnabled = enabled;
      save();
   }

   public static boolean isArrowsEnabled() {
      load();
      return arrowsEnabled;
   }

   public static void setArrowsEnabled(final boolean enabled) {
      load();
      arrowsEnabled = enabled;
      save();
   }

   public static boolean isPotionStatusEnabled() {
      load();
      return potionStatusEnabled;
   }

   public static void setPotionStatusEnabled(final boolean enabled) {
      load();
      potionStatusEnabled = enabled;
      save();
   }

   public static boolean isMinimapEnabled() {
      load();
      return minimapEnabled;
   }

   public static void setMinimapEnabled(final boolean enabled) {
      load();
      minimapEnabled = enabled;
      save();
   }

   public static boolean areChatTimestampsEnabled() {
      load();
      return chatTimestampsEnabled;
   }

   public static void setChatTimestampsEnabled(final boolean enabled) {
      load();
      chatTimestampsEnabled = enabled;
      save();
   }

   public static boolean isChatAntiSpamEnabled() {
      load();
      return chatAntiSpamEnabled;
   }

   public static void setChatAntiSpamEnabled(final boolean enabled) {
      load();
      chatAntiSpamEnabled = enabled;
      save();
   }

   public static int getAccentColor() {
      load();
      return ACCENT_COLORS[accentIndex];
   }

   public static String getAccentName() {
      load();
      return ACCENT_NAMES[accentIndex];
   }

   public static void cycleAccentColor() {
      load();
      accentIndex = (accentIndex + 1) % ACCENT_COLORS.length;
      save();
   }

   public static int getInfoHudX() {
      load();
      return infoHudX;
   }

   public static int getInfoHudY() {
      load();
      return infoHudY;
   }

   public static void setInfoHudPosition(final int x, final int y) {
      load();
      infoHudX = x;
      infoHudY = y;
   }

   public static int getArmorHudX() {
      load();
      return armorHudX;
   }

   public static int getArmorHudBottom() {
      load();
      return armorHudBottom;
   }

   public static void setArmorHudPosition(final int x, final int bottom) {
      load();
      armorHudX = x;
      armorHudBottom = bottom;
   }

   public static int getMapHudRight() {
      load();
      return mapHudRight;
   }

   public static int getMapHudBottom() {
      load();
      return mapHudBottom;
   }

   public static void setMapHudPosition(final int right, final int bottom) {
      load();
      mapHudRight = right;
      mapHudBottom = bottom;
   }

   public static void saveSettings() {
      load();
      save();
   }

   public static void recordLeftClick() {
      long now = Util.getMillis();
      leftClicks.addLast(now);
      trimClicks(now);
   }

   public static int getClicksPerSecond() {
      long now = Util.getMillis();
      trimClicks(now);
      return leftClicks.size();
   }

   private static void trimClicks(final long now) {
      Iterator<Long> clicks = leftClicks.iterator();
      while (clicks.hasNext()) {
         if (now - clicks.next() > 1000L) {
            clicks.remove();
         } else {
            break;
         }
      }
   }

   private static void load() {
      if (loaded) {
         return;
      }

      loaded = true;
      byte[] stored = EagRuntime.getStorage(STORAGE_KEY);
      if (stored == null) {
         return;
      }

      try {
         JsonObject settings = JsonParser.parseString(new String(stored, StandardCharsets.UTF_8)).getAsJsonObject();
         coordinatesEnabled = settings.has("coordinates") && settings.get("coordinates").getAsBoolean();
         fpsEnabled = settings.has("fps") && settings.get("fps").getAsBoolean();
         fullbrightEnabled = settings.has("fullbright") && settings.get("fullbright").getAsBoolean();
            chatTimestampsEnabled = settings.has("chat_timestamps") && settings.get("chat_timestamps").getAsBoolean();
            chatAntiSpamEnabled = settings.has("chat_anti_spam") && settings.get("chat_anti_spam").getAsBoolean();
            accentIndex = settings.has("accent") ? Math.floorMod(settings.get("accent").getAsInt(), ACCENT_COLORS.length) : 0;
            infoHudX = settings.has("info_hud_x") ? settings.get("info_hud_x").getAsInt() : 2;
            infoHudY = settings.has("info_hud_y") ? settings.get("info_hud_y").getAsInt() : 2;
            armorHudX = settings.has("armor_hud_x") ? settings.get("armor_hud_x").getAsInt() : 2;
            armorHudBottom = settings.has("armor_hud_bottom") ? settings.get("armor_hud_bottom").getAsInt() : 40;
            mapHudRight = settings.has("map_hud_right") ? settings.get("map_hud_right").getAsInt() : 8;
            mapHudBottom = settings.has("map_hud_bottom") ? settings.get("map_hud_bottom").getAsInt() : 8;
         keystrokesEnabled = settings.has("keystrokes") && settings.get("keystrokes").getAsBoolean();
         directionEnabled = settings.has("direction") && settings.get("direction").getAsBoolean();
         pingEnabled = settings.has("ping") && settings.get("ping").getAsBoolean();
         cpsEnabled = settings.has("cps") && settings.get("cps").getAsBoolean();
         armorStatusEnabled = settings.has("armor_status") && settings.get("armor_status").getAsBoolean();
         saturationEnabled = settings.has("saturation") && settings.get("saturation").getAsBoolean();
         arrowsEnabled = settings.has("arrows") && settings.get("arrows").getAsBoolean();
         potionStatusEnabled = settings.has("potion_status") && settings.get("potion_status").getAsBoolean();
         minimapEnabled = settings.has("minimap") && settings.get("minimap").getAsBoolean();
      } catch (RuntimeException ignored) {
      }
   }

   private static void save() {
      JsonObject settings = new JsonObject();
      settings.addProperty("coordinates", coordinatesEnabled);
      settings.addProperty("fps", fpsEnabled);
      settings.addProperty("fullbright", fullbrightEnabled);
      settings.addProperty("keystrokes", keystrokesEnabled);
      settings.addProperty("direction", directionEnabled);
      settings.addProperty("ping", pingEnabled);
      settings.addProperty("cps", cpsEnabled);
      settings.addProperty("armor_status", armorStatusEnabled);
      settings.addProperty("saturation", saturationEnabled);
      settings.addProperty("arrows", arrowsEnabled);
      settings.addProperty("potion_status", potionStatusEnabled);
      settings.addProperty("minimap", minimapEnabled);
      settings.addProperty("chat_timestamps", chatTimestampsEnabled);
      settings.addProperty("chat_anti_spam", chatAntiSpamEnabled);
      settings.addProperty("accent", accentIndex);
      settings.addProperty("info_hud_x", infoHudX);
      settings.addProperty("info_hud_y", infoHudY);
      settings.addProperty("armor_hud_x", armorHudX);
      settings.addProperty("armor_hud_bottom", armorHudBottom);
      settings.addProperty("map_hud_right", mapHudRight);
      settings.addProperty("map_hud_bottom", mapHudBottom);
      EagRuntime.setStorage(STORAGE_KEY, settings.toString().getBytes(StandardCharsets.UTF_8));
   }
}