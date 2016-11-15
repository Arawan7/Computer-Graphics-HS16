#version 150
// GLSL version 1.50
// Fragment shader for diffuse shading in combination with a texture map

#define MAX_LIGHTS 8

// Uniform variables passed in from host program
uniform sampler2D myTexture;
uniform int nLights;
uniform vec3 diffuse_reflection;
uniform vec3 specular_reflection;
uniform vec3 light_color[MAX_LIGHTS];
uniform float shininess;

// Variables passed in from the vertex shader
in vec2 frag_texcoord;
in float diffuse_dots[MAX_LIGHTS];
in float specular_dots[MAX_LIGHTS];

// Output variable, will be written to framebuffer automatically
out vec4 frag_shaded;

vec4 calculateColor()
{
	vec3 radiance = vec3(0,0,0);
	for(int i=0; i<nLights; i++)
	{
		vec3 diffuse = diffuse_reflection * diffuse_dots[i];	
		vec3 specular = specular_reflection * pow(specular_dots[i], shininess);
		
		radiance += light_color[i] * (diffuse + specular);
	}
	return vec4(radiance,0);
}

void main()
{
	// The built-in GLSL function "texture" performs the texture lookup	
	frag_shaded = calculateColor();
}