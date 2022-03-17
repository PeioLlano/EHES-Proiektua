package code;

import java.nio.file.FileSystems;

public class LagMethods {

	public static String relative2absolute(String relativePath) {
		return FileSystems.getDefault().getPath(relativePath).normalize().toAbsolutePath().toString();
		
	}
	
}
