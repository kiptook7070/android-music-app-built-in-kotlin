package code.sn.monkey.retromusic.model.smartplaylist

import code.sn.monkey.retromusic.App
import code.sn.monkey.retromusic.R
import code.sn.monkey.retromusic.model.Song
import kotlinx.android.parcel.Parcelize

@Parcelize
class LastAddedPlaylist : AbsSmartPlaylist(
    name = App.getContext().getString(R.string.last_added),
    iconRes = R.drawable.ic_library_add
) {
    override fun songs(): List<Song> {
        return lastAddedRepository.recentSongs()
    }
}