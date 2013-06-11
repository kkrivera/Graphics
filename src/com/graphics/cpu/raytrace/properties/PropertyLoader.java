package com.graphics.cpu.raytrace.properties;

import java.io.File;
import java.io.FileInputStream;
import java.util.Properties;

public class PropertyLoader {
	private static Properties properties;

	private Properties getInstance() {
		try {
			if (properties == null) {
				File f = new File("");
				StringBuilder path = new StringBuilder(f.getAbsolutePath());
				path.append("/src/").append(this.getClass().getCanonicalName().replace(".", "/").replace(this.getClass().getSimpleName(), ""))
						.append("cfg.properties");

				properties = new Properties();
				properties.load(new FileInputStream(new File(path.toString())));
			}

			return properties;
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	public String getProperty(String key) {
		if (getInstance().containsKey(key)) {
			return getInstance().getProperty(key);
		}

		throw new RuntimeException("Key: '" + key + "' can not be found");
	}
}
