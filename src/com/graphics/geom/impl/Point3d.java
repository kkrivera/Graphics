package com.graphics.geom.impl;

import com.graphics.geom.Geometry;
import com.graphics.geom.Transform3d;

public class Point3d implements Geometry, Transform3d {

	public double x, y, z;

	public Point3d(Point3d p) {
		this(p.x, p.y, p.z);
	}

	public Point3d(double[] v) {
		this(v[0], v[1], v[2]);
	}

	public Point3d(double x, double y, double z) {
		this.x = x;
		this.y = y;
		this.z = z;
	}

	public Vector3d minus(Point3d p) {
		return new Vector3d(this.x - p.x, this.y - p.y, this.z - p.z);
	}

	public Point3d plus(double x, double y, double z) {
		return new Point3d(this.x + x, this.y + y, this.z + z);
	}

	@Override
	public void translate(double x, double y, double z) {
		this.x += x;
		this.y += y;
		this.z += z;
	}

	@Override
	public void rotate(Point3d about, double x, double y, double z) {

		// Change coordinate to world
		this.translate(-about.x, -about.y, -about.z);

		double Rx = this.x * (Math.cos(y) * Math.cos(z)) + this.y * (Math.cos(z) * Math.sin(x) * Math.sin(y) - Math.cos(x) * Math.sin(z)) + this.z
				* (Math.cos(x) * Math.cos(z) * Math.sin(y) + Math.sin(x) * Math.sin(z));
		double Ry = this.x * (Math.cos(y) * Math.sin(z)) + this.y * (Math.cos(x) * Math.cos(z) + Math.sin(x) * Math.sin(y) * Math.sin(z)) - this.z
				* (-Math.cos(z) * Math.sin(x) + Math.cos(x) * Math.sin(y) * Math.sin(z));
		double Rz = this.x * (-Math.sin(y)) + this.y * (Math.cos(y) * Math.sin(x)) + this.z * (Math.cos(x) * Math.cos(y));

		this.x = Rx;
		this.y = Ry;
		this.z = Rz;

		// Change coordinate back to original
		this.translate(about.x, about.y, about.z);
	}

	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder();
		sb.append("[X: ").append(x).append(", Y: ").append(y).append(", Z: ").append(z).append("]");
		return sb.toString();
	}

	public Point3d clone() {
		return new Point3d(x, y, z);
	}

	public double get(int index) {
		switch (index) {
		case 0:
			return x;
		case 1:
			return y;
		case 2:
			return z;
		default:
			throw new RuntimeException(index + " is Out of bounds");
		}
	}

	public void set(int index, double val) {
		switch (index) {
		case 0:
			x = val;
			break;
		case 1:
			y = val;
			break;
		case 2:
			z = val;
			break;
		default:
			throw new RuntimeException(index + " is Out of bounds");
		}
	}
}
