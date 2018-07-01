#version 140

in vec2 textureCoords;

out vec4 out_Color;

uniform sampler2D guiTexture;
uniform int mouseOver;

void main(void){

	out_Color = texture(guiTexture, textureCoords);
	if (mouseOver == 1) {
		out_Color = vec4(out_Color.x + 0.2, out_Color.y + 0.2, out_Color.z + 0.2, out_Color.w + 0.2);
	}
}
