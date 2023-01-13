
package code.sn.monkey.retromusic.adapter.album

import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.FragmentActivity
import code.sn.monkey.retromusic.glide.AlbumGlideRequest
import code.sn.monkey.retromusic.glide.RetroMusicColoredTarget
import code.sn.monkey.retromusic.helper.HorizontalAdapterHelper
import code.sn.monkey.retromusic.interfaces.IAlbumClickListener
import code.sn.monkey.retromusic.interfaces.ICabHolder
import code.sn.monkey.retromusic.model.Album
import code.sn.monkey.retromusic.util.MusicUtil
import code.sn.monkey.retromusic.util.color.MediaNotificationProcessor
import com.bumptech.glide.Glide

class HorizontalAlbumAdapter(
    activity: FragmentActivity,
    dataSet: List<Album>,
    ICabHolder: ICabHolder?,
    albumClickListener: IAlbumClickListener
) : AlbumAdapter(
    activity, dataSet, HorizontalAdapterHelper.LAYOUT_RES, ICabHolder, albumClickListener
) {

    override fun createViewHolder(view: View, viewType: Int): ViewHolder {
        val params = view.layoutParams as ViewGroup.MarginLayoutParams
        HorizontalAdapterHelper.applyMarginToLayoutParams(activity, params, viewType)
        return ViewHolder(view)
    }

    override fun setColors(color: MediaNotificationProcessor, holder: ViewHolder) {
        // holder.title?.setTextColor(ATHUtil.resolveColor(activity, android.R.attr.textColorPrimary))
        // holder.text?.setTextColor(ATHUtil.resolveColor(activity, android.R.attr.textColorSecondary))
    }

    override fun loadAlbumCover(album: Album, holder: ViewHolder) {
        if (holder.image == null) return
        AlbumGlideRequest.Builder.from(Glide.with(activity), album.safeGetFirstSong())
            .checkIgnoreMediaStore()
            .generatePalette(activity)
            .build()
            .into(object : RetroMusicColoredTarget(holder.image!!) {
                override fun onColorReady(colors: MediaNotificationProcessor) {
                    setColors(colors, holder)
                }
            })
    }

    override fun getAlbumText(album: Album): String? {
        return MusicUtil.getYearString(album.year)
    }

    override fun getItemViewType(position: Int): Int {
        return HorizontalAdapterHelper.getItemViewType(position, itemCount)
    }

    override fun getItemCount(): Int {
        return dataSet.size
    }

    companion object {
        val TAG: String = AlbumAdapter::class.java.simpleName
    }
}
