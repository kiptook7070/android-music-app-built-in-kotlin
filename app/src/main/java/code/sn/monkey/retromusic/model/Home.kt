
package code.sn.monkey.retromusic.model

import androidx.annotation.StringRes
import code.sn.monkey.retromusic.HomeSection

data class Home(
    val arrayList: List<Any>,
    @HomeSection
    val homeSection: Int,
    @StringRes
    val titleRes: Int
)