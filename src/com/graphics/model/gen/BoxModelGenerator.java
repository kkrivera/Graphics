package com.graphics.model.gen;

import java.util.HashSet;
import java.util.Set;

import com.graphics.geom.impl.Point3d;
import com.graphics.geom.impl.Vector3d;
import com.graphics.model.Model;
import com.graphics.model.geom.ModelTriangle;
import com.graphics.model.geom.Polygon;

public class BoxModelGenerator extends BaseModelGenerator {

	private Point3d center;
	double length, width, height;

	public BoxModelGenerator(Point3d center, double length, double width, double height) {
		this.center = center;
		this.length = length;
		this.width = width;
		this.height = height;
	}

	@Override
	public Model generate() {
		Set<ModelTriangle> modelTriangles = new HashSet<ModelTriangle>();

		Vector3d i = new Vector3d(1, 0, 0);
		Vector3d ni = i.times(-1);
		Vector3d j = new Vector3d(0, 1, 0);
		Vector3d nj = j.times(-1);
		Vector3d k = new Vector3d(0, 0, 1);
		Vector3d nk = k.times(-1);

		double x = width / 2.0;
		double y = height / 2.0;
		double z = length / 2.0;

		int[] colors = { color, color, color, color };
		Set<Polygon> polygons = new HashSet<Polygon>();

		/*
		 * Generates the box centered at the origin
		 */

		// FRONT
		polygons.add(new Polygon(new Point3d[] { new Point3d(-x, -y, z), new Point3d(x, -y, z), new Point3d(x, y, z), new Point3d(-x, y, z) }, new Vector3d[] {
				k.clone(), k.clone(), k.clone(), k.clone() }, colors, mtl));

		// BACK
		polygons.add(new Polygon(new Point3d[] { new Point3d(x, -y, -z), new Point3d(-x, -y, -z), new Point3d(-x, y, -z), new Point3d(x, y, -z) },
				new Vector3d[] { nk.clone(), nk.clone(), nk.clone(), nk.clone() }, colors, mtl));

		// TOP
		polygons.add(new Polygon(new Point3d[] { new Point3d(-x, y, z), new Point3d(x, y, z), new Point3d(x, y, -z), new Point3d(-x, y, -z) }, new Vector3d[] {
				j.clone(), j.clone(), j.clone(), j.clone() }, colors, mtl));

		// BOTTOM
		polygons.add(new Polygon(new Point3d[] { new Point3d(x, -y, z), new Point3d(-x, -y, z), new Point3d(-x, -y, -z), new Point3d(x, -y, -z) },
				new Vector3d[] { nj.clone(), nj.clone(), nj.clone(), nj.clone() }, colors, mtl));

		// RIGHT
		polygons.add(new Polygon(new Point3d[] { new Point3d(x, -y, z), new Point3d(x, -y, -z), new Point3d(x, y, -z), new Point3d(x, y, z) }, new Vector3d[] {
				i.clone(), i.clone(), i.clone(), i.clone() }, colors, mtl));

		// LEFT
		polygons.add(new Polygon(new Point3d[] { new Point3d(-x, -y, z), new Point3d(-x, y, z), new Point3d(-x, y, -z), new Point3d(-x, -y, -z) },
				new Vector3d[] { ni.clone(), ni.clone(), ni.clone(), ni.clone() }, colors, mtl));

		for (Polygon polygon : polygons) {
			// Translate the box from the origin to its intended center
			polygon.translate(center.x, center.y, center.z);

			modelTriangles.addAll(polygon.modelTriangles);
		}

		return new Model("Generated Box Model", modelTriangles);
	}

}
