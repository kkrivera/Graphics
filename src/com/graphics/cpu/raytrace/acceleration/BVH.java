package com.graphics.cpu.raytrace.acceleration;

import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import com.graphics.cpu.ThreadManager;
import com.graphics.cpu.raytrace.Ray;
import com.graphics.geom.impl.Box;
import com.graphics.geom.impl.Point3d;
import com.graphics.model.Model;
import com.graphics.model.geom.ModelTriangle;

/**
 * Bounding Volume Hierarchy {@link IntersectionAlgorithm}
 */
public class BVH implements IntersectionAlgorithm {

	static Voxel root = null;

	@Override
	public Map<Ray, Map<ModelTriangle, IntersectionBundle>> intersect(Model model, Set<Ray> rays) {
		Map<Ray, Map<ModelTriangle, IntersectionBundle>> triangles = new HashMap<Ray, Map<ModelTriangle, IntersectionBundle>>();

		if (root == null) {
			root = new Voxel(model.triangles);
			buildBVH(root);
		}

		ThreadManager threadManager = new ThreadManager();
		threadManager.executeForResult(new HashSet<Ray>(rays), new ThreadManager.ThreadedAction<Ray, Map<Ray, Map<ModelTriangle, IntersectionBundle>>>() {
			@Override
			public Map<Ray, Map<ModelTriangle, IntersectionBundle>> execute(Collection<Ray> input) {
				Map<Ray, Map<ModelTriangle, IntersectionBundle>> triangles = new HashMap<Ray, Map<ModelTriangle, IntersectionBundle>>();
				for (Ray ray : input) {

					Map<ModelTriangle, IntersectionBundle> results = new HashMap<ModelTriangle, IntersectionBundle>();
					intersect(root, ray, results);

					if (!results.isEmpty()) {
						triangles.put(ray, results);
					}
				}

				return triangles;
			}
		}, triangles);

		return triangles;
	}

	private Voxel buildBVH(Voxel root) {

		Point3d[] range = root.box.getRange();
		Point3d rangeDiff = range[1].minus(range[0]);

		AXIS axis = AXIS.X;
		if (rangeDiff.y > rangeDiff.x && rangeDiff.y > rangeDiff.z) {
			axis = AXIS.Y;
		} else if (rangeDiff.z > rangeDiff.x && rangeDiff.z > rangeDiff.y) {
			axis = AXIS.Z;
		}
		double val = root.box.getCenter().get(axis.ordinal());

		/*
		 * Add triangles to triangle set
		 */

		Set<ModelTriangle> left = new HashSet<ModelTriangle>();
		Set<ModelTriangle> right = new HashSet<ModelTriangle>();

		for (ModelTriangle modelTriangle : root.triangles) {
			double pVal = AABB.getAABB(Collections.singleton(modelTriangle)).getCenter().get(axis.ordinal());
			if (pVal <= val) {
				left.add(modelTriangle);
			} else {
				right.add(modelTriangle);
			}
		}

		branch(root, left);
		branch(root, right);
		return root;
	}

	private void branch(Voxel root, Set<ModelTriangle> branch) {
		if (branch.size() <= 1 || root.triangles.size() == branch.size()) {
			return;
		}

		// if (root.triangles.size() != branch.size()) {
		root.children.add(buildBVH(new Voxel(branch)));
		// } else {
		// Set<ModelTriangle> subLeft = new HashSet<ModelTriangle>();
		// Set<ModelTriangle> subRight = new HashSet<ModelTriangle>();
		// int i = 0;
		// for (ModelTriangle mT : branch) {
		// if (i % 2 == 0) {
		// subLeft.add(mT);
		// } else {
		// subRight.add(mT);
		// }
		// i++;
		// }
		//
		// branch(root, subLeft);
		// branch(root, subRight);
		// }
	}

	private void intersect(Voxel root, Ray ray, Map<ModelTriangle, IntersectionBundle> results) {
		if (!ray.intersects(root.box)) {
			return;
		}

		if (!root.children.isEmpty()) {
			for (Voxel child : root.children) {
				intersect(child, ray, results);
			}
			return;
		}

		for (ModelTriangle modelTriangle : root.triangles) {
			IntersectionBundle intersetcion = ray.intersects(modelTriangle);
			if (intersetcion != null) {
				results.put(modelTriangle, intersetcion);
			}
		}
	}

	private enum AXIS {
		X, Y, Z
	}

	private class Voxel {
		public Box box;
		public Set<ModelTriangle> triangles = new HashSet<ModelTriangle>();
		public Set<Voxel> children = new HashSet<Voxel>();

		public Voxel(Set<ModelTriangle> t) {
			this.triangles = t;
			this.box = AABB.getAABB(t);
		}
	}
}
