// Adapted from https://manual.yoyogames.com/Additional_Information/Guide_To_Using_Shaders.htm#

#ifdef GL_ES
    precision mediump float;
#endif

varying vec4 v_color;
varying vec2 v_texCoords;
uniform sampler2D u_texture;
uniform float u_offset;
uniform float u_saturation;
uniform float u_brightness;
uniform float u_opacity;


vec3 hsv2rgb(vec3 c)
{
    vec4 K = vec4(1.0, 2.0 / 3.0, 1.0 / 3.0, 3.0);
    vec3 p = abs(fract(c.xxx + K.xyz) * 6.0 - K.www);
    return c.z * mix(K.xxx, clamp(p - K.xxx, 0.0, 1.0), c.y);
}

void main() {
    vec3 col = vec3(v_texCoords.x, 1.0, 1.0);
    float alpha = texture2D(u_texture, v_texCoords).a;
    gl_FragColor = v_color * vec4(hsv2rgb(col), alpha);

    //vec3 col = vec3(v_texCoords.x + u_offset, u_saturation, u_brightness);
    //vec4 texColor = texture2D(u_texture, v_texCoords);
    //vec4 finalCol = mix(texColor, vec4(hsv2rgb(col), texColor.a), u_opacity);

    gl_FragColor = v_color * finalCol;
}