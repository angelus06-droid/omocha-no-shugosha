package uut.entity.render.model.layer;

import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.entity.layers.LayerRenderer;
import net.minecraft.item.EnumDyeColor;
import net.minecraft.util.ResourceLocation;
import uut.entity.EntityToyCrimsonBunny;
import uut.entity.render.RenderToyCrimsonBunny;

public class LayerToyCrimsonBunnyCollar implements LayerRenderer<EntityToyCrimsonBunny> {
    private static final ResourceLocation COLLAR_TEXTURE = new ResourceLocation("uut:textures/model/crimson_bunny/toy_crimsonbunny_collar.png");
    private final RenderToyCrimsonBunny yetiRenderer;

    public LayerToyCrimsonBunnyCollar(RenderToyCrimsonBunny rendererIn) {
        this.yetiRenderer = rendererIn;
    }

    @Override
    public void doRenderLayer(EntityToyCrimsonBunny entitylivingbaseIn, float limbSwing, float limbSwingAmount, float partialTicks, float ageInTicks, float netHeadYaw, float headPitch, float scale) {
        if (entitylivingbaseIn.isTamed() && !entitylivingbaseIn.isInvisible()) {
            this.yetiRenderer.bindTexture(COLLAR_TEXTURE);

            EnumDyeColor enumdyecolor = entitylivingbaseIn.getCollarColor();
            float[] afloat = enumdyecolor.getColorComponentValues();

            GlStateManager.color(afloat[0], afloat[1], afloat[2]);

            this.yetiRenderer.getMainModel().render(entitylivingbaseIn, limbSwing, limbSwingAmount, ageInTicks, netHeadYaw, headPitch, scale);
            GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
        }
    }

    @Override
    public boolean shouldCombineTextures() {
        return true;
    }
}