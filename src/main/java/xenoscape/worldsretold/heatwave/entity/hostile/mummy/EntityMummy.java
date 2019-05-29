package xenoscape.worldsretold.heatwave.entity.hostile.mummy;

import javax.annotation.Nullable;

import net.minecraft.block.Block;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityCreature;
import net.minecraft.entity.EntityList;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.IRangedAttackMob;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.ai.EntityAIAttackRanged;
import net.minecraft.entity.ai.EntityAIAttackRangedBow;
import net.minecraft.entity.ai.EntityAIAvoidEntity;
import net.minecraft.entity.ai.EntityAILookIdle;
import net.minecraft.entity.ai.EntityAIMoveTowardsRestriction;
import net.minecraft.entity.ai.EntityAINearestAttackableTarget;
import net.minecraft.entity.ai.EntityAISwimming;
import net.minecraft.entity.ai.EntityAIWanderAvoidWater;
import net.minecraft.entity.ai.EntityAIWatchClosest;
import net.minecraft.entity.ai.EntityAIZombieAttack;
import net.minecraft.entity.monster.AbstractSkeleton;
import net.minecraft.entity.monster.EntityEnderman;
import net.minecraft.entity.monster.EntityMob;
import net.minecraft.entity.monster.EntityZombie;
import net.minecraft.entity.monster.EntityZombieVillager;
import net.minecraft.entity.passive.EntityOcelot;
import net.minecraft.entity.passive.EntityVillager;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.projectile.EntityArrow;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.init.MobEffects;
import net.minecraft.init.SoundEvents;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.datasync.DataParameter;
import net.minecraft.network.datasync.DataSerializers;
import net.minecraft.network.datasync.EntityDataManager;
import net.minecraft.pathfinding.PathNavigateGround;
import net.minecraft.potion.PotionEffect;
import net.minecraft.util.DamageSource;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.SoundEvent;
import net.minecraft.util.datafix.DataFixer;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.EnumDifficulty;
import net.minecraft.world.World;
import net.minecraft.world.storage.loot.LootTableList;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import xenoscape.worldsretold.WorldsRetold;
import xenoscape.worldsretold.hailstorm.init.HailstormSounds;
import xenoscape.worldsretold.heatwave.entity.IDesertCreature;
import xenoscape.worldsretold.heatwave.entity.neutral.cobra.EntityCobra;
import xenoscape.worldsretold.heatwave.init.HeatwavePotions;

public class EntityMummy extends EntityZombie implements IDesertCreature
{
    protected static final DataParameter<Byte> HIDDEN = EntityDataManager.<Byte>createKey(EntityMummy.class, DataSerializers.BYTE);
    public float risingTime;
    public float prevRisingTime;
	
    public EntityMummy(World worldIn)
    {
        super(worldIn);
        this.experienceValue = 10;
        this.setSize(0.5F, 1.8F);
    }
    
    protected void applyEntityAttributes()
    {
        super.applyEntityAttributes();
        this.getEntityAttribute(SharedMonsterAttributes.MAX_HEALTH).setBaseValue(40D);
        this.getEntityAttribute(SharedMonsterAttributes.ATTACK_DAMAGE).setBaseValue(7.0D);
        this.getEntityAttribute(SharedMonsterAttributes.ARMOR).setBaseValue(4.0D);
        this.getEntityAttribute(SPAWN_REINFORCEMENTS_CHANCE).setBaseValue(0D);
    }
    
    protected void initEntityAI()
    {
        this.tasks.addTask(0, new EntityAISwimming(this));
        this.tasks.addTask(1, new EntityAIAvoidEntity(this, EntityOcelot.class, 8.0F, 1.2D, 1.5D));
        this.tasks.addTask(3, new EntityAIZombieAttack(this, 1.0D, false));
        this.tasks.addTask(5, new EntityAIMoveTowardsRestriction(this, 1.0D));
        this.tasks.addTask(7, new EntityAIWanderAvoidWater(this, 1.0D));
        this.tasks.addTask(8, new EntityAIWatchClosest(this, EntityPlayer.class, 8.0F));
        this.tasks.addTask(8, new EntityAILookIdle(this));
        this.applyEntityAI();
    }
    

    protected void entityInit() 
    {
        super.entityInit();
        this.dataManager.register(HIDDEN, Byte.valueOf((byte) 0));
    }
    
