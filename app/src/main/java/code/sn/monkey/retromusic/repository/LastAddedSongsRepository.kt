

package code.sn.monkey.retromusic.repository

import android.database.Cursor
import android.provider.MediaStore
import code.sn.monkey.retromusic.model.Album
import code.sn.monkey.retromusic.model.Artist
import code.sn.monkey.retromusic.model.Song
import code.sn.monkey.retromusic.util.PreferenceUtil

/**
 * Created by hemanths on 16/08/17.
 */
interface LastAddedRepository {
    fun recentSongs(): List<Song>

    fun recentAlbums(): List<Album>

    fun recentArtists(): List<Artist>
}

class RealLastAddedRepository(
    private val songRepository: RealSongRepository,
    private val albumRepository: RealAlbumRepository,
    private val artistRepository: RealArtistRepository
) : LastAddedRepository {
    override fun recentSongs(): List<Song> {
        return songRepository.songs(makeLastAddedCursor())
    }

    override fun recentAlbums(): List<Album> {
        return albumRepository.splitIntoAlbums(recentSongs())
    }

    override fun recentArtists(): List<Artist> {
        return artistRepository.splitIntoArtists(recentAlbums())
    }

    private fun makeLastAddedCursor(): Cursor? {
        val cutoff = PreferenceUtil.lastAddedCutoff
        return songRepository.makeSongCursor(
            MediaStore.Audio.Media.DATE_ADDED + ">?",
            arrayOf(cutoff.toString()),
            MediaStore.Audio.Media.DATE_ADDED + " DESC"
        )
    }
}
