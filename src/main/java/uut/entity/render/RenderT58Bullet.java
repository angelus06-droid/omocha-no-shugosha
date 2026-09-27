package uut.entity.render;

import net.minecraft.client.renderer.*;
import net.minecraft.client.renderer.entity.Render;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import uut.entity.projectiles.EntityT58Bullet;
import uut.entity.render.model.ModelT58Bullet;

@SideOnly(Side.CLIENT)
public class RenderT58Bullet extends Render<EntityT58Bullet> {
    private final ModelT58Bullet model = new ModelT58Bullet();
    private static final ResourceLocation TEXTURE = new ResourceLocation("uut:textures/model/t58_bullet.png");

    public RenderT58Bullet(RenderManager renderManager) {
        super(renderManager);
    }

    @Override
    public void doRender(EntityT58Bullet entity, double x, double y, double z, float entityYaw, float partialTicks) {
        this.bindEntityTexture(entity);
        GlStateManager.pushMatrix();

        GlStateManager.translate((float)x, (float)y, (float)z);

        float yaw = entity.prevRotationYaw + (entity.rotationYaw - entity.prevRotationYaw) * partialTicks;
        float pitch = entity.prevRotationPitch + (entity.rotationPitch - entity.prevRotationPitch) * partialTicks;

        GlStateManager.rotate(yaw + 180.0F, 0.0F, 1.0F, 0.0F);

        GlStateManager.rotate(pitch, 1.0F, 0.0F, 0.0F);

        GlStateManager.enableRescaleNormal();
        GlStateManager.disableLighting();
        OpenGlHelper.setLightmapTextureCoords(OpenGlHelper.lightmapTexUnit, 240.0F, 240.0F);

        this.model.render(entity, 0.0F, 0.0F, 0.0F, 0.0F, 0.0F, 0.0625F);

        GlStateManager.enableLighting();
        GlStateManager.disableRescaleNormal();
        GlStateManager.popMatrix();

        super.doRender(entity, x, y, z, entityYaw, partialTicks);
    }

    @Override
    protected ResourceLocation getEntityTexture(EntityT58Bullet entity) {
        return TEXTURE;
    }
}