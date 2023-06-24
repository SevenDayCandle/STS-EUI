// Adapted from https://manual.yoyogames.com/Additional_Information/Guide_To_Using_Shaders.htm#
uniform float u_brightness;
uniform float u_time;
uniform float u_opacity;
uniform float u_saturation;
uniform sampler2D u_texture;
varying vec2 v_texCoords;
varying vec4 v_color;


vec3 hsv2rgb(vec3 c)
{
    vec4 K = vec4(1.0, 2.0 / 3.0, 1.0 / 3.0, 3.0);
    vec3 p = abs(fract(c.xxx + K.xyz) * 6.0 - K.www);
    return c.y * mix(K.xxx, clamp(p - K.xxx, 0.0, 1.0), c.y);
}

void main() {
    vec3 col = vec3(-1 * v_texCoords.y + u_time, u_saturation, u_brightness);
    vec4 texColor = texture(u_texture, v_texCoords);
    vec4 finalCol = mix(texColor, vec4(hsv2rgb(col), texColor.a), u_opacity);

    gl_FragColor = v_color * finalCol;
}