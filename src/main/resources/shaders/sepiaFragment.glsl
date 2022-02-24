#ifdef GL_ES
    precision mediump float;
#endif

varying vec4 v_color;
varying vec2 v_texCoords;
uniform sampler2D u_texture;

void main() {
  vec4 c = v_color * texture2D(u_texture, v_texCoords);
  mat4 Mat = mat4(
       0.393, 0.769, 0.189, 0,
       0.349, 0.686, 0.168, 0,
       0.272, 0.534, 0.131, 0,
       0,0,0,1);
  gl_FragColor = c * Mat;
}