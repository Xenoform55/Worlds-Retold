package xenoscape.worldsretold.heatwave.entity.neutral.cobra;

import java.util.Random;
import javax.annotation.Nullable;

import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.IEntityLivingData;
import net.minecraft.entity.MoverType;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.ai.EntityAIBase;
import net.minecraft.entity.ai.EntityAIHurtByTarget;
import net.minecraft.entity.ai.EntityAILookIdle;
import net.minecraft.entity.ai.EntityAIMoveTowardsRestriction;
import net.minecraft.entity.ai.EntityAIRestrictSun;
import net.minecraft.entity.ai.EntityAISwimming;
import net.minecraft.entity.ai.EntityAIWanderAvoidWater;
import net.minecraft.entity.ai.EntityAIWatchClosest;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.MobEffects;
import net.minecraft.init.SoundEvents;
import net.minecraft.network.datasync.DataParameter;
import net.minecraft.network.datasync.DataSerializers;
import net.minecraft.network.datasync.EntityDataManager;
import net.minecraft.pathfinding.Path;
import net.minecraft.pathfinding.PathNavigate;
import net.minecraft.pathfinding.PathNavigateGround;
import net.minecraft.potion.Potion;
import net.minecraft.potion.PotionEffect;
import net.minecraft.util.DamageSource;
import net.minecraft.util.EnumHand;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.SoundEvent;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import xenoscape.worldsretold.WorldsRetold;
import xenoscape.worldsretold.defaultmod.basic.EntitySurfaceMonster;
import xenoscape.worldsretold.hailstorm.init.HailstormSounds;
import xenoscape.worldsretold.heatwave.entity.IDesertCreature;
import xenoscape.worldsretold.heatwave.init.HeatwavePotions;

public class EntityCobra extends EntitySurfaceMonster implements IDesertCreature {
    private static final DataParameter<Byte> CLIMBING = EntityDataManager.<Byte>createKey(EntityCobra.class, DataSerializers.BYTE);
    protected static final DataParameter<Byte> AGGRESSIVE = EntityDataManager.<Byte>createKey(EntityCobra.class, DataSerializers.BYTE);
    public float rearingRot;
    public float prevRearingRot;

    public EntityCobra(World worldIn) {
        super(worldIn);
        this.setSize(0.4F, 0.2F);
    }

    protected void initEntityAI() {
        this.tasks.addTask(0, new EntityAISwimming(this));
        this.tasks.addTask(1, new EntityCobra.AICobraAttack(this, false));
        this.tasks.addTask(2, new EntityAIRestrictSun(this));
        this.tasks.addTask(3, new EntityCobra.EntityAISeekShelter(this, 1.0D));
        this.tasks.addTask(5, new EntityAIMoveTowardsRestriction(this, 1.0D));
        this.tasks.addTask(7, new EntityAIWanderAvoidWater(this, 1.0D));
        this.tasks.addTask(8, new EntityAIWatchClosest(this, EntityPlayer.class, 8.0F));
        this.tasks.addTask(8, new EntityAILookIdle(this));
        this.targetTasks.addTask(1, new EntityAIHurtByTarget(this, true, new Class[0]));
    }
    
    public boolean isPreventingPlayerRest(EntityPlayer playerIn)
    {
        return this.isAggressive();
    }

    /**
     * Returns the Y offset from the entity's position for any entity riding this one.
     */
    public double getMountedYOffset() {
        return (double) (this.height * 0.5F);
    }

    /**
     * Returns new PathNavigateGround instance
     */
    protected PathNavigate createNavigator(World worldIn) {
        return new PathNavigateGround(this, worldIn);
    }

    protected void entityInit() {
        super.entityInit();
        this.dataManager.register(CLIMBING, Byte.valueOf((byte) 0));
        this.dataManager.register(AGGRESSIVE, Byte.valueOf((byte) 0));
    }

    /**
     * Called to update the entity's position/logic.
     */
    public void onUpdate() {
        super.onUpdate();


        if (this.getAttackTarget() == null && this.world.getClosestPlayerToEntity(this, 6D) != null && this.canEntityBeSeen(this.world.getClosestPlayerToEntity(this, 6D)) && !this.world.getClosestPlayerToEntity(this, 6D).capabilities.disableDamage)
            this.setAttackTarget(this.world.getClosestPlayerToEntity(this, 6D));

        if (this.getAttackTarget() != null && (!this.getAttackTarget().isEntityAlive() || this.getDistance(this.getAttackTarget()) > 12D || !this.canEntityBeSeen(this.getAttackTarget())))
            this.setAttackTarget(null);
        
        if (this.getRevengeTarget() != null && !this.getRevengeTarget().isEntityAlive())
        	this.setRevengeTarget(null);

        this.prevRearingRot = this.rearingRot;

        if (this.isAggressive())
            this.rearingRot = MathHelper.clamp(this.rearingRot + 0.1F, 0F, 1F);
        else 
            this.rearingRot = MathHelper.clamp(this.rearingRot - 0.1F, 0F, 1F);

        this.setSize(0.4F, 0.2F + rearingRot);

        if (!this.world.isRemote) {
            this.setBesideClimbableBlock(this.collidedHorizontally);
        }
    }

