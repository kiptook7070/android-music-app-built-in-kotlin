package code.sn.monkey.retromusic.model.smartplaylist

import code.sn.monkey.retromusic.App
import code.sn.monkey.retromusic.R
import code.sn.monkey.retromusic.model.Song
import kotlinx.android.parcel.Parcelize

@Parcelize
class NotPlayedPlaylist : AbsSmartPlaylist(
    name = App.getContext().getString(R.string.not_recently_played),
    iconRes = R.drawable.ic_watch_later
) {
    override fun songs(): List<Song> {
        return topPlayedRepository.notRecentlyPlayedTracks()
    }
}