package engine.utils;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class FileSystem {

	public static List<String> listFilesForFolder(File folder) {
		List<String> files = new ArrayList<String>();
	    for (final File fileEntry : folder.listFiles()) {
	        if (fileEntry.isDirectory()) {
	            listFilesForFolder(fileEntry);
	        } else {
	        	files.add(fileEntry.getName());
	        }
	    }
	    return files;
	}
}
