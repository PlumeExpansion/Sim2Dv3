package me.plume.components;

import javafx.scene.canvas.GraphicsContext;

public abstract class Marker {
	private Vessel vessel;
	private double x, y;
	public Marker(Vessel vessel, double x, double y) {
		this.vessel = vessel;
		this.x = x;
		this.y = y;
	}
	public Vessel getVessel() {return vessel;}
	public double getX() {return x;}
	public double getY() {return y;}
	public abstract void render(WorldPortal portal, GraphicsContext c);
}
