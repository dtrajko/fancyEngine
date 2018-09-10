package engine.tm.shadows;

import org.joml.Matrix4f;
import org.joml.Vector3f;
import org.joml.Vector4f;

import engine.Window;
import engine.graph.ICamera;
import engine.tm.entities.Camera;
import engine.tm.render.MasterRenderer;
import engine.utils.Util;

/**
 * Represents the 3D cuboidal area of the world in which objects will cast
 * shadows (basically represents the orthographic projection area for the shadow
 * render pass). It is updated each frame to optimise the area, making it as
 * small as possible (to allow for optimal shadow map resolution) while not
 * being too small to avoid objects not having shadows when they should.
 * Everything inside the cuboidal area represented by this object will be
 * rendered to the shadow map in the shadow render pass. Everything outside the
 * area won't be.
 * 
 * @author Karl
 *
 */
public class ShadowBox {

	private static final float OFFSET = 20;
	private static final Vector4f UP = new Vector4f(0, 1, 0, 0);
	private static final Vector4f FORWARD = new Vector4f(0, 0, -1, 0);
	private static final float SHADOW_DISTANCE = 150;

	private float minX, maxX;
	private float minY, maxY;
	private float minZ, maxZ;
	private Matrix4f lightViewMatrix;
	private Camera cam;

	private float farHeight, farWidth, nearHeight, nearWidth;

	/**
	 * Creates a new shadow box and calculates some initial values relating to
	 * the camera's view frustum, namely the width and height of the near plane
	 * and (possibly adjusted) far plane.
	 * 
	 * @param lightViewMatrix
	 *            - basically the "view matrix" of the light. Can be used to
	 *            transform a point from world space into "light" space (i.e.
	 *            changes a point's coordinates from being in relation to the
	 *            world's axis to being in terms of the light's local axis).
	 * @param camera
	 *            - the in-game camera.
	 */
	protected ShadowBox(Matrix4f lightViewMatrix, ICamera camera) {
		this.lightViewMatrix = lightViewMatrix;
		this.cam = (Camera) camera;
		calculateWidthsAndHeights();
	}

	/**
	 * Updates the bounds of the shadow box based on the light direction and the
	 * camera's view frustum, to make sure that the box covers the smallest area
	 * possible while still ensuring that everything inside the camera's view
	 * (within a certain range) will cast shadows.
	 */
	protected void update() {
		Matrix4f rotation = calculateCameraRotationMatrix();
		Vector4f forwardVector4f = new Vector4f();
		rotation.transform(FORWARD, forwardVector4f);
		Vector3f forwardVector3f = new Vector3f(forwardVector4f.x, forwardVector4f.y, forwardVector4f.z);
		Vector3f toFar = new Vector3f(forwardVector3f);
		toFar.normalize(SHADOW_DISTANCE);
		Vector3f toNear = new Vector3f(forwardVector3f);
		toNear.normalize(MasterRenderer.NEAR_PLANE);
		Vector3f centerNear = toNear.add(new Vector3f(cam.getPosition()));
		Vector3f centerFar = toFar.add(new Vector3f(cam.getPosition()));

		Vector4f[] points = calculateFrustumVertices(rotation, forwardVector3f, centerNear, centerFar);

		boolean first = true;
		for (Vector4f point : points) {
			if (first) {
				minX = point.x;
				maxX = point.x;
				minY = point.y;
				maxY = point.y;
				minZ = point.z;
				maxZ = point.z;
				first = false;
				continue;
			}
			if (point.x > maxX) {
				maxX = point.x;
			} else if (point.x < minX) {
				minX = point.x;
			}
			if (point.y > maxY) {
				maxY = point.y;
			} else if (point.y < minY) {
				minY = point.y;
			}
			if (point.z > maxZ) {
				maxZ = point.z;
			} else if (point.z < minZ) {
				minZ = point.z;
			}
		}
		maxZ += OFFSET;
	}

