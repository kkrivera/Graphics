package com.graphics.cpu.raytrace.lighting;

import java.util.Set;

import com.graphics.cpu.raytrace.Ray;
import com.graphics.cpu.raytrace.acceleration.IntersectionBundle;
import com.graphics.model.Model;
import com.graphics.model.geom.ModelTriangle;

public interface LightingAlgorithm {

	/**
	 * Initializes the lighting algorithm
	 */
	public void init(Set<Ray> rays, Model... models);

	/**
	 * Renders the {@link Ray} provided the set of intersected {@link ModelTriangle}s
	 * 
	 * @return rendered pixel color
	 */
	public int render(Ray ray, IntersectionBundle intersection, ModelTriangle triangle);
}
