#version 330

layout (location = 0) in vec3 position;
layout (location = 1) in vec2 texCoord;
layout (location = 2) in float selectedInstanced;

out vec2 outTexCoord;
out float outSelected;

uniform int isInstanced;
uniform mat4 modelViewMatrix;
uniform mat4 projectionMatrix;
uniform float selectedNonInstanced;

void main()
{
	gl_Position = projectionMatrix * modelViewMatrix * vec4(position, 1.0);
    outTexCoord = texCoord;
    
    if ( isInstanced > 0 )
	{
		outSelected = selectedInstanced;
	}
	else
	{
		outSelected = selectedNonInstanced;
	}
}
