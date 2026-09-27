package uut.entity.render.model.layer;

import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.entity.RenderLivingBase;
import net.minecraft.client.renderer.entity.layers.LayerRenderer;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import uut.entity.EntityToyArrowTower;
import uut.entity.render.model.ModelToyArrowTowerInfinity;

@SideOnly(Side.CLIENT)
public class LayerInfiniteAura implements LayerRenderer<EntityToyArrowTower> {
    private static final ResourceLocation LIGHTNING_TEXTURE = new ResourceLocation("uut:textures/model/arrow_tower/toy_arrowtower_infinity.png");
    private final RenderLivingBase<EntityToyArrowTower> renderer;

    private final ModelToyArrowTowerInfinity layerModel = new ModelToyArrowTowerInfinity();

    public LayerInfiniteAura(RenderLivingBase<EntityToyArrowTower> rendererIn) {
        this.renderer = rendererIn;
    }

    @Override
    public void doRenderLayer(EntityToyArrowTower entity, float limbSwing, float limbSwingAmount, float partialTicks, float ageInTicks, float netHeadYaw, float headPitch, float scale) {
        if (entity.isInfinite()) {
            GlStateManager.depthMask(!entity.isInvisible());
            this.renderer.bindTexture(LIGHTNING_TEXTURE);

            GlStateManager.matrixMode(5890); // GL_TEXTURE
            GlStateManager.loadIdentity();
            float f = (float)entity.ticksExisted + partialTicks;
            GlStateManager.translate(f * 0.01F, f * 0.01F, 0.0F);

            GlStateManager.matrixMode(5888);
            GlStateManager.enableBlend();
            GlStateManager.color(0.5F, 0.5F, 0.5F, 1.0F);
            GlStateManager.disableLighting();
            GlStateManager.blendFunc(GlStateManager.SourceFactor.ONE, GlStateManager.DestFactor.ONE);

            GlStateManager.pushMatrix();

            this.layerModel.render(entity, limbSwing, limbSwingAmount, ageInTicks, netHeadYaw, headPitch, scale * 1.0F);

            GlStateManager.popMatrix();

            GlStateManager.matrixMode(5890);
            GlStateManager.loadIdentity();
            GlStateManager.matrixMode(5888);
            GlStateManager.enableLighting();
            GlStateManager.disableBlend();
            GlStateManager.depthMask(true);
        }
    }

    @Override
    public boolean shouldCombineTextures() {
        return false;
    }
}