/* ----------------------------------------------------------------------------
 * This file was automatically generated by SWIG (http://www.swig.org).
 * Version 4.0.2
 *
 * Do not make changes to this file unless you know what you are doing--modify
 * the SWIG interface file instead.
 * ----------------------------------------------------------------------------- */

package extendedui.swig;

public class EffekseerManagerCore {
  private transient long swigCPtr;
  protected transient boolean swigCMemOwn;

  protected EffekseerManagerCore(long cPtr, boolean cMemoryOwn) {
    swigCMemOwn = cMemoryOwn;
    swigCPtr = cPtr;
  }

  protected static long getCPtr(EffekseerManagerCore obj) {
    return (obj == null) ? 0 : obj.swigCPtr;
  }

  @SuppressWarnings("deprecation")
  protected void finalize() {
    delete();
  }

  public synchronized void delete() {
    if (swigCPtr != 0) {
      if (swigCMemOwn) {
        swigCMemOwn = false;
        EffekseerCoreJNI.delete_EffekseerManagerCore(swigCPtr);
      }
      swigCPtr = 0;
    }
  }

  public EffekseerManagerCore() {
    this(EffekseerCoreJNI.new_EffekseerManagerCore(), true);
  }

  public boolean Initialize(int spriteMaxCount, boolean srgbMode) {
    return EffekseerCoreJNI.EffekseerManagerCore_Initialize__SWIG_0(swigCPtr, this, spriteMaxCount, srgbMode);
  }

  public boolean Initialize(int spriteMaxCount) {
    return EffekseerCoreJNI.EffekseerManagerCore_Initialize__SWIG_1(swigCPtr, this, spriteMaxCount);
  }

  public void Update(float deltaFrames) {
    EffekseerCoreJNI.EffekseerManagerCore_Update(swigCPtr, this, deltaFrames);
  }

  public void BeginUpdate() {
    EffekseerCoreJNI.EffekseerManagerCore_BeginUpdate(swigCPtr, this);
  }

  public void EndUpdate() {
    EffekseerCoreJNI.EffekseerManagerCore_EndUpdate(swigCPtr, this);
  }

  public void UpdateHandleToMoveToFrame(int handle, float v) {
    EffekseerCoreJNI.EffekseerManagerCore_UpdateHandleToMoveToFrame(swigCPtr, this, handle, v);
  }

  public int Play(EffekseerEffectCore effect) {
    return EffekseerCoreJNI.EffekseerManagerCore_Play(swigCPtr, this, EffekseerEffectCore.getCPtr(effect), effect);
  }

  public float GetFrame(int handle) {
    return EffekseerCoreJNI.EffekseerManagerCore_GetFrame(swigCPtr, this, handle);
  }

  public int GetInstanceCount(int handle) {
    return EffekseerCoreJNI.EffekseerManagerCore_GetInstanceCount(swigCPtr, this, handle);
  }

  public int GetLayer(int handle) {
    return EffekseerCoreJNI.EffekseerManagerCore_GetLayer(swigCPtr, this, handle);
  }

  public void StopAllEffects() {
    EffekseerCoreJNI.EffekseerManagerCore_StopAllEffects(swigCPtr, this);
  }

  public void Stop(int handle) {
    EffekseerCoreJNI.EffekseerManagerCore_Stop(swigCPtr, this, handle);
  }

  public void SetPaused(int handle, boolean v) {
    EffekseerCoreJNI.EffekseerManagerCore_SetPaused(swigCPtr, this, handle, v);
  }

  public void SetShown(int handle, boolean v) {
    EffekseerCoreJNI.EffekseerManagerCore_SetShown(swigCPtr, this, handle, v);
  }

  public void SetAllColor(int handle, float r, float g, float b, float a) {
    EffekseerCoreJNI.EffekseerManagerCore_SetAllColor(swigCPtr, this, handle, r, g, b, a);
  }

  public void SetEffectPosition(int handle, float x, float y, float z) {
    EffekseerCoreJNI.EffekseerManagerCore_SetEffectPosition(swigCPtr, this, handle, x, y, z);
  }

  public void SetEffectRotation(int handle, float x, float y, float z) {
    EffekseerCoreJNI.EffekseerManagerCore_SetEffectRotation(swigCPtr, this, handle, x, y, z);
  }

