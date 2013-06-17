package com.graphics.model;

import java.util.HashSet;

import com.graphics.model.geom.ModelTriangle;

public class Scene extends Model {

	public Scene(String name, Model... models) {
		super(name, new HashSet<ModelTriangle>());

		for (Model model : models) {
			triangles.addAll(model.triangles);
		}
	}
}
