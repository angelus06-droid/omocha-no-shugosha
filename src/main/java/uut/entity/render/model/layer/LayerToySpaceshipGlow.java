package uut.entity.render.model.layer;

import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.OpenGlHelper;
import net.minecraft.client.renderer.entity.layers.LayerRenderer;
import net.minecraft.util.ResourceLocation;
import uut.entity.EntityToySpaceship;
import uut.entity.render.RenderToySpaceship;

public class LayerToySpaceshipGlow implements LayerRenderer<EntityToySpaceship> {

    private static final ResourceLocation GLOW_TEXTURE = new ResourceLocation("uut", "textures/model/spaceship/toy_spaceship_glow.png");

    private final RenderToySpaceship renderer;

    public LayerToySpaceshipGlow(RenderToySpaceship rendererIn) {
        this.renderer = rendererIn;
    }

    public void doRenderLayer(EntityToySpaceship entity, float limbSwing, float limbSwingAmount, float partialTicks, float ageInTicks, float netHeadYaw, float headPitch, float scale) {

        this.renderer.bindTexture(GLOW_TEXTURE);

        GlStateManager.enableBlend();
        GlStateManager.disableAlpha();
        GlStateManager.blendFunc(GlStateManager.SourceFactor.ONE, GlStateManager.DestFactor.ONE);
        OpenGlHelper.setLightmapTextureCoords(OpenGlHelper.lightmapTexUnit, 61680.0F, 0.0F);

        GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);

        this.renderer.getMainModel().render(entity, limbSwing, limbSwingAmount, ageInTicks, netHeadYaw, headPitch, scale);

        this.renderer.setLightmap(entity);
        GlStateManager.disableBlend();
        GlStateManager.enableAlpha();
    }

    public boolean shouldCombineTextures() {
        return false;
    }
}