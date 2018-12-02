#version 330 core

in vec4 vertex_Color;
out vec4 fragColor;

void main(){
	fragColor = vertex_Color;
}