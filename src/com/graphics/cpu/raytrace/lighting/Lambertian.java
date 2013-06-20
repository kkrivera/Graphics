package com.graphics.cpu.raytrace.lighting;

import java.util.Collections;
import java.util.Map;
import java.util.Set;

import com.graphics.cpu.raytrace.Ray;
import com.graphics.cpu.raytrace.RayTracer;
import com.graphics.cpu.raytrace.acceleration.IntersectionAlgorithm;
import com.graphics.cpu.raytrace.acceleration.IntersectionBundle;
import com.graphics.cpu.raytrace.properties.PropertyLoader;
import com.graphics.geom.impl.Point3d;
import com.graphics.geom.impl.Vector3d;
import com.graphics.model.Material;
import com.graphics.model.Model;
import com.graphics.model.Scene;
import com.graphics.model.geom.ModelTriangle;
import com.graphics.window.Window;

public class Lambertian implements LightingAlgorithm {

	static final int MAX_ELEMENT_COLOR = 255;
	static final int MAX_COLOR = Window.getColor(MAX_ELEMENT_COLOR, MAX_ELEMENT_COLOR, MAX_ELEMENT_COLOR);
	static final Vector3d WHITE = new Vector3d(MAX_ELEMENT_COLOR, MAX_ELEMENT_COLOR, MAX_ELEMENT_COLOR);

	Scene scene;
	IntersectionAlgorithm intersectionAlgorithm;
	Vector3d light;
	double intensity = 1;
	int maxReflections = 1;

	@Override
	public void init(Set<Ray> rays, Model... models) {
		this.scene = new Scene("RayTrace Scene", models);

		PropertyLoader propertyLoader = new PropertyLoader();
		try {
			intersectionAlgorithm = IntersectionAlgorithm.class.cast(Class.forName(propertyLoader.getProperty("acceleration.algorithm.class")).newInstance());
		} catch (Exception e) {
			throw new RuntimeException(e);
		}

		light = new Vector3d(propertyLoader.getProperty("light.point").split(","));
		intensity = Double.parseDouble(propertyLoader.getProperty("light.intensity"));
		maxReflections = Integer.parseInt(propertyLoader.getProperty("light.reflections.max"));
	}

	@Override
	public int render(Ray ray, IntersectionBundle intersection, ModelTriangle triangle) {
		return render(ray, intersection, triangle, 0);
	}

	private int render(Ray ray, IntersectionBundle intersection, ModelTriangle triangle, int reflectiveBounces) {
		Material mtl = triangle.mtl;

		Vector3d n = triangle.normal;
		Point3d p = ray.getPoint(intersection.t).plus(n.times(-.01));
		Vector3d L = p.minus(light);

		// Provide a shadow modification
		double shadowMod = 1;
		if (underShadow(new Ray(p, light.minus(p)))) {
			shadowMod = .1;
		}

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
		double nDotL = n.dot(L.normalize());
		if (nDotL > 0) {
			double distFromLight = L.mag();
			double dropoff = distFromLight > 0 ? 1.0 / (Math.pow(distFromLight, 2)) : 1;

			Vector3d rgbDiffuse = mtl.Kd.times(mtlColors).times(nDotL * dropoff * intensity * shadowMod);
			rgb.plusEquals(rgbDiffuse);
		}

		// PHONG
		Vector3d I = ray.d;
		Vector3d R = I.minus(n.times(2 * I.dot(n))).normalize();
		Vector3d V = ray.d.times(-1).normalize();

		double rDotV = R.dot(V);
		if (rDotV > 0) {
			Vector3d rgbSpecular = mtl.Ks.times(Math.pow(rDotV, mtl.Ns)).times(WHITE).times(shadowMod);
			rgb.plusEquals(rgbSpecular);
		}

		Vector3d reflectiveColor = reflect(new Ray(p, R), reflectiveBounces).times(mtl.Rr * shadowMod);
		rgb.plusEquals(reflectiveColor);

		Vector3d rgbAmbient = mtl.Ka.times(WHITE).times(shadowMod);
		rgb.plusEquals(rgbAmbient);

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

	private Vector3d reflect(Ray reflection, int reflectiveBounces) {

		if (reflectiveBounces >= maxReflections) {
			return new Vector3d(0, 0, 0);
		}

		Map<Ray, Map<ModelTriangle, IntersectionBundle>> intersections = intersectionAlgorithm.intersect(scene, Collections.singleton(reflection));

		if (intersections.isEmpty()) {
			return new Vector3d(0, 0, 0);
		}

		Map<ModelTriangle, IntersectionBundle> results = intersections.get(reflection);
		ModelTriangle triangle = RayTracer.closestTriangle(results);

		int reflectiveColor = render(reflection, results.get(triangle), triangle, reflectiveBounces + 1);

		return new Vector3d(Window.splitColor(reflectiveColor));
	}

	private boolean underShadow(Ray lightRay) {
		Map<Ray, Map<ModelTriangle, IntersectionBundle>> intersections = intersectionAlgorithm.intersect(scene, Collections.singleton(lightRay));

		if (intersections.isEmpty()) {
			return false;
		}

		for (IntersectionBundle intersectionBundle : intersections.get(lightRay).values()) {
			if (intersectionBundle.t > 0.01 && intersectionBundle.t < 1) {
				return true;
			}
		}

		return false;
	}
}
