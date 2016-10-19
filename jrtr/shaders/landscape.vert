#version 150
// GLSL version 1.50 

uniform mat4 projection;
uniform mat4 modelview;

in vec3 normal;
in vec4 position;
in vec4 color;

out float ndotl;
out vec4 frag_color;

void main()
{		
	ndotl = max(dot(modelview * vec4(normal,0), vec4(0,1,0,0)),0);

	gl_Position = projection * modelview * position;
	
	frag_color = color;
}
