package code.sn.monkey.retromusic.interfaces

import android.view.View
import code.sn.monkey.retromusic.model.Genre

interface IGenreClickListener {
    fun onClickGenre(genre: Genre, view: View)
}