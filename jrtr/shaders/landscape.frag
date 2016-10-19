#version 150
// GLSL version 1.50

in float ndotl;
in vec4 frag_color;

out vec4 frag_shaded;

void main()
{		
	frag_shaded = ndotl * frag_color;
}

