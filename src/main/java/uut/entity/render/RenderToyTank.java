package uut.entity.render;

import uut.entity.*;
import net.minecraftforge.fml.relauncher.*;
import net.minecraft.util.*;
import net.minecraft.client.renderer.entity.*;
import uut.entity.render.model.*;
import net.minecraft.client.model.*;
import net.minecraft.client.renderer.*;
import uut.entity.render.model.layer.LayerToyTankCollar;

@SideOnly(Side.CLIENT)
public class RenderToyTank extends RenderLiving<EntityToyTank> {
    private static final ResourceLocation TEXTURE_WILD = new ResourceLocation("uut:textures/model/tank/toy_tank.png");
    private static final ResourceLocation TEXTURE_TAMED = new ResourceLocation("uut:textures/model/tank/toy_tank_tamed.png");
    
    public RenderToyTank(final RenderManager renderManagerIn) {
        super(renderManagerIn, (ModelBase)new ModelToyTank(), 0.5f);
        this.addLayer(new LayerToyTankCollar(this));
    }

    @Override
    protected ResourceLocation getEntityTexture(final EntityToyTank entity) {
        return entity.isTamed() ? TEXTURE_TAMED : TEXTURE_WILD;
    }

    @Override
    protected void preRenderCallback(final EntityToyTank entity, final float partialTickTime) {
        GlStateManager.scale(0.9f, 0.9f, 0.9f);

        GlStateManager.enableBlend();
        GlStateManager.blendFunc(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA);
    }

    @Override
    public void doRender(EntityToyTank entity, double x, double y, double z, float entityYaw, float partialTicks) {
        super.doRender(entity, x, y, z, entityYaw, partialTicks);
        GlStateManager.disableBlend();
    }
}
