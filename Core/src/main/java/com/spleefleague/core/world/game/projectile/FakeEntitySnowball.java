package com.spleefleague.core.world.game.projectile;

import com.comphenix.protocol.wrappers.BlockPosition;
import com.spleefleague.core.Core;
import com.spleefleague.core.menu.InventoryMenuUtils;
import com.spleefleague.core.player.BattleState;
import com.spleefleague.core.player.CorePlayer;
import com.spleefleague.core.util.CoreUtils;
import com.spleefleague.core.util.variable.BlockRaycastResult;
import com.spleefleague.core.util.variable.EntityRaycastResult;
import com.spleefleague.core.util.variable.Point;
import com.spleefleague.core.util.variable.RaycastResult;
import com.spleefleague.core.world.FakeBlock;
import com.spleefleague.core.world.game.GameWorld;
import com.spleefleague.core.world.game.GameWorldPlayer;
import com.spleefleague.core.world.game.PortalPair;
import net.minecraft.server.v1_15_R1.EntitySnowball;
import net.minecraft.server.v1_15_R1.EntityTypes;
import net.minecraft.server.v1_15_R1.MovingObjectPosition;
import net.minecraft.server.v1_15_R1.Vec3D;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.craftbukkit.v1_15_R1.CraftWorld;
import org.bukkit.craftbukkit.v1_15_R1.entity.CraftEntity;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.entity.Snowball;
import org.bukkit.util.Vector;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Random;

/**
 * @author NickM13
 * @since 5/19/2020
 */
public class FakeEntitySnowball extends EntitySnowball {

    protected final GameWorld gameWorld;
    protected final ProjectileStats projectileStats;
    protected Point lastLoc;
    protected int bounces;
    protected final CorePlayer cpShooter;
    protected Entity lastHit = null;
    protected BlockPosition stuck = null;
    protected int lifeTicks;
    private BlockPosition lastBlock = null;
    private final Vector size;
    private double charge;
    private int breakAfter;

    public FakeEntitySnowball(GameWorld gameWorld, CorePlayer shooter, Location location, ProjectileStats projectileStats) {
        this(gameWorld, shooter, location, projectileStats, 1.);
    }

    public FakeEntitySnowball(GameWorld gameWorld, CorePlayer shooter, Location location, ProjectileStats projectileStats, Double charge) {
        super(EntityTypes.SNOWBALL, ((CraftWorld) gameWorld.getWorld()).getHandle());
        this.cpShooter = shooter;
        this.charge = charge;

        this.gameWorld = gameWorld;
        this.projectileStats = projectileStats;
        this.size = new Vector(this.projectileStats.size, this.projectileStats.size, this.projectileStats.size);

        if (projectileStats.customModelDatas.isEmpty()) {
            ((Snowball) getBukkitEntity()).setItem(InventoryMenuUtils.createCustomItem(Material.SNOWBALL, projectileStats.customModelData));
        } else {
            ((Snowball) getBukkitEntity()).setItem(InventoryMenuUtils.createCustomItem(Material.SNOWBALL, projectileStats.customModelDatas.get(new Random().nextInt(projectileStats.customModelDatas.size()))));
        }

        Location handLocation = location.clone()
                .add(location.getDirection()
                        .crossProduct(new Vector(0, 1, 0)).normalize()
                        .multiply(0.15).add(new Vector(0, -0.15, 0)));
        setPositionRotation(handLocation.getX(), handLocation.getY(), handLocation.getZ(), pitch, yaw);

        Random rand = new Random();
        Location lookLoc = location.clone();
        Vector lookDir;
        if (projectileStats.shape == ProjectileStats.Shape.DEFAULT ||
                projectileStats.shape == ProjectileStats.Shape.CONE) {
            if (projectileStats.vSpread > 0) {
                lookLoc.setPitch(lookLoc.getPitch() + rand.nextInt(projectileStats.vSpread) - (projectileStats.vSpread / 2.f));
            }
            if (projectileStats.hSpread > 0) {
                Location temp = lookLoc.clone();
                temp.setPitch(lookLoc.getPitch() + 90);
                lookDir = lookLoc.getDirection().rotateAroundNonUnitAxis(temp.getDirection(), Math.toRadians(rand.nextInt(projectileStats.hSpread) - (projectileStats.hSpread / 2.f)));
            } else {
                lookDir = lookLoc.getDirection();
            }
        } else {
            lookDir = lookLoc.getDirection();
        }
        Vector direction = lookDir.normalize().multiply(projectileStats.fireRange * 0.25 * charge);
        setMot(new Vec3D(direction.getX(), direction.getY(), direction.getZ()));

        lastLoc = new Point(getPositionVector());

        setNoGravity(!projectileStats.gravity);
        this.bounces = projectileStats.bounces;
        this.noclip = projectileStats.noClip;
        this.lifeTicks = projectileStats.lifeTicks;
        this.breakAfter = projectileStats.breakAfterBounces;
    }

