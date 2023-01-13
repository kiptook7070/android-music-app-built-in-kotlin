
package code.sn.monkey.retromusic.glide

import android.graphics.drawable.Drawable
import android.widget.ImageView
import code.sn.monkey.appthemehelper.util.ATHUtil
import code.sn.monkey.retromusic.App
import code.sn.monkey.retromusic.R
import code.sn.monkey.retromusic.glide.palette.BitmapPaletteTarget
import code.sn.monkey.retromusic.glide.palette.BitmapPaletteWrapper
import code.sn.monkey.retromusic.util.color.MediaNotificationProcessor
import com.bumptech.glide.request.animation.GlideAnimation

abstract class RetroMusicColoredTarget(view: ImageView) : BitmapPaletteTarget(view) {

    protected val defaultFooterColor: Int
        get() = ATHUtil.resolveColor(getView().context, R.attr.colorControlNormal)

    abstract fun onColorReady(colors: MediaNotificationProcessor)

    override fun onLoadFailed(e: Exception?, errorDrawable: Drawable?) {
        super.onLoadFailed(e, errorDrawable)
        val colors = MediaNotificationProcessor(App.getContext(), errorDrawable)
        onColorReady(colors)
    }

    override fun onResourceReady(
        resource: BitmapPaletteWrapper?,
        glideAnimation: GlideAnimation<in BitmapPaletteWrapper>?
    ) {
        super.onResourceReady(resource, glideAnimation)
        resource?.let { bitmapWrap ->
            MediaNotificationProcessor(App.getContext()).getPaletteAsync({
                onColorReady(it)
            }, bitmapWrap.bitmap)
        }
    }
}
