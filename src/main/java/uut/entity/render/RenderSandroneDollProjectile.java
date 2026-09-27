package uut.entity.render;

import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.entity.Render;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import uut.entity.projectiles.EntitySandroneDollProjectile;
import uut.entity.render.model.ModelSandroneDollMissile;

@SideOnly(Side.CLIENT)
public class RenderSandroneDollProjectile extends Render<EntitySandroneDollProjectile> {
    private final ModelSandroneDollMissile model = new ModelSandroneDollMissile();
    private static final ResourceLocation TEXTURE = new ResourceLocation("uut:textures/model/sandrone_doll/sandrone_doll_missile.png");

    public RenderSandroneDollProjectile(RenderManager renderManager) {
        super(renderManager);
    }

    @Override
    public void doRender(EntitySandroneDollProjectile entity, double x, double y, double z, float entityYaw, float partialTicks) {
        this.bindEntityTexture(entity);
        GlStateManager.pushMatrix();

        float renderOffsetY = 0.05F;
        GlStateManager.translate((float)x, (float)y + renderOffsetY, (float)z);

        float yaw = entity.prevRotationYaw + (entity.rotationYaw - entity.prevRotationYaw) * partialTicks;
        float pitch = entity.prevRotationPitch + (entity.rotationPitch - entity.prevRotationPitch) * partialTicks;

        GlStateManager.rotate(yaw + 180.0F, 0.0F, 1.0F, 0.0F);
        GlStateManager.rotate(pitch, 1.0F, 0.0F, 0.0F);

        GlStateManager.enableRescaleNormal();
        GlStateManager.scale(0.4F, 0.4F, 0.4F);
        this.model.render(entity, 0.0F, 0.0F, 0.0F, 0.0F, 0.0F, 0.0625F);
        GlStateManager.disableRescaleNormal();

        GlStateManager.popMatrix();
        super.doRender(entity, x, y, z, entityYaw, partialTicks);
    }

    @Override
    protected ResourceLocation getEntityTexture(EntitySandroneDollProjectile entity) {
        return TEXTURE;
    }
}