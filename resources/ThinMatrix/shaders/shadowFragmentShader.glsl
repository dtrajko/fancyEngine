#version 150

in vec2 pass_textureCoords;

out vec4 out_color;

uniform sampler2D modelTexture;

void main(void){

	float alpha = texture(modelTexture, pass_textureCoords).a;
	if (alpha < 0.5) {
		discard;
	}

	out_color = vec4(1.0);
	
}
