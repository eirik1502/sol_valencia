#version 430 core

layout(location=0) in vec3 position;
layout(location=1) in vec3 normal;
layout(location=2) in vec2 uv;

uniform mat4 modelTransform;
uniform mat4 viewTransform;
uniform mat4 projectionTransform;

uniform mat4 normalTransform;

out vec3 WorldPosition;
out vec2 Uv;
out vec3 Normal;

void main() {
	vec4 pos = vec4(position, 1.0);

    vec4 worldPos = modelTransform * pos;
    gl_Position = projectionTransform * viewTransform * worldPos;

	WorldPosition = worldPos.xyz;
	Normal = mat3( transpose(inverse(modelTransform)) ) * normal; //the transpose/inverse is only necessary if we do non-uniform scaling

    Uv = uv;
}