package uut.entity.render;

import uut.entity.*;
import net.minecraftforge.fml.relauncher.*;
import net.minecraft.util.*;
import net.minecraft.client.renderer.entity.*;
import uut.entity.render.model.*;
import net.minecraft.client.model.*;
import net.minecraft.client.renderer.*;
import uut.entity.render.model.layer.LayerToyGunnerHeldItem;
import uut.entity.render.model.layer.LayerToyGunnerCollar;

@SideOnly(Side.CLIENT)
public class RenderToyGunner extends RenderLiving<EntityToyGunner>
{
    private static final ResourceLocation TEXTURE_WILD = new ResourceLocation("uut:textures/model/gunner/toy_gunner.png");
    private static final ResourceLocation TEXTURE_TAMED = new ResourceLocation("uut:textures/model/gunner/toy_gunner_tamed.png");
    
    public RenderToyGunner(final RenderManager renderManagerIn) {
        super(renderManagerIn, (ModelBase)new ModelToyGunner(), 0.35f);
        this.addLayer(new LayerToyGunnerCollar(this));
        this.addLayer(new LayerToyGunnerHeldItem(this));
    }

    @Override
    protected ResourceLocation getEntityTexture(final EntityToyGunner entity) {
        return entity.isTamed() ? TEXTURE_TAMED : TEXTURE_WILD;
    }

    @Override
    protected void preRenderCallback(final EntityToyGunner entity, final float partialTickTime) {
        GlStateManager.scale(0.6f, 0.6f, 0.6f);

        GlStateManager.enableBlend();
        GlStateManager.blendFunc(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA);
    }

    @Override
    public void doRender(EntityToyGunner entity, double x, double y, double z, float entityYaw, float partialTicks) {
        super.doRender(entity, x, y, z, entityYaw, partialTicks);
        GlStateManager.disableBlend();
    }
}
