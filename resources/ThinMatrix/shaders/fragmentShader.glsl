#version 400 core

in vec2 pass_textureCoords;
in vec3 surfaceNormal;
in vec3 toLightVector[5];
in vec3 toCameraVector;
in float visibility;

out vec4 out_Color;

uniform sampler2D modelTexture;
uniform vec3 lightColor[5];
uniform vec3 attenuation[5];
uniform float shineDamper;
uniform float reflectivity;
uniform vec3 skyColor;

void main(void) {

	vec4 textureColor = texture(modelTexture, pass_textureCoords);
	if (textureColor.a < 0.5) {
		discard;
	}

	vec3 unitNormal = normalize(surfaceNormal);
	vec3 unitVectorToCamera = normalize(toCameraVector);

	vec3 totalDiffuse = vec3(0.0);
	vec3 totalSpecular = vec3(0.0);

	for (int i = 0; i < 5; i++) {
		float distance = length(toLightVector[i]);
		float attFactor = attenuation[i].x + attenuation[i].y * distance + attenuation[i].z * distance * distance;
		vec3 unitLightVector = normalize(toLightVector[i]);
		float nDotl = dot(unitNormal, unitLightVector);
		float brightness = max(nDotl, 0.0);
		vec3 lightDirection = -unitLightVector;
		vec3 reflectedLightDirection = reflect(lightDirection, unitNormal);
		float specularFactor = dot(reflectedLightDirection, unitVectorToCamera);
		specularFactor = max(specularFactor, 0.0);
		float dampedFactor = pow(specularFactor, shineDamper);
		totalDiffuse = totalDiffuse + (brightness * lightColor[i]) / attFactor;
		totalSpecular = totalSpecular + (dampedFactor * reflectivity * lightColor[i]) / attFactor;
	}

	float ambientLight = 0.2;
	totalDiffuse = max(totalDiffuse, ambientLight);

	out_Color = vec4(totalDiffuse, 1.0) * textureColor + vec4(totalSpecular, 1.0);
	out_Color = mix(vec4(skyColor, 1.0), out_Color, visibility);

}
