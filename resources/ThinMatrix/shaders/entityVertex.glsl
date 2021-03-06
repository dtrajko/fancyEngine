#version 400 core

in vec3 position;
in vec2 textureCoords;
in vec3 normal;

out vec2 pass_textureCoords;
out vec3 surfaceNormal;
out vec3 toLightVector[5];
out vec3 toCameraVector;
out float visibility;

uniform mat4 transformationMatrix;
uniform mat4 projectionMatrix;
uniform mat4 viewMatrix;
uniform vec3 lightPosition[5];
uniform bool useFakeLighting;
uniform float textureAtlasNumRows;
uniform vec2 textureAtlasOffset;
uniform vec4 clipPlane;

const float density = 0.000;
const float gradient = 3.0;

void main(void) {

	vec4 worldPosition = transformationMatrix * vec4(position, 1.0);

	gl_ClipDistance[0] = dot(worldPosition, clipPlane);

	vec4 positionRelativeToCamera = viewMatrix * worldPosition;
	gl_Position = projectionMatrix * positionRelativeToCamera;
	pass_textureCoords = (textureCoords / textureAtlasNumRows) + textureAtlasOffset;

	vec3 actualNormal = normal;
	if (useFakeLighting) {
		actualNormal = vec3(0.0, 1.0, 0.0);
	}
	surfaceNormal = (transformationMatrix * vec4(actualNormal, 0.0)).xyz;

	for (int i = 0; i < 5; i++) {
		toLightVector[i] = lightPosition[i] - worldPosition.xyz;
	}

	toCameraVector = (inverse(viewMatrix) * vec4(0.0, 0.0, 0.0, 1.0)).xyz - worldPosition.xyz;
	float distance = length(positionRelativeToCamera.xyz);
	visibility = exp(-pow((distance * density), gradient));
	visibility = clamp(visibility, 0.0, 1.0);

}
