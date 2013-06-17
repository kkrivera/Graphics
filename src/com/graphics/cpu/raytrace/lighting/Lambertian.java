package com.graphics.cpu.raytrace.lighting;

import java.util.HashSet;
import java.util.Set;

import com.graphics.cpu.raytrace.Ray;
import com.graphics.cpu.raytrace.acceleration.IntersectionAlgorithm;
import com.graphics.cpu.raytrace.properties.PropertyLoader;
import com.graphics.geom.impl.Point3d;
import com.graphics.geom.impl.Vector3d;
import com.graphics.model.Material;
import com.graphics.model.Model;
import com.graphics.model.geom.ModelTriangle;
import com.graphics.window.Window;

public class Lambertian implements LightingAlgorithm {

	static final int MAX_ELEMENT_COLOR = 255;
	static final int MAX_COLOR = Window.getColor(MAX_ELEMENT_COLOR, MAX_ELEMENT_COLOR, MAX_ELEMENT_COLOR);

	IntersectionAlgorithm intersectionAlgorithm;
	Set<ModelTriangle> scene = new HashSet<ModelTriangle>();
	Vector3d light;
	double intensity = 1;

	@Override
	public void init(Set<Ray> rays, Model... models) {
		PropertyLoader propertyLoader = new PropertyLoader();
		try {
			intersectionAlgorithm = IntersectionAlgorithm.class.cast(Class.forName(propertyLoader.getProperty("acceleration.algorithm.class")).newInstance());
		} catch (Exception e) {
			throw new RuntimeException(e);
		}

		light = new Vector3d(propertyLoader.getProperty("light.point").split(","));
		intensity = Double.parseDouble(propertyLoader.getProperty("light.intensity"));
	}

	@Override
	public int render(Ray ray, double t, ModelTriangle triangle, Material mtl) {

		Point3d p = ray.getPoint(t);

		// TODO - barycentric value
		// int color = (triangle.colors[0] + triangle.colors[1]
		// +triangle.colors[2]) / 3;
		int mtlColor = triangle.colors[0];

		Vector3d mtlColors = new Vector3d(mtlColor >> 16 & 0xFF, mtlColor >> 8 & 0xFF, mtlColor >> 0 & 0xFF);

		Vector3d n = triangle.normal;
		Vector3d L = p.minus(light).normalize();

		double nDotL = n.dot(L);
		if (nDotL < 0) {
			nDotL = 0;
		}

		Vector3d R = n.times(-2 * nDotL).normalize();
		Vector3d V = ray.d.times(-1).normalize();

		double rDotV = R.dot(V);
		if (rDotV < 0) {
			rDotV = 0;
		}

		Vector3d reflectiveColor = new Vector3d(0, 0, 0);

		Vector3d rgbDiffuse = mtl.Kd.times(mtlColors).times(nDotL);
		Vector3d rgbSpecular = mtl.Ks.times(Math.pow(rDotV, mtl.Ns)).times(new Vector3d(MAX_ELEMENT_COLOR, MAX_ELEMENT_COLOR, MAX_ELEMENT_COLOR));
		Vector3d rgbAmbient = mtl.Ka;

		Vector3d rgb = rgbDiffuse.plus(rgbSpecular).plus(rgbAmbient).plus(reflectiveColor);

		// Clamp rgb values
		for (int i = 0; i < 3; i++) {
			double color = rgb.get(i);
			if (color < 0) {
				rgb.set(i, 0);
			} else if (color > MAX_ELEMENT_COLOR) {
				rgb.set(i, MAX_ELEMENT_COLOR);
			}
		}

		return Window.getColor((int) rgb.x, (int) rgb.y, (int) rgb.z);
	}
}
