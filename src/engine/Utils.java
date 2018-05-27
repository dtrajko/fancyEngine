package engine;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
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

    public static List<String> readAllLines(String fileName) throws Exception {
        List<String> list = new ArrayList<>();
        // try (BufferedReader br = new BufferedReader(new InputStreamReader(Class.forName(Utils.class.getName()).getResourceAsStream(fileName)))) {
        try (BufferedReader br = new BufferedReader(new FileReader(new File(fileName)))) {
            String line;
            while ((line = br.readLine()) != null) {
                list.add(line);
            }
        }
        return list;
    }

    public static float[] listToArray(List<Float> list) {
        int size = list != null ? list.size() : 0;
        float[] floatArr = new float[size];
        for (int i = 0; i < size; i++) {
            floatArr[i] = list.get(i);
        }
        return floatArr;
    }

    public static int[] listIntToArray(List<Integer> list) {
        int[] result = list.stream().mapToInt((Integer v) -> v).toArray();
        return result;
    }

    public static boolean existsResourceFile(String fileName) {
        boolean result;
        try (InputStream is = Utils.class.getResourceAsStream(fileName)) {
            result = is != null;
        } catch (Exception excp) {
            result = false;
        }
        return result;
    }
}
