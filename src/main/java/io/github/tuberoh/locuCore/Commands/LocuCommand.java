package io.github.tuberoh.locuCore.Commands;
import io.github.tuberoh.locuCore.LocuCore;
import io.github.tuberoh.locuCore.Menus.MMenu;
import io.github.tuberoh.locuCore.Utilities.DataController;
import org.bukkit.*;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import java.util.*;
import org.bukkit.command.TabCompleter;
import org.bukkit.util.StringUtil;
import java.util.Locale;


public class LocuCommand implements CommandExecutor, TabCompleter {

    private final LocuCore plugin;
    private final DataController dc;

    public LocuCommand(LocuCore plugin, DataController dc) {
        this.plugin = plugin;
        this.dc = dc;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {

        if (!(sender instanceof Player)) {

            plugin.getLogger().severe("Only players can use this type of command");
            return true;

        }
        UUID playeruuid = ((Player) sender).getUniqueId();
        String uuid_string = playeruuid.toString();

        if (args.length == 0) {

            sender.sendMessage("§8[§6LocuCore§8] §cSorry, the command is incorrect. Type /help");
            return true;

        } else if (args[0].equalsIgnoreCase("set")) {

            //luc set <waypoint> <x> <y> <z>

            if(!sender.hasPermission("locucore.set")){

                sender.sendMessage("§8[§6LocuCore§8] §cSorry, you don't have the right permission!");
                return true;

            }

            if (args.length < 2) {

                sender.sendMessage("§8[§6LocuCore§8] §cIncorrect usage. /luc set <location> <x> <y> <z>");
                return true;

            }
            if (dc.wpDuplication(args[1], uuid_string)) {
                sender.sendMessage("§8[§6LocuCore§8] §cThis location already exists");
                return true;
            }

            // luc set <name> <x> <y> <z>
            if (args.length < 5 && args.length > 2) {

                sender.sendMessage("§8[§6LocuCore§8] §cInsert complete coordinates!");
                return true;
            }
            if(args[1].length() > 16){

                sender.sendMessage("§8[§6LocuCore§8] §cName must be 16 characters or less");
                return true;

            }
            if(!args[1].matches("[a-zA-Z0-9_]+")){

                sender.sendMessage("§8[§6LocuCore§8] §cName can only contain letters, numbers and underscores");
                return true;

            }


            Player p = (Player) sender;
            String name = args[1];

            try{

                double x, y, z;
                String username = sender.getName();
                String world = p.getWorld().getName();
                float pitch = p.getLocation().getPitch();
                float yaw = p.getLocation().getYaw();

                if (args.length == 2) {

                    x = p.getLocation().getX();
                    y = p.getLocation().getY();
                    z = p.getLocation().getZ();

                } else {

                    x = Double.parseDouble(args[2]);
                    y = Double.parseDouble(args[3]);
                    z = Double.parseDouble(args[4]);

                }

                if(dc.setWaypoint(name, x, y, z, username, yaw, pitch, world, uuid_string, false)){

                    sender.sendMessage("§8[§6LocuCore§8] §a" + name + " was saved successfully ");

                }
                else{

                    sender.sendMessage("§8[§6LocuCore§8] §cAn error occurred while saving the waypoint");

                }


            } catch (NumberFormatException e){

                sender.sendMessage("§8[§6LocuCore§8] §cThe coordinates should be numbers");

            }


        } else if (args[0].equalsIgnoreCase("remove")) {

            // luc remove name

            if (args.length < 2){

                sender.sendMessage("§8[§6LocuCore§8] §cIncorrect usage. /luc remove <location>");
                return true;

            }
            if (dc.empty()){
                sender.sendMessage("§8[§6LocuCore§8] §cThere are no saved locations");
                return true;
            }
            String name = args[1];
            String owner = dc.getOwner(uuid_string, name);

            if (owner == null){

                sender.sendMessage("§8[§6LocuCore§8] §cThis waypoint doesn't exist");
                return true;

            }
            if (!(owner.equals(sender.getName()) || sender.hasPermission("locucore.rank.admin"))){

                sender.sendMessage("§8[§6LocuCore§8] §aYou can't remove the location");
                return true;

            }

            if (!(dc.getOwner(uuid_string, name).equals(sender.getName()) || sender.hasPermission("locucore.rank.admin") || sender.hasPermission("locucore.remove"))) {

                sender.sendMessage("§8[§6LocuCore§8] §aYou can't remove the location");
                return true;

            }
            if(dc.deleteWaypoint(uuid_string, args[1])){

                sender.sendMessage("§8[§6LocuCore§8] §c" + name + " has been deleted successfully");

            }
            else{

                sender.sendMessage("§8[§6LocuCore§8] §cImpossible to delete the location");

            }


        } else if (args[0].equalsIgnoreCase("tp")){

            //- Waypoints propria
            //luc tp <location_name>


            //- Waypoints di un altro giocatore
            // luc tp <player_name> <location_name>

            if(!sender.hasPermission("locucore.tp")) {

                sender.sendMessage("§8[§6LocuCore§8] §cSorry, you don't have the right permission!");
                return true;

            }

            if(args.length < 2 || args.length > 3){

                sender.sendMessage("§8[§6LocuCore§8] §cWrong usage. Type /luc help");
                return true;

            }
            if (dc.empty()) {
                sender.sendMessage("§8[§6LocuCore§8] §cThere are no saved locations");
                return true;
            }
            if(args.length == 2){

                if (!dc.wpDuplication(args[1], uuid_string)) {
                    sender.sendMessage("§8[§6LocuCore§8] §cThis waypoint doesn't exists");
                    return true;
                }
                Player p = Bukkit.getPlayer(sender.getName());

                if (p == null) {

                    sender.sendMessage("§8[§6LocuCore§8] §cPlayer isn't online");
                    return true;

                }
                if(dc.getWorld(uuid_string, args[1]).equals("null")){

                    sender.sendMessage("§8[§6LocuCore§8] §cSorry, this waypoint doesn't exists");
                    return true;

                }
                World world = Bukkit.getWorld(dc.getWorld(uuid_string, args[1]));
                double x = dc.getX(uuid_string, args[1]);
                double y = dc.getY(uuid_string, args[1]);
                double z = dc.getZ(uuid_string, args[1]);
                double pitch = dc.getPitch(uuid_string, args[1]);
                double yaw = dc.getYaw(uuid_string, args[1]);
                Location loc = new Location(world, x, y, z, (float) yaw, (float) pitch);
                try {

                    p.teleport(loc);
                    Bukkit.getScheduler().runTask(plugin, () -> {
                        p.playSound(p.getLocation(), Sound.ENTITY_ENDERMAN_TELEPORT, 1.0f, 1.0f);
                        p.spawnParticle(Particle.END_ROD, p.getLocation(), 80, 1, 1, 1, 0.1);
                    });


                } catch (NullPointerException e) {

                    plugin.getLogger().severe("Error: " + e);

                }
                sender.sendMessage("§8[§6LocuCore§8] §a" + p.getName() + " was successfully teleported to:§e " + args[1]);


            }
            if(args.length == 3){

                //luc tp <player> <location_name>

                OfflinePlayer owner = Bukkit.getOfflinePlayer(args[1]);
                String owner_uuid;
                if (owner.hasPlayedBefore() || owner.isOnline()) {

                    UUID uuidt = owner.getUniqueId();
                     owner_uuid = uuidt.toString();

                }
                else{

                    sender.sendMessage("§8[§6LocuCore§8] §cThe player doesn't exists");
                    return true;

                }

                if (!dc.wpDuplication(args[2], owner_uuid)) {
                    sender.sendMessage("§8[§6LocuCore§8] §cThe player doesn't own a waypoint with that name");
                    return true;
                }

                if (!dc.getIsPublic(owner_uuid, args[2]) && !sender.hasPermission("locucore.rank.admin")) {

                    sender.sendMessage("§8[§6LocuCore§8] §cThis waypoint is private");
                    return true;

                }


                Player p = Bukkit.getPlayer(sender.getName());


                if (p == null) {

                    sender.sendMessage("§8[§6LocuCore§8] §cPlayer isn't online");
                    return true;

                }
                if(dc.getWorld(owner_uuid, args[2]).equals("null")){

                    sender.sendMessage("§8[§6LocuCore§8] §cThis waypoint doesn't exists");
                    return true;

                }
                World world = Bukkit.getWorld(dc.getWorld(owner_uuid, args[2]));
                double x = dc.getX(owner_uuid, args[2]);
                double y = dc.getY(owner_uuid, args[2]);
                double z = dc.getZ(owner_uuid, args[2]);
                double pitch = dc.getPitch(owner_uuid, args[2]);
                double yaw = dc.getYaw(owner_uuid, args[2]);
                Location loc = new Location(world, x, y, z, (float) yaw, (float) pitch);
                try {

                    p.teleport(loc);
                    Bukkit.getScheduler().runTask(plugin, () -> {
                        p.playSound(p.getLocation(), Sound.ENTITY_ENDERMAN_TELEPORT, 1.0f, 1.0f);
                        p.spawnParticle(Particle.END_ROD, p.getLocation(), 80, 1, 1, 1, 0.1);
                    });


                } catch (NullPointerException e) {

                    plugin.getLogger().severe("Error: " + e);

                }
                sender.sendMessage("§8[§6LocuCore§8] §aTeleported to: §e" + args[2]);

            }

        } else if (args[0].equalsIgnoreCase("menu")) {

            //luc menu

            if (args.length > 1) {

                sender.sendMessage("§8[§6LocuCore§8] §cWrong usage. /luc menu");
                return true;

            }
            new MMenu(plugin, dc).open((Player) sender);


        }else if(args[0].equalsIgnoreCase("help")){

            sender.sendMessage("§8|------------ §6§lLocuCore§8 ------------|");
            sender.sendMessage(" ");
            sender.sendMessage("§7Commands:");
            sender.sendMessage(" ");
            sender.sendMessage("§6- /luc set <waypoint> <x> <y> <z>");
            sender.sendMessage("§6- /luc set <waypoint>");
            sender.sendMessage("§6- /luc remove <waypoint>");
            sender.sendMessage("§6- /luc tp <player> <waypoint>");
            sender.sendMessage("§6- /luc tp <waypoint>");
            sender.sendMessage("§6- /luc edit <waypoint> coordinates <x> <y> <z>");
            sender.sendMessage("§6- /luc edit <waypoint> owner <player>");
            sender.sendMessage("§6- /luc edit <waypoint> public <true/false>");
            sender.sendMessage("§6- /luc edit <waypoint> name <new_name>");
            sender.sendMessage("§6- /luc menu");
            sender.sendMessage(" ");
            sender.sendMessage("§8|--------------------------------------|");

        }
        else if(args[0].equalsIgnoreCase("edit")){

            //Command usable just for your own waypoints (Permission granted to admin permission holders to add)
            //luc edit <name> coordinates x y z
            //luc edit <name> owner <player_name>

            if(!sender.hasPermission("locucore.edit")){

                sender.sendMessage("§8[§6LocuCore§8] §cSorry, you don't have the right permission!");
                return true;

            }

            Player p = (Player) sender;

            if(args.length<2){

                sender.sendMessage("§8[§6LocuCore§8] §cIncorrect usage. /luc edit <location> <coordinates/owner> <xyz/player>");
                return true;

            }
            if (!dc.wpDuplication(args[1], uuid_string)) {

                sender.sendMessage("§8[§6LocuCore§8] §cThis location doesn't exists");
                return true;

            }
            if(args.length<3){

                sender.sendMessage("§8[§6LocuCore§8] §cIncorrect usage. /luc edit <location> <coordinates/owner/visibility> <xyz/player/true/false>");
                return true;

            }

            if(args[2].equalsIgnoreCase("coordinates")){

                //luc edit <location_name> coordinates <x> <y> <z>
                if (args.length != 3 && args.length != 6) {
                    sender.sendMessage("§8[§6LocuCore§8] §cInsert complete coordinates!");
                    return true;
                }


                double x, y, z;
                String world = p.getWorld().getName();

                if(args.length == 3){

                    x = p.getLocation().getX();
                    y = p.getLocation().getY();
                    z = p.getLocation().getZ();

                }
                else{

                    try{

                        x = Double.parseDouble(args[3]);
                        y = Double.parseDouble(args[4]);
                        z = Double.parseDouble(args[5]);

                    }
                    catch(NumberFormatException e){

                        sender.sendMessage("§8[§6LocuCore§8] §cThe coordinates should be numbers");
                        return true;

                    }

                }

                if(dc.coordinatesDuplication(uuid_string, x, y, z, world)){

                    sender.sendMessage("§8[§6LocuCore§8] §cCoordinates already used on other waypoint");
                    return true;

                }
                float pitch = p.getLocation().getPitch();
                float yaw = p.getLocation().getYaw();

                if(dc.editCoordinates(uuid_string, args[1], x, y, z, yaw, pitch, world)){

                    sender.sendMessage("§8[§6LocuCore§8] §aCoordinates were successfully edited");

                }
                else{

                    sender.sendMessage("§8[§6LocuCore§8] §aAn error occurred while editing coordinates");

                }

            }
            if(args[2].equalsIgnoreCase("owner")){

                //luc edit <name> owner <player_name>

                if(args.length<4){

                    sender.sendMessage("§8[§6LocuCore§8] §cIncorrect usage. /luc edit <location> owner <player>");
                    return true;

                }

                OfflinePlayer owner = Bukkit.getOfflinePlayer(args[3]);
                String owner_uuid;
                if (owner.hasPlayedBefore() || owner.isOnline()) {

                    UUID uuidt = owner.getUniqueId();
                    owner_uuid = uuidt.toString();

                }
                else{

                    sender.sendMessage("§8[§6LocuCore§8] §cThe player doesn't exists or never joined the server");
                    return true;

                }

                if(dc.wpDuplication(args[1], owner_uuid)){

                    sender.sendMessage("§8[§6LocuCore§8] §c This player has already a waypoint with the same name");
                    return true;
                }


                String ex_owner = dc.getOwner(uuid_string, args[1]);
                String uuid_exowner =  dc.getUUID(ex_owner, args[1]);
                String new_uuid= Bukkit.getOfflinePlayer(args[3]).getUniqueId().toString();

                if(new_uuid.equals(uuid_exowner)){

                    sender.sendMessage("§8[§6LocuCore§8] §c This is the same owner!");
                    return true;

                }

                if(dc.editOwner(uuid_exowner, owner_uuid, args[1], args[3])){

                    sender.sendMessage("§8[§6LocuCore§8] §a Property was successfully edited!");

                }
                else{

                    sender.sendMessage("§8[§6LocuCore§8] §c An error occurred while editing owner");

                }

            }
            else if(args[2].equalsIgnoreCase("public")){

                //luc edit <location> public <true/false>
                boolean status = dc.getIsPublic(uuid_string, args[1]);
                String st1 = status ? "true" : "false";
                String st = status ? "public" : "private";


                if(args.length<4){

                    sender.sendMessage("§8[§6LocuCore§8] §cIncorrect usage. /luc edit <location> public <true/false>");
                    return true;

                }
                if(!(args[3].equalsIgnoreCase("true") || args[3].equalsIgnoreCase("false"))){

                    sender.sendMessage("§8[§6LocuCore§8] §cIncorrect usage. /luc edit <location> public <true/false>");

                }


                if(st1.equalsIgnoreCase(args[3])){

                    sender.sendMessage("§8[§6LocuCore§8] §cThis location is already " + st);
                    return true;

                }
                else{

                    if(args[3].equalsIgnoreCase("true")){

                        if(dc.editVisibility(uuid_string, args[1], true)){

                            sender.sendMessage("§8[§6LocuCore§8] §a" + args[1] + " is now " + "§2"+ st1);

                        }
                        else{

                            sender.sendMessage("§8[§6LocuCore§8] §cAn error occurred while editing visibility");

                        }

                    }
                    else if(args[3].equalsIgnoreCase("false")){

                        if(dc.editVisibility(uuid_string, args[1], false)){

                            sender.sendMessage("§8[§6LocuCore§8] §a" + args[1] + " is now " + "§4"+ st1);

                        }
                        else{

                            sender.sendMessage("§8[§6LocuCore§8] §cAn error occurred while editing visibility");

                        }

                    }

                }

            }
            else if(args[2].equalsIgnoreCase("name")){

                //luc edit <waypoint> name <new_name>

                if(args.length<4){

                    sender.sendMessage("§8[§6LocuCore§8] §cIncorrect usage. /luc edit <location> name <name>");
                    return true;

                }
                if(args[3].length() > 16){

                    sender.sendMessage("§8[§6LocuCore§8] §cName must be 16 characters or less");
                    return true;

                }
                if(!args[3].matches("[a-zA-Z0-9_]+")){

                    sender.sendMessage("§8[§6LocuCore§8] §cName can only contain letters, numbers and underscores");
                    return true;

                }


                if(args[1].equalsIgnoreCase(args[3])){

                    sender.sendMessage("§8[§6LocuCore§8] §cThis is the same name");
                    return true;

                }

                if(dc.wpDuplication(args[3], uuid_string)){

                    sender.sendMessage("§8[§6LocuCore§8] §cA waypoint with the same name already exists");
                    return true;

                }

                if(dc.editName(uuid_string, args[1], args[3])){

                    sender.sendMessage("§8[§6LocuCore§8] §e" + args[1] + "§a is now §6" + args[3]);
                    return true;

                }
                else{

                    sender.sendMessage("§8[§6LocuCore§8] §cAn error occurred while editing name");
                    return true;

                }

            }

        }

        return true;
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String alias, String[] args){

        Player p = (Player) sender;
        String uuid = p.getUniqueId().toString();
        List<String> completions = new ArrayList<>();
        List<String> section = new ArrayList<>(dc.getWaypointsNames(uuid));


        if (args.length == 1) {

            if(sender.hasPermission("locucore.set")){

                completions.add("set");

            }
            if(sender.hasPermission("locucore.edit")){

                completions.add("edit");

            }
            if(sender.hasPermission("locucore.remove")){

                completions.add("remove");

            }
            if(sender.hasPermission("locucore.tp")){

                completions.add("tp");

            }
            completions.add("menu");
            completions.add("help");

            return completions;
        }

        if (args.length == 2 && args[0].equalsIgnoreCase("set")){
            completions.add("<name>");
            return completions;
        }

        // /luc set <name> <x> <y> <z>
        if (args[0].equalsIgnoreCase("set")){

            if (args.length == 3){

                completions.add(String.format(Locale.US,"%.3f", p.getLocation().getX()));

            }
            if (args.length == 4){

                completions.add(String.format(Locale.US,"%.3f", p.getLocation().getY()));

            }
            if (args.length == 5){

                completions.add(String.format(Locale.US,"%.3f", p.getLocation().getZ()));

            }

            return completions;
        }
        if (args[0].equalsIgnoreCase("remove") && args.length == 2){


            if (section.isEmpty()) {

                return completions;
            }


            Collections.sort(section);
            completions.addAll(section);

            return completions;
        }
        if (args[0].equalsIgnoreCase("tp")){

            if (args.length == 2){

                if (args[1].isEmpty()) {
                    return completions;
                }

                // filtra player
                for (Player online : Bukkit.getOnlinePlayers()){
                    StringUtil.copyPartialMatches(args[1],
                            Collections.singletonList(online.getName()),
                            completions);
                }

                return completions;
            }

            // /luc tp <player> <TAB>
            if (args.length == 3){

                if (args[1].isEmpty()){

                    return completions;

                }

                OfflinePlayer owner = Bukkit.getOfflinePlayer(args[1]);
                if (!owner.hasPlayedBefore() && !owner.isOnline()){

                    return completions;

                }


                String owner_uuid = owner.getUniqueId().toString();
                section = dc.getPublicWaypointsNames(owner_uuid);

                StringUtil.copyPartialMatches(args[2], section, completions);
                return completions;
            }

            return completions;
        }
        if (args[0].equalsIgnoreCase("edit") && args.length == 2){

            //luc edit <location_name>
            if (section.isEmpty()){

                return completions;

            }

            StringUtil.copyPartialMatches(args[1], section, completions);
            Collections.sort(completions);
            return completions;

        }
        if (args[0].equalsIgnoreCase("edit") && args.length == 3){

            //luc edit <waypoint> <owner/coordinates/public>
            completions.add("owner");
            completions.add("coordinates");
            completions.add("public");
            completions.add("name");
            return completions;
        }


        if (args[0].equalsIgnoreCase("edit") && args.length == 4 && args[2].equalsIgnoreCase("owner")){
            completions.add("<player>");
            return completions;
        }


        if (args[0].equalsIgnoreCase("edit") && args.length >= 4 && args[2].equalsIgnoreCase("coordinates")){

            if (args.length == 4){

                completions.add(String.format(Locale.US, "%.3f", p.getLocation().getX()));

            }


            if (args.length == 5){

                completions.add(String.format(Locale.US,"%.3f", p.getLocation().getY()));

            }


            if (args.length == 6){

                completions.add(String.format(Locale.US,"%.3f", p.getLocation().getZ()));

            }


            return completions;
        }
        if (args[0].equalsIgnoreCase("edit") && args.length == 4 && args[2].equalsIgnoreCase("public")){

            completions.add("true");
            completions.add("false");
            return completions;

        }
        if(args[0].equalsIgnoreCase("edit") && args.length == 4 && args[2].equalsIgnoreCase("name")){

            completions.add("<new_name>");
            return completions;

        }

        return completions;
    }

}


