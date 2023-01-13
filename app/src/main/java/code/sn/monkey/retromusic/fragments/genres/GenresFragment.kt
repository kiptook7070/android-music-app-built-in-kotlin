
package code.sn.monkey.retromusic.fragments.genres

import android.os.Bundle
import android.view.View
import androidx.core.os.bundleOf
import androidx.lifecycle.Observer
import androidx.navigation.fragment.FragmentNavigatorExtras
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import code.sn.monkey.retromusic.EXTRA_GENRE
import code.sn.monkey.retromusic.R
import code.sn.monkey.retromusic.adapter.GenreAdapter
import code.sn.monkey.retromusic.fragments.base.AbsRecyclerViewFragment
import code.sn.monkey.retromusic.interfaces.IGenreClickListener
import code.sn.monkey.retromusic.model.Genre
import com.google.android.material.transition.MaterialElevationScale

class GenresFragment : AbsRecyclerViewFragment<GenreAdapter, LinearLayoutManager>(),
    IGenreClickListener {
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        libraryViewModel.getGenre().observe(viewLifecycleOwner, Observer {
            if (it.isNotEmpty())
                adapter?.swapDataSet(it)
            else
                adapter?.swapDataSet(listOf())
        })
    }

    override fun createLayoutManager(): LinearLayoutManager {
        return LinearLayoutManager(activity)
    }

    override fun createAdapter(): GenreAdapter {
        val dataSet = if (adapter == null) ArrayList() else adapter!!.dataSet
        return GenreAdapter(requireActivity(), dataSet, R.layout.item_list_no_image, this)
    }

    override val emptyMessage: Int
        get() = R.string.no_genres

    companion object {
        @JvmField
        val TAG: String = GenresFragment::class.java.simpleName

        @JvmStatic
        fun newInstance(): GenresFragment {
            return GenresFragment()
        }
    }

    override fun onClickGenre(genre: Genre, view: View) {
        exitTransition = MaterialElevationScale(false).apply {
            duration = 300L
        }
        reenterTransition = MaterialElevationScale(true).apply {
            duration = 300L
        }
        findNavController().navigate(
            R.id.genreDetailsFragment,
            bundleOf(EXTRA_GENRE to genre),
            null,
            FragmentNavigatorExtras(
                view to "genre"
            )
        )
    }
}
