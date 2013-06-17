package com.graphics.cpu.raytrace.acceleration;

public class IntersectionBundle {
	public double t, alpha, beta, gamma;

	public IntersectionBundle(double t, double alpha, double beta) {
		this.t = t;
		this.alpha = alpha;
		this.beta = beta;
		this.gamma = 1 - (alpha + beta);
	}

}
