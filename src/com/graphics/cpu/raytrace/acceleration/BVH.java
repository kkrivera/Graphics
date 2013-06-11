package com.graphics.cpu.raytrace.acceleration;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import com.graphics.cpu.raytrace.Ray;
import com.graphics.model.Model;
import com.graphics.model.geom.ModelTriangle;

public class BVH implements IntersectionAlgorithm {

	@Override
	public Map<Ray, Map<ModelTriangle, Double>> intersect(Model model, Set<Ray> rays) {
		Map<Ray, Map<ModelTriangle, Double>> triangles = new HashMap<Ray, Map<ModelTriangle, Double>>();

		return triangles;
	}

}
