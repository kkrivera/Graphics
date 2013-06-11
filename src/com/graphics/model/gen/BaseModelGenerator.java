package com.graphics.model.gen;

import java.util.Set;

import com.graphics.model.Material;
import com.graphics.model.Model;
import com.graphics.model.geom.ModelTriangle;
import com.graphics.model.load.ModelLoader;

public abstract class BaseModelGenerator implements ModelLoader {

	int color;
	Material mtl;

	/**
	 * Generates the model via a {@link Set} of {@link ModelTriangle}s
	 * 
	 * @return {@link Set} of {@link ModelTriangle}
	 */
	public abstract Model generate();

	/**
	 * Overrides default functionality of the {@link ModelLoader} but generating a model instead of loading the files
	 */
	@Override
	public Model load(String path) {
		return this.generate();
	}

	public void configure(int color, Material mtl) {
		this.mtl = mtl;
		this.color = color;
	}
}
