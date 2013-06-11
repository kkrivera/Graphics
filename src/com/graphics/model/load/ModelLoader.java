package com.graphics.model.load;

import java.util.Set;

import com.graphics.model.Model;
import com.graphics.model.geom.ModelTriangle;

public interface ModelLoader {

	/**
	 * Loads a model from a given {@link String} location
	 * 
	 * @return {@link Set} {@link ModelTriangle}
	 */
	public Model load(String path);
}
