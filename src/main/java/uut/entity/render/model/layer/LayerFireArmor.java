package uut.entity.render.model.layer;

import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.entity.layers.LayerRenderer;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import uut.entity.EntityToyTeddyBear;
import uut.entity.render.RenderToyTeddyBear;
import uut.entity.render.model.ModelToyTeddyBearFireArmor;

@SideOnly(Side.CLIENT)
public class LayerFireArmor implements LayerRenderer<EntityToyTeddyBear> {

    private static final ResourceLocation FIRE_ARMOR_TEXTURE = new ResourceLocation("uut:textures/model/teddy_bear/toy_teddybear_fire_armor.png");
    private final RenderToyTeddyBear teddyRenderer;
    private final ModelToyTeddyBearFireArmor teddyModel = new ModelToyTeddyBearFireArmor();

    public LayerFireArmor(RenderToyTeddyBear teddyRendererIn) {
        this.teddyRenderer = teddyRendererIn;
    }

    @Override
    public void doRenderLayer(EntityToyTeddyBear entitylivingbaseIn, float limbSwing, float limbSwingAmount, float partialTicks, float ageInTicks, float netHeadYaw, float headPitch, float scale) {
        if (entitylivingbaseIn.getArmored()) {
            boolean flag = entitylivingbaseIn.isInvisible();
            GlStateManager.depthMask(!flag);
            this.teddyRenderer.bindTexture(FIRE_ARMOR_TEXTURE);

            GlStateManager.matrixMode(5890);
            GlStateManager.loadIdentity();
            float f = (float)entitylivingbaseIn.ticksExisted + partialTicks;

            GlStateManager.translate(f * -0.005F, 0.0F, 0.0F);

            GlStateManager.matrixMode(5888);

            GlStateManager.enableBlend();
            GlStateManager.disableLighting();
            GlStateManager.blendFunc(GlStateManager.SourceFactor.ONE, GlStateManager.DestFactor.ONE);
            GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);

            this.teddyModel.setModelAttributes(this.teddyRenderer.getMainModel());
            this.teddyModel.setLivingAnimations(entitylivingbaseIn, limbSwing, limbSwingAmount, partialTicks);
            this.teddyModel.setRotationAngles(limbSwing, limbSwingAmount, ageInTicks, netHeadYaw, headPitch, scale, entitylivingbaseIn);

            GlStateManager.pushMatrix();

            GlStateManager.translate(0.0F, 0.0F, 0.0F);

            GlStateManager.scale(1.0F, 1.0F, 1.0F);
            this.teddyModel.render(entitylivingbaseIn, limbSwing, limbSwingAmount, ageInTicks, netHeadYaw, headPitch, scale);

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