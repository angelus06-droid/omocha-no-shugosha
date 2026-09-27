package uut.entity.render.model.layer;

import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.entity.layers.LayerRenderer;
import net.minecraft.item.EnumDyeColor;
import net.minecraft.util.ResourceLocation;
import uut.entity.EntityToyTyrannosaur;
import uut.entity.render.RenderToyTyrannosaur;

public class LayerToyTyrannosaurCollar implements LayerRenderer<EntityToyTyrannosaur> {
    private static final ResourceLocation COLLAR_TEXTURE = new ResourceLocation("uut:textures/model/tyrannosaur/toy_tyrannosaur_collar.png");
    private final RenderToyTyrannosaur trRenderer;

    public LayerToyTyrannosaurCollar(RenderToyTyrannosaur rendererIn) {
        this.trRenderer = rendererIn;
    }

    @Override
    public void doRenderLayer(EntityToyTyrannosaur entitylivingbaseIn, float limbSwing, float limbSwingAmount, float partialTicks, float ageInTicks, float netHeadYaw, float headPitch, float scale) {
        if (entitylivingbaseIn.isTamed() && !entitylivingbaseIn.isInvisible()) {
            this.trRenderer.bindTexture(COLLAR_TEXTURE);

            EnumDyeColor enumdyecolor = entitylivingbaseIn.getCollarColor();
            float[] afloat = enumdyecolor.getColorComponentValues();

            GlStateManager.color(afloat[0], afloat[1], afloat[2]);

            this.trRenderer.getMainModel().render(entitylivingbaseIn, limbSwing, limbSwingAmount, ageInTicks, netHeadYaw, headPitch, scale);
            GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
        }
    }

    @Override
    public boolean shouldCombineTextures() {
        return true;
    }
}