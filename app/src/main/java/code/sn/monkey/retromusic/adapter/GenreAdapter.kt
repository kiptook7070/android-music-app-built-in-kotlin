
package code.sn.monkey.retromusic.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.ViewCompat
import androidx.fragment.app.FragmentActivity
import androidx.recyclerview.widget.RecyclerView
import code.sn.monkey.retromusic.R
import code.sn.monkey.retromusic.adapter.base.MediaEntryViewHolder
import code.sn.monkey.retromusic.interfaces.IGenreClickListener
import code.sn.monkey.retromusic.model.Genre
import java.util.*

/**
 * @author Hemanth S (h4h13).
 */

class GenreAdapter(
    private val activity: FragmentActivity,
    var dataSet: List<Genre>,
    private val mItemLayoutRes: Int,
    private val listener: IGenreClickListener
) : RecyclerView.Adapter<GenreAdapter.ViewHolder>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(LayoutInflater.from(activity).inflate(mItemLayoutRes, parent, false))
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val genre = dataSet[position]
        holder.title?.text = genre.name
        holder.text?.text = String.format(
            Locale.getDefault(),
            "%d %s",
            genre.songCount,
            if (genre.songCount > 1) activity.getString(R.string.songs) else activity.getString(R.string.song)
        )
    }

    override fun getItemCount(): Int {
        return dataSet.size
    }

    fun swapDataSet(list: List<Genre>) {
        dataSet = list
        notifyDataSetChanged()
    }

    inner class ViewHolder(itemView: View) : MediaEntryViewHolder(itemView) {
        override fun onClick(v: View?) {
            ViewCompat.setTransitionName(itemView, "genre")
            listener.onClickGenre(dataSet[layoutPosition], itemView)
        }
    }
}
