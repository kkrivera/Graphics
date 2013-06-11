package com.graphics.model;

import java.util.HashSet;
import java.util.Set;

import com.graphics.geom.Transform3d;
import com.graphics.geom.impl.Point3d;
import com.graphics.model.geom.ModelTriangle;

public class Model implements Transform3d {
	public Set<ModelTriangle> triangles;
	public String name;
	public Material mtl;

	public Model(String name, Set<ModelTriangle> triangles, Material mtl) {
		this.name = name;
		this.triangles = triangles;
		this.mtl = mtl;
	}

	public Model concat(Model m) {
		try {
			Model m1 = (Model) this.clone();
			Model m2 = (Model) m.clone();

			Set<ModelTriangle> modelSet = new HashSet<ModelTriangle>(m1.triangles);
			modelSet.addAll(m2.triangles);
			return new Model(m1.name + " -- " + m2.name, modelSet, m1.mtl);

		} catch (CloneNotSupportedException e) {
			throw new RuntimeException(e);
		}
	}

	@Override
	public void translate(double x, double y, double z) {
		for (ModelTriangle triangle : triangles) {
			triangle.translate(x, y, z);
		}
	}

	@Override
	public void rotate(Point3d about, double x, double y, double z) {
		for (ModelTriangle triangle : triangles) {
			triangle.rotate(about, x, y, z);
		}
	}

	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder();
		sb.append(this.name).append("\nTriangles: ").append(this.triangles.size());
		return sb.toString();
	}

	@Override
	public Object clone() throws CloneNotSupportedException {
		Set<ModelTriangle> modelSet = new HashSet<ModelTriangle>();
		for (ModelTriangle t : triangles) {
			modelSet.add((ModelTriangle) t.clone());
		}

		return new Model(new String(name), modelSet, mtl);
	}
}
