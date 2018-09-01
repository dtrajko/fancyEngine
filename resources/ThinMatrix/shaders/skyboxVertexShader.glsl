#version 400

in vec3 position;
out vec3 textureCoords;

uniform mat4 projectionMatrix;
uniform mat4 viewMatrix;
uniform vec4 clipPlane;

void main(void){

	gl_Position = projectionMatrix * viewMatrix * vec4(position, 1.0);

	gl_ClipDistance[0] = dot(gl_Position, clipPlane);

	textureCoords = position;

}
