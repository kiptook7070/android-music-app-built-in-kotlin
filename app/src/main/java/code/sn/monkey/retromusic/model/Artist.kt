
package code.sn.monkey.retromusic.model

import code.sn.monkey.retromusic.util.MusicUtil
import code.sn.monkey.retromusic.util.PreferenceUtil
import java.util.*

data class Artist(
    val id: Long,
    val albums: List<Album>
) {

    val name: String
        get() {
            val name = safeGetFirstAlbum().safeGetFirstSong().albumArtist
            if (PreferenceUtil.albumArtistsOnly && MusicUtil.isVariousArtists(name)) {
                return VARIOUS_ARTISTS_DISPLAY_NAME
            }
            return if (MusicUtil.isArtistNameUnknown(name)) {
                UNKNOWN_ARTIST_DISPLAY_NAME
            } else safeGetFirstAlbum().safeGetFirstSong().artistName
        }

    val songCount: Int
        get() {
            var songCount = 0
            for (album in albums) {
                songCount += album.songCount
            }
            return songCount
        }

    val albumCount: Int
        get() = albums.size

    val songs: List<Song>
        get() = albums.flatMap { it.songs }

    fun safeGetFirstAlbum(): Album {
        return albums.firstOrNull() ?: Album.empty
    }

    companion object {
        const val UNKNOWN_ARTIST_DISPLAY_NAME = "Unknown Artist"
        const val VARIOUS_ARTISTS_DISPLAY_NAME = "Various Artists"
        const val VARIOUS_ARTISTS_ID : Long = -2
        val empty = Artist(-1, emptyList())

    }
}
