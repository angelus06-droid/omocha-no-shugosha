package uut.entity.render;

import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.entity.RenderLiving;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import uut.entity.EntityToyKleeBomby;
import uut.entity.render.model.ModelToyKleeBomby;
import uut.entity.render.model.layer.LayerToyKleeBombyCollar;

@SideOnly(Side.CLIENT)
public class RenderToyKleeBomby extends RenderLiving<EntityToyKleeBomby> {
    private static final ResourceLocation TEXTURE_WILD = new ResourceLocation("uut:textures/model/klee_bomby/toy_kleebomby.png");
    private static final ResourceLocation TEXTURE_TAMED = new ResourceLocation("uut:textures/model/klee_bomby/toy_kleebomby_tamed.png");

    public RenderToyKleeBomby(RenderManager renderManagerIn) {
        super(renderManagerIn, new ModelToyKleeBomby(), 0.35f);
        this.addLayer(new LayerToyKleeBombyCollar(this));
    }

    @Override
    protected ResourceLocation getEntityTexture(final EntityToyKleeBomby entity) {
        return entity.isTamed() ? TEXTURE_TAMED : TEXTURE_WILD;
    }

    @Override
    protected void preRenderCallback(final EntityToyKleeBomby entity, final float partialTickTime) {
        GlStateManager.scale(0.8f, 0.8f, 0.8f);

        GlStateManager.enableBlend();
        GlStateManager.blendFunc(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA);
    }

    @Override
    public void doRender(EntityToyKleeBomby entity, double x, double y, double z, float entityYaw, float partialTicks) {
        super.doRender(entity, x, y, z, entityYaw, partialTicks);
        GlStateManager.disableBlend();
    }

    @Override
    protected void applyRotations(EntityToyKleeBomby entityLiving, float p_77043_2_, float rotationYaw, float partialTicks) {
        super.applyRotations(entityLiving, p_77043_2_, rotationYaw, partialTicks);

        if ((double)entityLiving.limbSwingAmount >= 0.01D) {
            float f = 13.0F;
            float f2 = entityLiving.limbSwing - entityLiving.limbSwingAmount * (1.0F - partialTicks) + 6.0F;
            float f3 = (Math.abs(f2 % 13.0F - 6.5F) - 3.25F) / 3.25F;
            GlStateManager.rotate(6.5F * f3, 0.0F, 0.0F, 1.0F);
        }
    }
}