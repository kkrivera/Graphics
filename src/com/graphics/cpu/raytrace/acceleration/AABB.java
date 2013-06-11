package com.graphics.cpu.raytrace.acceleration;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import com.graphics.cpu.raytrace.Ray;
import com.graphics.geom.impl.Box;
import com.graphics.geom.impl.Point3d;
import com.graphics.model.Model;
import com.graphics.model.geom.ModelTriangle;

public class AABB implements IntersectionAlgorithm {

	@Override
	public Map<Ray, Map<ModelTriangle, Double>> intersect(Model model, Set<Ray> rays) {
		double[] min = new double[] { Double.MAX_VALUE, Double.MAX_VALUE, Double.MAX_VALUE };
		double[] max = new double[] { Double.MIN_VALUE, Double.MIN_VALUE, Double.MIN_VALUE };

		for (ModelTriangle mT : model.triangles) {
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

		Box aabb = new Box(new Point3d(max), new Point3d(min));

		Map<Ray, Map<ModelTriangle, Double>> triangles = new HashMap<Ray, Map<ModelTriangle, Double>>();
		for (Ray ray : rays) {

			if (ray.intersects(aabb)) {
				for (ModelTriangle triangle : model.triangles) {

					double t = ray.intersects(triangle);
					if (t > 0) {
						if (!triangles.containsKey(ray)) {
							triangles.put(ray, new HashMap<ModelTriangle, Double>());
						}
						triangles.get(ray).put(triangle, t);
					}
				}
			}
		}

		return triangles;
	}
}