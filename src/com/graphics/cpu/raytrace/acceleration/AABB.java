package com.graphics.cpu.raytrace.acceleration;

import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import com.graphics.cpu.ThreadManager;
import com.graphics.cpu.raytrace.Ray;
import com.graphics.geom.impl.Box;
import com.graphics.geom.impl.Point3d;
import com.graphics.model.Model;
import com.graphics.model.geom.ModelTriangle;

public class AABB implements IntersectionAlgorithm {

	@Override
	public Map<Ray, Map<ModelTriangle, IntersectionBundle>> intersect(final Model model, Set<Ray> rays) {

		ThreadManager threadManager = new ThreadManager();

		final Box aabb = getAABB(model.triangles);
		Map<Ray, Map<ModelTriangle, IntersectionBundle>> triangles = new HashMap<Ray, Map<ModelTriangle, IntersectionBundle>>();
		threadManager.executeForResult(new HashSet<Ray>(rays), new ThreadManager.ThreadedAction<Ray, Map<Ray, Map<ModelTriangle, IntersectionBundle>>>() {
			@Override
			public Map<Ray, Map<ModelTriangle, IntersectionBundle>> execute(Collection<Ray> input) {

				Map<Ray, Map<ModelTriangle, IntersectionBundle>> triangles = new HashMap<Ray, Map<ModelTriangle, IntersectionBundle>>();
				for (Ray ray : input) {

					if (ray.intersects(aabb)) {
						for (ModelTriangle triangle : model.triangles) {

							IntersectionBundle intersection = ray.intersects(triangle);
							if (intersection != null) {
								if (!triangles.containsKey(ray)) {
									triangles.put(ray, new HashMap<ModelTriangle, IntersectionBundle>());
								}
								triangles.get(ray).put(triangle, intersection);
							}
						}
					}
				}

				return triangles;
			}
		}, triangles);

		return triangles;
	}

	public static Box getAABB(Set<ModelTriangle> triangles) {
		double[] min = new double[] { Double.MAX_VALUE, Double.MAX_VALUE, Double.MAX_VALUE };
		double[] max = new double[] { Double.MIN_VALUE, Double.MIN_VALUE, Double.MIN_VALUE };

		for (ModelTriangle mT : triangles) {
			for (Point3d p : mT.triangle.points) {
				for (int i = 0; i < 3; i++) {
					double val = p.get(i);
					if (val < min[i]) {
						min[i] = val;
					} else if (val > max[i]) {
						max[i] = val;
					}
				}
			}
		}

		return new Box(new Point3d(max), new Point3d(min));
	}
}
