package com.graphics.geom.impl;

import com.graphics.geom.Transform3d;

public class Box implements Transform3d {
	// {0-3} +Z points, {4-7} -Z points
	public Point3d[] points = new Point3d[8];

	// {MIN,MAX}
	Point3d[] range = null;
	Point3d center = null;

	public Box(Point3d[] points) {
		this.points = points;
	}

	// C1 contains point on +z face, C2 contains point on -z face
	public Box(Point3d c1, Point3d c2) {
		// Construct +Z points first
		Vector3d c1Dir = c2.minus(c1);
		points[0] = c1;
		points[1] = c1.plus(c1Dir.x, 0, 0);
		points[2] = c1.plus(c1Dir.x, c1Dir.y, 0);
		points[3] = c1.plus(0, c1Dir.y, 0);

		// Construct -Z points
		Vector3d c2Dir = c1.minus(c2);
		points[4] = c2;
		points[5] = c2.plus(c2Dir.x, 0, 0);
		points[6] = c2.plus(c2Dir.x, c2Dir.y, 0);
		points[7] = c2.plus(0, c2Dir.y, 0);

		center = new Point3d((c1.x + c2.x) / 2.0, (c1.y + c2.y) / 2.0, (c1.z + c2.z) / 2.0);
	}

	public Point3d[] getRange() {
		if (this.range != null) {
			return this.range;
		}

		Point3d[] range = new Point3d[] { new Point3d(points[0]), new Point3d(points[0]) };

		for (Point3d p : points) {
			if (p.x < range[0].x && p.y < range[0].y && p.z < range[0].z) {
				range[0] = new Point3d(p);
			} else if (p.x > range[1].x && p.y > range[1].y && p.z > range[1].z) {
				range[1] = new Point3d(p);
			}
		}

		this.range = range;
		return this.range;
	}

	public Point3d getCenter() {
		getRange();

		if (this.center != null) {
			this.center = this.range[0].plus(this.range[1]).over(2.0);
		}
		return this.center;
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
}