    public float getRearingRot(float p_189795_1_) {
        return (this.prevRearingRot + (this.rearingRot - this.prevRearingRot) * p_189795_1_);
    }

    public boolean isAggressive() {
        return ((Byte) this.dataManager.get(AGGRESSIVE)).byteValue() == 1;
    }

    public void setAggressive(boolean value) {
        int i = ((Byte) this.dataManager.get(AGGRESSIVE)).byteValue();

        if (value) {
            i = i | 1;
        } else {
            i = i & ~1;
        }

        this.dataManager.set(AGGRESSIVE, Byte.valueOf((byte) (i & 255)));
    }

    protected void applyEntityAttributes() {
        super.applyEntityAttributes();
        this.getEntityAttribute(SharedMonsterAttributes.MOVEMENT_SPEED).setBaseValue(0.25D);
        this.getEntityAttribute(SharedMonsterAttributes.ATTACK_DAMAGE).setBaseValue(1D);
        this.getEntityAttribute(SharedMonsterAttributes.MAX_HEALTH).setBaseValue(12D);
        this.getEntityAttribute(SharedMonsterAttributes.FOLLOW_RANGE).setBaseValue(12D);
    }

    protected SoundEvent getAmbientSound() {
        return this.isAggressive() ? HailstormSounds.ENTITY_SNAKE_HISS : null;
    }

    protected SoundEvent getHurtSound(DamageSource damageSourceIn) {
        return HailstormSounds.ENTITY_SNAKE_HURT;
    }

    protected SoundEvent getDeathSound() {
        return HailstormSounds.ENTITY_SNAKE_DEATH;
    }

    protected void playStepSound(BlockPos pos, Block blockIn) {
        this.playSound(SoundEvents.ENTITY_SILVERFISH_STEP, 0.2F, 0.9F);
    }

	protected ResourceLocation getLootTable() 
	{
		return new ResourceLocation(WorldsRetold.MODID, "entity/cobra");
	}

    public boolean attackEntityAsMob(Entity entityIn) {
        int i = 20 * 1 + (int)this.world.getDifficultyForLocation(new BlockPos(this)).getAdditionalDifficulty();
        PotionEffect poison = new PotionEffect(HeatwavePotions.VENOM, i * 20, this.world.getDifficulty().getDifficultyId());
        this.playSound(SoundEvents.ENTITY_HOSTILE_HURT, this.getSoundVolume(), this.getSoundPitch() + 0.3F);
        this.playSound(HailstormSounds.ENTITY_SNAKE_STRIKE, this.getSoundVolume(), this.getSoundPitch());
        entityIn.hurtResistantTime = 0;
        if (entityIn instanceof EntityLivingBase && ((EntityLivingBase) entityIn).isPotionApplicable(poison) && super.attackEntityAsMob(entityIn)) {
            if (i > 0) {
                ((EntityLivingBase) entityIn).addPotionEffect(poison);
            }

            return true;
        } else {
            return false;
        }
    }

    /**
     * Returns true if this entity should move as if it were on a ladder (either because it's actually on a ladder, or
     * for AI reasons)
     */
    public boolean isOnLadder() {
        return this.isBesideClimbableBlock();
    }

    public boolean isPotionApplicable(PotionEffect potioneffectIn) {
        return potioneffectIn.getPotion() == MobEffects.POISON || potioneffectIn.getPotion() == HeatwavePotions.VENOM ? false : super.isPotionApplicable(potioneffectIn);
    }

    /**
     * Returns true if the WatchableObject (Byte) is 0x01 otherwise returns false. The WatchableObject is updated using
     * setBesideClimableBlock.
     */
    public boolean isBesideClimbableBlock() {
        return (((Byte) this.dataManager.get(CLIMBING)).byteValue() & 1) != 0;
    }

    /**
     * Updates the WatchableObject (Byte) created in entityInit(), setting it to 0x01 if par1 is true or 0x00 if it is
     * false.
     */
    public void setBesideClimbableBlock(boolean climbing) {
        byte b0 = ((Byte) this.dataManager.get(CLIMBING)).byteValue();

        if (climbing) {
            b0 = (byte) (b0 | 1);
        } else {
            b0 = (byte) (b0 & -2);
        }

        this.dataManager.set(CLIMBING, Byte.valueOf(b0));
    }

