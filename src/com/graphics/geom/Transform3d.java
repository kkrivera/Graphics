package com.graphics.geom;

import com.graphics.geom.impl.Point3d;

public interface Transform3d {

	/**
	 * Translates the {@link Geometry} by x,y,z
	 */
	public void translate(double x, double y, double z);

	/**
	 * Rotates the {@link Geometry} to a certain radian about a {@link Point3d}
	 */
	public void rotate(Point3d about, double x, double y, double z);
}
