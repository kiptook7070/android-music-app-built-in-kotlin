
package code.sn.monkey.retromusic.glide

import android.content.Context
import code.sn.monkey.retromusic.glide.artistimage.ArtistImage
import code.sn.monkey.retromusic.glide.artistimage.Factory
import code.sn.monkey.retromusic.glide.audiocover.AudioFileCover
import code.sn.monkey.retromusic.glide.audiocover.AudioFileCoverLoader
import com.bumptech.glide.Glide
import com.bumptech.glide.GlideBuilder
import com.bumptech.glide.module.GlideModule
import java.io.InputStream

class RetroMusicGlideModule : GlideModule {
    override fun applyOptions(context: Context, builder: GlideBuilder) {
    }

    override fun registerComponents(context: Context, glide: Glide) {
        glide.register(
            AudioFileCover::class.java,
            InputStream::class.java,
            AudioFileCoverLoader.Factory()
        )
        glide.register(ArtistImage::class.java, InputStream::class.java, Factory(context))
    }
}
