package io.github.tuberoh.locuCore.Utilities;

import io.github.tuberoh.locuCore.LocuCore;
import io.github.tuberoh.locuCore.Objects.Waypoints;

import java.io.File;
import java.nio.file.Files;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class DataController {

    private final LocuCore plugin;
    private Connection connection;
    private String url;

    public DataController(LocuCore plugin) {
        this.plugin = plugin;
    }

    public boolean connect(){
        File dataFolder = plugin.getDataFolder();

        try{

            Files.createDirectories(dataFolder.toPath());

        }catch (Exception e){

            plugin.getLogger().severe("Impossibile to create the file: " + e.getMessage());
            return false;

        }

        File dbFile = new File(dataFolder, "Locuwaypoints.db");
        this.url = "jdbc:sqlite:" + dbFile.getAbsolutePath();

        try{

            this.connection = DriverManager.getConnection(url);
            initTables();
            enableWAL();
            return true;

        }
        catch (SQLException e){

            plugin.getLogger().severe("Connection error: " + e.getMessage());
            this.connection = null;
            return false;

        }
    }

    private void initTables() throws SQLException {
        String sql = "CREATE TABLE IF NOT EXISTS waypoints (" +
                "id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                "name TEXT NOT NULL, " +
                "x REAL NOT NULL, " +
                "y REAL NOT NULL, " +
                "z REAL NOT NULL, " +
                "creation_date DATETIME NOT NULL, " +
                "owner TEXT NOT NULL, " +
                "world TEXT NOT NULL, " +
                "yaw REAL NOT NULL, " +
                "pitch REAL NOT NULL, " +
                "uuid TEXT NOT NULL," +
                "isPublic INTEGER NOT NULL, " +
                "UNIQUE(uuid, name)" +
                ");";
        try (var stmt = connection.createStatement()) {
            stmt.execute(sql);
        }
    }

    public void disconnect() {
        try {
            if (connection != null && !connection.isClosed()) {
                connection.close();
            }
        } catch (SQLException e) {
            plugin.getLogger().severe("Error: " + e.getMessage());
        }
    }
    private void enableWAL() throws SQLException {
        try (var stmt = connection.createStatement()) {
            stmt.execute("PRAGMA journal_mode=WAL;");
        }
    }

    public boolean empty() {

        String sql = "SELECT EXISTS(SELECT 1 FROM waypoints LIMIT 1)";

        try (var pstmt = connection.prepareStatement(sql)) {

            var rs = pstmt.executeQuery();

            if (rs.next()) {
                return !rs.getBoolean(1);
            }

        } catch (SQLException e) {
            plugin.getLogger().severe("Error: " + e.getMessage());
        }

        return true;
    }

    public Boolean wpDuplication(String name, String uuid){

        String sql = "SELECT COUNT(*) FROM waypoints WHERE uuid = ? AND name = ?";

        try(var pstmt = connection.prepareStatement(sql)){

            pstmt.setString(1, uuid);
            pstmt.setString(2, name);

            ResultSet rs = pstmt.executeQuery();
            if(rs.next()){

                return rs.getInt(1)>0;

            }

        }
        catch (SQLException e){

            plugin.getLogger().severe("Error: " + e.getMessage());
            return false;

        }
        return false;

    }
    public Boolean coordinatesDuplication(String uuid, double x, double y, double z, String world){

        String sql = "SELECT COUNT(*) FROM waypoints WHERE uuid = ? AND x = ? AND y = ? AND z = ? AND world = ?";

        try(var pstmt = connection.prepareStatement(sql)){

            pstmt.setString(1, uuid);
            pstmt.setDouble(2, x);
            pstmt.setDouble(3, y);
            pstmt.setDouble(4, z);
            pstmt.setString(5, world);
            ResultSet rs = pstmt.executeQuery();
            if(rs.next()){

                return rs.getInt(1)>0;

            }

        }
        catch (SQLException e){

            plugin.getLogger().severe("Error: " + e.getMessage());
            return false;

        }
        return false;

    }
    public boolean WpExists(String name, String uuid){

        return wpDuplication(name, uuid);

    }
    public Boolean setWaypoint(String name, double x, double y, double z, String owner, double yaw, double pitch, String world, String uuid, boolean isPublic){

        if (connection == null) {
            plugin.getLogger().severe("[DB] Connection is NULL!");
            return false;
        }


        String sql = "INSERT INTO waypoints(name," +
                "x," +
                "y," +
                "z," +
                "creation_date," +
                "owner," +
                "yaw," +
                "pitch," +
                "world," +
                "uuid," +
                "isPublic) VALUES (?,?,?,?,datetime('now'),?,?,?,?,?,?)";

        try(var pstmt = connection.prepareStatement(sql)){

            pstmt.setString(1, name);
            pstmt.setDouble(2, x);
            pstmt.setDouble(3, y);
            pstmt.setDouble(4, z);
            pstmt.setString(5, owner);
            pstmt.setDouble(6, yaw);
            pstmt.setDouble(7, pitch);
            pstmt.setString(8, world);
            pstmt.setString(9, uuid);
            pstmt.setBoolean(10, isPublic);
            return pstmt.executeUpdate()==1;

        }
        catch (SQLException e){

            plugin.getLogger().severe("Error: " + e.getMessage());
            return false;
        }

    }
    public boolean deleteWaypoint(String uuid, String name) {

        String sql = "DELETE from waypoints WHERE uuid = ? AND name = ?";

        try(var pstmt= connection.prepareStatement(sql)){

            pstmt.setString(1, uuid);
            pstmt.setString(2, name);

            return pstmt.executeUpdate()>0;

        }
        catch (SQLException e){

            plugin.getLogger().severe("Error: " + e.getMessage());
            return false;

        }

    }
    public boolean editCoordinates(String uuid, String name, double x, double y, double z, double yaw, double pitch, String world){

        String sql = "UPDATE waypoints SET x = ? ," +
                "y = ? ," +
                "z = ?," +
                "world = ?," +
                "yaw = ?," +
                "pitch = ? " +
                "WHERE uuid = ? AND name = ?";

        try(var pstmt = connection.prepareStatement(sql)) {

            pstmt.setDouble(1, x);
            pstmt.setDouble(2, y);
            pstmt.setDouble(3, z);
            pstmt.setString(4, world);
            pstmt.setDouble(5, yaw);
            pstmt.setDouble(6, pitch);
            pstmt.setString(7, uuid);
            pstmt.setString(8, name);

            return pstmt.executeUpdate()>0;


        }
        catch (SQLException e){

            plugin.getLogger().severe("Error: " + e.getMessage());
            return false;

        }

    }

    public boolean editOwner(String uuid, String new_uuid, String name, String owner){

        String sql = "UPDATE waypoints SET uuid = ? , " +
                "owner = ? " +
                "WHERE uuid = ? AND name = ?";

        try(var pstmt = connection.prepareStatement(sql)){

            pstmt.setString(1, new_uuid);
            pstmt.setString(2, owner);
            pstmt.setString(3, uuid);
            pstmt.setString(4, name);

            return pstmt.executeUpdate()>0;

        }
        catch (SQLException e){

            plugin.getLogger().severe("Error: " + e.getMessage());
            return false;

        }

    }

    public boolean editVisibility(String uuid, String name, boolean vis){

        String sql = "UPDATE waypoints SET isPublic = ? WHERE uuid = ? AND name = ?";

        try(var pstmt = connection.prepareStatement(sql)){

            pstmt.setBoolean(1, vis);
            pstmt.setString(2, uuid);
            pstmt.setString(3, name);

            return pstmt.executeUpdate()>0;

        }
        catch (SQLException e){

            plugin.getLogger().severe("Error: " + e.getMessage());
            return false;
        }


    }
    public boolean editName(String uuid, String name, String new_name){

        String sql = "UPDATE waypoints SET name = ? WHERE uuid = ? AND name = ?";

        try(var pstmt = connection.prepareStatement(sql)){

            pstmt.setString(1,new_name);
            pstmt.setString(2, uuid);
            pstmt.setString(3, name);

            return pstmt.executeUpdate() > 0;

        }
        catch (SQLException e){

            plugin.getLogger().severe("Error: " + e.getMessage());
            return false;

        }

    }

    public String getName(String uuid){

        String sql = "SELECT name FROM waypoints WHERE uuid = ?";

        try(var pstmt = connection.prepareStatement(sql)){

            pstmt.setString(1, uuid);

            var rs = pstmt.executeQuery();
            if(rs.next()){

                return rs.getString("name");

            }

        }
        catch (SQLException e){

            plugin.getLogger().severe("Error: " + e.getMessage());


        }
        return null;

    }
    public double getX(String uuid, String name){

        String sql = "SELECT x FROM waypoints WHERE uuid = ? AND name = ?";

        try(var pstmt = connection.prepareStatement(sql)){

            pstmt.setString(1, uuid);
            pstmt.setString(2, name);

            var rs = pstmt.executeQuery();

            if(rs.next()){

                return rs.getDouble("x");

            }

        }
        catch (SQLException e){

            plugin.getLogger().severe("Error: " + e.getMessage());

        }
        return 0;


    }
    public double getY(String uuid, String name){

        String sql = "SELECT y FROM waypoints WHERE uuid = ? AND name = ?";

        try(var pstmt = connection.prepareStatement(sql)){

            pstmt.setString(1, uuid);
            pstmt.setString(2, name);

            var rs = pstmt.executeQuery();

            if(rs.next()){

                return rs.getDouble("y");

            }

        }
        catch (SQLException e){

            plugin.getLogger().severe("Error: " + e.getMessage());

        }
        return 0;

    }
    public double getZ(String uuid, String name){

        String sql = "SELECT z FROM waypoints WHERE uuid = ? AND name = ?";

        try(var pstmt = connection.prepareStatement(sql)){

            pstmt.setString(1, uuid);
            pstmt.setString(2, name);

            var rs = pstmt.executeQuery();

            if(rs.next()){

                return rs.getDouble("z");

            }

        }
        catch (SQLException e){

            plugin.getLogger().severe("Error: " + e.getMessage());

        }
        return 0;

    }
    public String getOwner(String uuid, String name){

        String sql = "SELECT owner FROM waypoints WHERE uuid = ? AND name = ?";

        try(var pstmt = connection.prepareStatement(sql)){

            pstmt.setString(1, uuid);
            pstmt.setString(2, name);

            var rs = pstmt.executeQuery();

            if(rs.next()){

                return rs.getString("owner");

            }

        }
        catch (SQLException e){

            plugin.getLogger().severe("Error: " + e.getMessage());

        }
        return null;

    }

    public double getYaw(String uuid, String name){

        String sql = "SELECT yaw FROM waypoints WHERE uuid = ? AND name = ?";

        try(var pstmt = connection.prepareStatement(sql)){

            pstmt.setString(1, uuid);
            pstmt.setString(2, name);

            var rs = pstmt.executeQuery();

            if(rs.next()){

                return rs.getDouble("yaw");

            }

        }
        catch (SQLException e){

            plugin.getLogger().severe("Error: " + e.getMessage());

        }
        return 0;

    }

    public double getPitch(String uuid, String name){

        String sql = "SELECT pitch FROM waypoints WHERE uuid = ? AND name = ?";

        try(var pstmt = connection.prepareStatement(sql)){

            pstmt.setString(1, uuid);
            pstmt.setString(2, name);

            var rs = pstmt.executeQuery();

            if(rs.next()){

                return rs.getDouble("pitch");

            }

        }
        catch (SQLException e){

            plugin.getLogger().severe("Error: " + e.getMessage());

        }
        return 0;

    }

    public String getWorld(String uuid, String name){

        String sql = "SELECT world FROM waypoints WHERE uuid = ? AND name = ?";

        try(var pstmt = connection.prepareStatement(sql)){

            pstmt.setString(1, uuid);
            pstmt.setString(2, name);

            var rs = pstmt.executeQuery();

            if(rs.next()){

                return rs.getString("world");

            }

        }
        catch (SQLException e){

            plugin.getLogger().severe("Error: " + e.getMessage());

        }
        return null;

    }
    public String getUUID(String owner, String name){

        String sql = "SELECT uuid FROM waypoints WHERE owner = ? AND name = ?";

        try(var pstmt = connection.prepareStatement(sql)){

            pstmt.setString(1, owner);
            pstmt.setString(2, name);

            var rs = pstmt.executeQuery();

            if(rs.next()){

                return rs.getString("uuid");

            }

        }
        catch (SQLException e){

            plugin.getLogger().severe("Error: " + e.getMessage());
            return null;
        }
        return null;


    }
    public Boolean getIsPublic(String uuid, String name){

        String sql = "SELECT isPublic FROM waypoints WHERE uuid = ? AND name = ?";

        try(var pstmt = connection.prepareStatement(sql)){

            pstmt.setString(1, uuid);
            pstmt.setString(2, name);

            var rs = pstmt.executeQuery();

            if(rs.next()){

                return rs.getBoolean("isPublic");

            }

        }
        catch (SQLException e){

            plugin.getLogger().severe("Error: " + e.getMessage());
            return null;

        }
        return null;

    }
    public List<String> getWaypointsNames(String uuid) {

        List<String> names = new ArrayList<>();
        String sql = "SELECT name FROM waypoints WHERE uuid = ?";

        try (var pstmt = connection.prepareStatement(sql)) {

            pstmt.setString(1, uuid);

            try (var rs = pstmt.executeQuery()){
                while (rs.next()){

                    names.add(rs.getString("name"));

                }
            }

        } catch (SQLException e){

            plugin.getLogger().severe("Error: " + e.getMessage());
        }

        return names;
    }
    public List<String> getPublicWaypointsNames(String uuid) {
        List<String> names = new ArrayList<>();

        String sql = "SELECT name FROM waypoints WHERE uuid = ? AND isPublic = ?";

        try(var pstmt = connection.prepareStatement(sql)){

            pstmt.setString(1, uuid);
            pstmt.setBoolean(2, true);

            try(var rs = pstmt.executeQuery()){

                while(rs.next()){

                    names.add(rs.getString("name"));

                }

            }

        }
        catch (SQLException e){

            plugin.getLogger().severe("Error: " + e.getMessage());

        }
        return names;


    }
    public List<String> getPrivateWaypointsNames(String uuid) {

        List<String> names = new ArrayList<>();

        String sql = "SELECT name FROM waypoints WHERE uuid = ? AND isPublic = ?";

        try(var pstmt = connection.prepareStatement(sql)){

            pstmt.setString(1, uuid);
            pstmt.setBoolean(2, false);

            try(var rs = pstmt.executeQuery()){

                while(rs.next()){

                    names.add(rs.getString("name"));

                }

            }

        }
        catch (SQLException e){

            plugin.getLogger().severe("Error: " + e.getMessage());

        }

        return names;

    }
    public List<String> getAllPublicWaypointsNames() {
        List<String> names = new ArrayList<>();

        String sql = "SELECT name FROM waypoints WHERE isPublic = ?";

        try(var pstmt = connection.prepareStatement(sql)){

            pstmt.setBoolean(1, true);

            try(var rs = pstmt.executeQuery()){

                while(rs.next()){

                    names.add(rs.getString("name"));

                }

            }


        }catch (SQLException e){

            plugin.getLogger().severe("Error: " + e.getMessage());

        }
        return names;


    }
    public List<Waypoints> getPrivateWaypoints(String ownerUUID) {
        List<Waypoints> result = new ArrayList<>();
        String sql = "SELECT name, x, y, z, world, yaw, pitch, uuid FROM waypoints WHERE uuid = ? AND isPublic = 0";
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setString(1, ownerUUID);
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                result.add(new Waypoints(
                        rs.getString("name"),
                        rs.getDouble("x"),
                        rs.getDouble("y"),
                        rs.getDouble("z"),
                        rs.getString("world"),
                        rs.getDouble("yaw"),
                        rs.getDouble("pitch"),
                        ownerUUID,
                        false
                ));
            }
        } catch (SQLException e) {

            plugin.getLogger().severe("Error: " + e.getMessage());

        }

        return result;

    }

    public List<Waypoints> getAllPublicWaypoints(){

        List<Waypoints> result = new ArrayList<>();

        String sql = "SELECT name, x, y, z, world, yaw, pitch, uuid FROM waypoints WHERE isPublic = 1";

        try (var pstmt = connection.prepareStatement(sql)){

            ResultSet rs = pstmt.executeQuery();

            while (rs.next()) {

                result.add(new Waypoints(
                        rs.getString("name"),
                        rs.getDouble("x"),
                        rs.getDouble("y"),
                        rs.getDouble("z"),
                        rs.getString("world"),
                        rs.getDouble("yaw"),
                        rs.getDouble("pitch"),
                        rs.getString("uuid"),
                        true
                ));

            }
        } catch (SQLException e) {

            plugin.getLogger().severe("Error: " + e.getMessage());

        }

        return result;

    }
    public List<Waypoints> getPublicWaypoints(String uuid){

        List<Waypoints> result = new ArrayList<>();

        String sql = "SELECT name, x, y, z, world, yaw, pitch FROM waypoints WHERE isPublic = 1 AND uuid = ?";

        try (var pstmt = connection.prepareStatement(sql)){

            pstmt.setString(1, uuid);
            ResultSet rs = pstmt.executeQuery();

            while (rs.next()) {

                result.add(new Waypoints(
                        rs.getString("name"),
                        rs.getDouble("x"),
                        rs.getDouble("y"),
                        rs.getDouble("z"),
                        rs.getString("world"),
                        rs.getDouble("yaw"),
                        rs.getDouble("pitch"),
                        uuid,
                        true
                ));

            }

        } catch (SQLException e) {

            plugin.getLogger().severe("Error: " + e.getMessage());

        }

        return result;

    }

}