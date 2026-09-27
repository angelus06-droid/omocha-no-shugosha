package uut.entity.render.model.layer;

import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.entity.RenderLivingBase;
import net.minecraft.client.renderer.entity.layers.LayerRenderer;
import net.minecraft.item.EnumDyeColor;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import uut.entity.EntityToySpaceship;
import uut.entity.EntityToyTeddyQilin;
import uut.entity.render.model.ModelToyTeddyQilin;

@SideOnly(Side.CLIENT)
public class LayerToyTeddyQilinCollar implements LayerRenderer<EntityToyTeddyQilin> {
    private static final ResourceLocation COLLAR_TEXTURE = new ResourceLocation("uut:textures/model/teddy_qilin/toy_teddyqilin_collar.png");
    private final RenderLivingBase<?> renderer;

    public LayerToyTeddyQilinCollar(RenderLivingBase<?> rendererIn) {
        this.renderer = rendererIn;
    }

    @Override
    public void doRenderLayer(EntityToyTeddyQilin entitylivingbaseIn, float limbSwing, float limbSwingAmount, float partialTicks, float ageInTicks, float netHeadYaw, float headPitch, float scale) {
        if (entitylivingbaseIn.isTamed() && !entitylivingbaseIn.isInvisible()) {
            this.renderer.bindTexture(COLLAR_TEXTURE);

            EnumDyeColor enumdyecolor = entitylivingbaseIn.getCollarColor();
            float[] afloat = enumdyecolor.getColorComponentValues();

            GlStateManager.color(afloat[0], afloat[1], afloat[2]);

            this.renderer.getMainModel().render(entitylivingbaseIn, limbSwing, limbSwingAmount, ageInTicks, netHeadYaw, headPitch, scale);
            GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
        }
    }

    @Override
    public boolean shouldCombineTextures() {
        return false;
    }
}