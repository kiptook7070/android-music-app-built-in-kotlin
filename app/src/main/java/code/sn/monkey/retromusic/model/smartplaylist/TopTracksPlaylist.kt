package code.sn.monkey.retromusic.model.smartplaylist

import code.sn.monkey.retromusic.App
import code.sn.monkey.retromusic.R
import code.sn.monkey.retromusic.model.Song
import kotlinx.android.parcel.Parcelize

@Parcelize
class TopTracksPlaylist : AbsSmartPlaylist(
    name = App.getContext().getString(R.string.my_top_tracks),
    iconRes = R.drawable.ic_trending_up
) {
    override fun songs(): List<Song> {
        return topPlayedRepository.topTracks()
    }
}