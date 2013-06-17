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
	public Map<Ray, Map<ModelTriangle, IntersectionBundle>> intersect(final Model model, Set<Ray> rays) {

		ThreadManager threadManager = new ThreadManager();

		Map<Ray, Map<ModelTriangle, IntersectionBundle>> intersections = new HashMap<Ray, Map<ModelTriangle, IntersectionBundle>>();
		threadManager.executeForResult(new HashSet<Ray>(rays), new ThreadManager.ThreadedAction<Ray, Map<Ray, Map<ModelTriangle, IntersectionBundle>>>() {
			@Override
			public Map<Ray, Map<ModelTriangle, IntersectionBundle>> execute(Collection<Ray> input) {
				Map<Ray, Map<ModelTriangle, IntersectionBundle>> intersections = new HashMap<Ray, Map<ModelTriangle, IntersectionBundle>>();

				for (Ray ray : input) {
					for (ModelTriangle modelTriangle : model.triangles) {

						IntersectionBundle bundle = ray.intersects(modelTriangle);
						if (bundle != null) {

							// Add intersected ray
							if (!intersections.containsKey(ray)) {
								intersections.put(ray, new HashMap<ModelTriangle, IntersectionBundle>());
							}

							intersections.get(ray).put(modelTriangle, bundle);
						}
					}
				}

				return intersections;
			}
		}, intersections);

		return intersections;
	}
}
