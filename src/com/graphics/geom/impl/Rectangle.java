package com.graphics.geom.impl;

import com.graphics.geom.Shape;
import com.graphics.geom.Transform3d;

public class Rectangle implements Shape, Transform3d {
	public Point3d p0, p1, p2, p3;

	public Rectangle(Point3d[] points) {
		this(points[0], points[1], points[2], points[3]);
	}

	public Rectangle(Point3d p0, Point3d p1, Point3d p2, Point3d p3) {
		this.p0 = p0;
		this.p1 = p1;
		this.p2 = p2;
		this.p3 = p3;
	}

	@Override
	public void translate(double x, double y, double z) {
		this.p0.translate(x, y, z);
		this.p1.translate(x, y, z);
		this.p2.translate(x, y, z);
		this.p3.translate(x, y, z);
	}

	@Override
	public void rotate(Point3d about, double x, double y, double z) {
		this.p0.rotate(about, x, y, z);
		this.p1.rotate(about, x, y, z);
		this.p2.rotate(about, x, y, z);
		this.p3.rotate(about, x, y, z);
	}
}