    public CorePlayer getCpShooter() {
        return cpShooter;
    }

    public ProjectileStats getStats() {
        return projectileStats;
    }

    public void reducedStats(FakeEntitySnowball entity) {
        this.lifeTicks = entity.getRemainingLife();
        this.bounces = entity.getRemainingBounces();
        this.breakAfter = entity.getRemainingBreakAfter();
    }

    public int getRemainingLife() {
        return lifeTicks;
    }

    public int getRemainingBounces() {
        return bounces;
    }

    public int getRemainingBreakAfter() {
        return breakAfter;
    }

    protected void setStuck(BlockPosition pos) {
        stuck = pos;
        if (stuck != null) {
            setNoGravity(true);
            getBukkitEntity().setVelocity(new Vector(0, 0, 0));
        } else {
            setNoGravity(false);
        }
    }

    @Override
    public void tick() {
        super.tick();

        CraftEntity craftEntity = getBukkitEntity();
        if (craftEntity.getTicksLived() > lifeTicks) {
            killEntity();
            return;
        }
        List<RaycastResult> results = new ArrayList<>();
        if (lastLoc != null) {
            Vector pos = new Vector(getPositionVector().getX(), getPositionVector().getY(), getPositionVector().getZ());
            Vector direction = pos.subtract(lastLoc.toVector());
            if (stuck != null) {
                FakeBlock fb = gameWorld.getFakeBlocks().get(stuck);
                if ((fb == null || fb.getBlockData().getMaterial().isAir()) &&
                        gameWorld.getWorld().getBlockAt(stuck.getX(), stuck.getY(), stuck.getZ()).getType().isAir()) {
                    setStuck(null);
                }
            }
            if (projectileStats.collidable) {
                List<Entity> entities = new ArrayList<>();
                for (GameWorldPlayer gwp : gameWorld.getPlayerMap().values()) {
                    if (gwp.getCorePlayer().getBattleState() == BattleState.BATTLER &&
                            (!gwp.getCorePlayer().equals(cpShooter) || craftEntity.getTicksLived() > 10) &&
                            gwp.hit()) {
                        entities.add(gwp.getPlayer());
                    }
                }
                if (stuck == null) {
                    results.addAll(lastLoc.cast(direction, size, direction.length(), entities));
                } else {
                    results.addAll(lastLoc.castEntities(direction, size, direction.length(), entities));
                }
            } else {
                results.addAll(lastLoc.castBlocks(direction, direction.length()));
            }
        }
        craftEntity.setVelocity(craftEntity.getVelocity().multiply(projectileStats.drag));
        Map<BlockPosition, FakeBlock> fakeBlocks = gameWorld.getFakeBlocks();
        for (RaycastResult result : results) {
            if (result instanceof BlockRaycastResult) {
                BlockRaycastResult blockResult = (BlockRaycastResult) result;
                FakeBlock fb = fakeBlocks.get(blockResult.getBlockPos());
                Material mat;
                if (!blockResult.getBlockPos().equals(lastBlock)) {
                    blockChange(craftEntity, blockResult);
                    if (checkPortals(craftEntity, blockResult)) {
                        break;
                    }
                }
                if (fb != null) {
                    mat = fb.getBlockData().getMaterial();
                } else {
                    mat = gameWorld.getWorld().getBlockAt(
                            blockResult.getBlockPos().getX(),
                            blockResult.getBlockPos().getY(),
                            blockResult.getBlockPos().getZ()).getType();
                }
                if (!mat.isAir()) {
                    if (onBlockHit(craftEntity, blockResult)) {
                        break;
                    }
                }
            } else if (result instanceof EntityRaycastResult) {
                EntityRaycastResult entityResult = (EntityRaycastResult) result;
                Entity hitEntity = entityResult.getEntity();
                if (!hitEntity.equals(lastHit)) {
                    onEntityHit(craftEntity, entityResult);
                    lastHit = hitEntity;
                    break;
                }
            }
        }

        lastLoc = new Point(getPositionVector());
    }

