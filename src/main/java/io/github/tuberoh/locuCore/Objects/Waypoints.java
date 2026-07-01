package io.github.tuberoh.locuCore.Objects;

public class Waypoints {

    private String name;
    private double x;
    private double y;
    private double z;
    private String world;
    private String owner_uuid;
    private boolean status;
    private double yaw;
    private double pitch;

    public Waypoints(String name, double x, double y, double z, String world, double yaw, double pitch, String owner_uuid, boolean status){

        this.name = name;
        this.x = x;
        this.y = y;
        this.z = z;
        this.world = world;
        this.yaw = yaw;
        this.pitch = pitch;
        this.owner_uuid= owner_uuid;
        this.status = status;

    }

    public String getName() {
        return name;
    }

    public double getX() {
        return x;
    }

    public double getY() {
        return y;
    }

    public double getZ() {
        return z;
    }

    public String getWorld() {
        return world;
    }

    public String getOwner_uuid() {
        return owner_uuid;
    }

    public boolean getStatus() {return status;}

    public double getYaw(){return yaw;}

    public double getPitch(){return pitch;}

    public void setStatus(boolean status) {
        this.status = status;
    }
}
