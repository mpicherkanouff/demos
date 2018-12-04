#version 330 core

in vec4 vertex_Color;
out vec4 fragColor;

uniform float alpha;

void main(){
	
	fragColor.x = vertex_Color.x;
	fragColor.y = vertex_Color.y;
	fragColor.z = vertex_Color.z;
    fragColor.w = alpha + 0.0000000001 * vertex_Color.w;
}