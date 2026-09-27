package uut.entity.render.model.layer;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.block.model.ItemCameraTransforms;
import net.minecraft.client.renderer.entity.RenderLivingBase;
import net.minecraft.client.renderer.entity.layers.LayerRenderer;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumHandSide;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import uut.entity.render.model.ModelToyTeddyBear;

@SideOnly(Side.CLIENT)
public class LayerToyTeddyBearHeldItem implements LayerRenderer<EntityLivingBase> {

    protected final RenderLivingBase<?> livingEntityRenderer;

    public LayerToyTeddyBearHeldItem(RenderLivingBase<?> livingEntityRendererIn) {
        this.livingEntityRenderer = livingEntityRendererIn;
    }

    @Override
    public void doRenderLayer(EntityLivingBase entitylivingbaseIn, float limbSwing, float limbSwingAmount, float partialTicks, float ageInTicks, float netHeadYaw, float headPitch, float scale) {
        ItemStack mainHand = entitylivingbaseIn.getHeldItemMainhand();
        ItemStack offHand = entitylivingbaseIn.getHeldItemOffhand();

        if (!mainHand.isEmpty() || !offHand.isEmpty()) {
            GlStateManager.pushMatrix();

            if (this.livingEntityRenderer.getMainModel().isChild) {
                GlStateManager.translate(0.0F, 0.75F, 0.0F);
                GlStateManager.scale(0.5F, 0.5F, 0.5F);
            }

            this.renderHeldItem(entitylivingbaseIn, mainHand, ItemCameraTransforms.TransformType.THIRD_PERSON_RIGHT_HAND, EnumHandSide.RIGHT);
            this.renderHeldItem(entitylivingbaseIn, offHand, ItemCameraTransforms.TransformType.THIRD_PERSON_LEFT_HAND, EnumHandSide.LEFT);

            GlStateManager.popMatrix();
        }
    }


    private void renderHeldItem(EntityLivingBase entity, ItemStack stack, ItemCameraTransforms.TransformType transform, EnumHandSide handSide) {
        if (!stack.isEmpty()) {
            GlStateManager.pushMatrix();

            if (entity.isSneaking()) {
                GlStateManager.translate(0.0F, 0.2F, 0.0F);
            }

            this.translateToHand(handSide);

            GlStateManager.rotate(-90.0F, 1.0F, 0.0F, 0.0F);
            GlStateManager.rotate(180.0F, 0.0F, 1.0F, 0.0F);

            boolean isLeft = handSide == EnumHandSide.LEFT;
            GlStateManager.translate((isLeft ? -1 : 1) / 16.0F, 0.09F, -0.45F);

            GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
            GlStateManager.disableCull();

            Minecraft.getMinecraft().getItemRenderer().renderItemSide(entity, stack, transform, isLeft);

            GlStateManager.enableCull();
            GlStateManager.enableLighting();
            GlStateManager.enableRescaleNormal();
            GlStateManager.disableBlend();
            GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);

            GlStateManager.popMatrix();
        }
    }

    protected void translateToHand(EnumHandSide handSide) {
        ((ModelToyTeddyBear)this.livingEntityRenderer.getMainModel()).postRenderArm(0.0625F, handSide);
    }

    @Override
    public boolean shouldCombineTextures() {
        return false;
    }
}