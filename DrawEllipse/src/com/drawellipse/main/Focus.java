package com.drawellipse.main;

import java.awt.Point;

public class Focus {
	
	// For this example we use Point. Later will use LatLong.
	private final Point point;
	private final double weight;
	
	// Constructor
	public Focus(int x, int y, double weight) {
		this.point = new Point(x,y);
		this.weight = weight;
	}
	
	// Constructor
	public Focus(int x, int y) {
		this.point = new Point(x,y);
		this.weight = 1;
	}
	
	// Constructor
	public Focus(Point f, double weight) {
		this.point = f;
		this.weight = weight;
	}
	
	// Constructor
	public Focus(Point f) {
		this.point = f;
		this.weight = 1;
	}
	
	public Point getPoint() {
		return this.point;
	}
	
	public double getWeight() {
		return this.weight;
	}
}
