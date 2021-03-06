package com.graphics.model.geom;

import com.graphics.geom.Transform3d;
import com.graphics.geom.impl.Point3d;
import com.graphics.geom.impl.Triangle;
import com.graphics.geom.impl.Vector3d;
import com.graphics.model.Material;

public class ModelTriangle implements Transform3d {
	public Triangle triangle;
	public Vector3d normal;
	public Vector3d[] vertexNormals = new Vector3d[3];
	public int[] colors;
	public Material mtl;

	public ModelTriangle(Point3d[] vertices, Vector3d[] vertexNormals, int[] colors, Material mtl) {
		this.triangle = new Triangle(vertices);

		this.vertexNormals[0] = vertexNormals[0].normalize();
		this.vertexNormals[1] = vertexNormals[1].normalize();
		this.vertexNormals[2] = vertexNormals[2].normalize();

		this.colors = colors;

		Vector3d v1 = triangle.points[1].minus(triangle.points[0]);
		Vector3d v2 = triangle.points[2].minus(triangle.points[0]);
		this.normal = v2.cross(v1).normalize();

		this.mtl = mtl;
	}

	@Override
	public void translate(double x, double y, double z) {
		this.triangle.translate(x, y, z);
	}

	@Override
	public void rotate(Point3d about, double x, double y, double z) {
		this.triangle.rotate(about, x, y, z);
		this.normal.rotate(about, x, y, z);
		for (Vector3d vertexNormal : this.vertexNormals) {
			vertexNormal.rotate(about, x, y, z);
		}
	}

	@Override
	public String toString() {
		return triangle.toString();
	}

	public ModelTriangle clone() {
		return new ModelTriangle(new Point3d[] { triangle.points[0].clone(), triangle.points[1].clone(), triangle.points[2].clone() }, new Vector3d[] {
				vertexNormals[0].clone(), vertexNormals[1].clone(), vertexNormals[2].clone() }, colors, mtl);
	}

}