    public float getEyeHeight() {
        return 0.1F + this.rearingRot;
    }

    public float getBlockPathWeight(BlockPos pos) {
        return 0.5F - this.world.getLightBrightness(pos);
    }

	public int getSpawnType()
	{
		return 5;
	}

    public int getMaxSpawnedInChunk() 
    {
        return 1;
    }
    
    public void travel(float strafe, float vertical, float forward)
    {
    	if (this.isEntityAlive())
    	{
    		super.travel(strafe, vertical, forward);
    	}
    	else
    	{
            float f6 = 0.91F;
            BlockPos.PooledMutableBlockPos blockpos$pooledmutableblockpos = BlockPos.PooledMutableBlockPos.retain(this.posX, this.getEntityBoundingBox().minY - 1.0D, this.posZ);

            if (this.onGround)
            {
                IBlockState underState = this.world.getBlockState(blockpos$pooledmutableblockpos);
                f6 = underState.getBlock().getSlipperiness(underState, this.world, blockpos$pooledmutableblockpos, this) * 0.91F;
            }

            float f7 = 0.16277136F / (f6 * f6 * f6);
            float f8;

            if (this.onGround)
            {
                f8 = this.getAIMoveSpeed() * f7;
            }
            else
            {
                f8 = this.jumpMovementFactor;
            }

            this.moveRelative(strafe, vertical, forward, f8);
            this.move(MoverType.SELF, this.motionX, this.motionY, this.motionZ);
            if (this.isPotionActive(MobEffects.LEVITATION))
            {
                this.motionY += (0.05D * (double)(this.getActivePotionEffect(MobEffects.LEVITATION).getAmplifier() + 1) - this.motionY) * 0.2D;
            }
            else
            {
                blockpos$pooledmutableblockpos.setPos(this.posX, 0.0D, this.posZ);

                if (!this.world.isRemote || this.world.isBlockLoaded(blockpos$pooledmutableblockpos) && this.world.getChunkFromBlockCoords(blockpos$pooledmutableblockpos).isLoaded())
                {
                    if (!this.hasNoGravity())
                    {
                        this.motionY -= 0.08D;
                    }
                }
                else if (this.posY > 0.0D)
                {
                    this.motionY = -0.1D;
                }
                else
                {
                    this.motionY = 0.0D;
                }
            }

            this.motionY *= 0.9800000190734863D;
            this.motionX *= (double)f6;
            this.motionZ *= (double)f6;
            this.prevLimbSwingAmount = this.limbSwingAmount;
            this.limbSwing = this.limbSwingAmount;
    	}
    }

    public class AICobraAttack extends EntityAIBase {
        World world;
        protected EntityCobra attacker;
        /**
         * An amount of decrementing ticks that allows the entity to attack once the tick reaches 0.
         */
        protected int attackTick;
        /**
         * When true, the mob will continue chasing its target, even if it can't find a path to them right now.
         */
        boolean longMemory;
        /**
         * The PathEntity of our entity.
         */
        Path path;
        private int delayCounter;
        private double targetX;
        private double targetY;
        private double targetZ;
        protected final int attackInterval = 20;
        private int failedPathFindingPenalty = 0;
        private boolean canPenalize = false;

        public AICobraAttack(EntityCobra creature, boolean useLongMemory) {
            this.attacker = creature;
            this.world = creature.world;
            this.longMemory = useLongMemory;
            this.setMutexBits(3);
        }

        /**
         * Returns whether the EntityAIBase should begin execution.
         */
        public boolean shouldExecute() {
            EntityLivingBase entitylivingbase = this.attacker.getAttackTarget();

            if (entitylivingbase == null) {
                return false;
            } else if (!entitylivingbase.isEntityAlive()) {
                return false;
            } else {
                return this.attacker.getDistance(entitylivingbase) <= 12D;
            }
        }

        /**
         * Returns whether an in-progress EntityAIBase should continue executing
         */
        public boolean shouldContinueExecuting() {
            EntityLivingBase entitylivingbase = this.attacker.getAttackTarget();

            if (entitylivingbase == null) {
                return false;
            } else if (!entitylivingbase.isEntityAlive()) {
                return false;
            } else {
                return this.attacker.getDistance(entitylivingbase) <= 12D;
            }
        }

        /**
         * Execute a one shot task or start executing a continuous task
         */
        public void startExecuting() {
            this.delayCounter = 0;
            this.attacker.setAggressive(true);
        }

