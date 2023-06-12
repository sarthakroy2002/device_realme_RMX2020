#include <gui/SurfaceComposerClient.h>
#include <ui/Size.h>

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

  void _ZN7android21SurfaceComposerClient20getStaticDisplayInfoERKNS_2spINS_7IBinderEEEPNS_2ui17StaticDisplayInfoE(void);

  void _ZN7android21SurfaceComposerClient14getDisplayInfoERKNS_2spINS_7IBinderEEEPNS_11DisplayInfoE(void) {
    _ZN7android21SurfaceComposerClient20getStaticDisplayInfoERKNS_2spINS_7IBinderEEEPNS_2ui17StaticDisplayInfoE();
  }

  void _ZN7android14SurfaceControl10getSurfaceEv(void);

  void _ZNK7android14SurfaceControl10getSurfaceEv(void) {
    _ZN7android14SurfaceControl10getSurfaceEv();
  }

  status_t _ZN7android21SurfaceComposerClient11Transaction5applyEbb(void* thisptr, bool synchronous, bool oneWay);

  status_t _ZN7android21SurfaceComposerClient11Transaction5applyEb(void* thisptr, bool synchronous) {
    return _ZN7android21SurfaceComposerClient11Transaction5applyEbb(thisptr, synchronous, false /* oneWay */);
  }

  ui::Size _ZN7android2ui4Size7INVALIDE;

}
