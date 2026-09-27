package uut.entity.render.model.layer;

import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.entity.RenderLivingBase;
import net.minecraft.client.renderer.entity.layers.LayerRenderer;
import net.minecraft.item.EnumDyeColor;
import net.minecraft.util.ResourceLocation;
import uut.entity.EntityToySandroneDoll;
import uut.entity.render.RenderToySandroneDoll;

public class LayerToySandroneDollCollar implements LayerRenderer<EntityToySandroneDoll> {
    private static final ResourceLocation COLLAR_TEXTURE = new ResourceLocation("uut:textures/model/sandrone_doll/toy_sandrone_doll_collar.png");
    private final RenderLivingBase<?> renderer;

    public LayerToySandroneDollCollar(RenderToySandroneDoll rendererIn) {
        this.renderer = rendererIn;
    }

    @Override
    public void doRenderLayer(EntityToySandroneDoll entitylivingbaseIn, float limbSwing, float limbSwingAmount, float partialTicks, float ageInTicks, float netHeadYaw, float headPitch, float scale) {
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