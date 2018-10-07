#version 330

flat in vec3 pass_color;
in vec4 shadowCoords;

out vec4 out_color;

uniform sampler2D shadowMap;
uniform float shadowMapSize;

void main(void) {

	float objectNearestLight = texture(shadowMap, shadowCoords.xy).r;
	float lightFactor = 1.0;
	if (shadowCoords.z > objectNearestLight) {
		lightFactor = 1.0 - 0.4;
	}

	vec3 totalDiffuse = vec3(0.0);
	totalDiffuse = max(totalDiffuse, 0.4) * lightFactor;

	out_color = vec4(pass_color, 1.0);
	out_color = mix(vec4(totalDiffuse, 1.0), out_color, 0.5);

}