        /**
         * Reset the task's internal state. Called when this task is interrupted by another one
         */
        public void resetTask() {
            EntityLivingBase entitylivingbase = this.attacker.getAttackTarget();

            if (entitylivingbase instanceof EntityPlayer && (((EntityPlayer) entitylivingbase).isSpectator() || ((EntityPlayer) entitylivingbase).isCreative()))
                this.attacker.setAttackTarget((EntityLivingBase) null);

            this.attacker.setAggressive(false);
        }

        /**
         * Keep ticking a continuous task that has already been started
         */
        public void updateTask() {
            EntityLivingBase entitylivingbase = this.attacker.getAttackTarget();
            this.attacker.getLookHelper().setLookPositionWithEntity(entitylivingbase, 180.0F, 30.0F);
            double d0 = this.attacker.getDistanceSq(entitylivingbase.posX, entitylivingbase.getEntityBoundingBox().minY, entitylivingbase.posZ);
            --this.delayCounter;
            this.attacker.getNavigator().clearPath();
            this.attackTick = Math.max(this.attackTick - 1, 0);
            this.checkAndPerformAttack(entitylivingbase, d0);
        }

        protected void checkAndPerformAttack(EntityLivingBase p_190102_1_, double p_190102_2_) {
            double d0 = this.getAttackReachSqr(p_190102_1_);

            if (p_190102_2_ <= d0 && this.attackTick <= 0) {
                this.attackTick = 10;
                this.attacker.swingArm(EnumHand.MAIN_HAND);
                this.attacker.attackEntityAsMob(p_190102_1_);
                this.attacker.renderYawOffset = this.attacker.rotationYaw = this.attacker.rotationYawHead;
            }
        }

        protected double getAttackReachSqr(EntityLivingBase attackTarget) {
            return (double) (this.attacker.width * 5F * this.attacker.width * 5F + attackTarget.width);
        }
    }

    public class EntityAISeekShelter extends EntityAIBase {
        private final EntityCobra creature;
        private double shelterX;
        private double shelterY;
        private double shelterZ;
        private final double movementSpeed;
        private final World world;

        public EntityAISeekShelter(EntityCobra theCreatureIn, double movementSpeedIn) {
            this.creature = theCreatureIn;
            this.movementSpeed = movementSpeedIn;
            this.world = theCreatureIn.world;
            this.setMutexBits(1);
        }

        /**
         * Returns whether the EntityAIBase should begin execution.
         */
        public boolean shouldExecute() {
            if (!this.world.isDaytime()) {
                return false;
            } else if (!this.world.canSeeSky(new BlockPos(this.creature.posX, this.creature.getEntityBoundingBox().minY, this.creature.posZ))) {
                return false;
            } else {
                Vec3d vec3d = this.findPossibleShelter();

                if (vec3d == null) {
                    return false;
                } else {
                    this.shelterX = vec3d.x;
                    this.shelterY = vec3d.y;
                    this.shelterZ = vec3d.z;
                    return true;
                }
            }
        }

        /**
         * Returns whether an in-progress EntityAIBase should continue executing
         */
        public boolean shouldContinueExecuting() {
            return !this.creature.getNavigator().noPath();
        }

        /**
         * Execute a one shot task or start executing a continuous task
         */
        public void startExecuting() {
            this.creature.createNavigator(world);
            if (!this.world.canSeeSky(new BlockPos(this.shelterX, this.shelterY, this.shelterZ)))
                this.creature.getNavigator().tryMoveToXYZ(this.shelterX, this.shelterY, this.shelterZ, this.movementSpeed);
            else
                this.creature.getNavigator().clearPath();
        }

        @Nullable
        private Vec3d findPossibleShelter() {
            Random random = this.creature.getRNG();
            BlockPos blockpos = new BlockPos(this.creature.posX, this.creature.getEntityBoundingBox().minY, this.creature.posZ);

            for (int i = 0; i < 10; ++i) {
                BlockPos blockpos1 = blockpos.add(random.nextInt(40) - 20, random.nextInt(10) - 5, random.nextInt(40) - 20);

                if (!this.world.canSeeSky(blockpos1) && this.creature.getBlockPathWeight(blockpos1) < 0.0F) {
                    return new Vec3d((double) blockpos1.getX(), (double) blockpos1.getY(), (double) blockpos1.getZ());
                }
            }

            return null;
        }
    }

    public static class GroupData implements IEntityLivingData {
        public Potion effect;

        public void setRandomEffect(Random rand) {
            int i = rand.nextInt(5);

            if (i <= 1) {
                this.effect = MobEffects.SPEED;
            } else if (i <= 2) {
                this.effect = MobEffects.STRENGTH;
            } else if (i <= 3) {
                this.effect = MobEffects.REGENERATION;
            } else if (i <= 4) {
                this.effect = MobEffects.INVISIBILITY;
            }
        }
    }
}