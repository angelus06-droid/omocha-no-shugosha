package uut.entity.render.model.layer;

import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.entity.layers.LayerRenderer;
import net.minecraft.item.EnumDyeColor;
import net.minecraft.util.ResourceLocation;
import uut.entity.EntityToyHayFarmer;
import uut.entity.render.RenderToyHayFarmer;

public class LayerToyHayFarmerCollar implements LayerRenderer<EntityToyHayFarmer> {
    private static final ResourceLocation COLLAR_TEXTURE = new ResourceLocation("uut:textures/model/hay_farmer/toy_hayfarmer_collar.png");
    private final RenderToyHayFarmer hayRenderer;

    public LayerToyHayFarmerCollar(RenderToyHayFarmer rendererIn) {
        this.hayRenderer = rendererIn;
    }

    @Override
    public void doRenderLayer(EntityToyHayFarmer entitylivingbaseIn, float limbSwing, float limbSwingAmount, float partialTicks, float ageInTicks, float netHeadYaw, float headPitch, float scale) {
        if (entitylivingbaseIn.isTamed() && !entitylivingbaseIn.isInvisible()) {
            this.hayRenderer.bindTexture(COLLAR_TEXTURE);

            GlStateManager.enableLighting();
            GlStateManager.enableRescaleNormal();
            GlStateManager.enableColorMaterial();
            GlStateManager.enableTexture2D();
            GlStateManager.disableBlend();

            EnumDyeColor enumdyecolor = entitylivingbaseIn.getCollarColor();
            float[] afloat = enumdyecolor.getColorComponentValues();

            GlStateManager.color(afloat[0], afloat[1], afloat[2], 1.0F);

            this.hayRenderer.getMainModel().render(entitylivingbaseIn, limbSwing, limbSwingAmount, ageInTicks, netHeadYaw, headPitch, scale);

            GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
        }
    }

    @Override
    public boolean shouldCombineTextures() {
        return true;
    }
}