    public void onLivingUpdate()
    {
    	super.onLivingUpdate();
    	
    	Entity mob = EntityList.createEntityByIDFromName(new ResourceLocation("worldsretold", "hellhound"), world);
    	
    	this.getEntityAttribute(SharedMonsterAttributes.KNOCKBACK_RESISTANCE).setBaseValue(this.isHidden() ? 1D : 0.05D);
    	
    	if (this.isHidden())
    	{
    		this.motionX *= 0.25D;
    		this.motionZ *= 0.25D;
    		if (this.motionY > 0D)
        		this.motionY = 0D;
    	}
    	
    	if (!this.world.isRemote && !this.isHidden() && this.getAttackTarget() == null && this.world.getBlockState(this.getPosition().down()).getMaterial().isSolid() && rand.nextInt(this.world.isDaytime() && this.world.canSeeSky(new BlockPos(this.posX, this.posY + (double)this.getEyeHeight(), this.posZ)) ? 5 : 500) == 0 && this.ticksExisted % 20 == 0)
    		this.setHidden(true);
    	
    	if (!this.world.isRemote && this.isHidden() && (this.getAttackTarget() != null || this.getRevengeTarget() != null || this.hurtResistantTime > 0 || !this.world.getBlockState(this.getPosition().down()).getMaterial().isSolid()))
    	{
    		this.setHidden(false);
    		this.playSound(HailstormSounds.ENTITY_MUMMY_INFECT, 3F, this.isChild() ? 1.5F : 1F);
    	}
    	
        this.height = (1.95F - ((this.prevRisingTime + (this.risingTime - this.prevRisingTime)) * 1.75F)) * (this.isChild() ? 0.5F : 1F);
        double d0 = (double)width / 2.0D;
        this.setEntityBoundingBox(new AxisAlignedBB(this.posX - d0, this.posY, this.posZ - d0, this.posX + d0, this.posY + (double)this.height, this.posZ + d0));
        
        this.prevRisingTime = this.risingTime;

        if (this.isHidden())
            this.risingTime = MathHelper.clamp(this.risingTime + 0.025F, 0F, 1F);
        else 
            this.risingTime = MathHelper.clamp(this.risingTime - 0.025F, 0F, 1F);
        
        if (this.risingTime > 0.01F && this.risingTime < 0.99F)
        {
        	++this.limbSwingAmount;
        	this.rotationPitch = (this.risingTime * 60F);
        }
        
        if (this.risingTime > 0.25F && this.risingTime < 0.99F)
        	this.world.playEvent(2001, this.getPosition().down(), Block.getStateId(this.world.getBlockState(this.getPosition().down())));
        
        this.setInvisible(this.risingTime == 1F);
    }
    
    public float getRisingRot(float p_189795_1_) 
    {
        return (this.prevRisingTime + (this.risingTime - this.prevRisingTime) * p_189795_1_);
    }
    
    /**
     * (abstract) Protected helper method to write subclass entity data to NBT.
     */
    public void writeEntityToNBT(NBTTagCompound compound)
    {
        super.writeEntityToNBT(compound);
        compound.setBoolean("Hidden", this.isHidden());
    }

    /**
     * (abstract) Protected helper method to read subclass entity data from NBT.
     */
    public void readEntityFromNBT(NBTTagCompound compound)
    {
        super.readEntityFromNBT(compound);
        this.setHidden(compound.getBoolean("Hidden"));
    }
    
    public boolean isHidden() 
    {
        return ((Byte) this.dataManager.get(HIDDEN)).byteValue() == 1;
    }

    public void setHidden(boolean value) 
    {
        int i = ((Byte) this.dataManager.get(HIDDEN)).byteValue();

        if (value) 
        {
            i = i | 1;
        } else 
        {
            i = i & ~1;
        }

        this.dataManager.set(HIDDEN, Byte.valueOf((byte) (i & 255)));
    }

    protected boolean shouldBurnInDay()
    {
        return false;
    }

    protected SoundEvent getAmbientSound()
    {
        return this.isHidden() ? null : HailstormSounds.ENTITY_MUMMY_AMBIENT;
    }

    protected SoundEvent getHurtSound(DamageSource damageSourceIn)
    {
        return HailstormSounds.ENTITY_MUMMY_HURT;
    }

    protected SoundEvent getDeathSound()
    {
        return HailstormSounds.ENTITY_MUMMY_DEATH;
    }

    protected SoundEvent getStepSound()
    {
        return this.isHidden() ? null : SoundEvents.BLOCK_CLOTH_BREAK;
    }

	protected ResourceLocation getLootTable() 
	{
		return new ResourceLocation(WorldsRetold.MODID, "entity/mummy");
	}

    public boolean attackEntityAsMob(Entity entityIn)
    {
        boolean flag = super.attackEntityAsMob(entityIn);

        if (flag && this.getHeldItemMainhand().isEmpty() && entityIn instanceof EntityLivingBase)
        {
            float f = this.world.getDifficultyForLocation(new BlockPos(this)).getAdditionalDifficulty();
            ((EntityLivingBase)entityIn).addPotionEffect(new PotionEffect(MobEffects.HUNGER, 240 * (int)f));
            if (f >= 1F)
            	((EntityLivingBase)entityIn).addPotionEffect(new PotionEffect(MobEffects.WEAKNESS, 160 * (int)f));
            if (f >= 1.5F)
            	((EntityLivingBase)entityIn).addPotionEffect(new PotionEffect(MobEffects.BLINDNESS, 80 * (int)f));
            if (f >= 2F)
                ((EntityLivingBase)entityIn).addPotionEffect(new PotionEffect(MobEffects.WITHER, 80 * (int)f));
        }

        return flag;
    }

    protected ItemStack getSkullDrop()
    {
        return ItemStack.EMPTY;
    }

    public boolean getCanSpawnHere() {
        return this.world.provider.getDimension() == 0
                && super.getCanSpawnHere();
    }
    
    public float getEyeHeight()
    {
        float f = 1.725F - ((this.prevRisingTime + (this.risingTime - this.prevRisingTime)) * 1.5F);

        if (this.isChild())
        {
            f = (float)((double)f - 0.81D);
        }

        if (f <= 0.15F)
        	f = 0.15F;
        
        return f;
    }

	public void setSwingingArms(boolean swingingArms) {}
}