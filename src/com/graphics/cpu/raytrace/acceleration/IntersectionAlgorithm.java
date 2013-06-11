package com.graphics.cpu.raytrace.acceleration;

import java.util.Map;
import java.util.Set;

import com.graphics.cpu.raytrace.Ray;
import com.graphics.model.Model;
import com.graphics.model.geom.ModelTriangle;

public interface IntersectionAlgorithm {

	/**
	 * Returns a map of {@link ModelTriangle} which were intersected with a given {@link Set} of {@link Ray}s
	 */
	public Map<Ray, Map<ModelTriangle, Double>> intersect(Model model, Set<Ray> rays);
}
