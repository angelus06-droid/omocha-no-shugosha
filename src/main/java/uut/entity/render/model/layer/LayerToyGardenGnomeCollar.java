package uut.entity.render.model.layer;

import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.OpenGlHelper;
import net.minecraft.client.renderer.entity.layers.LayerRenderer;
import net.minecraft.item.EnumDyeColor;
import net.minecraft.util.ResourceLocation;
import uut.entity.EntityToyGardenGnome;
import uut.entity.render.RenderToyGardenGnome;

public class LayerToyGardenGnomeCollar implements LayerRenderer<EntityToyGardenGnome> {
    private static final ResourceLocation COLLAR_TEXTURE = new ResourceLocation("uut:textures/model/garden_gnome/toy_gardengnome_collar.png");
    private final RenderToyGardenGnome gnomeRenderer;

    public LayerToyGardenGnomeCollar(RenderToyGardenGnome rendererIn) {
        this.gnomeRenderer = rendererIn;
    }

    @Override
    public void doRenderLayer(EntityToyGardenGnome entitylivingbaseIn, float limbSwing, float limbSwingAmount, float partialTicks, float ageInTicks, float netHeadYaw, float headPitch, float scale) {
        if (entitylivingbaseIn.isTamed() && !entitylivingbaseIn.isInvisible()) {
            this.gnomeRenderer.bindTexture(COLLAR_TEXTURE);

            GlStateManager.enableLighting();
            GlStateManager.enableRescaleNormal();
            GlStateManager.enableColorMaterial();
            GlStateManager.enableTexture2D();
            GlStateManager.disableBlend();

            EnumDyeColor enumdyecolor = entitylivingbaseIn.getCollarColor();
            float[] afloat = enumdyecolor.getColorComponentValues();

            GlStateManager.color(afloat[0], afloat[1], afloat[2], 1.0F);

            this.gnomeRenderer.getMainModel().render(entitylivingbaseIn, limbSwing, limbSwingAmount, ageInTicks, netHeadYaw, headPitch, scale);

            GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
        }
    }

    @Override
    public boolean shouldCombineTextures() {
        return true;
    }
}