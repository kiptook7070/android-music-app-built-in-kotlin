
package code.sn.monkey.retromusic.glide

import android.graphics.drawable.Drawable
import android.widget.ImageView
import code.sn.monkey.appthemehelper.util.ATHUtil
import code.sn.monkey.retromusic.R
import code.sn.monkey.retromusic.glide.palette.BitmapPaletteTarget
import code.sn.monkey.retromusic.glide.palette.BitmapPaletteWrapper
import code.sn.monkey.retromusic.util.ColorUtil
import com.bumptech.glide.request.animation.GlideAnimation

abstract class SingleColorTarget(view: ImageView) : BitmapPaletteTarget(view) {

    private val defaultFooterColor: Int
        get() = ATHUtil.resolveColor(view.context, R.attr.colorControlNormal)

    abstract fun onColorReady(color: Int)

    override fun onLoadFailed(e: Exception?, errorDrawable: Drawable?) {
        super.onLoadFailed(e, errorDrawable)
        onColorReady(defaultFooterColor)
    }

    override fun onResourceReady(
        resource: BitmapPaletteWrapper?,
        glideAnimation: GlideAnimation<in BitmapPaletteWrapper>?
    ) {
        super.onResourceReady(resource, glideAnimation)
        resource?.let {
            onColorReady(
                ColorUtil.getColor(
                    it.palette,
                    ATHUtil.resolveColor(view.context, R.attr.colorPrimary)
                )
            )
        }
    }
}
