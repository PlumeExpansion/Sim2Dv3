package me.plume.components;

import java.util.List;

import javafx.scene.Scene;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.layout.Pane;

public abstract class WorldPortal {
	private Scene scene;
	private GraphicsContext c;
	private double nextFrame, delay, buffer;
	private long now, lastFrameMillis;
	private List<Marker> markers;
	public WorldPortal(WorldEngine world) {
		world.setOnPortalTick(time -> {
			now = System.currentTimeMillis();
			if (time>=nextFrame) try {
				buffer = delay - (now-lastFrameMillis)/1000;
				Thread.sleep((long) buffer*1000);
			} catch (Exception e) {}
			lastFrameMillis = now;
			nextFrame = time+delay;
			markers = world.genMarkers();
			render();
		});
		
		Pane pane = new Pane();
		Canvas canvas = new Canvas();
		pane.getChildren().add(canvas);
		c = canvas.getGraphicsContext2D();
		scene = new Scene(pane);
		init(world, scene);
		scene.widthProperty().addListener((obv, ov, nv) -> canvas.setWidth(nv.doubleValue()));
		scene.heightProperty().addListener((obv, ov, nv) -> canvas.setHeight(nv.doubleValue()));
	}
	private void render() {
		markers.forEach(m -> m.render(this, c));
	}
	public void setTimeScale(double frameFreq) {delay=1.0/frameFreq;}
	public Scene getScene() {return scene;}
	public abstract void init(WorldEngine world, Scene scene);
	public abstract double screenX(double x);
	public abstract double screenY(double y);
	public abstract double worldX(double x);
	public abstract double worldY(double y);
}
