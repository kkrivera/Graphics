package com.graphics.model.gen;

import java.util.HashSet;

import com.graphics.geom.impl.Point3d;
import com.graphics.model.Material;
import com.graphics.model.Model;
import com.graphics.model.geom.ModelTriangle;
import com.graphics.window.Window;

public class RoomModelGenerator extends BaseModelGenerator {
	BoxModelGenerator leftWall;
	BoxModelGenerator rightWall;
	BoxModelGenerator backWall;
	BoxModelGenerator frontWall;
	BoxModelGenerator topWall;
	BoxModelGenerator bottomWall;

	public RoomModelGenerator(Point3d center, double size) {

		double hSize = size / 2.0;
		double thick = .1;

		leftWall = new BoxModelGenerator(center.plus(-hSize, 0, 0), size, thick, size);
		rightWall = new BoxModelGenerator(center.plus(hSize, 0, 0), size, thick, size);
		backWall = new BoxModelGenerator(center.plus(0, 0, -hSize), thick, size, size);
		frontWall = new BoxModelGenerator(center.plus(0, 0, hSize), thick, size, size);
		topWall = new BoxModelGenerator(center.plus(0, hSize, 0), size, size, thick);
		bottomWall = new BoxModelGenerator(center.plus(0, -hSize, 0), size, size, thick);

		Material mtl = new Material(new double[] { .05, .05, .05 }, new double[] { 1, 1, 1 }, new double[] { .01, .01, .01 }, 10, 1);
		int color = Window.getColor(125, 0, 0);
		leftWall.configure(color, mtl);
		rightWall.configure(color, mtl);
		backWall.configure(color, mtl);
		frontWall.configure(color, mtl);
		topWall.configure(color, mtl);
		bottomWall.configure(color, mtl);
	}

	@Override
	public Model generate() {

		Model model = new Model("Generated Room Model", new HashSet<ModelTriangle>());
		model.triangles.addAll(leftWall.generate().triangles);
		model.triangles.addAll(rightWall.generate().triangles);
		model.triangles.addAll(backWall.generate().triangles);
		model.triangles.addAll(frontWall.generate().triangles);
		model.triangles.addAll(topWall.generate().triangles);
		model.triangles.addAll(bottomWall.generate().triangles);

		return model;
	}

}