	/**
	 * Calculates the center of the "view cuboid" in light space first, and then
	 * converts this to world space using the inverse light's view matrix.
	 * 
	 * @return The center of the "view cuboid" in world space.
	 */
	protected Vector3f getCenter() {
		float x = (minX + maxX) / 2f;
		float y = (minY + maxY) / 2f;
		float z = (minZ + maxZ) / 2f;
		Vector4f center4f = new Vector4f(x, y, z, 1);
		Matrix4f invertedLight = new Matrix4f();
		lightViewMatrix.invert(invertedLight);
		invertedLight.transform(center4f, center4f);
		// System.out.println("SB getCenter minX: " + minX + " maxX: " + maxX);
		// System.out.println("SB getCenter minY: " + minY + " maxY: " + maxY);
		// System.out.println("SB getCenter minZ: " + minZ + " maxZ: " + maxZ);
		// System.out.println("SB getCenter cen: " + Util.printVector4f(center4f));
		Vector3f center3f = new Vector3f(center4f.x, center4f.y, center4f.z);
		return center3f;
	}

	/**
	 * @return The width of the "view cuboid" (orthographic projection area).
	 */
	protected float getWidth() {
		return maxX - minX;
	}

	/**
	 * @return The height of the "view cuboid" (orthographic projection area).
	 */
	protected float getHeight() {
		return maxY - minY;
	}

	/**
	 * @return The length of the "view cuboid" (orthographic projection area).
	 */
	protected float getLength() {
		return maxZ - minZ;
	}

	/**
	 * Calculates the position of the vertex at each corner of the view frustum
	 * in light space (8 vertices in total, so this returns 8 positions).
	 * 
	 * @param rotation
	 *            - camera's rotation.
	 * @param forwardVector
	 *            - the direction that the camera is aiming, and thus the
	 *            direction of the frustum.
	 * @param centerNear
	 *            - the center point of the frustum's near plane.
	 * @param centerFar
	 *            - the center point of the frustum's (possibly adjusted) far
	 *            plane.
	 * @return The positions of the vertices of the frustum in light space.
	 */
	private Vector4f[] calculateFrustumVertices(Matrix4f rotation, Vector3f forwardVector,
			Vector3f centerNear, Vector3f centerFar) {

		/*
		System.out.println("SB FrustumVertices rotation:\n" + Util.printMatrix4f(rotation));
		System.out.println("SB FrustumVertices forwardVector: " + Util.printVector3f(forwardVector));
		System.out.println("SB FrustumVertices centerNear: " + Util.printVector3f(centerNear));
		System.out.println("SB FrustumVertices centerFar: " + Util.printVector3f(centerFar));
		*/
		Vector3f upVector = new Vector3f(rotation.transform(UP).x, rotation.transform(UP).y, rotation.transform(UP).z);		
		Vector3f rightVector = new Vector3f();
		forwardVector.cross(upVector, rightVector);
		Vector3f downVector = new Vector3f(-upVector.x, -upVector.y, -upVector.z);
		Vector3f leftVector = new Vector3f(-rightVector.x, -rightVector.y, -rightVector.z);
		Vector3f farTop = new Vector3f();
		centerFar.add(new Vector3f(upVector.x * farHeight, upVector.y * farHeight, upVector.z * farHeight), farTop);
		Vector3f farBottom = new Vector3f();
		centerFar.add(new Vector3f(downVector.x * farHeight, downVector.y * farHeight, downVector.z * farHeight), farBottom);
		Vector3f nearTop = new Vector3f();
		centerNear.add(new Vector3f(upVector.x * nearHeight, upVector.y * nearHeight, upVector.z * nearHeight), nearTop);
		Vector3f nearBottom = new Vector3f();
		centerNear.add(new Vector3f(downVector.x * nearHeight, downVector.y * nearHeight, downVector.z * nearHeight), nearBottom);

		Vector4f[] points = new Vector4f[8];		
		points[0] = calculateLightSpaceFrustumCorner(farTop, rightVector, farWidth);
		points[1] = calculateLightSpaceFrustumCorner(farTop, leftVector, farWidth);
		points[2] = calculateLightSpaceFrustumCorner(farBottom, rightVector, farWidth);
		points[3] = calculateLightSpaceFrustumCorner(farBottom, leftVector, farWidth);
		points[4] = calculateLightSpaceFrustumCorner(nearTop, rightVector, nearWidth);
		points[5] = calculateLightSpaceFrustumCorner(nearTop, leftVector, nearWidth);
		points[6] = calculateLightSpaceFrustumCorner(nearBottom, rightVector, nearWidth);
		points[7] = calculateLightSpaceFrustumCorner(nearBottom, leftVector, nearWidth);

		/*
		System.out.println("SB upVector: " + Util.printVector3f(upVector));
		System.out.println("SB rightVector: " + Util.printVector3f(rightVector));
		System.out.println("SB downVector: " + Util.printVector3f(downVector));
		System.out.println("SB leftVector: " + Util.printVector3f(leftVector));
		System.out.println("SB farTop: " + Util.printVector3f(farTop));
		System.out.println("SB farBottom: " + Util.printVector3f(farBottom));
		System.out.println("SB nearTop: " + Util.printVector3f(nearTop));
		System.out.println("SB nearBottom: " + Util.printVector3f(nearBottom));
		System.out.println("SB FrustumVertices point 0: " + Util.printVector4f(points[0]));
		System.out.println("SB FrustumVertices point 1: " + Util.printVector4f(points[1]));
		System.out.println("SB FrustumVertices point 2: " + Util.printVector4f(points[2]));
		System.out.println("SB FrustumVertices point 3: " + Util.printVector4f(points[3]));
		System.out.println("SB FrustumVertices point 4: " + Util.printVector4f(points[4]));
		System.out.println("SB FrustumVertices point 5: " + Util.printVector4f(points[5]));
		System.out.println("SB FrustumVertices point 6: " + Util.printVector4f(points[6]));
		System.out.println("SB FrustumVertices point 7: " + Util.printVector4f(points[7]));
		*/
		return points;
	}

