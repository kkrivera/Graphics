package com.graphics.cpu.raytrace.acceleration;

import java.util.Map;
import java.util.Set;

import com.graphics.cpu.raytrace.Ray;
import com.graphics.model.Model;
import com.graphics.model.geom.ModelTriangle;

public interface IntersectionAlgorithm {

	/**
	 * Returns a map of {@link ModelTriangle} which were intersected with a given {@link Set} of {@link Ray}s
	 * 
	 * The Double array contains the following elements {t, alpha, beta} where t is the intersection length on the ray. Alpha and Beta are Barycentric values
	 */
	public Map<Ray, Map<ModelTriangle, IntersectionBundle>> intersect(Model model, Set<Ray> rays);
}
