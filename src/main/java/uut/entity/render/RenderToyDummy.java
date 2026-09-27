package uut.entity.render;

import uut.entity.EntityToyDummy;
import uut.entity.render.model.ModelToyDummy;
import net.minecraft.client.model.ModelBase;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.entity.RenderLiving;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public class RenderToyDummy extends RenderLiving<EntityToyDummy> {

    private static final ResourceLocation TEXTURE = new ResourceLocation("uut", "textures/model/toy_dummy.png");

    public RenderToyDummy(RenderManager renderManagerIn) {
        super(renderManagerIn, new ModelToyDummy(), 0.4f);
    }

    @Override
    protected ResourceLocation getEntityTexture(EntityToyDummy entity) {
        return TEXTURE;
    }

    @Override
    protected void preRenderCallback(EntityToyDummy entitylivingbaseIn, float partialTickTime) {
        GlStateManager.scale(0.65f, 0.65f, 0.65f);
    }

    @Override
    protected void applyRotations(EntityToyDummy entityLiving, float p_77043_2_, float rotationYaw, float partialTicks) {
        super.applyRotations(entityLiving, p_77043_2_, rotationYaw, partialTicks);
        if ((double)entityLiving.limbSwingAmount >= 0.01D) {
            float f1 = entityLiving.limbSwing - entityLiving.limbSwingAmount * (1.0F - partialTicks) + 6.0F;
            float f2 = (Math.abs(f1 % 13.0F - 6.5F) - 3.25F) / 3.25F;
            GlStateManager.rotate(6.5F * f2, 0.0F, 0.0F, 1.0F);
        }
    }
}