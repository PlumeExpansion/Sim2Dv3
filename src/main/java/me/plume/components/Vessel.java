package me.plume.components;

public abstract class Vessel {
	private String name;
	private double x, y, vx, vy, ax, ay;
	private double nAx, nAy;
	private double hitPoints;
	private boolean immune;
	public Vessel(String name, double x, double y) {
		this.x = x;
		this.y = y;
		this.name = name;
	}
	public String getName() {return name;}
	public double getHitpoints() {return hitPoints;}
	public boolean getImmune() {return immune;}
	public void setImmune(boolean immune) {this.immune = immune;}
	public void damage(double damage) {hitPoints-=damage;}
	public void setX(double x) {this.x=x;}
	public void setY(double y) {this.y=y;}
	public void setAx(double ax) {this.nAx=ax;}
	public void setAy(double ay) {this.nAy=ay;}
	public void setVx(double vx) {this.vx = vx;}
	public void setVy(double vy) {this.vy = vy;}
	public double getX() {return x;}
	public double getY() {return y;}
	public double getVx() {return vx;}
	public double getVy() {return vy;}
	public double getV() {return Math.sqrt(vx*vx+vy*vy);}
	public double getAx() {return ax;}
	public double getAy() {return ay;}
	public void syncKinematics() {
		ax = nAx;
		ay = nAy;
	}
	public abstract double getDamage();
	public abstract boolean checkCollision(Vessel v);
	public abstract void onTick(double time, double dt);
	public abstract void onRemove();
	public abstract Marker genMarker();
	
	public double dist(Vessel v) {
		double dx = this.x-x;
		double dy = this.y-y;
		return Math.sqrt(dx*dx+dy*dy);
	}
	public double vRelative(Vessel v) {
		double vRx = this.vx-vx;
		double vRy = this.vy-vy;
		return Math.sqrt(vRx*vRx+vRy*vRy);
	}
}
