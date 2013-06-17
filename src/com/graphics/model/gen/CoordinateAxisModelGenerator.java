package com.graphics.model.gen;

import com.graphics.geom.impl.Point3d;
import com.graphics.model.Material;
import com.graphics.model.Model;
import com.graphics.window.Window;

public class CoordinateAxisModelGenerator extends BaseModelGenerator {

	BoxModelGenerator xAxisModelGenerator;
	BoxModelGenerator yAxisModelGenerator;
	BoxModelGenerator zAxisModelGenerator;

	public CoordinateAxisModelGenerator(double cameraDistance) {
		double center = cameraDistance / 20.0;
		double thickness = center / 25.0;

		Material mtl = new Material(new double[] { 1, 1, 1 }, new double[] { 1, 1, 1 }, new double[] { .2, .2, .2 }, 1, 0);

		xAxisModelGenerator = new BoxModelGenerator(new Point3d(center, 0, 0), thickness, center * 2, thickness);
		xAxisModelGenerator.configure(Window.getColor(255, 0, 0), mtl);

		yAxisModelGenerator = new BoxModelGenerator(new Point3d(0, center, 0), thickness, thickness, center * 2);
		yAxisModelGenerator.configure(Window.getColor(0, 255, 0), mtl);

		zAxisModelGenerator = new BoxModelGenerator(new Point3d(0, 0, center), center * 2, thickness, thickness);
		zAxisModelGenerator.configure(Window.getColor(0, 0, 255), mtl);
	}

	@Override
	public Model generate() {
		Model xAxis = xAxisModelGenerator.generate();
		Model yAxis = yAxisModelGenerator.generate();
		Model zAxis = zAxisModelGenerator.generate();

		Model coordinateSystem = xAxis.concat(yAxis).concat(zAxis);
		coordinateSystem.name = "Coordinate Axis";

		return coordinateSystem;
	}

}
