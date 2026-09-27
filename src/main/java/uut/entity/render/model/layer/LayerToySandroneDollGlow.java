package uut.entity.render.model.layer;

import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.OpenGlHelper;
import net.minecraft.client.renderer.entity.layers.LayerRenderer;
import net.minecraft.util.ResourceLocation;
import uut.entity.EntityToySandroneDoll;
import uut.entity.render.RenderToySandroneDoll;

public class LayerToySandroneDollGlow implements LayerRenderer<EntityToySandroneDoll> {

    private static final ResourceLocation GLOW_TEXTURE = new ResourceLocation("uut", "textures/model/sandrone_doll/toy_sandrone_doll_glow.png");

    private final RenderToySandroneDoll renderer;

    public LayerToySandroneDollGlow(RenderToySandroneDoll rendererIn) {
        this.renderer = rendererIn;
    }

    @Override
    public void doRenderLayer(EntityToySandroneDoll entity, float limbSwing, float limbSwingAmount, float partialTicks, float ageInTicks, float netHeadYaw, float headPitch, float scale) {
        if (entity.isInvisible()) {
            return;
        }

        this.renderer.bindTexture(GLOW_TEXTURE);

        GlStateManager.pushMatrix();
        GlStateManager.enableBlend();
        GlStateManager.blendFunc(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA);
        GlStateManager.disableLighting();

        int brightness = entity.getBrightnessForRender();
        int originalX = brightness % 65536;
        int originalY = brightness / 65536;

        int fullBright = 15728880;
        int glowX = fullBright % 65536;
        int glowY = fullBright / 65536;
        OpenGlHelper.setLightmapTextureCoords(OpenGlHelper.lightmapTexUnit, (float) glowX, (float) glowY);

        this.renderer.getMainModel().render(entity, limbSwing, limbSwingAmount, ageInTicks, netHeadYaw, headPitch, scale);

        OpenGlHelper.setLightmapTextureCoords(OpenGlHelper.lightmapTexUnit, (float) originalX, (float) originalY);

        GlStateManager.enableLighting();
        GlStateManager.disableBlend();
        GlStateManager.popMatrix();
    }

    @Override
    public boolean shouldCombineTextures() {
        return false;
    }
}