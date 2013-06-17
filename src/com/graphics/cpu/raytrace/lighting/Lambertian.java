package com.graphics.cpu.raytrace.lighting;

import java.util.HashSet;
import java.util.Set;

import com.graphics.cpu.raytrace.Ray;
import com.graphics.cpu.raytrace.acceleration.IntersectionAlgorithm;
import com.graphics.cpu.raytrace.acceleration.IntersectionBundle;
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
	static final Vector3d WHITE = new Vector3d(MAX_ELEMENT_COLOR, MAX_ELEMENT_COLOR, MAX_ELEMENT_COLOR);

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
	public int render(Ray ray, IntersectionBundle intersection, ModelTriangle triangle) {

		Material mtl = triangle.mtl;
		Point3d p = ray.getPoint(intersection.t);

		Vector3d mtlColors = new Vector3d(0, 0, 0);
		for (int i = 0; i < 3; i++) {
			int color = triangle.colors[i];

			double modifier = intersection.gamma;
			switch (i) {
			case 1:
				modifier = intersection.alpha;
				break;
			case 2:
				modifier = intersection.beta;
				break;

			default:
				break;
			}

			// Separate RGB into Vector3d
			mtlColors.x += (color >> 16 & 0xFF) * modifier;
			mtlColors.y += (color >> 8 & 0xFF) * modifier;
			mtlColors.z += (color >> 0 & 0xFF) * modifier;
		}

		Vector3d rgb = new Vector3d(0, 0, 0);

		// LAMBERTIAN
		Vector3d n = triangle.normal;
		Vector3d L = p.minus(light);

		double nDotL = n.dot(L.normalize());
		if (nDotL > 0) {
			double distFromLight = L.mag();
			double dropoff = distFromLight > 0 ? 1.0 / (Math.pow(distFromLight, 2)) : 1;

			Vector3d rgbDiffuse = mtl.Kd.times(mtlColors).times(nDotL * dropoff * intensity);
			rgb.plusEquals(rgbDiffuse);
		}

		// PHONG
		Vector3d R = L.minus(n.times(2 * nDotL).normalize()).normalize();
		Vector3d V = ray.d.times(-1).normalize();

		double rDotV = R.dot(V);
		if (rDotV < 0) {
			Vector3d rgbSpecular = mtl.Ks.times(Math.pow(rDotV, mtl.Ns)).times(WHITE);
			rgb.plusEquals(rgbSpecular);
		}

		Vector3d reflectiveColor = new Vector3d(0, 0, 0);

		Vector3d rgbAmbient = mtl.Ka.times(WHITE);
		rgb.plusEquals(rgbAmbient).plusEquals(reflectiveColor);

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
