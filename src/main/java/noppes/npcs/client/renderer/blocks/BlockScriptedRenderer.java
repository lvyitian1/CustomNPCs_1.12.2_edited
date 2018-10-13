package noppes.npcs.client.renderer.blocks;

import java.util.Random;
import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.RenderHelper;
import net.minecraft.client.renderer.block.model.ItemCameraTransforms.TransformType;
import net.minecraft.client.renderer.texture.TextureMap;
import net.minecraft.client.renderer.tileentity.TileEntityRendererDispatcher;
import net.minecraft.client.renderer.tileentity.TileEntitySpecialRenderer;
import net.minecraft.init.Blocks;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ITickable;
import net.minecraftforge.fml.common.ObfuscationReflectionHelper;
import noppes.npcs.CustomItems;
import noppes.npcs.blocks.tiles.TileScripted;

public class BlockScriptedRenderer extends BlockRendererInterface {
   private static Random random = new Random();

   public void render(TileEntity var1, double x, double y, double z, float var8, int blockDamage, float alpha) {
      TileScripted tile = (TileScripted)var1;
      GlStateManager.pushMatrix();
      GlStateManager.disableBlend();
      RenderHelper.enableStandardItemLighting();
      GlStateManager.translate(x + 0.5D, y, z + 0.5D);
      if (this.overrideModel()) {
         GlStateManager.translate(0.0D, 0.5D, 0.0D);
         this.renderItem(new ItemStack(CustomItems.scripted));
      } else {
         GlStateManager.rotate((float)tile.rotationY, 0.0F, 1.0F, 0.0F);
         GlStateManager.rotate((float)tile.rotationX, 1.0F, 0.0F, 0.0F);
         GlStateManager.rotate((float)tile.rotationZ, 0.0F, 0.0F, 1.0F);
         GlStateManager.scale(tile.scaleX, tile.scaleY, tile.scaleZ);
         Block b = tile.blockModel;
         if (b != null && b != Blocks.AIR) {
            if (b == CustomItems.scripted) {
               GlStateManager.translate(0.0D, 0.5D, 0.0D);
               this.renderItem(tile.itemModel);
            } else {
               IBlockState state = b.getStateFromMeta(tile.itemModel.getItemDamage());
               this.renderBlock(tile, b, state);
               if (b.hasTileEntity(state) && !tile.renderTileErrored) {
                  try {
                     if (tile.renderTile == null) {
                        TileEntity entity = b.createTileEntity(this.getWorld(), state);
                        entity.setPos(tile.getPos());
                        entity.setWorld(this.getWorld());
                        ObfuscationReflectionHelper.setPrivateValue(TileEntity.class, entity, Integer.valueOf(tile.itemModel.getItemDamage()), 5);
                        ObfuscationReflectionHelper.setPrivateValue(TileEntity.class, entity, b, 6);
                        tile.renderTile = entity;
                        if (entity instanceof ITickable) {
                           tile.renderTileUpdate = (ITickable)entity;
                        }
                     }

                     TileEntitySpecialRenderer renderer = TileEntityRendererDispatcher.instance.getRenderer(tile.renderTile);
                     if (renderer != null) {
                        renderer.render(tile.renderTile, -0.5D, 0.0D, -0.5D, var8, blockDamage, alpha);
                     } else {
                        tile.renderTileErrored = true;
                     }
                  } catch (Exception var15) {
                     tile.renderTileErrored = true;
                  }
               }
            }
         } else {
            GlStateManager.translate(0.0D, 0.5D, 0.0D);
            this.renderItem(tile.itemModel);
         }
      }

      GlStateManager.popMatrix();
   }

   private void renderItem(ItemStack item) {
      Minecraft.getMinecraft().getRenderItem().renderItem(item, TransformType.NONE);
   }

   private void renderBlock(TileScripted tile, Block b, IBlockState state) {
      GlStateManager.pushMatrix();
      this.bindTexture(TextureMap.LOCATION_BLOCKS_TEXTURE);
      GlStateManager.translate(-0.5F, 0.0F, 0.5F);
      Minecraft.getMinecraft().getBlockRendererDispatcher().renderBlockBrightness(state, 1.0F);
      if (b.getTickRandomly() && random.nextInt(12) == 1) {
         b.randomDisplayTick(state, tile.getWorld(), tile.getPos(), random);
      }

      GlStateManager.popMatrix();
   }

   private boolean overrideModel() {
      ItemStack held = Minecraft.getMinecraft().player.getHeldItemMainhand();
      if (held == null) {
         return false;
      } else {
         return held.getItem() == CustomItems.wand || held.getItem() == CustomItems.scripter;
      }
   }
}
