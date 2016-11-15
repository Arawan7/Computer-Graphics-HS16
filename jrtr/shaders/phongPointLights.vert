#version 150
// GLSL version 1.50 
// Vertex shader for diffuse shading in combination with a texture map

#define MAX_LIGHTS 8

// Uniform variables, passed in from host program via suitable 
// variants of glUniform*
uniform mat4 projection;
uniform mat4 modelview;
uniform vec3 light_position[MAX_LIGHTS];
uniform vec3 camera_pos;
uniform vec4 light_direction[MAX_LIGHTS];
uniform int nLights;

// Input vertex attributes; passed in from host program to shader
// via vertex buffer objects
in vec3 normal;
in vec4 position;
in vec2 texcoord;

// Output variables for fragment shader
out vec2 frag_texcoord;
out float diffuse_dots[MAX_LIGHTS];
out float specular_dots[MAX_LIGHTS];

void setOutVars()
{
	for(int i=0; i<nLights; i++)
	{
		vec4 to_light = normalize(vec4(light_position[i],1) -  modelview * position);
		vec4 normal_transformed = modelview * vec4(normal,0);
		
		diffuse_dots[i] = dot(normal_transformed, to_light);
		
		vec4 e = normalize(vec4(camera_pos, 1) - modelview * position);
		vec4 R = reflect(to_light, normal_transformed);
		specular_dots[i] = dot(R,e);		
	}
}

void main()
{
	setOutVars();

	// Pass texture coordiantes to fragment shader, OpenGL automatically
	// interpolates them to each pixel  (in a perspectively correct manner) 
	frag_texcoord = texcoord;

	// Transform position, including projection matrix
	// Note: gl_Position is a default output variable containing
	// the transformed vertex position
	gl_Position = projection * modelview * position;
}
