package code.sn.monkey.retromusic.model

import code.sn.monkey.retromusic.repository.LastAddedRepository
import code.sn.monkey.retromusic.repository.SongRepository
import code.sn.monkey.retromusic.repository.TopPlayedRepository
import org.koin.core.KoinComponent
import org.koin.core.inject

abstract class AbsCustomPlaylist(
    id: Long,
    name: String
) : Playlist(id, name), KoinComponent {

    abstract fun songs(): List<Song>

    protected val songRepository by inject<SongRepository>()

    protected val topPlayedRepository by inject<TopPlayedRepository>()

    protected val lastAddedRepository by inject<LastAddedRepository>()
}