// GLSL version of ImageGlitcher: https://www.airtightinteractive.com/demos/js/imageglitcher/
// from: https://www.shadertoy.com/view/MtXBDs

#ifdef GL_ES
precision mediump float;
precision mediump int;
#endif

#define PROCESSING_TEXTURE_SHADER

varying vec4 v_color;
varying vec2 v_texCoords;
uniform sampler2D u_texture;
uniform float u_time;
uniform bool colorSeparation = true;
uniform float amp = 0.13;
uniform float glitchSpeed = 0.16;
uniform float barSize = 0.25;
uniform float numSlices = 10.0;
uniform float crossfade = 1.;

//2D (returns 0 - 1)
float random2d(vec2 n) {
  return fract(sin(dot(n, vec2(12.9898, 4.1414))) * 43758.5453);
}

float randomRange (in vec2 seed, in float min, in float max) {
  return min + random2d(seed) * (max - min);
}

// return 1 if v inside 1d range
float insideRange(float v, float bottom, float top) {
  return step(bottom, v) - step(top, v);
}

void main() {
  float timeAdjusted = floor(u_time * glitchSpeed * 60.0);

  // copy orig
  vec3 color = texture2D(u_texture, v_texCoords).rgb;
  vec3 glitchColor = texture2D(u_texture, v_texCoords).rgb;

  // randomly offset slices horizontally
  float maxOffset = amp/2.0;
  for (float i = 0.0; i < numSlices * amp; i += 1.0) {
    float sliceY = random2d(vec2(timeAdjusted , 2345.0 + float(i)));
    float sliceH = random2d(vec2(timeAdjusted , 9035.0 + float(i))) * barSize;
    float hOffset = randomRange(vec2(timeAdjusted , 9625.0 + float(i)), -maxOffset, maxOffset);
    vec2 uvOff = v_texCoords;
    uvOff.x += hOffset;
    if (insideRange(v_texCoords.y, sliceY, fract(sliceY + sliceH)) == 1.0 ){
      glitchColor = texture2D(u_texture, uvOff).rgb;
    }
  }

  // do slight offset on one entire channel
  if(colorSeparation == true) {
    float maxColOffset = amp/6.0;
    float rnd = random2d(vec2(timeAdjusted , 9545.0));
    vec2 colOffset = vec2(randomRange(vec2(timeAdjusted , 9545.0),-maxColOffset,maxColOffset),
    randomRange(vec2(timeAdjusted , 7205.0),-maxColOffset,maxColOffset));
    if (rnd < 0.33){
      glitchColor.r = texture2D(u_texture, v_texCoords + colOffset).r;

    }else if (rnd < 0.66){
      glitchColor.g = texture2D(u_texture, v_texCoords + colOffset).g;

    } else{
      glitchColor.b = texture2D(u_texture, v_texCoords + colOffset).b;
    }
  }

  gl_FragColor = vec4(mix(color, glitchColor, crossfade), 1.);
}