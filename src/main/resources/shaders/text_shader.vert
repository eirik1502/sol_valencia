#version 430 core

layout(location=0) in vec3 position;
layout(location=1) in vec2 uv;

uniform mat4 modelTransform;
uniform mat4 viewTransform;
uniform mat4 projectionTransform;

out vec2 Uv;

void main() {
    Uv = uv;

	vec4 pos = vec4(position, 1.0);

    gl_Position = projectionTransform * viewTransform * modelTransform * pos;
}