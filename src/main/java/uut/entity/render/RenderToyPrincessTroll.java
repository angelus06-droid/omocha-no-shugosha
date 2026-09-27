package uut.entity.render;

import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.entity.RenderLiving;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import uut.entity.EntityToyPrincessTroll;
import uut.entity.render.model.ModelToyPrincessTroll;


@SideOnly(Side.CLIENT)
public class RenderToyPrincessTroll extends RenderLiving<EntityToyPrincessTroll> {

    private static final ResourceLocation SANDRONE_TEXTURES = new ResourceLocation("uut", "textures/model/high_princess/toy_troll_minion.png");

    public RenderToyPrincessTroll(RenderManager renderManagerIn) {
        super(renderManagerIn, new ModelToyPrincessTroll(), 0.5f);
    }

    @Override
    protected ResourceLocation getEntityTexture(EntityToyPrincessTroll entity) {
        return SANDRONE_TEXTURES;
    }

    @Override
    protected void preRenderCallback(EntityToyPrincessTroll entitylivingbaseIn, float partialTickTime) {
        GlStateManager.scale(0.8f, 0.8f, 0.8f);
    }

    @Override
    protected void applyRotations(EntityToyPrincessTroll entityLiving, float p_77043_2_, float rotationYaw, float partialTicks) {
        super.applyRotations(entityLiving, p_77043_2_, rotationYaw, partialTicks);

        if ((double)entityLiving.limbSwingAmount >= 0.01D) {
            float f2 = entityLiving.limbSwing - entityLiving.limbSwingAmount * (1.0F - partialTicks) + 6.0F;
            float f3 = (Math.abs(f2 % 13.0F - 6.5F) - 3.25F) / 3.25F;
            GlStateManager.rotate(6.5F * f3, 0.0F, 0.0F, 1.0F);
        }
    }
}