package net.minecraft.client.gui.screens;

import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.input.MouseButtonEvent;
import net.minecraft.network.chat.Component;
import net.lax1dude.eaglercraft.v1_8.mods.CosmixMiniMap;
import net.lax1dude.eaglercraft.v1_8.mods.CosmixMods;
import org.jspecify.annotations.Nullable;

public class CosmixHudEditorScreen extends Screen {
   private static final int INFO_PANEL = 0;
   private static final int ARMOR_PANEL = 1;
   private static final int MAP_PANEL = 2;
   private static final int INFO_WIDTH = 132;
   private static final int INFO_HEIGHT = 46;
   private static final int ARMOR_WIDTH = 116;
   private static final int ARMOR_HEIGHT = 34;
   private final @Nullable Screen parent;
   private int dragging = -1;
   private int dragOffsetX;
   private int dragOffsetY;

   public CosmixHudEditorScreen(final @Nullable Screen parent) {
      super(Component.literal("Cosmix HUD Editor"));
      this.parent = parent;
   }

   @Override
   public void onClose() {
      CosmixMods.saveSettings();
      this.minecraft.gui.setScreen(this.parent);
   }

   @Override
   protected void init() {
      this.addRenderableWidget(
         Button.builder(Component.translatable("gui.done"), button -> {
            CosmixMods.saveSettings();
            this.minecraft.gui.setScreen(this.parent);
         }).bounds(this.width / 2 - 100, this.height - 28, 200, 20).build()
      );
   }

   @Override
   public boolean mouseClicked(final MouseButtonEvent event, final boolean doubleClick) {
      if (event.button() == 0) {
         int mouseX = (int)event.x();
         int mouseY = (int)event.y();
         int infoX = CosmixMods.getInfoHudX();
         int infoY = CosmixMods.getInfoHudY();
         int armorX = CosmixMods.getArmorHudX();
         int armorY = this.height - CosmixMods.getArmorHudBottom();
         int mapX = this.width - CosmixMiniMap.getMapSize() - CosmixMods.getMapHudRight();
         int mapY = this.height - CosmixMiniMap.getMapSize() - CosmixMods.getMapHudBottom();

         if (contains(mouseX, mouseY, mapX, mapY, CosmixMiniMap.getMapSize(), CosmixMiniMap.getMapSize())) {
            this.startDragging(MAP_PANEL, mouseX - mapX, mouseY - mapY);
            return true;
         }
         if (contains(mouseX, mouseY, armorX, armorY, ARMOR_WIDTH, ARMOR_HEIGHT)) {
            this.startDragging(ARMOR_PANEL, mouseX - armorX, mouseY - armorY);
            return true;
         }
         if (contains(mouseX, mouseY, infoX, infoY, INFO_WIDTH, INFO_HEIGHT)) {
            this.startDragging(INFO_PANEL, mouseX - infoX, mouseY - infoY);
            return true;
         }
      }

      return super.mouseClicked(event, doubleClick);
   }

   @Override
   public boolean mouseDragged(final MouseButtonEvent event, final double dx, final double dy) {
      if (event.button() != 0 || this.dragging < 0) {
         return super.mouseDragged(event, dx, dy);
      }

      int mouseX = (int)event.x() - this.dragOffsetX;
      int mouseY = (int)event.y() - this.dragOffsetY;
      if (this.dragging == INFO_PANEL) {
         CosmixMods.setInfoHudPosition(clamp(mouseX, 0, this.width - INFO_WIDTH), clamp(mouseY, 0, this.height - INFO_HEIGHT));
      } else if (this.dragging == ARMOR_PANEL) {
         int x = clamp(mouseX, 0, this.width - ARMOR_WIDTH);
         int bottom = clamp(this.height - mouseY, ARMOR_HEIGHT, this.height);
         CosmixMods.setArmorHudPosition(x, bottom);
      } else {
         int size = CosmixMiniMap.getMapSize();
         int right = clamp(this.width - mouseX - size, 0, this.width - size);
         int bottom = clamp(this.height - mouseY - size, 0, this.height - size);
         CosmixMods.setMapHudPosition(right, bottom);
      }
      return true;
   }

   @Override
   public boolean mouseReleased(final MouseButtonEvent event) {
      if (this.dragging >= 0) {
         this.dragging = -1;
         CosmixMods.saveSettings();
         return true;
      }
      return super.mouseReleased(event);
   }

   @Override
   public void extractRenderState(final GuiGraphicsExtractor graphics, final int mouseX, final int mouseY, final float a) {
      super.extractRenderState(graphics, mouseX, mouseY, a);
      int accent = CosmixMods.getAccentColor();
      graphics.text(this.font, this.title, this.width / 2 - this.font.width(this.title) / 2, 12, accent);
      this.drawPanel(graphics, CosmixMods.getInfoHudX(), CosmixMods.getInfoHudY(), INFO_WIDTH, INFO_HEIGHT, "Cosmix HUD", "FPS 120  XYZ 0 64 0", accent);
      this.drawPanel(graphics, CosmixMods.getArmorHudX(), this.height - CosmixMods.getArmorHudBottom(), ARMOR_WIDTH, ARMOR_HEIGHT,
         "Armor Status", "Helmet  Chest  Legs", accent);
      int mapSize = CosmixMiniMap.getMapSize();
      this.drawPanel(graphics, this.width - mapSize - CosmixMods.getMapHudRight(), this.height - mapSize - CosmixMods.getMapHudBottom(),
         mapSize, mapSize, "MiniMap", "N", accent);
   }

   private void drawPanel(
      final GuiGraphicsExtractor graphics,
      final int x,
      final int y,
      final int width,
      final int height,
      final String title,
      final String preview,
      final int accent
   ) {
      graphics.fill(x, y, x + width, y + height, 0xB0000000);
      graphics.outline(x, y, width, height, accent);
      graphics.text(this.font, title, x + 4, y + 4, accent, true);
      if (height > 40) {
         graphics.text(this.font, preview, x + 4, y + 17, 0xFFFFFFFF, true);
      }
   }

   private void startDragging(final int panel, final int offsetX, final int offsetY) {
      this.dragging = panel;
      this.dragOffsetX = offsetX;
      this.dragOffsetY = offsetY;
   }

   private static boolean contains(final int x, final int y, final int left, final int top, final int width, final int height) {
      return x >= left && x < left + width && y >= top && y < top + height;
   }

   private static int clamp(final int value, final int min, final int max) {
      return Math.max(min, Math.min(max, value));
   }
}