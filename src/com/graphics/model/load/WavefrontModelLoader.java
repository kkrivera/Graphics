package com.graphics.model.load;

import java.awt.image.BufferedImage;
import java.awt.image.DataBufferByte;
import java.awt.image.WritableRaster;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Scanner;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.imageio.ImageIO;

import com.graphics.geom.impl.Point3d;
import com.graphics.geom.impl.Vector2d;
import com.graphics.geom.impl.Vector3d;
import com.graphics.model.Material;
import com.graphics.model.Model;
import com.graphics.model.geom.ModelTriangle;
import com.graphics.model.geom.Polygon;

public class WavefrontModelLoader implements ModelLoader {

	@Override
	public Model load(String path) {
		try {
			File root = new File(".");
			final File obj = new File(root.getCanonicalPath() + path);
			final String objDir = obj.getAbsolutePath().replace(obj.getName(), "");

			final Model model = new Model(obj.getName(), new HashSet<ModelTriangle>(), new Material(new double[] { 1, 1, 1 }, new double[] { 1, 1, 1 },
					new double[] { 1, 1, 1 }, 1, 0));
			if (obj.exists()) {
				String objOutput = readFile(obj);

				parse("mtllib", objOutput, new Each() {

					@Override
					public void run(String matchedResult) {
						String mtlFileOutput = readFile(new File(objDir + matchedResult));

						// Ka
						parse("Ka", mtlFileOutput, new Each() {
							@Override
							public void run(String matchedResult) {
								String[] Ka = matchedResult.split(" ");
								model.mtl.Ka = new Vector3d(Double.parseDouble(Ka[0]), Double.parseDouble(Ka[1]), Double.parseDouble(Ka[2]));
							}
						});

						// Kd
						parse("Kd", mtlFileOutput, new Each() {
							@Override
							public void run(String matchedResult) {
								String[] Kd = matchedResult.split(" ");
								model.mtl.Kd = new Vector3d(Double.parseDouble(Kd[0]), Double.parseDouble(Kd[1]), Double.parseDouble(Kd[2]));
							}
						});

						// Ks
						parse("Ks", mtlFileOutput, new Each() {
							@Override
							public void run(String matchedResult) {
								String[] Ks = matchedResult.split(" ");
								model.mtl.Ks = new Vector3d(Double.parseDouble(Ks[0]), Double.parseDouble(Ks[1]), Double.parseDouble(Ks[2]));
							}
						});

						// Ns
						parse("Ns", mtlFileOutput, new Each() {
							@Override
							public void run(String matchedResult) {
								model.mtl.Ns = Double.parseDouble(matchedResult);
							}
						});

						// Tr
						parse("d|Tr", mtlFileOutput, new Each() {
							@Override
							public void run(String matchedResult) {
								model.mtl.Tr = Double.parseDouble(matchedResult);
							}
						});

						// map_Ka
						parse("map_Ka", mtlFileOutput, new Each() {
							@Override
							public void run(String matchedResult) {
								model.mtl.map_Ka = matchedResult;
							}
						});

						// map_Kd
						parse("map_Kd", mtlFileOutput, new Each() {
							@Override
							public void run(String matchedResult) {
								model.mtl.map_Kd = matchedResult;
							}
						});

						// map_Ks
						parse("map_Ks", mtlFileOutput, new Each() {
							@Override
							public void run(String matchedResult) {
								model.mtl.map_Ks = matchedResult;
							}
						});
					}
				});

				final BufferedImage mapKd = ImageIO.read(new File(objDir + model.mtl.map_Kd));
				WritableRaster raster = mapKd.getRaster();
				DataBufferByte data = (DataBufferByte) raster.getDataBuffer();

				final List<Point3d> vertices = new ArrayList<Point3d>();
				final List<Vector2d> textureCoordinates = new ArrayList<Vector2d>();
				final List<Vector3d> vertexNormals = new ArrayList<Vector3d>();
				final Set<ModelTriangle> triangles = new HashSet<ModelTriangle>();
				// TODO vp (parameter space normals)

				// Vertices
				parse("v", objOutput, new Each() {
					@Override
					public void run(String matchedResult) {
						String[] strVertices = matchedResult.split(" ");
						vertices.add(new Point3d(Double.parseDouble(strVertices[0]), Double.parseDouble(strVertices[1]), Double.parseDouble(strVertices[2])));
					}
				});

				// Texture Coordinates
				parse("vt", objOutput, new Each() {
					@Override
					public void run(String matchedResult) {
						String[] strTextureCoordinates = matchedResult.split(" ");
						textureCoordinates.add(new Vector2d(Double.parseDouble(strTextureCoordinates[0]), Double.parseDouble(strTextureCoordinates[1])));
					}
				});

				// Vertex Normals
				parse("vn", objOutput, new Each() {
					@Override
					public void run(String matchedResult) {
						String[] strVertexNormals = matchedResult.split(" ");
						vertexNormals.add(new Vector3d(Double.parseDouble(strVertexNormals[0]), Double.parseDouble(strVertexNormals[1]), Double
								.parseDouble(strVertexNormals[2])));
					}
				});

				// Faces
				parse("f", objOutput, new Each() {

					@Override
					public void run(String matchedResult) {
						String[] faceData = matchedResult.split(" ");

						int polygonSize = faceData.length;
						Point3d[] vertexArr = new Point3d[polygonSize];
						Vector2d[] textureArr = new Vector2d[polygonSize];
						Vector3d[] normalArr = new Vector3d[polygonSize];
						int[] colors = new int[polygonSize];

						for (int i = 0; i < faceData.length; i++) {
							String[] vertexPropertiesArr = faceData[i].split("/");

							for (int j = 0; j < vertexPropertiesArr.length; j++) {
								String data = vertexPropertiesArr[j];
								if (data.isEmpty()) {
									continue;
								}
								int index = Integer.parseInt(data) - 1;

								switch (j) {
								case 0:
									vertexArr[i] = vertices.get(index);
									break;
								case 1:
									textureArr[i] = textureCoordinates.get(index);
									break;
								case 2:
									normalArr[i] = vertexNormals.get(index);
									break;
								default:
									break;
								}
							}
						}

						// LOAD
						// TODO read color from mtl
						int w = mapKd.getWidth() - 1;
						int h = mapKd.getHeight() - 1;
						for (int i = 0; i < polygonSize; i++) {
							Vector2d textureCoordinate = textureArr[i];
							try {
								colors[i] = mapKd.getRGB((int) (w * (textureCoordinate.x)), (int) (h * (1.0 - textureCoordinate.y)));
							} catch (Exception e) {
								throw new RuntimeException(e);
							}
						}

						Polygon face = new Polygon(vertexArr, normalArr, colors);
						model.triangles.addAll(face.modelTriangles);
					}
				});
			}

			return model;
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
	}

	static String readFile(File file) {
		try {
			return (new Scanner(file).useDelimiter("\\Z").next()).toString();
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	static void parse(String key, String matchOn, Each each) {
		String[] keys = key.split("\\|");

		for (String keyVal : keys) {
			Matcher m = Pattern.compile("[^\\S+]" + keyVal + " (.*)").matcher(matchOn);
			while (m.find()) {
				each.run(m.group(1).trim());
			}
		}
	}

	interface Each {
		public void run(String matchedResult);
	}

}