  public void SetEffectScale(int handle, float x, float y, float z) {
    EffekseerCoreJNI.EffekseerManagerCore_SetEffectScale(swigCPtr, this, handle, x, y, z);
  }

  public void SetEffectTransformMatrix(int handle, float v0, float v1, float v2, float v3, float v4, float v5, float v6, float v7, float v8, float v9, float v10, float v11) {
    EffekseerCoreJNI.EffekseerManagerCore_SetEffectTransformMatrix(swigCPtr, this, handle, v0, v1, v2, v3, v4, v5, v6, v7, v8, v9, v10, v11);
  }

  public void SetEffectTransformBaseMatrix(int handle, float v0, float v1, float v2, float v3, float v4, float v5, float v6, float v7, float v8, float v9, float v10, float v11) {
    EffekseerCoreJNI.EffekseerManagerCore_SetEffectTransformBaseMatrix(swigCPtr, this, handle, v0, v1, v2, v3, v4, v5, v6, v7, v8, v9, v10, v11);
  }

  public void DrawBack(int layer) {
    EffekseerCoreJNI.EffekseerManagerCore_DrawBack__SWIG_0(swigCPtr, this, layer);
  }

  public void DrawBack() {
    EffekseerCoreJNI.EffekseerManagerCore_DrawBack__SWIG_1(swigCPtr, this);
  }

  public void DrawFront(int layer) {
    EffekseerCoreJNI.EffekseerManagerCore_DrawFront__SWIG_0(swigCPtr, this, layer);
  }

  public void DrawFront() {
    EffekseerCoreJNI.EffekseerManagerCore_DrawFront__SWIG_1(swigCPtr, this);
  }

  public void SetLayer(int handle, int layer) {
    EffekseerCoreJNI.EffekseerManagerCore_SetLayer(swigCPtr, this, handle, layer);
  }

  public void SetProjectionMatrix(float v0, float v1, float v2, float v3, float v4, float v5, float v6, float v7, float v8, float v9, float v10, float v11, float v12, float v13, float v14, float v15) {
    EffekseerCoreJNI.EffekseerManagerCore_SetProjectionMatrix(swigCPtr, this, v0, v1, v2, v3, v4, v5, v6, v7, v8, v9, v10, v11, v12, v13, v14, v15);
  }

  public void SetCameraMatrix(float v0, float v1, float v2, float v3, float v4, float v5, float v6, float v7, float v8, float v9, float v10, float v11, float v12, float v13, float v14, float v15) {
    EffekseerCoreJNI.EffekseerManagerCore_SetCameraMatrix(swigCPtr, this, v0, v1, v2, v3, v4, v5, v6, v7, v8, v9, v10, v11, v12, v13, v14, v15);
  }

  public boolean Exists(int handle) {
    return EffekseerCoreJNI.EffekseerManagerCore_Exists(swigCPtr, this, handle);
  }

  public void SetViewProjectionMatrixWithSimpleWindow(int windowWidth, int windowHeight) {
    EffekseerCoreJNI.EffekseerManagerCore_SetViewProjectionMatrixWithSimpleWindow(swigCPtr, this, windowWidth, windowHeight);
  }

  public void SetDynamicInput(int handle, int index, float value) {
    EffekseerCoreJNI.EffekseerManagerCore_SetDynamicInput(swigCPtr, this, handle, index, value);
  }

  public float GetDynamicInput(int handle, int index) {
    return EffekseerCoreJNI.EffekseerManagerCore_GetDynamicInput(swigCPtr, this, handle, index);
  }

  public void LaunchWorkerThreads(int n) {
    EffekseerCoreJNI.EffekseerManagerCore_LaunchWorkerThreads(swigCPtr, this, n);
  }

  public void SetBackground(long glid, boolean hasMipmap) {
    EffekseerCoreJNI.EffekseerManagerCore_SetBackground(swigCPtr, this, glid, hasMipmap);
  }

  public void UnsetBackground() {
    EffekseerCoreJNI.EffekseerManagerCore_UnsetBackground(swigCPtr, this);
  }

  public void SetDepth(long glid, boolean hasMipmap) {
    EffekseerCoreJNI.EffekseerManagerCore_SetDepth(swigCPtr, this, glid, hasMipmap);
  }

  public void UnsetDepth() {
    EffekseerCoreJNI.EffekseerManagerCore_UnsetDepth(swigCPtr, this);
  }

}
