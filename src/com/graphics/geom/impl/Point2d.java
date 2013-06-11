package com.graphics.geom.impl;

import com.graphics.geom.Geometry;

public class Point2d implements Geometry {
	public double x, y;

	public Point2d(double x, double y) {
		this.x = x;
		this.y = y;
	}

	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder();
		sb.append("[X: ").append(x).append(", Y: ").append(y).append("]");
		return sb.toString();
	}
}
