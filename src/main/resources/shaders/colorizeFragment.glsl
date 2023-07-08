#ifdef GL_ES
#define LOWP lowp
precision mediump float;
#else
#define LOWP 
#endif
varying vec2 v_texCoords;
varying LOWP vec4 v_color;
uniform sampler2D u_texture;
const vec3 forward = vec3(1.0 / 3.0);
void main()
{
  vec4 tgt = texture2D( u_texture, v_texCoords );
//// Ugly repeated matrix math to convert from RGB to Oklab. Oklab keeps lightness separate from hue and saturation.
  vec3 base = mat3(+0.2104542553, +1.9779984951, +0.0259040371, +0.7936177850, -2.4285922050, +0.7827717662, -0.0040720468, +0.4505937099, -0.8086757660) * +
              pow(mat3(0.4121656120, 0.2118591070, 0.0883097947, 0.5362752080, 0.6807189584, 0.2818474174, 0.0514575653, 0.1074065790, 0.6302613616) 
              * (tgt.rgb * tgt.rgb), forward);
  vec3 tint = mat3(+0.2104542553, +1.9779984951, +0.0259040371, +0.7936177850, -2.4285922050, +0.7827717662, -0.0040720468, +0.4505937099, -0.8086757660) * +
              pow(mat3(0.4121656120, 0.2118591070, 0.0883097947, 0.5362752080, 0.6807189584, 0.2818474174, 0.0514575653, 0.1074065790, 0.6302613616) 
              * (v_color.rgb * v_color.rgb), forward);
//// Sharply increases lightness contrast, to counteract the gray-ing caused by averaging base and tint lightness.
  tint.x = (tint.x + base.x) - 1.0;
  tint.x = sign(tint.x) * pow(abs(tint.x), 0.7) * 0.5 + 0.5;
//// Uncomment these next 3 lines if you want the original image to contribute some color, if it has any.
  float blen = length(base.yz);
  blen *= blen;
  tint.yz = clamp(tint.yz * (0.7 + blen) + base.yz * (0.3 - blen), -1.0, 1.0);
//// Reverse the Oklab conversion to get back to RGB. Uses the batch color's alpha normally.
  tint = mat3(1.0, 1.0, 1.0, +0.3963377774, -0.1055613458, -0.0894841775, +0.2158037573, -0.0638541728, -1.2914855480) * tint;
  gl_FragColor = vec4(sqrt(clamp( +
                 mat3(+4.0767245293, -1.2681437731, -0.0041119885, -3.3072168827, +2.6093323231, -0.7034763098, +0.2307590544, -0.3411344290, +1.7068625689) *
                 (tint * tint * tint), +
                 0.0, 1.0)), v_color.a * tgt.a);
}