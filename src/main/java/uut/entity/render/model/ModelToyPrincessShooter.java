package uut.entity.render.model;

import net.minecraft.client.model.ModelBase;
import net.minecraft.client.model.ModelBox;
import net.minecraft.client.model.ModelRenderer;
import net.minecraft.entity.Entity;
import net.minecraft.util.math.MathHelper;

public class ModelToyPrincessShooter extends ModelBase
{
    private final ModelRenderer all;
    private final ModelRenderer body;
    private final ModelRenderer mouth;
    private final ModelRenderer rightLeg;
    private final ModelRenderer leftLeg;

    public ModelToyPrincessShooter() {
        textureWidth = 32;
        textureHeight = 32;

        this.all = new ModelRenderer(this);
        this.all.setRotationPoint(0.0F, 21.0F, 0.0F);


        this.body = new ModelRenderer(this);
        this.body.setRotationPoint(3.0F, 0.0F, -2.0F);
        this.all.addChild(body);
        this.body.cubeList.add(new ModelBox(body, 0, 0, -6.5F, -7.0F, -1.5F, 7, 7, 7, 0.0F, false));

        this.mouth = new ModelRenderer(this);
        this.mouth.setRotationPoint(-3.0F, -4.0F, -1.5F);
        this.body.addChild(mouth);
        this.mouth.cubeList.add(new ModelBox(mouth, 0, 14, -1.0F, 0.0F, -4.0F, 2, 2, 4, 0.0F, false));

        this.rightLeg = new ModelRenderer(this);
        this.rightLeg.setRotationPoint(-1.5F, 0.0F, 0.0F);
        this.all.addChild(rightLeg);
        this.rightLeg.cubeList.add(new ModelBox(rightLeg, 0, 27, -1.0F, 0.0F, -1.0F, 2, 3, 2, 0.0F, false));

        this.leftLeg = new ModelRenderer(this);
        this.leftLeg.setRotationPoint(1.5F, 0.0F, 0.0F);
        this.all.addChild(leftLeg);
        this.leftLeg.cubeList.add(new ModelBox(leftLeg, 8, 27, -1.0F, 0.0F, -1.0F, 2, 3, 2, 0.0F, false));
    }

    @Override
    public void render(Entity entity, float f, float f1, float f2, float f3, float f4, float f5) {
        all.render(f5);
    }

    public void setRotationAngle(ModelRenderer modelRenderer, float x, float y, float z) {
        modelRenderer.rotateAngleX = x;
        modelRenderer.rotateAngleY = y;
        modelRenderer.rotateAngleZ = z;
    }
    
    public void setRotationAngles(final float limbSwing, final float limbSwingAmount, final float ageInTicks, final float netHeadYaw, final float headPitch, final float scaleFactor, final Entity entityIn) {
        final float f = 1.0f;
        this.rightLeg.rotateAngleX = MathHelper.cos(limbSwing * 1.4442f) * 1.0f * limbSwingAmount / f;
        this.leftLeg.rotateAngleX = MathHelper.cos(limbSwing * 1.4442f + 3.1415927f) * 1.0f * limbSwingAmount / f;
        this.rightLeg.rotateAngleY = 0.0f;
        this.leftLeg.rotateAngleY = 0.0f;
        this.rightLeg.rotateAngleZ = 0.0f;
        this.leftLeg.rotateAngleZ = 0.0f;
    }
}
