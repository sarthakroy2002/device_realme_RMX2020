#include <gui/SurfaceComposerClient.h>

using namespace android;

extern "C" {

  void _ZN7android21SurfaceComposerClient11Transaction20setDisplayProjectionERKNS_2spINS_7IBinderEEEjRKNS_4RectES9_(const sp<IBinder>& token,uint32_t orientation,const android::Rect& layerStackRect,const android::Rect& displayRect) {
    SurfaceComposerClient::Transaction *t = nullptr;
    t->setDisplayProjection(token, static_cast<ui::Rotation>(orientation), layerStackRect, displayRect);
  }

  void _ZN7android21SurfaceComposerClient13createSurfaceERKNS_7String8EjjijRKNS_2spINS_7IBinderEEENS_13LayerMetadataEPj(const String8& name, uint32_t w, uint32_t h, PixelFormat format, uint32_t flags, const sp<IBinder>& parentHandle, LayerMetadata metadata, uint32_t* outTransformHint);
  
  void _ZN7android21SurfaceComposerClient13createSurfaceERKNS_7String8EjjijPNS_14SurfaceControlENS_13LayerMetadataE(const String8& name, uint32_t w, uint32_t h, PixelFormat format, uint32_t flags, SurfaceControl* parent, LayerMetadata metadata) {
    _ZN7android21SurfaceComposerClient13createSurfaceERKNS_7String8EjjijRKNS_2spINS_7IBinderEEENS_13LayerMetadataEPj(name, w, h, format, flags, nullptr, metadata, nullptr);
  }

  void _ZN7android14SurfaceControlD0Ev(void);

  void _ZN7android14SurfaceControl7releaseEv(void) {
    _ZN7android14SurfaceControlD0Ev();
  }

}
