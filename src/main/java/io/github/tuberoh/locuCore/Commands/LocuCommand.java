package io.github.tuberoh.locuCore.Commands;
import io.github.tuberoh.locuCore.LocuCore;
import io.github.tuberoh.locuCore.Menus.MMenu;
import org.bukkit.*;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import java.time.LocalDate;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import java.io.File;
import java.io.IOException;
import org.bukkit.entity.Player;
import java.util.Collections;
import java.util.List;
import java.util.ArrayList;
import java.util.UUID;

import org.bukkit.command.TabCompleter;
import org.bukkit.configuration.ConfigurationSection;


public class LocuCommand implements CommandExecutor, TabCompleter {

    private final LocuCore plugin;
    private final File locuCorelist;
    private FileConfiguration locuCoreconfig;

    public LocuCommand(LocuCore plugin, File locuCorelist) {
        this.plugin = plugin;
        this.locuCorelist = locuCorelist;
        locuCoreconfig = YamlConfiguration.loadConfiguration(locuCorelist);
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {

        if (args.length == 0) {

            sender.sendMessage("§8[§6LocuCore§8] §cSorry, the command is incorrect");
            return true;

        } else if (args[0].equalsIgnoreCase("set")) {

            //luc set <name> <x> <y> <z>

            if (args.length < 2) {

                sender.sendMessage("§8[§6LocuCore§8] §cIncorrect usage. /luc set <location> <x> <y> <z>");
                return true;

            }
            if (locuCoreconfig.contains("Location." + args[1])) {
                sender.sendMessage("§8[§6LocuCore§8] §cThis location already exists");
                return true;
            }


            // luc set <name> <x> <y> <z>
            if (args.length < 5 && args.length > 2) {

                sender.sendMessage("§8[§6LocuCore§8] §cInsert complete coordinates!");
                return true;
            }
            if (!(sender instanceof Player)) {

                plugin.getLogger().severe("Only players can use this type of command");
                return true;

            }
            Player p = (Player) sender;
            String name = args[1];

            try {

                Double x, y, z;
                String username = sender.getName();
                UUID playeruuid = ((Player) sender).getUniqueId();
                String uuid_string = playeruuid.toString();
                String datastring = LocalDate.now().toString();
                String world = p.getWorld().getName();

                if (args.length == 2) {

                    x = p.getLocation().getX();
                    y = p.getLocation().getY();
                    z = p.getLocation().getZ();

                } else {

                    x = Double.parseDouble(args[2]);
                    y = Double.parseDouble(args[3]);
                    z = Double.parseDouble(args[4]);

                }
                if (coordinatesUsed(locuCoreconfig, x, y, z)) {
                    sender.sendMessage("§8[§6LocuCore§8] §cCoordinates already used on other location");
                    return true;
                }

                locuCoreconfig.set("Location." + name + ".Coordinate.x", x);
                locuCoreconfig.set("Location." + name + ".Coordinate.y", y);
                locuCoreconfig.set("Location." + name + ".Coordinate.z", z);
                locuCoreconfig.set("Location." + name + ".World", world);
                locuCoreconfig.set("Location." + name + ".Created_on", datastring);
                locuCoreconfig.set("Location." + name + ".Created_by", username);
                locuCoreconfig.set("Location." + name + ".UUID_creator", uuid_string);

                locuCoreconfig.save(locuCorelist);
                //§8[§6LocuCore§8]
                sender.sendMessage("§8[§6LocuCore§8] §a" + name + " was saved successfully ");

            } catch (NumberFormatException e) {
                sender.sendMessage("§8[§6LocuCore§8] §cThe coordinates should be numbers");
            } catch (IOException e) {
                sender.sendMessage("§8[§6LocuCore§8] §cError on saving the data. Try again");
                plugin.getLogger().severe("Error on saving data.yml: " + e.getMessage());
            }


        } else if (args[0].equalsIgnoreCase("remove")) {

            // luc remove name
            ConfigurationSection section = locuCoreconfig.getConfigurationSection("Location");
            if (args.length < 2) {

                sender.sendMessage("§8[§6LocuCore§8] §cIncorrect usage. /luc remove <location>");
                return true;

            }
            if (section == null || section.getKeys(false).isEmpty()) {
                sender.sendMessage("§8[§6LocuCore§8] §cThere are no saved locations");
                return true;
            }
            String name = args[1];
            String creator = locuCoreconfig.getString("Location." + name + ".Created_by");

            if (!(creator.equals(sender.getName()) || sender.hasPermission("locucore.rank.admin") || sender.hasPermission("locucore.remove"))) {

                sender.sendMessage("§8[§6LocuCore§8] §cYou can't remove the location");
                return true;

            }
            try {

                locuCoreconfig.set("Location." + name, null);
                locuCoreconfig.save(locuCorelist);

            } catch (IOException e) {

                sender.sendMessage("§8[§6LocuCore§8] §cError on saving the data. Try again");
                plugin.getLogger().severe("Error on saving data.yml: " + e.getMessage());

            }
            sender.sendMessage("§8[§6LocuCore§8] §aThe location is deleted");

        } else if (args[0].equalsIgnoreCase("tp")){

            // luc tp <player_name> <location_name>

            ConfigurationSection section = locuCoreconfig.getConfigurationSection("Location");
            if (args.length < 3) {

                sender.sendMessage("§8[§6LocuCore§8] §cWrong usage. /luc tp <player> <location>");
                return true;

            }
            if (section == null || section.getKeys(false).isEmpty()) {
                sender.sendMessage("§8[§6LocuCore§8] §cThere are no saved locations");
                return true;
            }
            if (!locuCoreconfig.contains("Location." + args[2])) {
                sender.sendMessage("§8[§6LocuCore§8] §cThis location doesn't exists");
                return true;
            }

            Player p = Bukkit.getPlayer(args[1]);

            if (p == null) {

                sender.sendMessage("§8[§6LocuCore§8] §cPlayer isn't online");
                return true;

            }

            World world = Bukkit.getWorld(locuCoreconfig.getString("Location." + args[2] + ".World"));
            double x = locuCoreconfig.getDouble("Location." + args[2] + ".Coordinate.x");
            double y = locuCoreconfig.getDouble("Location." + args[2] + ".Coordinate.y");
            double z = locuCoreconfig.getDouble("Location." + args[2] + ".Coordinate.z");
            Location loc = new Location(world, x, y, z);
            try {

                p.teleport(loc);
                p.playSound(p.getLocation(), Sound.ENTITY_ENDERMAN_TELEPORT, 1.0f, 1.0f);
                p.spawnParticle(Particle.END_ROD, p.getLocation(), 80, 1, 1, 1, 0.1);

            } catch (NullPointerException e) {

                plugin.getLogger().severe("Error: " + e);

            }
            sender.sendMessage("§8[§6LocuCore§8] §a" + args[1] + " was successfully teleported to:§e " + args[2]);


        } else if (args[0].equalsIgnoreCase("menu")) {
            //luc menu
            if (!(sender instanceof Player)) {

                plugin.getLogger().severe("Only players can use this type of command");
                return true;

            }
            if (args.length > 1) {

                sender.sendMessage("§8[§6LocuCore§8] §cWrong usage. /luc menu");
                return true;

            }
            new MMenu(plugin, locuCorelist).open((Player) sender);


        }else if(args[0].equalsIgnoreCase("help")){

            sender.sendMessage("§8|------------ §6§lLocuCore §8 ------------|");
            sender.sendMessage(" ");
            sender.sendMessage("§7Commands:");
            sender.sendMessage(" ");
            sender.sendMessage("§6- /luc set <location> <x> <y> <z>");
            sender.sendMessage("§6- /luc set <location>");
            sender.sendMessage("§6- /luc remove <location>");
            sender.sendMessage("§6- /luc tp <player> <location>");
            sender.sendMessage("§6- /luc edit <location> coordinates <x> <y> <z>");
            sender.sendMessage("§6- /luc edit <location> owner <player>");
            sender.sendMessage("§6- /luc menu");
            sender.sendMessage(" ");
            sender.sendMessage("§8|----------------------------------|");

        }
        else if(args[0].equalsIgnoreCase("edit")){

            //loc edit <name> coordinates x y z
            //loc edit <name> owner <player_name>
            //loc edit

            Player p = (Player) sender;

            if(args.length<2){

                sender.sendMessage("§8[§6LocuCore§8] §cIncorrect usage. /luc edit <location> <coordinates/owner> <xyz/player>");
                return true;

            }

            String uuid = locuCoreconfig.getString("Location." + args[1] + ".UUID_creator");


            if(!(p.hasPermission("locucore.rank.admin") || p.getUniqueId().toString().equals(uuid) || p.hasPermission("locucore.edit"))) {

                sender.sendMessage("§8[§6LocuCore§8] §cSorry, You don't have the right permissions");
                return true;

            }
            if (!locuCoreconfig.contains("Location." + args[1])){

                sender.sendMessage("§8[§6LocuCore§8] §cThis location doesn't exists");
                return true;

            }
            if(args.length<3){

                sender.sendMessage("§8[§6LocuCore§8] §cIncorrect usage. /luc edit <location> <coordinates/owner> <xyz/player>");
                return true;

            }

            if(args[2].equalsIgnoreCase("coordinates")){

                //luc edit <location_name> coordinates <x> <y> <z>
                if (args.length != 3 && args.length != 6) {
                    sender.sendMessage("§8[§6LocuCore§8] §cInsert complete coordinates!");
                    return true;
                }


                double x, y, z;

                if(args.length == 3){

                    x = p.getLocation().getX();
                    y = p.getLocation().getY();
                    z = p.getLocation().getZ();

                }else{

                    x = Double.parseDouble(args[3]);
                    y = Double.parseDouble(args[4]);
                    z = Double.parseDouble(args[5]);

                }

                if(coordinatesUsed(locuCoreconfig, x, y, z)){

                    sender.sendMessage("§8[§6LocuCore§8] §cCoordinates already used on other location");
                    return true;

                }

                try{

                    locuCoreconfig.set("Location." + args[1] + ".Coordinate.x", x);
                    locuCoreconfig.set("Location." + args[1] + ".Coordinate.y", y);
                    locuCoreconfig.set("Location." + args[1] + ".Coordinate.z", z);
                    locuCoreconfig.save(locuCorelist);

                    sender.sendMessage("§8[§6LocuCore§8] §a" + args[1] + " was modified successfully ");

                } catch (NumberFormatException e) {

                    sender.sendMessage("§8[§6LocuCore§8] §cThe coordinates should be numbers");

                } catch (IOException e) {

                    sender.sendMessage("§8[§6LocuCore§8] §cError on saving the data. Try again");
                    plugin.getLogger().severe("Error on saving data.yml: " + e.getMessage());

                }

            }
            if(args[2].equalsIgnoreCase("owner")){

                //luc edit <name> owner <player_name>
                if(args.length<4){

                    sender.sendMessage("§8[§6LocuCore§8] §cIncorrect usage. /luc edit <location> owner <player>");
                    return true;

                }
                OfflinePlayer target = Bukkit.getOfflinePlayerIfCached(args[3]);

                if (target == null) {
                    sender.sendMessage("§8[§6LocuCore§8] §cSorry, this player doesn't exist or never joined the server");
                    return true;
                }

                String ex_owner = locuCoreconfig.getString("Location." + args[1] + ".Created_by");
                String uuid_exowner =  locuCoreconfig.getString("Location." + args[1] + ".UUID_creator");
                String new_uuid= Bukkit.getOfflinePlayer(args[3]).getUniqueId().toString();

                if(new_uuid.equals(uuid_exowner)){

                    sender.sendMessage("§8[§6LocuCore§8] §c This is the same owner!");
                    return true;

                }
                try{

                    locuCoreconfig.set("Location." + args[1] + ".Created_by", args[3]);
                    locuCoreconfig.set("Location." + args[1] + ".UUID_creator", new_uuid);
                    locuCoreconfig.save(locuCorelist);
                    sender.sendMessage("§8[§6LocuCore§8] §a" + args[1] + " was successfully moved from §e" + ex_owner + " to §e" + args[3]);

                }catch(IOException e){

                    sender.sendMessage("§8[§6LocuCore§8] §cError on saving the data. Try again");
                    plugin.getLogger().severe("Error on saving data.yml: " + e.getMessage());

                }

            }

        }
        return true;

    }

    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String alias, String[] args) {

        locuCoreconfig = YamlConfiguration.loadConfiguration(locuCorelist);
        List<String> completions = new ArrayList<>();
        ConfigurationSection section = locuCoreconfig.getConfigurationSection("Location");

        if (args.length == 1) {
            completions.add("set");
            completions.add("remove");
            completions.add("tp");
            completions.add("menu");
            completions.add("help");
            completions.add("edit");
            return completions;
        }

        if (args.length == 2 && args[0].equalsIgnoreCase("set")) {
            completions.add("<name>");
            return completions;
        }

        // /luc set <name> <x> <y> <z>
        if (args[0].equalsIgnoreCase("set")) {

            Player p = (Player) sender;
            if (args.length == 3) completions.add(Integer.toString(p.getLocation().getBlockX()));
            if (args.length == 4) completions.add(Integer.toString(p.getLocation().getBlockY()));
            if (args.length == 5) completions.add(Integer.toString(p.getLocation().getBlockZ()));

            return completions;
        }
        if (args[0].equalsIgnoreCase("remove") && args.length == 2) {


            if (section == null) {
                return completions;
            }

            List<String> names = new ArrayList<>(section.getKeys(false));

            Collections.sort(names);
            completions.addAll(names);

            return completions;
        }
        if (args[0].equalsIgnoreCase("tp") && args.length == 2) {


            Player p = (Player) sender;
            completions.add(p.getName());

        }
        if (args[0].equalsIgnoreCase("tp") && args.length == 3) {

            //luc tp <player_name> <location_name>

            if (section == null) {
                return completions;
            }
            List<String> names = new ArrayList<>(section.getKeys(false));

            Collections.sort(names);
            completions.addAll(names);

            return completions;

        }
        if(args[0].equalsIgnoreCase("edit") && args.length == 2){

            //luc edit <location_name>
            if (section == null) {
                return completions;
            }
            List<String> names = new ArrayList<>(section.getKeys(false));
            Collections.sort(names);
            completions.addAll(names);
            return completions;

        }
        if(args[0].equalsIgnoreCase("edit") && args.length == 3){

            //luc edit <location_name> <owner/coordinates>
            completions.add("owner");
            completions.add("coordinates");
            return completions;

        }
        if(args[0].equalsIgnoreCase("edit") && args[2].equalsIgnoreCase("owner") && args.length == 4){

            //luc edit <location_name> <owner/coordinates> <player_name>
            completions.add("<player>");
            return completions;

        }
        if(args[0].equalsIgnoreCase("edit") && args[2].equalsIgnoreCase("coordinates") && args.length>3){

            //luc edit <location_name> <coordinates> <x> <y> <z>
            Player p = (Player) sender;
            if (args.length == 4) completions.add(Integer.toString(p.getLocation().getBlockX()));
            if (args.length == 5) completions.add(Integer.toString(p.getLocation().getBlockY()));
            if (args.length == 6) completions.add(Integer.toString(p.getLocation().getBlockZ()));

            return completions;
        }


        return null;
    }

    public boolean coordinatesUsed(FileConfiguration config, double x, double y, double z) {

        ConfigurationSection section = config.getConfigurationSection("Location");
        if (section == null) return false;

        for (String key : section.getKeys(false)) {

            double x2 = config.getDouble("Location." + key + ".Coordinate.x");
            double y2 = config.getDouble("Location." + key + ".Coordinate.y");
            double z2 = config.getDouble("Location." + key + ".Coordinate.z");

            if (x == x2 && y == y2 && z == z2) {
                return true;
            }
        }

        return false;
    }

}


