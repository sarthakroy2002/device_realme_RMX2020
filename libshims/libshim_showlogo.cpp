#include <gui/SurfaceComposerClient.h>

namespace android {

extern "C" void _ZN7android21SurfaceComposerClient11Transaction20setDisplayProjectionERKNS_2spINS_7IBinderEEEjRKNS_4RectES9_(const sp<IBinder>& token,uint32_t orientation,const android::Rect& layerStackRect,const android::Rect& displayRect) {
  android::SurfaceComposerClient::Transaction *t = nullptr;
  t->setDisplayProjection(token, static_cast<android::ui::Rotation>(orientation), layerStackRect, displayRect);
	}

extern "C" void _ZN7android21SurfaceComposerClient13createSurfaceERKNS_7String8EjjijPNS_14SurfaceControlENS_13LayerMetadataEPj(const String8& name, uint32_t w, uint32_t h, PixelFormat format, uint32_t flags, SurfaceControl* parent, LayerMetadata metadata, uint32_t* outTransformHint);

extern "C" void _ZN7android21SurfaceComposerClient13createSurfaceERKNS_7String8EjjijPNS_14SurfaceControlENS_13LayerMetadataE(const String8& name, uint32_t w, uint32_t h, PixelFormat format, uint32_t flags, SurfaceControl* parent, LayerMetadata metadata) {
	_ZN7android21SurfaceComposerClient13createSurfaceERKNS_7String8EjjijPNS_14SurfaceControlENS_13LayerMetadataEPj(name, w, h, format, flags, nullptr, metadata, nullptr);
		}

extern "C" void _ZN7android14SurfaceControl7releaseEv() { }

}
