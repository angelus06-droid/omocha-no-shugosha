package uut.entity.render;

import uut.entity.EntityToyDummyExplosive;
import uut.entity.render.model.ModelToyExplosiveDummy;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.entity.RenderLiving;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public class RenderToyDummyExplosive extends RenderLiving<EntityToyDummyExplosive> {

    private static final ResourceLocation TEXTURE = new ResourceLocation("uut", "textures/model/toy_dummy_explosive.png");

    public RenderToyDummyExplosive(RenderManager renderManagerIn) {
        super(renderManagerIn, new ModelToyExplosiveDummy(), 0.4f);
    }

    @Override
    protected ResourceLocation getEntityTexture(EntityToyDummyExplosive entity) {
        return TEXTURE;
    }

    @Override
    protected void preRenderCallback(EntityToyDummyExplosive entitylivingbaseIn, float partialTickTime) {
        GlStateManager.scale(0.65f, 0.65f, 0.65f);
    }

    @Override
    protected void applyRotations(EntityToyDummyExplosive entityLiving, float p_77043_2_, float rotationYaw, float partialTicks) {
        super.applyRotations(entityLiving, p_77043_2_, rotationYaw, partialTicks);

        if ((double)entityLiving.limbSwingAmount >= 0.01D) {
            float f = 13.0F;
            float f2 = entityLiving.limbSwing - entityLiving.limbSwingAmount * (1.0F - partialTicks) + 6.0F;
            float f3 = (Math.abs(f2 % 13.0F - 6.5F) - 3.25F) / 3.25F;
            GlStateManager.rotate(6.5F * f3, 0.0F, 0.0F, 1.0F);
        }
    }
}