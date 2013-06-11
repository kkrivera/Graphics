package com.graphics.cpu.raytrace.acceleration;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import com.graphics.cpu.ThreadManager;
import com.graphics.cpu.raytrace.Ray;
import com.graphics.model.Model;
import com.graphics.model.geom.ModelTriangle;

public class BruteForce implements IntersectionAlgorithm {

	@Override
	public Map<Ray, Map<ModelTriangle, Double>> intersect(Model model, Set<Ray> rays) {

		ThreadManager tm = new ThreadManager();

		Map<Ray, Map<ModelTriangle, Double>> intersections = new HashMap<Ray, Map<ModelTriangle, Double>>();

		for (Ray ray : rays) {
			for (ModelTriangle modelTriangle : model.triangles) {

				double t = ray.intersects(modelTriangle);
				if (t > 0) {

					// Add intersected ray
					if (!intersections.containsKey(ray)) {
						intersections.put(ray, new HashMap<ModelTriangle, Double>());
					}

					intersections.get(ray).put(modelTriangle, new Double(t));
				}
			}
		}

		return intersections;
	}
}
