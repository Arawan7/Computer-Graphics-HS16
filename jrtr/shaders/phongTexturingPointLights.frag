#version 150
// GLSL version 1.50
// Fragment shader for diffuse shading in combination with a texture map

#define MAX_LIGHTS 8

uniform sampler2D myTexture;
uniform sampler2D specular_map;
uniform vec3 light_color[MAX_LIGHTS];
uniform int nLights;
uniform float shininess;

// Variables passed in from the vertex shader
in vec2 frag_texcoord;
in float diffuse_dots[MAX_LIGHTS];
in float specular_dots[MAX_LIGHTS];

// Output variable, will be written to framebuffer automatically
out vec4 frag_shaded;

vec4 calculateColor()
{
	vec4 radiance = vec4(0,0,0,0);
	for(int i=0; i<nLights; i++)
	{
		vec4 k_d = texture(myTexture, frag_texcoord);
		vec4 k_s = texture(specular_map, frag_texcoord);
		
		vec4 diffuse = k_d * diffuse_dots[i];	
		vec4 specular = k_s * pow(specular_dots[i], shininess);
		
		radiance += vec4(light_color[i],0) * (diffuse + specular);
	}
	return radiance;
}

void main()
{
	// The built-in GLSL function "texture" performs the texture lookup
	frag_shaded = calculateColor();
	// frag_shaded = calculateColor() * texture(myTexture, frag_texcoord);
	// frag_shaded = texture(specular_map, frag_texcoord);
	// frag_shaded = texture(myTexture, frag_texcoord);
}