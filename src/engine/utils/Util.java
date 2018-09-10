package engine.utils;

import java.text.DecimalFormat;
import java.text.NumberFormat;

import org.joml.Matrix4f;
import org.joml.Vector3f;
import org.joml.Vector4f;

public class Util {

	static public String customFormat(String pattern, double value ) {
		DecimalFormat myFormatter = new DecimalFormat(pattern);
		String output = myFormatter.format(value);
		return output;
	}

	static public String printVector3f(Vector3f v) {
		NumberFormat nf = new DecimalFormat("#####0.00");
		String printText = "x: " + nf.format(v.x) + " y: " + nf.format(v.y) + " z: " + nf.format(v.z);
		return printText;
	}

	static public String printVector4f(Vector4f v) {
		NumberFormat nf = new DecimalFormat("#####0.00");
		String printText = "x: " + nf.format(v.x) + " y: " + nf.format(v.y) + " z: " + nf.format(v.z) + " w: " + nf.format(v.w);
		return printText;
	}

	static public String printMatrix4f(Matrix4f m) {
		// JOML matrix is column-major to match OpenGL's interpretation
		NumberFormat nf = new DecimalFormat("#####0.00");
		String printText = "";
		printText += "m00: " + nf.format(m.m00()) + " m10: " + nf.format(m.m10()) + " m20: " + nf.format(m.m20()) + " m30: " + nf.format(m.m30()) + "\n";
		printText += "m01: " + nf.format(m.m01()) + " m11: " + nf.format(m.m11()) + " m21: " + nf.format(m.m21()) + " m31: " + nf.format(m.m31()) + "\n";
		printText += "m02: " + nf.format(m.m02()) + " m12: " + nf.format(m.m12()) + " m22: " + nf.format(m.m22()) + " m32: " + nf.format(m.m32()) + "\n";
		printText += "m03: " + nf.format(m.m03()) + " m13: " + nf.format(m.m13()) + " m23: " + nf.format(m.m23()) + " m33: " + nf.format(m.m33());
		return printText;
	}
}
