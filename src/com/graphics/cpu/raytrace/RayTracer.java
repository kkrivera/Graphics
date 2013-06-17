package com.graphics.cpu.raytrace;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import com.graphics.cpu.ThreadManager;
import com.graphics.cpu.ThreadManager.ThreadedAction;
import com.graphics.cpu.raytrace.acceleration.IntersectionAlgorithm;
import com.graphics.cpu.raytrace.lighting.LightingAlgorithm;
import com.graphics.cpu.raytrace.properties.PropertyLoader;
import com.graphics.geom.impl.Point2d;
import com.graphics.geom.impl.Point3d;
import com.graphics.geom.impl.Vector3d;
import com.graphics.model.Material;
import com.graphics.model.Model;
import com.graphics.model.gen.BoxModelGenerator;
import com.graphics.model.gen.CoordinateAxisModelGenerator;
import com.graphics.model.geom.ModelTriangle;
import com.graphics.model.load.ModelLoader;
import com.graphics.window.Window;

public class RayTracer {

	public static void init() {

		final PropertyLoader properties = new PropertyLoader();

		/*
		 * Load Models
		 */
		List<Model> loadedModels = new ArrayList<Model>();

		BoxModelGenerator boxModel = new BoxModelGenerator(new Point3d(0, 0, 2), .5, .5, .5);
		boxModel.configure(Window.getColor(125, 255, 255), new Material(new double[] { 1, 1, 1 }, new double[] { 1, 1, 1 }, new double[] { .1, .1, .1 }, 1, 0));
		// loadedModels.add(boxModel.generate());

		// Begin Model load in separate thread
		try {
			String modelLoaders = properties.getProperty("model.loaders");
			for (String modelConfig : modelLoaders.split(",")) {

				String[] modelLoaderProperties = modelConfig.split("\\|");
				String modelLoadersClassName = modelLoaderProperties[0];
				String modelPath = modelLoaderProperties[1];

				ModelLoader modelLoader = ModelLoader.class.cast(Class.forName(modelLoadersClassName).getConstructor().newInstance());
				loadedModels.add(modelLoader.load(modelPath));
			}

		} catch (Exception e) {
			throw new RuntimeException(e);
		}

		/*
		 * Init variables
		 */
		int windowWidth = Integer.parseInt(properties.getProperty("window.width"));
		int windowHeight = Integer.parseInt(properties.getProperty("window.height"));
		double n = Integer.parseInt(properties.getProperty("camera.n"));

		double fov = Math.PI / 4.0;
		double aspect = (double) windowWidth / (double) windowHeight;

		double height = 2.0 * n * (Math.tan(fov / 2));
		double width = aspect * height;

		double pixelWidth = width / windowWidth;
		double pixelHeight = height / windowHeight;

		Vector3d eye = new Vector3d(properties.getProperty("camera.eye").split(","));
		Vector3d at = new Vector3d(properties.getProperty("camera.at").split(","));
		Vector3d up = new Vector3d(properties.getProperty("camera.up").split(","));

		Vector3d w = eye.minus(at).over(eye.minus(at).mag());
		Vector3d u = up.cross(w).over(up.cross(w).mag());
		Vector3d v = w.cross(u);

		Vector3d u00Vertical = v.times(pixelHeight);
		Vector3d u00Horizontal = u.times(pixelWidth);
		Vector3d u00 = w.times(-n).minus(v.times((double) height / 2.0)).minus(u.times((double) width / 2.0)).plus(u00Vertical.over(2))
				.plus(u00Horizontal.over(2));

		// Add coordinate system
		loadedModels.add(new CoordinateAxisModelGenerator(eye.mag()).generate());

		Window window = new Window();

		// Render passes at increasing resolutions
		for (double resolution = .125; resolution <= 1; resolution *= 2) {
			window.clear();

			Map<Ray, Set<Point2d>> pixelRayMap = buildRayMap(resolution, windowWidth, windowHeight, eye, u00, u00Vertical, u00Horizontal);

			long start = System.currentTimeMillis();
			render(pixelRayMap, loadedModels);
			long end = System.currentTimeMillis();

			System.out.println((int) (windowWidth * resolution) + "x" + (int) (windowHeight * resolution) + " -- " + (end - start) + "ms");
		}
	}

