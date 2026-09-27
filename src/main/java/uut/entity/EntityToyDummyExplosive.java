package uut.entity;

import net.minecraft.world.*;
import net.minecraft.entity.ai.*;
import net.minecraft.util.datafix.*;
import net.minecraft.entity.*;
import net.minecraft.util.*;
import javax.annotation.*;

public class EntityToyDummyExplosive extends EntityToyDummy {
    public EntityToyDummyExplosive(final World worldIn) {
        super(worldIn);
        this.setSize(0.8f, 1.55f);
    }
    
    protected void initEntityAI() {
        this.targetTasks.addTask(2, (EntityAIBase)new EntityAIHurtByTarget((EntityCreature)this, false, new Class[0]));
    }
    
    private void explode() {
        if (!this.world.isRemote) {
            final boolean flag = this.world.getGameRules().getBoolean("mobGriefing");
            this.dead = true;
            this.world.createExplosion((Entity)this, this.posX, this.posY + this.height / 16.0f, this.posZ, 4.0f, flag);
            this.setDead();
        }
    }
    
    public void onDeath(final DamageSource cause) {
        super.onDeath(cause);
        this.explode();
    }
    
    public static void registerFixesIronGolem(final DataFixer fixer) {
        EntityLiving.registerFixesMob(fixer, (Class)EntityToyDummyExplosive.class);
    }
    
    @Nullable
    protected SoundEvent getAmbientSound() {
        return null;
    }
}
