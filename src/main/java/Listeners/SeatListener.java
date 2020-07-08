package Listeners;

import me.shakeforprotein.shakeseats.ShakeSeats;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.block.data.Bisected;
import org.bukkit.block.data.type.Slab;
import org.bukkit.block.data.type.Stairs;
import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.spigotmc.event.entity.EntityDismountEvent;

import java.util.UUID;

public class SeatListener implements Listener {

    private ShakeSeats pl;

    public SeatListener(ShakeSeats main) {
        this.pl = main;
    }

    @EventHandler
    public void PlayerRightClickBlock(PlayerInteractEvent e) {
        if (e.getAction() == Action.RIGHT_CLICK_BLOCK) {
            if (!pl.noSit.contains(e.getPlayer().getName())) {
                if ((e.getItem() == null || e.getItem().getType() == Material.AIR) && pl.suitableBlocks.contains(e.getClickedBlock().getType())) {
                    ArmorStand armorStand = null;
                    Block block = e.getClickedBlock();
                    Block aboveBlock = e.getClickedBlock().getRelative(0,1,0);
                    boolean canSit = false;
                    int lightLevel = aboveBlock.getLightLevel();
                    if(lightLevel > 0
                            && !aboveBlock.getType().name().toLowerCase().contains("stairs")
                            && !aboveBlock.getType().name().toLowerCase().contains("slab")
                            && !aboveBlock.getType().name().toLowerCase().contains("chest")
                            && !aboveBlock.getType().name().toLowerCase().contains("wall")
                            && !aboveBlock.getType().name().toLowerCase().contains("fence")
                            && !aboveBlock.getType().name().toLowerCase().contains("table")
                            && !aboveBlock.getType().name().toLowerCase().contains("box")
                            && !aboveBlock.getType().name().toLowerCase().contains("composter")
                            && !aboveBlock.getType().name().toLowerCase().contains("grindstone")
                            && !aboveBlock.getType().name().toLowerCase().contains("bell")
                            && !aboveBlock.getType().name().toLowerCase().contains("pane"))
                    {canSit = true;}

                    if(canSit) {
                        if (e.getClickedBlock().getType().name().toLowerCase().contains("slab")) {
                            if (block.getWorld().getBlockAt(block.getLocation().add(0, 1, 0)).getType() == Material.AIR) {
                                Slab blockState = (Slab) block.getState().getBlockData();
                                if (blockState.getType().equals(Slab.Type.TOP) || blockState.getType().equals(Slab.Type.DOUBLE)) {
                                    armorStand = (ArmorStand) e.getPlayer().getWorld().spawnEntity(e.getClickedBlock().getLocation().subtract(-0.5, 0.7, -0.5), EntityType.ARMOR_STAND);
                                } else {
                                    armorStand = (ArmorStand) e.getPlayer().getWorld().spawnEntity(e.getClickedBlock().getLocation().subtract(-0.5, 1.2, -0.5), EntityType.ARMOR_STAND);
                                }
                            }
                        } else if (e.getClickedBlock().getType().name().toLowerCase().contains("stairs")) {
                            if (block.getWorld().getBlockAt(block.getLocation().add(0, 1, 0)).getType() == Material.AIR) {
                                if (((Stairs) block.getBlockData()).getHalf() != Bisected.Half.TOP) {
                                    armorStand = (ArmorStand) e.getPlayer().getWorld().spawnEntity(e.getClickedBlock().getLocation().subtract(-0.5, 1.2, -0.5), EntityType.ARMOR_STAND);
                                }
                            }
                        } else if (e.getClickedBlock().getType().name().toLowerCase().contains("carpet")) {
                            armorStand = (ArmorStand) e.getPlayer().getWorld().spawnEntity(e.getClickedBlock().getLocation().subtract(-0.5, 1.7, -0.5), EntityType.ARMOR_STAND);
                        } else if(e.getClickedBlock().getType().name().toLowerCase().contains("campfire")){
                            armorStand = (ArmorStand) e.getPlayer().getWorld().spawnEntity(e.getClickedBlock().getLocation().subtract(-0.5, 1.2, -0.5), EntityType.ARMOR_STAND);
                        }

                        if (armorStand != null) {
                            armorStand.setVisible(false);
                            armorStand.setCollidable(false);
                            armorStand.addPassenger(e.getPlayer());
                            armorStand.setBasePlate(false);
                            armorStand.setGravity(false);
                            armorStand.setGlowing(false);
                            pl.standHash.put(armorStand, e.getPlayer());
                        }
                    }
                }
            }
        }


    }

    @EventHandler
    public void PlayerQuit(PlayerQuitEvent e) {
        UUID uuid = null;
        if(!pl.standHash.isEmpty()) {
            for (ArmorStand as : pl.standHash.keySet()) {
                if (as.getPassengers().get(0).equals(e.getPlayer())) {
                    uuid = as.getUniqueId();
                }
            }
            if (uuid != null) {
                pl.standHash.remove(Bukkit.getEntity(uuid));
                Bukkit.getEntity(uuid).remove();
            }
        }
    }

    @EventHandler
    public void PlayerDismountArmorStand(EntityDismountEvent e) {
        if (e.getDismounted() instanceof ArmorStand && e.getEntity() instanceof Player && pl.standHash.containsKey((ArmorStand) e.getDismounted()) && pl.standHash.get((ArmorStand) e.getDismounted()) != null && pl.standHash.get((ArmorStand) e.getDismounted()) == e.getEntity()) {
            e.getDismounted().remove();
        }
    }

    @EventHandler
    public void PlayerDamagedEvent(EntityDamageEvent e) {
        if (e.getEntity() instanceof Player) {
            Player p = (Player) e.getEntity();
            if(!pl.standHash.isEmpty()) {
                if (pl.standHash.values() != null && pl.standHash.values().contains(p)) {
                    p.getVehicle().eject();
                }
            }
        }
    }
}
