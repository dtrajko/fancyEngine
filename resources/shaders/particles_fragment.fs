#version 330

in vec2 outTexCoord;
in vec3 mvPos;
out vec4 fragColor;

uniform sampler2D texture_sampler;
uniform float transparency;

void main()
{
    fragColor = texture(texture_sampler, outTexCoord);
    fragColor = vec4(fragColor.x, fragColor.y, fragColor.z, fragColor.w * transparency);
}
