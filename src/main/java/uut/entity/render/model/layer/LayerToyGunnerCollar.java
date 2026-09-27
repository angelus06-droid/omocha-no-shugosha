package uut.entity.render.model.layer;

import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.entity.RenderLivingBase;
import net.minecraft.client.renderer.entity.layers.LayerRenderer;
import net.minecraft.item.EnumDyeColor;
import net.minecraft.util.ResourceLocation;
import uut.entity.EntityToyGunner;
import uut.entity.render.RenderToyGunner;

public class LayerToyGunnerCollar implements LayerRenderer<EntityToyGunner> {
    private static final ResourceLocation COLLAR_TEXTURE = new ResourceLocation("uut:textures/model/gunner/toy_gunner_collar.png");
    private final RenderLivingBase<?> renderer;

    public LayerToyGunnerCollar(RenderToyGunner rendererIn) {
        this.renderer = rendererIn;
    }

    @Override
    public void doRenderLayer(EntityToyGunner entitylivingbaseIn, float limbSwing, float limbSwingAmount, float partialTicks, float ageInTicks, float netHeadYaw, float headPitch, float scale) {
        if (entitylivingbaseIn.isTamed() && !entitylivingbaseIn.isInvisible()) {
            this.renderer.bindTexture(COLLAR_TEXTURE);

            GlStateManager.enableLighting();
            GlStateManager.enableRescaleNormal();
            GlStateManager.enableColorMaterial();
            GlStateManager.enableTexture2D();
            GlStateManager.disableBlend();

            EnumDyeColor enumdyecolor = entitylivingbaseIn.getCollarColor();
            float[] afloat = enumdyecolor.getColorComponentValues();

            GlStateManager.color(afloat[0], afloat[1], afloat[2], 1.0F);

            this.renderer.getMainModel().render(entitylivingbaseIn, limbSwing, limbSwingAmount, ageInTicks, netHeadYaw, headPitch, scale);

            GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
        }
    }

    @Override
    public boolean shouldCombineTextures() {
        return true;
    }
}