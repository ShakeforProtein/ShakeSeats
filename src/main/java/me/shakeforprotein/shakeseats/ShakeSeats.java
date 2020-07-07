package me.shakeforprotein.shakeseats;

import Commands.ToggleSeat;
import Listeners.SeatListener;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.Player;
import org.bukkit.event.Listener;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.PrintStream;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.UUID;

public final class ShakeSeats extends JavaPlugin implements Listener {

    public String badge = getConfig().getString("general.messages.badge") == null ? ChatColor.translateAlternateColorCodes('&', "&3&l[&2Shake Seats&l]&r") : ChatColor.translateAlternateColorCodes('&', getConfig().getString("general.messages.badge"));
    public String err = badge + (getConfig().getString("general.messages.error") == null ? ChatColor.translateAlternateColorCodes('&', "&4&lERROR:&r&l") : ChatColor.translateAlternateColorCodes('&', getConfig().getString("general.messages.error")));
    public static List<Material> suitableBlocks = new ArrayList<>();
    public HashMap<ArmorStand, Player> standHash = new HashMap<>();
    public static List<String> uuidList = new ArrayList<>();
    public static List<String> noSit = new ArrayList<>();

    @Override
    public void onEnable() {

        saveDefaultConfig();
        // Plugin startup logic

        // Plugin startup logic
        for (Material mat : Material.values()) {
            if (mat.isBlock() && (mat.name().toLowerCase().contains("stairs") || mat.name().toLowerCase().contains("slab") || mat.name().toLowerCase().contains("carpet"))) {
                suitableBlocks.add(mat);
            }
        }

        Bukkit.getPluginManager().registerEvents(new SeatListener(this), this);
        this.getCommand("toggleseat").setExecutor(new ToggleSeat(this));

        Bukkit.getScheduler().runTaskLater(this, new Runnable() {
            @Override
            public void run() {
                uuidList = getConfig().getStringList("armorStands");
                noSit = getConfig().getStringList("noSit");
                /*for(String pn : noSit){
                    Bukkit.broadcastMessage(pn);
                }*/
                if(uuidList != null){
                    for(String uuid : uuidList) {
                        Bukkit.getEntity(UUID.fromString(uuid)).remove();
                    }
                }
            }
        }, 100L);

        Bukkit.getScheduler().runTaskTimer(this, new Runnable() {
            @Override
            public void run() {
                for (ArmorStand v : standHash.keySet()) {
                    Player p = standHash.get(v);
                    if (p.isInsideVehicle()) {
                        if (p.getVehicle() instanceof ArmorStand) {
                            if (standHash.containsKey(p.getVehicle())) {
                                Location l = v.getLocation();
                                l.setPitch(p.getLocation().getPitch());
                                v.teleport(l);
                                //v.setHeadPose(new EulerAngle(Math.toRadians((int) p.getLocation().getPitch()), (int) p.getLocation().getYaw(), 0));
                                v.setRotation(p.getLocation().getYaw(), p.getLocation().getPitch());
                            }
                        }
                    }
                }
            }
        }, 50, 5);
    }

    @Override
    public void onDisable() {
        List<String> tempList = new ArrayList<>();
        List<String> tempSitList = new ArrayList<>();
        for(ArmorStand as : standHash. keySet()){
            as.getPassengers().get(0).eject();
            tempList.add(as.getUniqueId().toString());
        }
        for(String nS : noSit){
            //Bukkit.broadcastMessage(nS);
            tempSitList.add(nS);
        }
        getConfig().set("noSit", tempSitList);
        getConfig().set("armorStands", tempList);
        saveConfig();
        for(String uuid : tempList){
            if(Bukkit.getEntity(UUID.fromString(uuid)) != null) {
                standHash.remove(Bukkit.getEntity(UUID.fromString(uuid)));
                Bukkit.getEntity(UUID.fromString(uuid)).remove();
            }
        }
    }

    public void makeLog(Exception tr) {
        System.out.println("Creating new log folder - " + new File(this.getDataFolder() + File.separator + "logs").mkdir());
        String dateTimeString = LocalDateTime.now().toString().replace(":", "_").replace("T", "__");
        File file = new File(this.getDataFolder() + File.separator + "logs" + File.separator + dateTimeString + "-" + tr.getCause() + ".log");
        try {
            PrintStream ps = new PrintStream(file);
            tr.printStackTrace(ps);
            System.out.println(this.getDescription().getName() + " - " + this.getDescription().getVersion() + "Encountered Error of type: " + tr.getCause());
            System.out.println("A log file has been generated at " + this.getDataFolder() + File.separator + "logs" + File.separator + dateTimeString + "-" + tr.getCause() + ".log");
            ps.close();
        } catch (FileNotFoundException e) {
            System.out.println("Error creating new log file for " + getDescription().getName() + " - " + getDescription().getVersion());
            System.out.println("Error was as follows");
            System.out.println(e.getMessage());
        }
    }
}

