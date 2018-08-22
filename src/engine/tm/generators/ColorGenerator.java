package engine.tm.generators;

import engine.tm.utils.Color;
import engine.utils.Maths;

/**
 * Generates the colours of all the vertices in a terrain based on their height.
 * The colour generator has preset colours which are interpolated over the
 * terrain based on height.
 * 
 * @author Karl
 *
 */
public class ColorGenerator {

	private final float spread;
	private final float halfSpread;

	private final Color[] biomeColors;
	private final float part;

	/**
	 * @param biomeColours
	 *            - The preset colours that will be interpolated over the
	 *            terrain. The first colours in this array will be used for the
	 *            lowest parts of the terrain, and the last colours in this
	 *            array will be used for the highest. All the other colours will
	 *            be spread out linearly inbetween.
	 * @param spread
	 *            - This indicates how much of the possible altitude range the
	 *            colours should be spread over. If this is too high the extreme
	 *            colours won't be used as there won't be any terrain vertices
	 *            high or low enough (the heights generator doesn't usually fill
	 *            the whole altitude range).
	 */
	public ColorGenerator(Color[] biomeColors, float spread) {
		this.biomeColors = biomeColors;
		this.spread = spread;
		this.halfSpread = spread / 2f;
		this.part = 1f / (biomeColors.length - 1);
	}

	/**
	 * Calculates the colour for every vertex of the terrain, by linearly
	 * interpolating between the biome colours depending on the vertex's height.
	 * 
	 * @param heights
	 *            -The heights of all the vertices in the terrain.
	 * @param amplitude
	 *            - The amplitude range of the terrain that was used in the
	 *            heights generation. Maximum possible height is
	 *            {@code altitude} and minimum possible is {@code -altitude}.
	 * @return The colours of all the vertices in the terrain, in a grid.
	 */
	public Color[][] generateColors(float[][] heights, float amplitude) {
		Color[][] colours = new Color[heights.length][heights.length];
		for (int z = 0; z < heights.length; z++) {
			for (int x = 0; x < heights[z].length; x++) {
				colours[z][x] = calculateColor(heights[z][x], amplitude);
			}
		}
		return colours;
	}

	/**Determines the colour of the vertex based on the provided height.
	 * @param height - Height of the vertex.
	 * @param amplitude - The maximum height that a vertex can be (
	 * @return
	 */
	private Color calculateColor(float height, float amplitude) {
		float value = (height + amplitude) / (amplitude * 2);
		value = Maths.clamp((value - halfSpread) * (1f / spread), 0f, 0.9999f);
		int firstBiome = (int) Math.floor(value / part);
		float blend = (value - (firstBiome * part)) / part;
		return Color.interpolateColours(biomeColors[firstBiome], biomeColors[firstBiome + 1], blend, null);
	}
}
