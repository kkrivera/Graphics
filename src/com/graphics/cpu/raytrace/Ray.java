package com.graphics.cpu.raytrace;

import com.graphics.cpu.raytrace.acceleration.IntersectionBundle;
import com.graphics.geom.impl.Box;
import com.graphics.geom.impl.Point3d;
import com.graphics.geom.impl.Triangle;
import com.graphics.geom.impl.Vector3d;
import com.graphics.model.geom.ModelTriangle;

public class Ray {
	public Point3d o;
	public Vector3d d;

	public Ray(Point3d o, Vector3d d) {
		this.o = o;
		this.d = d;
	}

	public Point3d getPoint(double d) {
		return o.plus(this.d.times(d));
	}

	public IntersectionBundle intersects(ModelTriangle modelTriangle) {
		double det = modelTriangle.normal.dot(d);

		// Parallel
		if (det == 0.0) {
			return null;
		}

		Triangle triangle = modelTriangle.triangle;
		double t = triangle.points[0].minus(o).dot(modelTriangle.normal) / det;

		// Triangle behind ray
		if (t < 0) {
			return null;
		}

		Point3d p = getPoint(t);
		Vector3d v = p.minus(triangle.points[0]);
		Vector3d v1 = triangle.points[1].minus(triangle.points[0]);
		Vector3d v2 = triangle.points[2].minus(triangle.points[0]);

		Vector3d v1cv2 = v1.cross(v2);
		double dotv1cv2 = v1cv2.dot(v1cv2);

		double a = v1cv2.dot(v.cross(v2)) / dotv1cv2; // alpha
		double b = v1cv2.dot(v1.cross(v)) / dotv1cv2; // beta

		if (a > 0 && a <= 1 && b > 0 && b <= 1 && (a + b) <= 1) {
			return new IntersectionBundle(t, a, b);
		}

		return null;
	}

	public boolean intersects(Box b) {

		Point3d[] boxRange = b.getRange();

		Vector3d tMin = boxRange[0].minus(o).over(d);
		Vector3d tMax = boxRange[1].minus(o).over(d);

		for (int i = 0; i < 3; i++) {
			double min = tMin.get(i);
			double max = tMax.get(i);

			if (max < min) {
				tMax.set(i, min);
				tMin.set(i, max);
			}
		}

		if (tMin.x > tMax.y || tMin.y > tMax.x) {
			return false;
		} else if (tMin.x > tMax.z || tMin.z > tMax.x) {
			return false;
		} else if (tMin.y > tMax.z || tMin.z > tMax.y) {
			return false;
		}

		return true;
	}

	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder();
		sb.append("Origin: ").append(o).append(", Direction: ").append(d);
		return sb.toString();
	}
}
