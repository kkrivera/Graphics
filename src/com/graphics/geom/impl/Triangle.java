package com.graphics.geom.impl;

import com.graphics.geom.Shape;
import com.graphics.geom.Transform3d;

public class Triangle implements Shape, Transform3d {
	public Point3d[] points = new Point3d[3];

	public Triangle(Point3d[] points) {
		this(points[0], points[1], points[2]);
	}

	public Triangle(Point3d p0, Point3d p1, Point3d p2) {
		this.points[0] = p0;
		this.points[1] = p1;
		this.points[2] = p2;
	}

	@Override
	public void translate(double x, double y, double z) {
		for (Point3d point : points) {
			point.translate(x, y, z);
		}
	}

	@Override
	public void rotate(Point3d about, double x, double y, double z) {
		for (Point3d point : points) {
			point.rotate(about, x, y, z);
		}
	}

	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder();
		sb.append("{P0: ").append(points[0]).append(", P1: ").append(points[1]).append(", P2: ").append(points[2]).append("}");
		return sb.toString();
	}

	@Override
	public Object clone() throws CloneNotSupportedException {
		return new Triangle((Point3d) points[0].clone(), (Point3d) points[1].clone(), (Point3d) points[2].clone());
	}
}
