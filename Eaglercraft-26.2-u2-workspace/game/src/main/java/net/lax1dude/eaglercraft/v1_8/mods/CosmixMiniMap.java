package net.lax1dude.eaglercraft.v1_8.mods;

import com.mojang.blaze3d.platform.NativeImage;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.client.renderer.RenderPipelines;
import net.minecraft.client.renderer.texture.DynamicTexture;
import net.minecraft.core.BlockPos;
import net.minecraft.resources.Identifier;
import net.minecraft.util.Util;
import net.minecraft.world.level.levelgen.Heightmap;
import net.minecraft.world.level.material.MapColor;

public final class CosmixMiniMap {
   private static final int GRID_SIZE = 25;
   private static final int GRID_CENTER = GRID_SIZE / 2;
   private static final int BLOCKS_PER_PIXEL = 2;
   private static final int PIXEL_SIZE = 3;
   private static final int MAP_SIZE = GRID_SIZE * PIXEL_SIZE;
   private static final Identifier TEXTURE_ID = Identifier.fromNamespaceAndPath("cosmix", "cosmix_minimap");
   private static final int[] colors = new int[GRID_SIZE * GRID_SIZE];
   private static DynamicTexture texture;
   private static ClientLevel cachedLevel;
   private static int cachedCenterX = Integer.MIN_VALUE;
   private static int cachedCenterZ = Integer.MIN_VALUE;
   private static long lastUpdate;

   private CosmixMiniMap() {
   }

   public static void extract(final Minecraft minecraft, final GuiGraphicsExtractor graphics) {
      ClientLevel level = minecraft.level;
      LocalPlayer player = minecraft.player;
      if (level == null || player == null) {
         return;
      }

      ensureTexture(minecraft);
      int centerX = player.blockPosition().getX();
      int centerZ = player.blockPosition().getZ();
      update(level, player, centerX, centerZ);

      int mapX = graphics.guiWidth() - MAP_SIZE - CosmixMods.getMapHudRight();
      int mapY = graphics.guiHeight() - MAP_SIZE - CosmixMods.getMapHudBottom();
      graphics.fill(mapX - 2, mapY - 2, mapX + MAP_SIZE + 2, mapY + MAP_SIZE + 2, 0xB0000000);
      graphics.blit(RenderPipelines.GUI_TEXTURED, TEXTURE_ID, mapX, mapY, 0.0F, 0.0F, MAP_SIZE, MAP_SIZE, GRID_SIZE, GRID_SIZE);
      int accent = CosmixMods.getAccentColor();
      graphics.outline(mapX - 2, mapY - 2, MAP_SIZE + 4, MAP_SIZE + 4, accent);
      graphics.fill(mapX + GRID_CENTER * PIXEL_SIZE + 1, mapY + GRID_CENTER * PIXEL_SIZE + 1,
         mapX + GRID_CENTER * PIXEL_SIZE + 2, mapY + GRID_CENTER * PIXEL_SIZE + 2, accent);
      graphics.text(minecraft.font, "N", mapX + MAP_SIZE / 2 - 3, mapY - 10, 0xFFFFFFFF, true);
   }

   private static void ensureTexture(final Minecraft minecraft) {
      if (texture == null) {
         texture = new DynamicTexture("Cosmix MiniMap", GRID_SIZE, GRID_SIZE, true);
         minecraft.getTextureManager().register(TEXTURE_ID, texture);
      }
   }

   public static int getMapSize() {
      return MAP_SIZE;
   }

   private static void update(final ClientLevel level, final LocalPlayer player, final int centerX, final int centerZ) {
      long now = Util.getMillis();
      if (cachedLevel == level && cachedCenterX == centerX && cachedCenterZ == centerZ && now - lastUpdate < 400L) {
         return;
      }

      cachedLevel = level;
      cachedCenterX = centerX;
      cachedCenterZ = centerZ;
      lastUpdate = now;
      int sampleY = player.blockPosition().getY();
      NativeImage image = texture.getPixels();
      for (int z = 0; z < GRID_SIZE; ++z) {
         for (int x = 0; x < GRID_SIZE; ++x) {
            int worldX = centerX + (x - GRID_CENTER) * BLOCKS_PER_PIXEL;
            int worldZ = centerZ + (z - GRID_CENTER) * BLOCKS_PER_PIXEL;
            BlockPos samplePos = new BlockPos(worldX, sampleY, worldZ);
            int index = z * GRID_SIZE + x;
            if (!level.hasChunkAt(samplePos)) {
               colors[index] = 0;
               image.setPixel(x, z, 0);
               continue;
            }

            int surfaceY = level.getHeight(Heightmap.Types.MOTION_BLOCKING, worldX, worldZ) - 1;
            if (surfaceY < level.getMinY()) {
               colors[index] = 0;
               image.setPixel(x, z, 0);
               continue;
            }

            BlockPos surfacePos = new BlockPos(worldX, surfaceY, worldZ);
            MapColor mapColor = level.getBlockState(surfacePos).getMapColor(level, surfacePos);
            colors[index] = mapColor.calculateARGBColor(MapColor.Brightness.NORMAL);
            image.setPixel(x, z, colors[index]);
         }
      }
      texture.upload();
   }
}