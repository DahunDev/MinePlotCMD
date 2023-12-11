package net.daniel.plotcmd.Utils;

import java.io.File;
import java.io.IOException;

import org.bukkit.plugin.Plugin;

public class FileUtils {
	public static File writeEnsure(File file) throws IOException {
		File parent = file.getParentFile();
		if (file.exists() || (parent == null || parent.exists() || parent.mkdirs()) && file.createNewFile()) {

		} else {
			throw new IllegalStateException("Exception when ensuring file");

		}
		return file;

	}

	public static File createFile(Plugin plugin, String fileName) {
		return new File(plugin.getDataFolder(), fileName);
	}
}
