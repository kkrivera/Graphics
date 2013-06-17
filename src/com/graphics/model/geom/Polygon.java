package com.graphics.model.geom;

import java.util.ArrayList;
import java.util.List;

import com.graphics.geom.Transform3d;
import com.graphics.geom.impl.Point3d;
import com.graphics.geom.impl.Vector3d;
import com.graphics.model.Material;

public class Polygon implements Transform3d {
	public List<ModelTriangle> modelTriangles = new ArrayList<ModelTriangle>();

	public Polygon(Point3d[] vertices, Vector3d[] vertexNormals, int[] colors, Material mtl) {

		int triangles = vertices.length - 2;

		// At least 3 vertices need to be present
		for (int i = 0; i < triangles; i++) {
			modelTriangles.add(new ModelTriangle(new Point3d[] { vertices[0].clone(), vertices[i + 1].clone(), vertices[i + 2].clone() }, new Vector3d[] {
					vertexNormals[0].clone(), vertexNormals[i + 1].clone(), vertexNormals[i + 2].clone() },
					new int[] { colors[0], colors[i + 1], colors[i + 2] }, mtl));
		}
	}

	@Override
	public void translate(double x, double y, double z) {
		for (ModelTriangle t : modelTriangles) {
			t.translate(x, y, z);
		}
	}

	@Override
	public void rotate(Point3d about, double x, double y, double z) {
		for (ModelTriangle t : modelTriangles) {
			t.rotate(about, x, y, z);
		}
	}

	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder();
		int i = 0;
		for (ModelTriangle t : modelTriangles) {
			sb.append("Triangle").append(i).append(": ").append(t).append("\n");
			i++;
		}
		return sb.toString();
	}

	@Override
	public Object clone() throws CloneNotSupportedException {
		int numVertices = modelTriangles.size() + 2;
		Point3d[] vertices = new Point3d[numVertices];
		Vector3d[] vertexNormals = new Vector3d[numVertices];
		int[] colors = new int[numVertices];

		vertices[0] = modelTriangles.get(0).triangle.points[0];
		vertexNormals[0] = modelTriangles.get(0).vertexNormals[0];
		colors[0] = modelTriangles.get(0).colors[0];

		for (int i = 0; i < modelTriangles.size(); i++) {
			ModelTriangle t = modelTriangles.get(i);
			int index1 = 1 + i * 2;
			int index2 = index1 + 1;

			vertices[index1] = t.triangle.points[1].clone();
			vertices[index2] = t.triangle.points[2].clone();

			vertexNormals[index1] = t.vertexNormals[1].clone();
			vertexNormals[index2] = t.vertexNormals[2].clone();

			colors[index1] = t.colors[1];
			colors[index2] = t.colors[2];
		}

		return new Polygon(vertices, vertexNormals, colors, modelTriangles.get(0).mtl);
	}
}
