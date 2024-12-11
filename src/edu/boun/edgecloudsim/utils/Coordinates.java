package edu.boun.edgecloudsim.utils;

public class Coordinates {

    private Integer X;
    private Integer Y;
    private Boolean isDead;
    private Integer id;

    private Double time;

    private Double energyConsumed;


    public Integer getX() {
        return X;
    }

    public Integer getY() {
        return Y;
    }

    public void setX(Integer x) {
        X = x;
    }

    public void setY(Integer y) {
        Y = y;
    }

    public Boolean isDead() {
        return isDead;
    }
    public Coordinates(int x, int y, boolean isDead, int id, Double time, Double energyConsumed){
        this.X=x;
        this.Y=y;
        this.isDead=isDead;
        this.id=id;
        this.time = time;
        this.energyConsumed = energyConsumed;
        if(isDead){
            this.energyConsumed = -1.0;
        }
    }

    @Override
    public String toString() {
        return "Coordinates{" +
                "X=" + X +
                ", Y=" + Y +
                ", isDead=" + isDead +
                ", id=" + id +
                ", time='" + time + '\'' +
                '}';
    }

    public Boolean getDead() {
        return isDead;
    }

    public void setDead(Boolean dead) {
        isDead = dead;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Double getTime() {
        return time;
    }

    public void setTime(Double time) {
        this.time = time;
    }

    public Double getEnergyConsumed() {
        return energyConsumed;
    }

    public void setEnergyConsumed(Double energyConsumed) {
        this.energyConsumed = energyConsumed;
    }
}
