package com.graphics.geom.impl;

public class Vector3d extends Point3d {
	public Vector3d(double x, double y, double z) {
		super(x, y, z);
	}

	public Vector3d(double[] xyz) {
		this(xyz[0], xyz[1], xyz[2]);
	}

	public Vector3d(String[] xyz) {
		this(Double.parseDouble(xyz[0]), Double.parseDouble(xyz[1]), Double.parseDouble(xyz[2]));
	}

	public Vector3d plus(Vector3d v) {
		return this.plus(v.x, v.y, v.z);
	}

	public Vector3d plus(double x, double y, double z) {
		return new Vector3d(this.x + x, this.y + y, this.z + z);
	}

	public Vector3d minus(Vector3d v) {
		return new Vector3d(this.x - v.x, this.y - v.y, this.z - v.z);
	}

	public Vector3d over(double div) {
		return new Vector3d(x / div, y / div, z / div);
	}

	public Vector3d over(Vector3d v) {
		return new Vector3d(x / v.x, y / v.y, z / v.z);
	}

	public Vector3d times(double mult) {
		return new Vector3d(x * mult, y * mult, z * mult);
	}

	public Vector3d times(Vector3d v) {
		return new Vector3d(x * v.x, y * v.y, z * v.z);
	}

	public double dot(Vector3d v) {
		return this.x * v.x + this.y * v.y + this.z * v.z;
	}

	public Vector3d cross(Vector3d v) {
		return new Vector3d(this.y * v.z - this.z * v.y, this.z * v.x - this.x * v.z, this.x * v.y - this.y * v.x);
	}

	public Vector3d normalize() {
		return this.over(mag());
	}

	public double mag() {
		return Math.sqrt(Math.pow(x, 2.0) + Math.pow(y, 2.0) + Math.pow(z, 2.0));
	}

	@Override
	public Object clone() throws CloneNotSupportedException {
		return new Vector3d(x, y, z);
	}

}
