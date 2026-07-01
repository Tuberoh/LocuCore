package io.github.tuberoh.locuCore.Utilities;
import io.github.tuberoh.locuCore.LocuCore;
import io.github.tuberoh.locuCore.Objects.Waypoints;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;


public class MigrationSystem {

    private final LocuCore plugin;
    private final FileConfiguration LocuCoreConfig;
    private final DataController dc;
    private final File LocuCoreList;




    public MigrationSystem(LocuCore plugin, File LocuCorelist, DataController dc) {

        this.plugin = plugin;
        this.LocuCoreList = LocuCorelist;
        LocuCoreConfig = YamlConfiguration.loadConfiguration(LocuCorelist);
        this.dc = dc;


    }


    public void startMigration(){

        Bukkit.getScheduler().runTaskAsynchronously(plugin, () -> {

            int migrated=0;
            int failed=0;
            ConfigurationSection locations = LocuCoreConfig.getConfigurationSection("Location");

            if(locations == null){

                return;

            }

            List <Waypoints> waypoints = new ArrayList<>(getWaypointsYaml(locations));
            if(waypoints.isEmpty()){

                return;

            }

            for(int i=0;i<waypoints.size();i++){

                String name = waypoints.get(i).getName();
                String dpname = name;
                String uuid = waypoints.get(i).getOwner_uuid();
                UUID owner_uuid = UUID.fromString(uuid);
                OfflinePlayer offlinePlayer = Bukkit.getOfflinePlayer(owner_uuid);
                String owner_name = offlinePlayer.getName();

                int h=1;

                if(dc.coordinatesDuplication(uuid, waypoints.get(i).getX(), waypoints.get(i).getY(), waypoints.get(i).getZ(), waypoints.get(i).getWorld())){

                    continue;

                }
                while(dc.WpExists(name, uuid)){

                    name = dpname + h;
                    h++;

                }

                Boolean st = dc.setWaypoint(name,
                            waypoints.get(i).getX(),
                            waypoints.get(i).getY(),
                            waypoints.get(i).getZ(),
                            owner_name,
                            waypoints.get(i).getYaw(),
                            waypoints.get(i).getPitch(),
                            waypoints.get(i).getWorld(),
                            uuid,
                            waypoints.get(i).getStatus());

                if(st){

                    migrated++;

                }
                else{

                    failed++;

                }

            }

            plugin.getLogger().info("Migration completed: " + migrated + ", Migration failed: " + failed);

            File newFile = new File(plugin.getDataFolder(), "data_lock.yml");
            LocuCoreList.renameTo(newFile);


        });


    }
    private List<Waypoints> getWaypointsYaml(ConfigurationSection locations){

        List<String> WaypointsNames = new ArrayList<>(locations.getKeys(false));
        List<Waypoints> result = new ArrayList<>();

        for(int i=0; i<WaypointsNames.size(); i++){

            String path = "Location." + WaypointsNames.get(i);
            String name = WaypointsNames.get(i);
            double x = LocuCoreConfig.getDouble(path + ".Coordinate.x");
            double y = LocuCoreConfig.getDouble(path + ".Coordinate.y");
            double z = LocuCoreConfig.getDouble(path + ".Coordinate.z");
            String world = LocuCoreConfig.getString(path + ".World");
            String uuid_creator = LocuCoreConfig.getString(path + ".UUID_creator");
            double yaw = LocuCoreConfig.getDouble(path + ".yaw");
            double pitch = LocuCoreConfig.getDouble(path + ".pitch");

            result.add(new Waypoints(
                    name,
                    x,
                    y,
                    z,
                    world,
                    yaw,
                    pitch,
                    uuid_creator,
                    false
                    )
            );

        }

        return result;

    }

}
