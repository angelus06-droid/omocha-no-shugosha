package uut.entity.render;

import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.entity.RenderLiving;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import uut.entity.EntityToyGardenGnome;
import uut.entity.render.model.ModelToyGardenGnome;
import uut.entity.render.model.layer.LayerToyGardenGnomeHeldItem;
import uut.entity.render.model.layer.LayerToyGardenGnomeCollar;

@SideOnly(Side.CLIENT)
public class RenderToyGardenGnome extends RenderLiving<EntityToyGardenGnome> {

    private static final ResourceLocation TEXTURE_WILD = new ResourceLocation("uut:textures/model/garden_gnome/toy_gardengnome.png");
    private static final ResourceLocation TEXTURE_TAMED = new ResourceLocation("uut:textures/model/garden_gnome/toy_gardengnome_tamed.png");

    public RenderToyGardenGnome(RenderManager renderManagerIn) {
        super(renderManagerIn, new ModelToyGardenGnome(), 0.35F);

        this.addLayer(new LayerToyGardenGnomeHeldItem(this));
        this.addLayer(new LayerToyGardenGnomeCollar(this));
    }

    @Override
    public void doRender(EntityToyGardenGnome entity, double x, double y, double z, float entityYaw, float partialTicks) {
        GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
        super.doRender(entity, x, y, z, entityYaw, partialTicks);
        GlStateManager.disableBlend();
        GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
    }

    @Override
    protected ResourceLocation getEntityTexture(final EntityToyGardenGnome entity) {
        return entity.isTamed() ? TEXTURE_TAMED : TEXTURE_WILD;
    }

    @Override
    protected void preRenderCallback(final EntityToyGardenGnome entity, final float partialTickTime) {
        GlStateManager.scale(0.65f, 0.65f, 0.65f);

        GlStateManager.enableBlend();
        GlStateManager.blendFunc(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA);
    }

    @Override
    protected void applyRotations(EntityToyGardenGnome entityLiving, float ageInTicks, float rotationYaw, float partialTicks) {
        super.applyRotations(entityLiving, ageInTicks, rotationYaw, partialTicks);
        float limbSwingAmount = entityLiving.limbSwingAmount;
        if (limbSwingAmount >= 0.01F) {
            float swingProgress = entityLiving.limbSwing - limbSwingAmount * (1.0F - partialTicks) + 6.0F;
            float wobble = (Math.abs(swingProgress % 13.0F - 6.5F) - 3.25F) / 3.25F;
            GlStateManager.rotate(6.5F * wobble, 0.0F, 0.0F, 1.0F);
        }
    }
}