	private static Map<Ray, Set<Point2d>> buildRayMap(double resolution, final int windowWidth, final int windowHeight, final Vector3d eye, final Vector3d u00,
			final Vector3d u00Vertical, final Vector3d u00Horizontal) {

		ThreadManager threadManager = new ThreadManager();

		Set<Integer> indices = new HashSet<Integer>();
		final int resolutionStep = (int) (1.0 / resolution);
		for (int i = 0; i < windowWidth; i += resolutionStep) {
			for (int j = 0; j < windowHeight; j += resolutionStep) {
				indices.add(i * windowHeight + j);
			}
		}

		Map<Ray, Set<Point2d>> pixelRayMap = new HashMap<Ray, Set<Point2d>>();
		threadManager.executeForResult(indices, new ThreadedAction<Integer, Map<Ray, Set<Point2d>>>() {
			@Override
			public Map<Ray, Set<Point2d>> execute(Collection<Integer> input) {
				Map<Ray, Set<Point2d>> pixelRayMap = new HashMap<Ray, Set<Point2d>>();

				for (Integer index : input) {

					int j = index % windowHeight;
					int i = (index - j) / windowHeight;

					Set<Point2d> pixels = new HashSet<Point2d>();
					for (int xRes = 0; xRes < resolutionStep; xRes++) {
						for (int yRes = 0; yRes < resolutionStep; yRes++) {
							pixels.add(new Point2d(windowWidth - 1 - (i + xRes), windowHeight - 1 - (j + yRes)));
						}
					}

					pixelRayMap.put(new Ray(eye, u00.plus(u00Vertical.times(j)).plus(u00Horizontal.times(i)).normalize()), pixels);
				}

				return pixelRayMap;
			}
		}, pixelRayMap);

		return pixelRayMap;
	}

	public static void render(final Map<Ray, Set<Point2d>> pixelRayMap, List<Model> models) {
		PropertyLoader propertyLoader = new PropertyLoader();
		final Window window = new Window();

		/*
		 * Load Intersection and Lighting Algorithm
		 */
		IntersectionAlgorithm intersection;
		final LightingAlgorithm lighting;
		try {
			intersection = IntersectionAlgorithm.class.cast(Class.forName(propertyLoader.getProperty("acceleration.algorithm.class")).newInstance());
			lighting = LightingAlgorithm.class.cast(Class.forName(propertyLoader.getProperty("lighting.algorithm.class")).newInstance());
		} catch (Exception e) {
			throw new RuntimeException(e);
		}

		lighting.init(pixelRayMap.keySet(), models.toArray(new Model[] {}));

		ThreadManager threadManager = new ThreadManager();

		for (final Model model : models) {

			final Map<Ray, Map<ModelTriangle, Double>> intersectedRays = intersection.intersect(model, pixelRayMap.keySet());

			threadManager.executeForResult(new HashSet<Ray>(intersectedRays.keySet()), new ThreadManager.ThreadedAction<Ray, Integer>() {
				@Override
				public Integer execute(Collection<Ray> input) {

					for (Ray ray : input) {

						/*
						 * Get closest triangle
						 */
						double t = Double.MAX_VALUE;
						ModelTriangle triangle = null;
						for (Entry<ModelTriangle, Double> entry : intersectedRays.get(ray).entrySet()) {

							if (triangle == null || entry.getValue() < t) {
								t = entry.getValue();
								triangle = entry.getKey();
							}
						}

						/*
						 * Render Triangle point
						 */
						if (triangle != null) {
							int pixelColor = lighting.render(ray, t, triangle, model.mtl);
							for (Point2d pixel : pixelRayMap.get(ray)) {
								window.setColor((int) pixel.x, (int) pixel.y, pixelColor);
							}
						}
					}
					return null;
				}
			});

		}
		window.update(null);
	}
}
