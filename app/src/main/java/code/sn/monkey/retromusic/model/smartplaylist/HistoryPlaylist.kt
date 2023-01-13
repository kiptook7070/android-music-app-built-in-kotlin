package code.sn.monkey.retromusic.model.smartplaylist

import code.sn.monkey.retromusic.App
import code.sn.monkey.retromusic.R
import code.sn.monkey.retromusic.model.Song
import kotlinx.android.parcel.Parcelize
import org.koin.core.KoinComponent

@Parcelize
class HistoryPlaylist : AbsSmartPlaylist(
    name = App.getContext().getString(R.string.history),
    iconRes = R.drawable.ic_history
), KoinComponent {

    override fun songs(): List<Song> {
        return topPlayedRepository.recentlyPlayedTracks()
    }
}