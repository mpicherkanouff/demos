#version 330 core

in vec4 position;
in vec4 color;

uniform mat4 modelviewMatrix;
uniform mat4 projectionMatrix;

out vec4 vertex_Color;

void main(){
    vec4 eyespacePosition =  modelviewMatrix * position;
    gl_Position = projectionMatrix * eyespacePosition;
    vertex_Color = color;
}
