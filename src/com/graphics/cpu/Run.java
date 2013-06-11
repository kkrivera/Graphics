package com.graphics.cpu;

import com.graphics.cpu.raytrace.RayTracer;
import com.graphics.window.Window;

public class Run {

	public static void main(String[] args) {

		Window window = new Window();
		window.show();

		RayTracer.init();

		System.out.append("done");
	}
}
