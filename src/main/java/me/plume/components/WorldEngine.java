package me.plume.components;

import java.util.LinkedList;
import java.util.List;
import java.util.OptionalDouble;
import java.util.function.Consumer;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public abstract class WorldEngine {
	private List<Vessel> mutColliders = new LinkedList<>();
	private List<Vessel> excColliders = new LinkedList<>();
	private List<Vessel> mutColliderQueue = new LinkedList<>();
	private List<Vessel> excColliderQueue = new LinkedList<>();
	private double time, delay;
	private Consumer<Double> onTick, onPortalTick;
	private Thread thread;
	private boolean terminated;
	public WorldEngine(double tickFreq, double maxTravel) {
		delay = 1.0/tickFreq;
		thread = new Thread(() -> {
			OptionalDouble maxVr;
			double dt;
			int n;
			double r;
			while (!terminated) {
				mutColliders.forEach(v -> v.onTick(time, delay));
				excColliders.forEach(v -> v.onTick(time, delay));
				mutColliders.forEach(v -> v.syncKinematics());
				excColliders.forEach(v -> v.syncKinematics());
				mutColliders.addAll(mutColliderQueue);
				excColliders.addAll(excColliderQueue);
				mutColliderQueue.clear();
				excColliderQueue.clear();
				maxVr = mutColliders.stream().mapToDouble(v -> {
					OptionalDouble maxVrl = mutColliders.stream().mapToDouble(v1 -> v.vRelative(v1)).max();
					return maxVrl.isPresent()? maxVrl.getAsDouble() : 0;
				}).max();
				if (maxVr.isPresent() && maxTravel/maxVr.getAsDouble()<delay) {
					dt = maxVr.getAsDouble();
					n = (int) (delay/dt);
					r = delay%dt;
					for (int i=0; i<n; i++) move(dt);
					move(r);
				} else move(delay);
			}
		});
	}
	private void move(double dt) {
		time += dt;
		mutColliders.forEach(v -> moveVessel(v, dt));
		excColliders.forEach(v -> moveVessel(v, dt));
		mutColliders.forEach(v -> {
			mutColliders.forEach(v1 -> {
				if (v1==v || !v.checkCollision(v1)) return;
				v.damage(v1.getDamage());
			});
			excColliders.forEach(v1 -> {
				if (!v.checkCollision(v1)) return;
				v.damage(v1.getDamage());
				v1.damage(v.getDamage());
			});
		});
		if (onTick!=null) onTick.accept(time);
		if (onPortalTick!=null) onPortalTick.accept(time);
		mutColliders = mutColliders.stream().filter(v -> v.getImmune()? true : v.getHitpoints()>0).collect(Collectors.toList());
		excColliders = excColliders.stream().filter(v -> v.getImmune()? true : v.getHitpoints()>0).collect(Collectors.toList());
	}
	private void moveVessel(Vessel v, double dt) {
		v.setVx(v.getVx()+v.getAx()*dt);
		v.setVy(v.getVy()+v.getAy()*dt);
		v.setX(v.getX()+v.getVx()*dt+v.getAx()*dt*dt/2);
		v.setY(v.getY()+v.getVy()*dt+v.getAy()*dt*dt/2);
	}
	public abstract void init();
	public List<Marker> genMarkers() {
		return Stream.concat(mutColliders.stream(), excColliders.stream())
				.map(v -> v.genMarker()).collect(Collectors.toList());
	}
	public void start() {
		time = 0;
		mutColliders.clear();
		excColliders.clear();
		init();
		thread.start();
	}
	public void stop() {terminated = true;}
	public void resume() {thread.start();}
	public void setOnTick(Consumer<Double> onTick) {this.onTick=onTick;}
	public void setOnPortalTick(Consumer<Double> onTick) {this.onPortalTick=onTick;}
	public void addMutCollider(Vessel v) {mutColliderQueue.add(v);}
	public void addExcCollider(Vessel v) {excColliderQueue.add(v);}
}
