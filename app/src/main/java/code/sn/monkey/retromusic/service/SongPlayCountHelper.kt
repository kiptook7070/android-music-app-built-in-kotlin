
package code.sn.monkey.retromusic.service

import code.sn.monkey.retromusic.helper.StopWatch
import code.sn.monkey.retromusic.model.Song

class SongPlayCountHelper {

    private val stopWatch = StopWatch()
    var song = Song.emptySong
        private set

    fun shouldBumpPlayCount(): Boolean {
        return song.duration * 0.5 < stopWatch.elapsedTime
    }

    fun notifySongChanged(song: Song) {
        synchronized(this) {
            stopWatch.reset()
            this.song = song
        }
    }

    fun notifyPlayStateChanged(isPlaying: Boolean) {
        synchronized(this) {
            if (isPlaying) {
                stopWatch.start()
            } else {
                stopWatch.pause()
            }
        }
    }

    companion object {
        val TAG: String = SongPlayCountHelper::class.java.simpleName
    }
}