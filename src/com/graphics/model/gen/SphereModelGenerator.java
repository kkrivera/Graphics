package com.graphics.model.gen;

import java.util.HashSet;
import java.util.Set;

import com.graphics.geom.impl.Point3d;
import com.graphics.geom.impl.Vector3d;
import com.graphics.model.Model;
import com.graphics.model.geom.ModelTriangle;
import com.graphics.model.geom.Polygon;

public class SphereModelGenerator extends BaseModelGenerator {

	double detail = 1;
	double r = 1;
	Point3d center;

	public SphereModelGenerator(Point3d center, double r, double detail) {
		this.detail = detail;
		this.r = r;
		this.center = center;
	}

	@Override
	public Model generate() {
		Set<ModelTriangle> triangles = new HashSet<ModelTriangle>();
		Vector3d n = new Vector3d(0, 1, 0);

		double phiStep = Math.PI * detail;
		double thetaStep = phiStep * 2;
		for (double phi = 0; phi < Math.PI; phi += phiStep) {
			for (double theta = 0; theta < Math.PI * 2; theta += thetaStep) {

				Polygon p = new Polygon(new Point3d[] { getPt(r, phi, theta), getPt(r, phi, theta + thetaStep), getPt(r, phi + phiStep, theta + thetaStep),
						getPt(r, phi + phiStep, theta) }, new Vector3d[] { n, n, n, n }, new int[] { color, color, color, color }, mtl);
				p.translate(center.x, center.y, center.z);

				triangles.addAll(p.modelTriangles);
			}
		}

		return new Model("Generated Sphere Model", triangles);
	}

	private Point3d getPt(double r, double phi, double theta) {
		return new Point3d(r * Math.sin(theta) * Math.cos(phi), r * Math.sin(theta) * Math.sin(phi), r * Math.cos(theta));
	}
}
