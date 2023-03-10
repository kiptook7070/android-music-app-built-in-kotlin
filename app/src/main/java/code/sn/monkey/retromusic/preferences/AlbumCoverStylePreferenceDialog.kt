

package code.sn.monkey.retromusic.preferences

import android.annotation.SuppressLint
import android.app.Dialog
import android.content.Context
import android.os.Bundle
import android.util.AttributeSet
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.core.graphics.BlendModeColorFilterCompat
import androidx.core.graphics.BlendModeCompat.SRC_IN
import androidx.fragment.app.DialogFragment
import androidx.viewpager.widget.PagerAdapter
import androidx.viewpager.widget.ViewPager
import code.sn.monkey.appthemehelper.common.prefs.supportv7.ATEDialogPreference
import code.sn.monkey.retromusic.App
import code.sn.monkey.retromusic.R
import code.sn.monkey.retromusic.extensions.*
import code.sn.monkey.retromusic.fragments.AlbumCoverStyle
import code.sn.monkey.retromusic.fragments.AlbumCoverStyle.*
import code.sn.monkey.retromusic.util.NavigationUtil
import code.sn.monkey.retromusic.util.PreferenceUtil
import code.sn.monkey.retromusic.util.ViewUtil
import com.bumptech.glide.Glide

class AlbumCoverStylePreference @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = -1,
    defStyleRes: Int = -1
) : ATEDialogPreference(context, attrs, defStyleAttr, defStyleRes) {

    private val mLayoutRes = R.layout.preference_dialog_now_playing_screen

    override fun getDialogLayoutResource(): Int {
        return mLayoutRes
    }

    init {
        icon?.colorFilter =
            BlendModeColorFilterCompat.createBlendModeColorFilterCompat(
                context.colorControlNormal(),
                SRC_IN
            )
    }
}

class AlbumCoverStylePreferenceDialog : DialogFragment(),
    ViewPager.OnPageChangeListener {

    private var viewPagerPosition: Int = 0

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        @SuppressLint("InflateParams") val view =
            LayoutInflater.from(requireContext())
                .inflate(R.layout.preference_dialog_now_playing_screen, null)
        val viewPager = view.findViewById<ViewPager>(R.id.now_playing_screen_view_pager)
        viewPager.adapter = AlbumCoverStyleAdapter(requireContext())
        viewPager.addOnPageChangeListener(this)
        viewPager.pageMargin = ViewUtil.convertDpToPixel(32f, resources).toInt()
        viewPager.currentItem = PreferenceUtil.albumCoverStyle.ordinal

        return materialDialog(R.string.pref_title_album_cover_style)
            .setPositiveButton(R.string.set) { _, _ ->
                val coverStyle = values()[viewPagerPosition]
                if (isAlbumCoverStyle(coverStyle)) {
                    val result = getString(coverStyle.titleRes) + " theme is Pro version feature."
                    Toast.makeText(context, result, Toast.LENGTH_SHORT).show()
                    NavigationUtil.goToProVersion(requireActivity())
                } else {
                    PreferenceUtil.albumCoverStyle = coverStyle
                }
            }
            .setView(view)
            .create()
            .colorButtons()
    }

    override fun onPageScrolled(position: Int, positionOffset: Float, positionOffsetPixels: Int) {
    }

    override fun onPageSelected(position: Int) {
        this.viewPagerPosition = position
    }

    override fun onPageScrollStateChanged(state: Int) {
    }

    private class AlbumCoverStyleAdapter(private val context: Context) :
        PagerAdapter() {

        override fun instantiateItem(collection: ViewGroup, position: Int): Any {
            val albumCoverStyle = values()[position]

            val inflater = LayoutInflater.from(context)
            val layout = inflater.inflate(
                R.layout.preference_now_playing_screen_item,
                collection,
                false
            ) as ViewGroup
            collection.addView(layout)

            val image = layout.findViewById<ImageView>(R.id.image)
            val title = layout.findViewById<TextView>(R.id.title)
            val proText = layout.findViewById<TextView>(R.id.proText)
            Glide.with(context).load(albumCoverStyle.drawableResId).into(image)
            title.setText(albumCoverStyle.titleRes)
            if (isAlbumCoverStyle(albumCoverStyle)) {
                proText.show()
                proText.setText(R.string.pro)
            } else {
                proText.hide()
            }
            return layout
        }

        override fun destroyItem(
            collection: ViewGroup,
            position: Int,
            view: Any
        ) {
            collection.removeView(view as View)
        }

        override fun getCount(): Int {
            return values().size
        }

        override fun isViewFromObject(view: View, instace: Any): Boolean {
            return view === instace
        }

        override fun getPageTitle(position: Int): CharSequence? {
            return context.getString(values()[position].titleRes)
        }
    }

    companion object {
        val TAG: String = AlbumCoverStylePreferenceDialog::class.java.simpleName

        fun newInstance(): AlbumCoverStylePreferenceDialog {
            return AlbumCoverStylePreferenceDialog()
        }
    }
}

private fun isAlbumCoverStyle(style: AlbumCoverStyle): Boolean {
    return (!App.isProVersion() && (style == Circle || style == Card || style == FullCard))
}