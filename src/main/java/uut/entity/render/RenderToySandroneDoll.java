package uut.entity.render;

import uut.entity.EntityToySandroneDoll;
import uut.entity.render.model.ModelToySandroneDoll;
import uut.entity.render.model.layer.LayerToySandroneDollCollar;
import uut.entity.render.model.layer.LayerToySandroneDollGlow;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.entity.RenderLiving;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import uut.entity.render.model.layer.LayerToySandroneDollHeldItem;

@SideOnly(Side.CLIENT)
public class RenderToySandroneDoll extends RenderLiving<EntityToySandroneDoll> {

    private static final ResourceLocation TEXTURE_WILD = new ResourceLocation("uut:textures/model/sandrone_doll/toy_sandrone_doll.png");
    private static final ResourceLocation TEXTURE_TAMED = new ResourceLocation("uut:textures/model/sandrone_doll/toy_sandrone_doll_tamed.png");

    public RenderToySandroneDoll(RenderManager renderManagerIn) {
        super(renderManagerIn, new ModelToySandroneDoll(), 0.5f);
        this.addLayer(new LayerToySandroneDollGlow(this));
        this.addLayer(new LayerToySandroneDollCollar(this));
        this.addLayer(new LayerToySandroneDollHeldItem(this));
    }

    @Override
    protected ResourceLocation getEntityTexture(final EntityToySandroneDoll entity) {
        return entity.isTamed() ? TEXTURE_TAMED : TEXTURE_WILD;
    }

    @Override
    protected void preRenderCallback(EntityToySandroneDoll entitylivingbaseIn, float partialTickTime) {
        GlStateManager.scale(0.7f, 0.7f, 0.7f);
    }

    @Override
    protected void applyRotations(EntityToySandroneDoll entityLiving, float p_77043_2_, float rotationYaw, float partialTicks) {
        super.applyRotations(entityLiving, p_77043_2_, rotationYaw, partialTicks);

        if ((double)entityLiving.limbSwingAmount >= 0.01D) {
            float f2 = entityLiving.limbSwing - entityLiving.limbSwingAmount * (1.0F - partialTicks) + 6.0F;
            float f3 = (Math.abs(f2 % 13.0F - 6.5F) - 3.25F) / 3.25F;
            GlStateManager.rotate(6.5F * f3, 0.0F, 0.0F, 1.0F);
        }
    }
}