// Adapted from https://www.shadertoy.com/view/ldXGW4

// change these values to 0.0 to turn off individual effects
varying vec4 v_color;
varying vec2 v_texCoords;
uniform sampler2D u_texture;
uniform float u_time;
uniform float u_vertJerk = 1.0;
uniform float u_bottomStatic = 1.0;
uniform float u_scanlines = 1.0;
uniform float u_rgbOffset = 1.0;
uniform float u_horzFuzz = 1.0;
const vec3 forward = vec3(1.0 / 3.0);
const vec4 C = vec4(0.211324865405187,  // (3.0-sqrt(3.0))/6.0
0.366025403784439,  // 0.5*(sqrt(3.0)-1.0)
-0.577350269189626,  // -1.0 + 2.0 * C.x
0.024390243902439); // 1.0 / 41.0

// Noise generation functions borrowed from:
// https://github.com/ashima/webgl-noise/blob/master/src/noise2D.glsl

vec3 mod289(vec3 x) {
  return x - floor(x * (1.0 / 289.0)) * 289.0;
}

vec2 mod289(vec2 x) {
  return x - floor(x * (1.0 / 289.0)) * 289.0;
}

vec3 permute(vec3 x) {
  return mod289(((x*34.0)+1.0)*x);
}

float snoise(vec2 v)
{
  // First corner
  vec2 i  = floor(v + dot(v, C.yy) );
  vec2 x0 = v -   i + dot(i, C.xx);

  // Other corners
  vec2 i1;
  //i1.x = step( x0.y, x0.x ); // x0.x > x0.y ? 1.0 : 0.0
  //i1.y = 1.0 - i1.x;
  i1 = (x0.x > x0.y) ? vec2(1.0, 0.0) : vec2(0.0, 1.0);
  // x0 = x0 - 0.0 + 0.0 * C.xx ;
  // x1 = x0 - i1 + 1.0 * C.xx ;
  // x2 = x0 - 1.0 + 2.0 * C.xx ;
  vec4 x12 = x0.xyxy + C.xxzz;
  x12.xy -= i1;

  // Permutations
  i = mod289(i); // Avoid truncation effects in permutation
  vec3 p = permute( permute( i.y + vec3(0.0, i1.y, 1.0 ))
  + i.x + vec3(0.0, i1.x, 1.0 ));

  vec3 m = max(0.5 - vec3(dot(x0,x0), dot(x12.xy,x12.xy), dot(x12.zw,x12.zw)), 0.0);
  m = m*m ;
  m = m*m ;

  // Gradients: 41 points uniformly over a line, mapped onto a diamond.
  // The ring size 17*17 = 289 is close to a multiple of 41 (41*7 = 287)

  vec3 x = 2.0 * fract(p * C.www) - 1.0;
  vec3 h = abs(x) - 0.5;
  vec3 ox = floor(x + 0.5);
  vec3 a0 = x - ox;

  // Normalise gradients implicitly by scaling m
  // Approximation of: m *= inversesqrt( a0*a0 + h*h );
  m *= 1.79284291400159 - 0.85373472095314 * ( a0*a0 + h*h );

  // Compute final noise value at P
  vec3 g;
  g.x  = a0.x  * x0.x  + h.x  * x0.y;
  g.yz = a0.yz * x12.xz + h.yz * x12.yw;
  return 130.0 * dot(m, g);
}

float staticV(vec2 uv) {
  float staticHeight = snoise(vec2(9.0,u_time*1.2+3.0))*0.3+5.0;
  float staticAmount = snoise(vec2(1.0,u_time*1.2-6.0))*0.1+0.3;
  float staticStrength = snoise(vec2(-9.75,u_time*0.6-3.0))*2.0+2.0;
  return (1.0-step(snoise(vec2(5.0*pow(u_time,2.0)+pow(uv.x*7.0,1.2),pow((mod(u_time,100.0)+100.0)*uv.y*0.3+3.0,staticHeight))),staticAmount))*staticStrength;
}


void main()
{
  float jerkOffset = (1.0-step(snoise(vec2(u_time*1.3,5.0)),0.8))*0.05;

  float fuzzOffset = snoise(vec2(u_time*15.0,v_texCoords.y*80.0))*0.003;
  float largeFuzzOffset = snoise(vec2(u_time*1.0,v_texCoords.y*25.0))*0.004;

  float vertJerk = (1.0-step(snoise(vec2(u_time*2.5,5.0)),0.6))*u_vertJerk;
  float vertJerk2 = (1.0-step(snoise(vec2(u_time*5.5,5.0)),0.2))*u_vertJerk;
  float yOffset = vertJerk*vertJerk2*0.3;
  float y = mod(v_texCoords.y+yOffset,1.0);


  float xOffset = (fuzzOffset + largeFuzzOffset) * u_horzFuzz;

  float staticVal = 0.0;

  for (float y = -1.0; y <= 1.0; y += 1.0) {
    float maxDist = 5.0/200.0;
    float dist = y/200.0;
    staticVal += staticV(vec2(v_texCoords.x,v_texCoords.y+dist))*(maxDist-abs(dist))*1.5;
  }

  staticVal *= u_bottomStatic;

  float red 	=   texture2D(	u_texture, 	vec2(v_texCoords.x + xOffset -0.01*u_rgbOffset,y)).r+staticVal;
  float green = 	texture2D(	u_texture, 	vec2(v_texCoords.x + xOffset,	  y)).g+staticVal;
  float blue 	=	texture2D(	u_texture, 	vec2(v_texCoords.x + xOffset +0.01*u_rgbOffset,y)).b+staticVal;
  float alpha =     texture2D(u_texture, v_texCoords).a;

  vec3 color = vec3(red,green,blue);
  float scanline = sin(v_texCoords.y*800.0)*0.04*u_scanlines;
  color -= scanline;

  //// Ugly repeated matrix math to convert from RGB to Oklab. Oklab keeps lightness separate from hue and saturation.
  vec3 base = mat3(+0.2104542553, +1.9779984951, +0.0259040371, +0.7936177850, -2.4285922050, +0.7827717662, -0.0040720468, +0.4505937099, -0.8086757660) * +
  pow(mat3(0.4121656120, 0.2118591070, 0.0883097947, 0.5362752080, 0.6807189584, 0.2818474174, 0.0514575653, 0.1074065790, 0.6302613616)
  * (color * color), forward);
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
  0.0, 1.0)), alpha);
}