    @Override
    protected void a(MovingObjectPosition var0) {
        if (!noclip) {
            super.a(var0);
        }
    }

    protected void blockChange(Entity craftEntity, BlockRaycastResult blockRaycastResult) {

    }

    public boolean checkPortals(Entity craftEntity, BlockRaycastResult blockRaycastResult) {
        for (PortalPair pair : gameWorld.getPlayerPortals().values()) {
            if (pair.isLinked()) {
                if (pair.getPortal1().getBlockPos().equals(blockRaycastResult.getBlockPos()) && pair.getPortal1().getBlockFace().equals(blockRaycastResult.getFace())) {
                    gameWorld.portalize(this, pair.getPortal2().getTpLoc(), pair.getNewVelocityTo2(craftEntity));
                    return true;
                } else if (pair.getPortal2().getBlockPos().equals(blockRaycastResult.getBlockPos()) && pair.getPortal2().getBlockFace().equals(blockRaycastResult.getFace())) {
                    gameWorld.portalize(this, pair.getPortal1().getTpLoc(), pair.getNewVelocityTo1(craftEntity));
                    return true;
                }
            }
        }
        return false;
    }

    protected boolean blockBounce(Entity craftEntity, BlockRaycastResult blockRaycastResult) {
        if (projectileStats.bounciness > 0) {
            switch (blockRaycastResult.getAxis()) {
                case 1:
                    craftEntity.setVelocity(new Vector(
                            getMot().getX() * -projectileStats.bounciness,
                            getMot().getY() * projectileStats.bounciness,
                            getMot().getZ() * projectileStats.bounciness));
                    craftEntity.teleport(new Location(gameWorld.getWorld(),
                            blockRaycastResult.getIntersection().getX() + (craftEntity.getVelocity().getX() < 0 ? -0.01 : 0.01),
                            blockRaycastResult.getIntersection().getY(),
                            blockRaycastResult.getIntersection().getZ()));
                    break;
                case 2:
                    craftEntity.setVelocity(new Vector(
                            getMot().getX() * projectileStats.bounciness,
                            getMot().getY() * -projectileStats.bounciness,
                            getMot().getZ() * projectileStats.bounciness));
                    if (craftEntity.getVelocity().getY() >= 0 && craftEntity.getVelocity().getY() < 0.03 && craftEntity.getVelocity().setY(0).length() < 0.005) {
                        setStuck(blockRaycastResult.getBlockPos());
                        craftEntity.teleport(new Location(gameWorld.getWorld(),
                                blockRaycastResult.getIntersection().getX(),
                                blockRaycastResult.getIntersection().getY(),
                                blockRaycastResult.getIntersection().getZ()));
                    } else {
                        craftEntity.teleport(new Location(gameWorld.getWorld(),
                                blockRaycastResult.getIntersection().getX(),
                                blockRaycastResult.getIntersection().getY() + (getMot().getY() < 0 ? -0.01 : 0.01),
                                blockRaycastResult.getIntersection().getZ()));
                    }
                    break;
                case 3:
                    craftEntity.setVelocity(new Vector(
                            getMot().getX() * projectileStats.bounciness,
                            getMot().getY() * projectileStats.bounciness,
                            getMot().getZ() * -projectileStats.bounciness));
                    craftEntity.teleport(new Location(gameWorld.getWorld(),
                            blockRaycastResult.getIntersection().getX(),
                            blockRaycastResult.getIntersection().getY(),
                            blockRaycastResult.getIntersection().getZ() + (craftEntity.getVelocity().getZ() < 0 ? -0.01 : 0.01)));
                    break;
                default:
                    craftEntity.teleport(new Location(gameWorld.getWorld(),
                            blockRaycastResult.getIntersection().getX(),
                            blockRaycastResult.getIntersection().getY(),
                            blockRaycastResult.getIntersection().getZ()));
                    craftEntity.setVelocity(craftEntity.getVelocity().multiply(-projectileStats.bounciness));
                    break;
            }
            return true;
        } else {
            craftEntity.setVelocity(craftEntity.getVelocity().multiply(-projectileStats.bounciness));
            if (projectileStats.bounciness < 0.0001 ||
                    projectileStats.bounciness > -0.0001) {
                craftEntity.teleport(new Location(gameWorld.getWorld(),
                        blockRaycastResult.getIntersection().getX(),
                        blockRaycastResult.getIntersection().getY(),
                        blockRaycastResult.getIntersection().getZ()));
                setStuck(blockRaycastResult.getBlockPos());
            }
            return false;
        }
    }

