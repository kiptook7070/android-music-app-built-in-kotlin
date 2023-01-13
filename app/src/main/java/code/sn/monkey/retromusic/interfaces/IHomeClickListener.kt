package code.sn.monkey.retromusic.interfaces

import code.sn.monkey.retromusic.model.Album
import code.sn.monkey.retromusic.model.Artist
import code.sn.monkey.retromusic.model.Genre

interface IHomeClickListener {
    fun onAlbumClick(album: Album)

    fun onArtistClick(artist: Artist)

    fun onGenreClick(genre: Genre)
}