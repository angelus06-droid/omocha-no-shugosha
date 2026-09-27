package uut.entity.render;

import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.entity.RenderLiving;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import uut.entity.EntityToyHayFarmer;
import uut.entity.render.model.ModelToyHayFarmer;
import uut.entity.render.model.layer.LayerToyHayFarmerHeldItem;
import uut.entity.render.model.layer.LayerToyHayFarmerCollar;

@SideOnly(Side.CLIENT)
public class RenderToyHayFarmer extends RenderLiving<EntityToyHayFarmer> {

    private static final ResourceLocation TEXTURE_WILD = new ResourceLocation("uut:textures/model/hay_farmer/toy_hayfarmer.png");
    private static final ResourceLocation TEXTURE_TAMED = new ResourceLocation("uut:textures/model/hay_farmer/toy_hayfarmer_tamed.png");

    public RenderToyHayFarmer(RenderManager renderManagerIn) {
        super(renderManagerIn, new ModelToyHayFarmer(), 0.35F);
        this.addLayer(new LayerToyHayFarmerHeldItem(this));
        this.addLayer(new LayerToyHayFarmerCollar(this));
    }

    @Override
    protected ResourceLocation getEntityTexture(final EntityToyHayFarmer entity) {
        return entity.isTamed() ? TEXTURE_TAMED : TEXTURE_WILD;
    }

    @Override
    protected void preRenderCallback(final EntityToyHayFarmer entity, final float partialTickTime) {
        GlStateManager.scale(0.65f, 0.65f, 0.65f);

        GlStateManager.enableBlend();
        GlStateManager.blendFunc(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA);
    }

    @Override
    public void doRender(EntityToyHayFarmer entity, double x, double y, double z, float entityYaw, float partialTicks) {
        super.doRender(entity, x, y, z, entityYaw, partialTicks);
        GlStateManager.disableBlend();
    }

    @Override
    protected void applyRotations(EntityToyHayFarmer entityLiving, float ageInTicks, float rotationYaw, float partialTicks) {
        super.applyRotations(entityLiving, ageInTicks, rotationYaw, partialTicks);
        float limbSwingAmount = entityLiving.limbSwingAmount;
        if (limbSwingAmount >= 0.01F) {
            float swingProgress = entityLiving.limbSwing - limbSwingAmount * (1.0F - partialTicks) + 6.0F;
            float wobble = (Math.abs(swingProgress % 13.0F - 6.5F) - 3.25F) / 3.25F;
            GlStateManager.rotate(6.5F * wobble, 0.0F, 0.0F, 1.0F);
        }
    }
}