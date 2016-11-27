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
uniform mat4 modelview;
uniform mat4 camera_matrix;
uniform vec3 light_position[MAX_LIGHTS];
uniform vec3 camera_pos;
uniform mat4 projection;
uniform vec4 light_direction[MAX_LIGHTS];

// Variables passed in from the vertex shader
in vec2 frag_texcoord;
in vec4 frag_position;
in vec3 frag_normal;

// Output variable, will be written to framebuffer automatically
out vec4 frag_shaded;

vec4 calculateColor()
{
	vec3 radiance = vec3(0,0,0);
	
	for(int i=0; i<nLights; i++)
	{
		vec4 to_light = normalize(camera_matrix * vec4(light_position[i],1) -  modelview * frag_position);
		vec4 normal_transformed = modelview * vec4(frag_normal,0);
		
		vec4 e = normalize(vec4(camera_pos, 1) - modelview * frag_position);
		vec4 R = reflect(to_light, normal_transformed);
		
		vec3 diffuse = diffuse_reflection * dot(normal_transformed, to_light);	
		vec3 specular = specular_reflection * pow(dot(R,e), shininess);
		
		radiance += light_color[i] * (diffuse + specular);
	}
	
	return vec4(radiance,0);
}

void main()
{
	// The built-in GLSL function "texture" performs the texture lookup	
	frag_shaded = calculateColor();
}