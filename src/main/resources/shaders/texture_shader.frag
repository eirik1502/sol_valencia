#version 430 core


in vec2 Uv;
in vec3 Normal;
in vec3 WorldPosition;

uniform vec3 lightPosition = vec3(0,0,0);
uniform sampler2D tex;

out vec4 outputColor;


void main() {
    vec4 texColor = texture(tex, Uv);

    vec3 norm = normalize(Normal); //the normal might not be normalized due to non-uniform scaling

    vec3 lightColor = vec3(1.0f, 1.0f, 1.0f);

    vec3 lightDir = normalize( lightPosition - WorldPosition );

    //AMBIENT LIGHT
    float ambientRatio = 0.1f;
    vec3 ambientLight = ambientRatio * lightColor;

    //DIFFUSE LIGHT
    float diffuseRatio = max(dot(norm, lightDir), 0.0);
    vec3 diffuseLight = diffuseRatio * lightColor;


    //RESULT COLOR
    vec3 result = (ambientLight + diffuseLight) * texColor.xyz;

    //if (texColor.w != 1) discard; //---------------------------------------------------CHEEEEEEEEEEEAT-------------------------------------
	outputColor = vec4(result, texColor.w);
	//outputColor = vec4(1.0, 0, 0, 1.0);
}