	/**
	 * Calculates one of the corner vertices of the view frustum in world space
	 * and converts it to light space.
	 * 
	 * @param startPoint
	 *            - the starting center point on the view frustum.
	 * @param direction
	 *            - the direction of the corner from the start point.
	 * @param width
	 *            - the distance of the corner from the start point.
	 * @return - The relevant corner vertex of the view frustum in light space.
	 */
	private Vector4f calculateLightSpaceFrustumCorner(Vector3f startPoint, Vector3f direction, float width) {
		Vector3f point = startPoint.add(new Vector3f(direction.x * width, direction.y * width, direction.z * width));		
		Vector4f point4f = new Vector4f(point.x, point.y, point.z, 1f);		
		lightViewMatrix.transform(point4f, point4f);
		return point4f;
	}

	/**
	 * @return The rotation of the camera represented as a matrix.
	 */
	private Matrix4f calculateCameraRotationMatrix() {
		Matrix4f rotation = new Matrix4f();
		rotation.rotate((float) Math.toRadians(-cam.getYaw()),   new Vector3f(0, 1, 0));
		rotation.rotate((float) Math.toRadians(-cam.getPitch()), new Vector3f(1, 0, 0));
		return rotation;		
	}

	/**
	 * Calculates the width and height of the near and far planes of the
	 * camera's view frustum. However, this doesn't have to use the "actual" far
	 * plane of the view frustum. It can use a shortened view frustum if desired
	 * by bringing the far-plane closer, which would increase shadow resolution
	 * but means that distant objects wouldn't cast shadows.
	 */
	private void calculateWidthsAndHeights() {
		farWidth = (float) (SHADOW_DISTANCE * Math.tan(Math.toRadians(MasterRenderer.FOV)));
		nearWidth = (float) (MasterRenderer.NEAR_PLANE
				* Math.tan(Math.toRadians(MasterRenderer.FOV)));
		farHeight = farWidth / getAspectRatio();
		nearHeight = nearWidth / getAspectRatio();
	}

	/**
	 * @return The aspect ratio of the display (width:height ratio).
	 */
	private float getAspectRatio() {
		return (float) Window.width / (float) Window.height;
	}

}
