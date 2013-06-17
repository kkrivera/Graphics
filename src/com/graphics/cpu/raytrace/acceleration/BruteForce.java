package com.graphics.cpu.raytrace.acceleration;

import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import com.graphics.cpu.ThreadManager;
import com.graphics.cpu.raytrace.Ray;
import com.graphics.model.Model;
import com.graphics.model.geom.ModelTriangle;

public class BruteForce implements IntersectionAlgorithm {

	@Override
	public Map<Ray, Map<ModelTriangle, Double>> intersect(final Model model, Set<Ray> rays) {

		ThreadManager threadManager = new ThreadManager();

		Map<Ray, Map<ModelTriangle, Double>> intersections = new HashMap<Ray, Map<ModelTriangle, Double>>();
		threadManager.executeForResult(new HashSet<Ray>(rays), new ThreadManager.ThreadedAction<Ray, Map<Ray, Map<ModelTriangle, Double>>>() {
			@Override
			public Map<Ray, Map<ModelTriangle, Double>> execute(Collection<Ray> input) {
				Map<Ray, Map<ModelTriangle, Double>> intersections = new HashMap<Ray, Map<ModelTriangle, Double>>();

				for (Ray ray : input) {
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
		}, intersections);

		return intersections;
	}
}
