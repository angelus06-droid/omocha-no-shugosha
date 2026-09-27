package uut.entity.render.model.layer;

import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.entity.layers.LayerRenderer;
import net.minecraft.item.EnumDyeColor;
import net.minecraft.util.ResourceLocation;
import uut.entity.EntityToySpaceship;
import uut.entity.render.RenderToySpaceship;

public class LayerToySpaceshipCollar implements LayerRenderer<EntityToySpaceship> {
    private static final ResourceLocation COLLAR_TEXTURE = new ResourceLocation("uut:textures/model/spaceship/toy_spaceship_collar.png");
    private final RenderToySpaceship spaceshipRenderer;

    public LayerToySpaceshipCollar(RenderToySpaceship rendererIn) {
        this.spaceshipRenderer = rendererIn;
    }

    @Override
    public void doRenderLayer(EntityToySpaceship entitylivingbaseIn, float limbSwing, float limbSwingAmount, float partialTicks, float ageInTicks, float netHeadYaw, float headPitch, float scale) {
        if (entitylivingbaseIn.isTamed() && !entitylivingbaseIn.isInvisible()) {
            this.spaceshipRenderer.bindTexture(COLLAR_TEXTURE);

            EnumDyeColor enumdyecolor = entitylivingbaseIn.getCollarColor();
            float[] afloat = enumdyecolor.getColorComponentValues();

            GlStateManager.color(afloat[0], afloat[1], afloat[2]);

            this.spaceshipRenderer.getMainModel().render(entitylivingbaseIn, limbSwing, limbSwingAmount, ageInTicks, netHeadYaw, headPitch, scale);
            GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
        }
    }

    @Override
    public boolean shouldCombineTextures() {
        return true;
    }
}