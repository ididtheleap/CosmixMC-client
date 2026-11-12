package net.minecraft.client.gui.screens;

import java.util.function.BooleanSupplier;
import java.util.function.Consumer;

import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.components.Button;
import net.minecraft.network.chat.Component;
import net.lax1dude.eaglercraft.v1_8.mods.CosmixMods;
import org.jspecify.annotations.Nullable;

public class CosmixModMenuScreen extends Screen {
   private final @Nullable Screen parent;
   private int titleY;

   public CosmixModMenuScreen(final @Nullable Screen parent) {
      super(Component.literal("Cosmix Mods"));
      this.parent = parent;
   }

   @Override
   public void onClose() {
      this.minecraft.gui.setScreen(this.parent);
   }

   @Override
   protected void init() {
      int toggleCount = 14;
      int columns = this.width >= 390 ? 3 : 2;
      int rows = (toggleCount + columns - 1) / columns;
      int buttonWidth = Math.min(170, (this.width - 24 - (columns - 1) * 4) / columns);
      int totalWidth = columns * buttonWidth + (columns - 1) * 4;
      int left = (this.width - totalWidth) / 2;
      int top = Math.max(2, (this.height - (rows * 22 + 66)) / 2 + 10);
      this.titleY = top - 18;
      this.addToggleAt(0, columns, left, top, buttonWidth, "Coordinates", CosmixMods::isCoordinatesEnabled, CosmixMods::setCoordinatesEnabled);
      this.addToggleAt(1, columns, left, top, buttonWidth, "FPS", CosmixMods::isFpsEnabled, CosmixMods::setFpsEnabled);
      this.addToggleAt(2, columns, left, top, buttonWidth, "Fullbright", CosmixMods::isFullbrightEnabled, CosmixMods::setFullbrightEnabled);
      this.addToggleAt(3, columns, left, top, buttonWidth, "Keystrokes", CosmixMods::isKeystrokesEnabled, CosmixMods::setKeystrokesEnabled);
      this.addToggleAt(4, columns, left, top, buttonWidth, "Direction", CosmixMods::isDirectionEnabled, CosmixMods::setDirectionEnabled);
      this.addToggleAt(5, columns, left, top, buttonWidth, "Ping", CosmixMods::isPingEnabled, CosmixMods::setPingEnabled);
      this.addToggleAt(6, columns, left, top, buttonWidth, "CPS", CosmixMods::isCpsEnabled, CosmixMods::setCpsEnabled);
      this.addToggleAt(7, columns, left, top, buttonWidth, "Armor Status", CosmixMods::isArmorStatusEnabled, CosmixMods::setArmorStatusEnabled);
      this.addToggleAt(8, columns, left, top, buttonWidth, "Saturation", CosmixMods::isSaturationEnabled, CosmixMods::setSaturationEnabled);
      this.addToggleAt(9, columns, left, top, buttonWidth, "Show Arrows", CosmixMods::isArrowsEnabled, CosmixMods::setArrowsEnabled);
      this.addToggleAt(10, columns, left, top, buttonWidth, "Potion Status", CosmixMods::isPotionStatusEnabled, CosmixMods::setPotionStatusEnabled);
      this.addToggleAt(11, columns, left, top, buttonWidth, "MiniMap", CosmixMods::isMinimapEnabled, CosmixMods::setMinimapEnabled);
      this.addToggleAt(12, columns, left, top, buttonWidth, "Chat Time", CosmixMods::areChatTimestampsEnabled, CosmixMods::setChatTimestampsEnabled);
      this.addToggleAt(13, columns, left, top, buttonWidth, "Chat Anti-Spam", CosmixMods::isChatAntiSpamEnabled, CosmixMods::setChatAntiSpamEnabled);
      this.addRenderableWidget(
         Button.builder(Component.literal("Theme: " + CosmixMods.getAccentName()), button -> {
            CosmixMods.cycleAccentColor();
            button.setMessage(Component.literal("Theme: " + CosmixMods.getAccentName()));
         }).bounds(this.width / 2 - 100, top + rows * 22, 98, 20).build()
      );
      this.addRenderableWidget(
         Button.builder(Component.literal("Edit HUD"), button -> this.minecraft.gui.setScreen(new CosmixHudEditorScreen(this)))
            .bounds(this.width / 2 + 2, top + rows * 22, 98, 20)
            .build()
      );
      this.addRenderableWidget(
         Button.builder(Component.translatable("gui.back"), button -> this.minecraft.gui.setScreen(this.parent))
            .bounds(this.width / 2 - 100, top + rows * 22 + 22, 200, 20)
            .build()
      );
   }

   private void addToggleAt(
      final int index,
      final int columns,
      final int left,
      final int top,
      final int width,
      final String name,
      final BooleanSupplier getter,
      final Consumer<Boolean> setter
   ) {
      this.addToggle(left + index % columns * (width + 4), top + index / columns * 22, width, name, getter, setter);
   }

   private void addToggle(
      final int x,
      final int y,
      final int width,
      final String name,
      final BooleanSupplier getter,
      final Consumer<Boolean> setter
   ) {
      this.addRenderableWidget(
         Button.builder(this.toggleLabel(name, getter.getAsBoolean()), button -> {
            boolean enabled = !getter.getAsBoolean();
            setter.accept(enabled);
            button.setMessage(this.toggleLabel(name, enabled));
         }).bounds(x, y, width, 20).build()
      );
   }

   private Component toggleLabel(final String name, final boolean enabled) {
      return Component.literal(name + ": " + (enabled ? "On" : "Off"));
   }

   @Override
   public void extractRenderState(final GuiGraphicsExtractor graphics, final int mouseX, final int mouseY, final float a) {
      super.extractRenderState(graphics, mouseX, mouseY, a);
      int accent = CosmixMods.getAccentColor();
      graphics.text(this.font, this.title, this.width / 2 - this.font.width(this.title) / 2, this.titleY, accent);
      graphics.fill(this.width / 2 - 40, this.titleY + 12, this.width / 2 + 40, this.titleY + 14, accent);
   }
}