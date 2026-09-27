package uut.entity.ai;

import net.minecraft.entity.ai.*;
import uut.entity.*;
import net.minecraft.entity.*;

public class EntityAIToySwell extends EntityAIBase
{
    EntityToyBomby swellingCreeper;
    EntityLivingBase creeperAttackTarget;
    
    public EntityAIToySwell(final EntityToyBomby EntityToyBombyIn) {
        this.swellingCreeper = EntityToyBombyIn;
        this.setMutexBits(1);
    }
    
    public boolean shouldExecute() {
        final EntityLivingBase entitylivingbase = this.swellingCreeper.getAttackTarget();
        return this.swellingCreeper.getCreeperState() > 0 || (entitylivingbase != null && this.swellingCreeper.getDistanceSq((Entity)entitylivingbase) < 9.0);
    }
    
    public void startExecuting() {
        this.swellingCreeper.getNavigator().clearPath();
        this.creeperAttackTarget = this.swellingCreeper.getAttackTarget();
    }
    
    public void resetTask() {
        this.creeperAttackTarget = null;
    }
    
    public void updateTask() {
        if (this.creeperAttackTarget == null) {
            this.swellingCreeper.setCreeperState(-1);
        }
        else if (this.swellingCreeper.getDistanceSq((Entity)this.creeperAttackTarget) > 49.0) {
            this.swellingCreeper.setCreeperState(-1);
        }
        else if (!this.swellingCreeper.getEntitySenses().canSee((Entity)this.creeperAttackTarget)) {
            this.swellingCreeper.setCreeperState(-1);
        }
        else {
            this.swellingCreeper.setCreeperState(1);
        }
    }
}
