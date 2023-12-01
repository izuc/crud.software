package crud.software.Utility;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.nio.file.StandardOpenOption;

public class CopyDir {

    public static void copy(String sourceDirectory, String targetDirectory, String projectName, String existingProjectName) {
        File source = new File(sourceDirectory);
        File target = new File(targetDirectory);
        copyAll(source, target, projectName, existingProjectName);
    }

	public static void copyAll(File source, File target, String projectName, String existingProjectName) {
		if (!target.exists()) {
			target.mkdirs();
		}

		File[] files = source.listFiles();
		for (File file : files) {
			if (file.isFile()) {
				try {
					Path targetFilePath = new File(target, file.getName()).toPath();
					String mimeType = Files.probeContentType(file.toPath());

					if (mimeType != null && mimeType.startsWith("text")) {
						byte[] bytes = Files.readAllBytes(file.toPath());

						// Check for BOM and remove it
						if (bytes.length > 3 && bytes[0] == (byte) 0xEF && bytes[1] == (byte) 0xBB && bytes[2] == (byte) 0xBF) {
							bytes = java.util.Arrays.copyOfRange(bytes, 3, bytes.length);
						}

						String content = new String(bytes, StandardCharsets.UTF_8);
						content = content.replace(existingProjectName, projectName.trim());

						if (file.getName().endsWith(".php")) {
							writeWithoutBOM(targetFilePath.toString(), content);
						} else {
							Files.write(targetFilePath, content.getBytes(StandardCharsets.UTF_8), StandardOpenOption.CREATE, StandardOpenOption.TRUNCATE_EXISTING);
						}
					} else {
						Files.copy(file.toPath(), targetFilePath, StandardCopyOption.REPLACE_EXISTING);
					}
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}

		File[] directories = source.listFiles(File::isDirectory);
		for (File dir : directories) {
			if (!dir.getName().equals(".vs")) {
				File targetDir = new File(target, dir.getName());
				copyAll(dir, targetDir, projectName, existingProjectName);
			}
		}
	}

    public static void writeWithoutBOM(String filePath, String content) throws IOException {
        try (FileOutputStream outputStream = new FileOutputStream(new File(filePath))) {
            byte[] bytes = content.getBytes(StandardCharsets.UTF_8);
            outputStream.write(bytes);
        }
    }
}
