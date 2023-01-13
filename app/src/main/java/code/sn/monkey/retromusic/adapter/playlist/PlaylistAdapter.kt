
package code.sn.monkey.retromusic.adapter.playlist

import android.graphics.Color
import android.graphics.drawable.Drawable
import android.text.TextUtils
import android.view.LayoutInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.widget.PopupMenu
import androidx.core.view.ViewCompat
import androidx.fragment.app.FragmentActivity
import androidx.lifecycle.lifecycleScope
import code.sn.monkey.appthemehelper.util.ATHUtil
import code.sn.monkey.appthemehelper.util.TintHelper
import code.sn.monkey.retromusic.R
import code.sn.monkey.retromusic.adapter.base.AbsMultiSelectAdapter
import code.sn.monkey.retromusic.adapter.base.MediaEntryViewHolder
import code.sn.monkey.retromusic.db.PlaylistEntity
import code.sn.monkey.retromusic.db.PlaylistWithSongs
import code.sn.monkey.retromusic.db.SongEntity
import code.sn.monkey.retromusic.db.toSongs
import code.sn.monkey.retromusic.extensions.hide
import code.sn.monkey.retromusic.extensions.show
import code.sn.monkey.retromusic.helper.menu.PlaylistMenuHelper
import code.sn.monkey.retromusic.helper.menu.SongsMenuHelper
import code.sn.monkey.retromusic.interfaces.ICabHolder
import code.sn.monkey.retromusic.interfaces.IPlaylistClickListener
import code.sn.monkey.retromusic.model.Song
import code.sn.monkey.retromusic.util.AutoGeneratedPlaylistBitmap
import code.sn.monkey.retromusic.util.MusicUtil
import kotlinx.coroutines.Dispatchers.IO
import kotlinx.coroutines.Dispatchers.Main
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class PlaylistAdapter(
    private val activity: FragmentActivity,
    private var dataSet: List<PlaylistWithSongs>,
    private var itemLayoutRes: Int,
    ICabHolder: ICabHolder?,
    private val listener: IPlaylistClickListener
) : AbsMultiSelectAdapter<PlaylistAdapter.ViewHolder, PlaylistWithSongs>(
    activity,
    ICabHolder,
    R.menu.menu_playlists_selection
) {

    init {
        setHasStableIds(true)
    }

    fun swapDataSet(dataSet: List<PlaylistWithSongs>) {
        this.dataSet = dataSet
        notifyDataSetChanged()
    }

    override fun getItemId(position: Int): Long {
        return dataSet[position].playlistEntity.playListId
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(activity).inflate(itemLayoutRes, parent, false)
        return createViewHolder(view)
    }

    fun createViewHolder(view: View): ViewHolder {
        return ViewHolder(view)
    }

    private fun getPlaylistTitle(playlist: PlaylistEntity): String {
        return if (TextUtils.isEmpty(playlist.playlistName)) "-" else playlist.playlistName
    }

    private fun getPlaylistText(playlist: PlaylistWithSongs): String {
        return MusicUtil.getPlaylistInfoString(activity, playlist.songs.toSongs())
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val playlist = dataSet[position]
        holder.itemView.isActivated = isChecked(playlist)
        holder.title?.text = getPlaylistTitle(playlist.playlistEntity)
        holder.text?.text = getPlaylistText(playlist)
        holder.image?.setImageDrawable(getIconRes())
        val isChecked = isChecked(playlist)
        if (isChecked) {
            holder.menu?.hide()
        } else {
            holder.menu?.show()
        }
        //playlistBitmapLoader(activity, holder, playlist)
    }

    private fun getIconRes(): Drawable = TintHelper.createTintedDrawable(
        activity,
        R.drawable.ic_playlist_play,
        ATHUtil.resolveColor(activity, R.attr.colorControlNormal)
    )

    override fun getItemCount(): Int {
        return dataSet.size
    }

    override fun getIdentifier(position: Int): PlaylistWithSongs? {
        return dataSet[position]
    }

    override fun getName(playlist: PlaylistWithSongs): String {
        return playlist.playlistEntity.playlistName
    }

    override fun onMultipleItemAction(menuItem: MenuItem, selection: List<PlaylistWithSongs>) {
        when (menuItem.itemId) {
            else -> SongsMenuHelper.handleMenuClick(
                activity,
                getSongList(selection),
                menuItem.itemId
            )
        }
    }

    private fun getSongList(playlists: List<PlaylistWithSongs>): List<Song> {
        val songs = mutableListOf<Song>()
        playlists.forEach {
            songs.addAll(it.songs.toSongs())
        }
        return songs
    }

    private fun getSongs(playlist: PlaylistWithSongs): List<SongEntity> =
        mutableListOf<SongEntity>().apply {
            addAll(playlist.songs)
        }

    inner class ViewHolder(itemView: View) : MediaEntryViewHolder(itemView) {
        init {
            image?.apply {
                val iconPadding =
                    activity.resources.getDimensionPixelSize(R.dimen.list_item_image_icon_padding)
                setPadding(iconPadding, iconPadding, iconPadding, iconPadding)
            }
            menu?.setOnClickListener { view ->
                val popupMenu = PopupMenu(activity, view)
                popupMenu.inflate(R.menu.menu_item_playlist)
                popupMenu.setOnMenuItemClickListener { item ->
                    PlaylistMenuHelper.handleMenuClick(activity, dataSet[layoutPosition], item)
                }
                popupMenu.show()
            }

            imageTextContainer?.apply {
                cardElevation = 0f
                setCardBackgroundColor(Color.TRANSPARENT)
            }
        }

        override fun onClick(v: View?) {
            if (isInQuickSelectMode) {
                toggleChecked(layoutPosition)
            } else {
                ViewCompat.setTransitionName(itemView, "playlist")
                listener.onPlaylistClick(dataSet[layoutPosition], itemView)
            }
        }

        override fun onLongClick(v: View?): Boolean {
            toggleChecked(layoutPosition)
            return true
        }
    }

    private fun playlistBitmapLoader(
        activity: FragmentActivity,
        viewHolder: ViewHolder,
        playlist: PlaylistWithSongs
    ) {

        activity.lifecycleScope.launch(IO) {
            val songs = playlist.songs.toSongs()
            val bitmap = AutoGeneratedPlaylistBitmap.getBitmap(activity, songs, false, false)
            withContext(Main) { viewHolder.image?.setImageBitmap(bitmap) }
        }

        /*
          override fun doInBackground(vararg params: Void?): Bitmap {
              val songs = playlist.songs.toSongs()
              return AutoGeneratedPlaylistBitmap.getBitmap(activity, songs, false, false)
          }

          override fun onPostExecute(result: Bitmap?) {
              super.onPostExecute(result)
              viewHolder.image?.setImageBitmap(result)
              val color = RetroColorUtil.getColor(
                  RetroColorUtil.generatePalette(
                      result
                  ),
                  ATHUtil.resolveColor(activity, R.attr.colorSurface)
              )
              viewHolder.paletteColorContainer?.setBackgroundColor(color)
          }*/
    }

    companion object {
        val TAG: String = PlaylistAdapter::class.java.simpleName
    }
}
