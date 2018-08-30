#version 400

in vec3 textureCoords;
out vec4 out_Color;

uniform samplerCube cubeMap;
uniform samplerCube cubeMapNight;
uniform float blendFactor;
uniform vec3 fogColor;

const float upperLimit = 1000; // 60.0;
const float lowerLimit = -1000; // 10.0;

void main(void){

	vec4 textureDay = texture(cubeMap, textureCoords);
	vec4 textureNight = texture(cubeMapNight, textureCoords);
    vec4 finalColor = mix(textureDay, textureNight, blendFactor);
    float factor = (textureCoords.y - lowerLimit) / (upperLimit - lowerLimit);
    factor = clamp(factor, 0.0, 1.0);
    out_Color = mix(vec4(fogColor, 1.0), finalColor, factor);

}
