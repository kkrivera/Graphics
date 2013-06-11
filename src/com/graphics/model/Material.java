package com.graphics.model;

import com.graphics.geom.impl.Vector3d;

public class Material {
	public Vector3d Ka;
	public Vector3d Kd;
	public Vector3d Ks;
	public double Ns;
	public double Tr;
	public String map_Ka = "";
	public String map_Kd = "";
	public String map_Ks = "";
	public String map_Ns = "";
	public String map_bump = "";

	public Material(double[] Ka, double[] Kd, double[] Ks, double Ns, double Tr) {
		this.Ka = new Vector3d(Ka);
		this.Kd = new Vector3d(Kd);
		this.Ks = new Vector3d(Ks);
		this.Ns = Ns;
		this.Tr = Tr;
	}
}
