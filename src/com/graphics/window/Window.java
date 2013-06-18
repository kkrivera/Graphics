package com.graphics.window;

import java.awt.image.BufferedImage;

import javax.swing.ImageIcon;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;

import com.graphics.cpu.raytrace.properties.PropertyLoader;

public class Window {
	private static BufferedImage image;
	private static JPanel panel;
	private static int height;
	private static int width;

	public Window() {
		PropertyLoader properties = new PropertyLoader();
		width = Integer.parseInt(properties.getProperty("window.width"));
		height = Integer.parseInt(properties.getProperty("window.height"));
	}

	private BufferedImage getInstance() {
		if (image == null) {
			image = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
		}
		return image;
	}

	public void setColor(int x, int y, int rgb) {
		getInstance().setRGB(x, y, rgb);
	}

	public void setColor(int x, int y, int r, int g, int b) {
		setColor(x, y, getColor(r, g, b));
	}

	public void clear() {
		clear(0, 0, 0);
	}

	public void clear(int r, int g, int b) {
		int color = getColor(r, g, b);
		for (int i = 0; i < width; i++) {
			for (int j = 0; j < height; j++) {
				setColor(i, j, color);
			}
		}
	}

	public void update(Runnable post) {
		panel.repaint();

		if (post != null) {
			post.run();
		}
	}

	public void show() {

		panel = new JPanel();
		panel.add(new JLabel(new ImageIcon(getInstance())));

		final JFrame frame = new JFrame();
		frame.setSize(width, height);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.add(panel);
		frame.setVisible(true);
	}

	public static int getColor(int r, int g, int b) {
		return (int) (r * Math.pow(256, 2) + g * 256 + b);
	}

	public static int[] splitColor(int color) {
		return new int[] { color >> 16 & 0xFF, color >> 8 & 0xFF, color >> 0 & 0xFF };
	}
}