    /**
     * Called when the entity collides with a solid block
     *
     * @param craftEntity Self Craft Entity
     * @param blockRaycastResult Raycast Result
     * @return Should ignore next raycast results
     */
    protected boolean onBlockHit(Entity craftEntity, BlockRaycastResult blockRaycastResult) {
        if (breakAfter <= 0) {
            switch (projectileStats.breakStyle) {
                case DEFAULT:
                    cpShooter.getStatistics().get("splegg").add("blocksBroken", gameWorld.breakBlocks(blockRaycastResult.getBlockPos(), projectileStats.breakRadius, projectileStats.breakPercent));
                    break;
                case CORROSIVE:
                    cpShooter.getStatistics().get("splegg").add("blocksCorroded", gameWorld.corrodeBlocks(blockRaycastResult.getBlockPos(), projectileStats.breakRadius, projectileStats.breakPercent));
                    break;
                case FIRE:
                    cpShooter.getStatistics().get("splegg").add("blocksBurnt", gameWorld.burnBlocks(blockRaycastResult.getBlockPos(), projectileStats.breakRadius, projectileStats.breakPercent));
                    break;
                case ICE:
                    cpShooter.getStatistics().get("splegg").add("blocksIced", gameWorld.iceBlocks(blockRaycastResult.getBlockPos(), projectileStats.breakRadius, projectileStats.breakPercent));
                    break;
                case REGENERATE:
                    cpShooter.getStatistics().get("splegg").add("blocksEndered", gameWorld.breakRegenBlocks(blockRaycastResult.getBlockPos(), projectileStats.breakRadius, projectileStats.breakPercent));
                    break;
            }
        } else {
            breakAfter--;
        }

        bounces--;
        if (bounces < 0) {
            killEntity();
            return true;
        } else {
            return blockBounce(craftEntity, blockRaycastResult);
        }
    }

    protected void entityBounce(Entity craftEntity, EntityRaycastResult entityRaycastResult) {
        craftEntity.teleport(new Location(gameWorld.getWorld(),
                entityRaycastResult.getIntersection().getX(),
                entityRaycastResult.getIntersection().getY(),
                entityRaycastResult.getIntersection().getZ()));
        craftEntity.setVelocity(craftEntity.getVelocity().multiply(-projectileStats.bounciness));
    }

    protected void onEntityHit(Entity craftEntity, EntityRaycastResult entityRaycastResult) {
        bounces--;
        if (projectileStats.hitKnockback > 0) {
            CoreUtils.knockbackEntity(entityRaycastResult.getEntity(), craftEntity.getVelocity(), projectileStats.hitKnockback);
        }
        if (bounces < 0) {
            killEntity();
        } else {
            entityBounce(craftEntity, entityRaycastResult);
        }
        if (entityRaycastResult.getEntity() instanceof Player) {
            CorePlayer target = Core.getInstance().getPlayers().get(entityRaycastResult.getEntity().getUniqueId());
            if (target.isInBattle() && target.getBattleState() == BattleState.BATTLER) {
                target.getBattle().onPlayerHit(cpShooter, target);
            }
        }
    }

    @Override
    public void killEntity() {
        super.killEntity();
    }

    /**
     * Make entity never underwater to not slow down
     *
     * @return
     */
    @Override
    public boolean aC() {
        if (projectileStats.noClip) {
            this.inWater = false;
            return false;
        } else {
            return super.aC();
        }
    }
    /*
    @Override
    public boolean aF() {
        if (projectileStats.noClip) {
            this.inWater = false;
            return false;
        } else {
            return super.aF();
        }
    }
     */

}
