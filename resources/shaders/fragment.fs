#version 330

in vec2 outTexCoord;
in float outSelected;

out vec4 fragColor;

uniform sampler2D texture_sampler;

void main()
{
	fragColor = texture(texture_sampler, outTexCoord);
	if ( outSelected > 0 ) {
		fragColor = vec4(fragColor.x, fragColor.y, 1, 1);
	}
}
