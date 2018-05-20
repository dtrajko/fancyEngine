package engine;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.util.Scanner;

public class Utils {

    public static String loadResource(String fileName) throws Exception {
        String result = "";
        try (InputStream in = Class.forName(Utils.class.getName()).getResourceAsStream(fileName);
             Scanner scanner = new Scanner(in, "UTF-8")) {
            result = scanner.useDelimiter("\\A").next();
        } catch (Exception e) {
        	System.err.println("[" + methodName() + "] failed to load a resource [" + fileName + "]");
        	e.printStackTrace();
        }
        return result;
    }

    public static String readFile(String fileName) {
		StringBuilder string = new StringBuilder();
		BufferedReader br;
		try {
			br = new BufferedReader(new FileReader(new File(fileName)));
			String line;
			while((line = br.readLine()) != null) {
				string.append(line);
				string.append("\n");
			}
			br.close();
		} catch(IOException e) {
			System.err.println("[" + methodName() + "] failed to load a file [" + fileName + "]");
			e.printStackTrace();
		}
		return string.toString();
	}

	public static String methodName() {
		return new Object(){}.getClass().getEnclosingMethod().getName();
	}
}
