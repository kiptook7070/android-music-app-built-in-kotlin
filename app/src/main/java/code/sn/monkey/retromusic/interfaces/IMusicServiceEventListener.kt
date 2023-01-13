
package code.sn.monkey.retromusic.interfaces

interface IMusicServiceEventListener {
    fun onServiceConnected()

    fun onServiceDisconnected()

    fun onQueueChanged()

    fun onPlayingMetaChanged()

    fun onPlayStateChanged()

    fun onRepeatModeChanged()

    fun onShuffleModeChanged()

    fun onMediaStoreChanged()
}
