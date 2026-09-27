package uut.entity.render;

import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.entity.RenderLiving;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import uut.entity.EntityToyPrincessGolem;
import uut.entity.EntityToyPrincessShooter;
import uut.entity.render.model.ModelToyPrincessGolem;
import uut.entity.render.model.ModelToyPrincessShooter;

@SideOnly(Side.CLIENT)
public class RenderToyPrincessShooter extends RenderLiving<EntityToyPrincessShooter> {

    private static final ResourceLocation SHOOTER_TEXTURES = new ResourceLocation("uut", "textures/model/high_princess/toy_shooter_minion.png");

    public RenderToyPrincessShooter(RenderManager renderManagerIn) {
        super(renderManagerIn, new ModelToyPrincessShooter(), 0.3f);
    }

    @Override
    protected ResourceLocation getEntityTexture(EntityToyPrincessShooter entity) {
        return SHOOTER_TEXTURES;
    }

    @Override
    protected void preRenderCallback(EntityToyPrincessShooter entitylivingbaseIn, float partialTickTime) {
        GlStateManager.scale(0.6f, 0.6f, 0.6f);
    }
}