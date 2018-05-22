#version 330

in vec2 outTexCoord;
in float outSelected;

out vec4 fragColor;

uniform sampler2D texture_sampler;
uniform vec4 color;
uniform int hasTexture;

void main()
{
    if ( hasTexture == 1 )
    {
        fragColor = color * texture(texture_sampler, outTexCoord);
    }
    else
    {
        fragColor = color;
    }
	if ( outSelected > 0 ) {
		fragColor = vec4(fragColor.x, fragColor.y, 1, 1);
	}
}
