package uut.entity.render;

import uut.entity.projectiles.EntityToyBullet;
import uut.entity.render.model.ModelToyBullet;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.entity.Render;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public class RenderToyBullet extends Render<EntityToyBullet>
{
    private static final ResourceLocation BULLET_TEXTURE = new ResourceLocation("uut:textures/model/bullet.png");
    private final ModelToyBullet model;

    public RenderToyBullet(RenderManager renderManager) {
        super(renderManager);
        this.model = new ModelToyBullet();
    }

    @Override
    public void doRender(EntityToyBullet entity, double x, double y, double z, float entityYaw, float partialTicks) {
        GlStateManager.pushMatrix();

        GlStateManager.translate((float)x, (float)y - 0.15F, (float)z);

        float yaw = entity.prevRotationYaw + (entity.rotationYaw - entity.prevRotationYaw) * partialTicks - 90.0F;
        float pitch = entity.prevRotationPitch + (entity.rotationPitch - entity.prevRotationPitch) * partialTicks;

        GlStateManager.rotate(yaw, 0.0F, 1.0F, 0.0F);
        GlStateManager.rotate(pitch, 0.0F, 0.0F, 1.0F);

        float scale = 0.2F;
        GlStateManager.scale(scale, scale, scale);

        this.bindEntityTexture(entity);

        if (this.renderOutlines) {
            GlStateManager.enableColorMaterial();
            GlStateManager.enableOutlineMode(this.getTeamColor(entity));
        }

        this.model.render(entity, 0.0F, 0.0F, 0.0F, entity.rotationYaw, entity.rotationPitch, 0.0625F);

        if (this.renderOutlines) {
            GlStateManager.disableOutlineMode();
            GlStateManager.disableColorMaterial();
        }

        GlStateManager.popMatrix();
        super.doRender(entity, x, y, z, entityYaw, partialTicks);
    }

    @Override
    protected ResourceLocation getEntityTexture(EntityToyBullet entity) {
        return BULLET_TEXTURE;
    }
}