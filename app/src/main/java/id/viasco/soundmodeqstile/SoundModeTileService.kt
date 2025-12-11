package id.viasco.soundmodeqstile

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.graphics.drawable.Icon
import android.media.AudioManager
import android.os.Handler
import android.os.Looper
import android.service.quicksettings.Tile
import android.service.quicksettings.TileService
import android.util.Log

class SoundModeTileService : TileService() {

    private val PREFS_NAME = "SoundModePrefs"
    private val KEY_LAST_VOLUME = "last_ring_volume"
    private val uiHandler = Handler(Looper.getMainLooper())

    private val receiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context?, intent: Intent?) {
            // Update tile on main thread to ensure UI refreshes
            uiHandler.post { updateTile() }
        }
    }

    override fun onStartListening() {
        super.onStartListening()
        val filter = IntentFilter().apply {
            addAction(AudioManager.RINGER_MODE_CHANGED_ACTION)
            addAction("android.media.VOLUME_CHANGED_ACTION")
        }
        registerReceiver(receiver, filter)
        uiHandler.post { updateTile() }
    }

    override fun onStopListening() {
        super.onStopListening()
        try {
            unregisterReceiver(receiver)
        } catch (e: Exception) {
            // Ignore
        }
    }

    override fun onClick() {
        super.onClick()
        val audioManager = getSystemService(Context.AUDIO_SERVICE) as AudioManager
        val currentRingerMode = audioManager.ringerMode
        val currentVolume = audioManager.getStreamVolume(AudioManager.STREAM_RING)

        var nextState = "RING"

        if (currentRingerMode == AudioManager.RINGER_MODE_NORMAL && currentVolume > 0) {
            // Ring -> Vibrate
            nextState = "VIBRATE"
            saveVolume(currentVolume)
        } else if (currentRingerMode == AudioManager.RINGER_MODE_VIBRATE) {
            // Vibrate -> Silent (True Silent / Volume 0)
            nextState = "SILENT"
        } else {
            // Silent -> Ring
            nextState = "RING"
        }

        try {
            when (nextState) {
                "VIBRATE" -> {
                    audioManager.ringerMode = AudioManager.RINGER_MODE_VIBRATE
                }
                "SILENT" -> {
                    // Switch to Normal first (to allow volume change)
                    audioManager.ringerMode = AudioManager.RINGER_MODE_NORMAL
                    // Mute Streams
                    audioManager.adjustStreamVolume(AudioManager.STREAM_RING, AudioManager.ADJUST_MUTE, 0)
                    audioManager.adjustStreamVolume(AudioManager.STREAM_NOTIFICATION, AudioManager.ADJUST_MUTE, 0)
                }
                "RING" -> {
                    // Ensure Normal mode
                    audioManager.ringerMode = AudioManager.RINGER_MODE_NORMAL
                    // Unmute
                    audioManager.adjustStreamVolume(AudioManager.STREAM_RING, AudioManager.ADJUST_UNMUTE, 0)
                    audioManager.adjustStreamVolume(AudioManager.STREAM_NOTIFICATION, AudioManager.ADJUST_UNMUTE, 0)
                    
                    // If still 0 after unmute (or was manually set to 0), restore saved volume
                    val savedVolume = getSavedVolume(audioManager)
                    if (audioManager.getStreamVolume(AudioManager.STREAM_RING) == 0) {
                        audioManager.setStreamVolume(AudioManager.STREAM_RING, savedVolume, 0)
                        audioManager.setStreamVolume(AudioManager.STREAM_NOTIFICATION, savedVolume, 0)
                    }
                }
            }
            // Delay update slightly to let system state settle
            uiHandler.postDelayed({ updateTile() }, 100)
        } catch (e: Exception) {
            Log.e("SoundModeTile", "Error changing mode", e)
        }
    }

    private fun updateTile() {
        val tile = qsTile ?: return
        val audioManager = getSystemService(Context.AUDIO_SERVICE) as AudioManager
        val currentRingerMode = audioManager.ringerMode
        val currentVolume = audioManager.getStreamVolume(AudioManager.STREAM_RING)

        Log.d("SoundModeTile", "UpdateTile: Mode=$currentRingerMode, Vol=$currentVolume")

        if (currentRingerMode == AudioManager.RINGER_MODE_NORMAL && currentVolume > 0) {
            // Normal Mode + Volume > 0 = Ring
            tile.state = Tile.STATE_ACTIVE
            tile.label = "Ring"
            tile.icon = Icon.createWithResource(this, R.drawable.ic_ring)
        } else if (currentRingerMode == AudioManager.RINGER_MODE_VIBRATE) {
            // Vibrate Mode
            tile.state = Tile.STATE_ACTIVE
            tile.label = "Vibrate"
            tile.icon = Icon.createWithResource(this, R.drawable.ic_vibrate)
        } else {
            // Silent State: Can be RINGER_MODE_SILENT (DND) OR RINGER_MODE_NORMAL with Volume 0
            tile.state = Tile.STATE_ACTIVE
            tile.label = "Silent"
            tile.icon = Icon.createWithResource(this, R.drawable.ic_mute)
        }
        tile.updateTile()
    }

    private fun saveVolume(volume: Int) {
        val prefs = getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        prefs.edit().putInt(KEY_LAST_VOLUME, volume).apply()
    }

    private fun getSavedVolume(audioManager: AudioManager): Int {
        val prefs = getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        val maxVolume = audioManager.getStreamMaxVolume(AudioManager.STREAM_RING)
        val defaultVolume = (maxVolume * 0.7).toInt().coerceAtLeast(1)
        return prefs.getInt(KEY_LAST_VOLUME, defaultVolume)
    }
}