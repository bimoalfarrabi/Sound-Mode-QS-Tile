package id.viasco.soundmodeqstile

import android.app.NotificationManager
import android.app.PendingIntent
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.graphics.drawable.Icon
import android.media.AudioManager
import android.os.Build
import android.provider.Settings
import android.service.quicksettings.Tile
import android.service.quicksettings.TileService
import android.util.Log
import android.widget.Toast

class SoundModeTileService : TileService() {

    private val receiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context?, intent: Intent?) {
            updateTile()
        }
    }

    override fun onStartListening() {
        super.onStartListening()
        val filter = IntentFilter(AudioManager.RINGER_MODE_CHANGED_ACTION)
        registerReceiver(receiver, filter)
        updateTile()
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
        val currentMode = audioManager.ringerMode

        // Calculate new mode
        val newMode = when (currentMode) {
            AudioManager.RINGER_MODE_NORMAL -> AudioManager.RINGER_MODE_VIBRATE
            AudioManager.RINGER_MODE_VIBRATE -> AudioManager.RINGER_MODE_SILENT
            AudioManager.RINGER_MODE_SILENT -> AudioManager.RINGER_MODE_NORMAL
            else -> AudioManager.RINGER_MODE_NORMAL
        }

        // Check permission if switching TO or FROM Silent
        if (newMode == AudioManager.RINGER_MODE_SILENT || currentMode == AudioManager.RINGER_MODE_SILENT) {
            val notificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            if (!notificationManager.isNotificationPolicyAccessGranted) {
                openDndSettings()
                return
            }
        }

        try {
            audioManager.ringerMode = newMode
            // Update will be handled by BroadcastReceiver, but we can optimistically update UI
            // Thread.sleep(50) // Optional small delay
        } catch (e: Exception) {
            Log.e("SoundModeTile", "Error setting ringer mode", e)
        }
    }

    private fun updateTile() {
        val tile = qsTile ?: return
        val audioManager = getSystemService(Context.AUDIO_SERVICE) as AudioManager
        val currentMode = audioManager.ringerMode

        when (currentMode) {
            AudioManager.RINGER_MODE_NORMAL -> {
                tile.state = Tile.STATE_ACTIVE
                tile.label = "Ring"
                tile.icon = Icon.createWithResource(this, R.drawable.ic_ring)
            }
            AudioManager.RINGER_MODE_VIBRATE -> {
                tile.state = Tile.STATE_ACTIVE
                tile.label = "Vibrate"
                tile.icon = Icon.createWithResource(this, R.drawable.ic_vibrate)
            }
            AudioManager.RINGER_MODE_SILENT -> {
                tile.state = Tile.STATE_ACTIVE
                tile.label = "Silent"
                tile.icon = Icon.createWithResource(this, R.drawable.ic_mute)
            }
        }
        tile.updateTile()
    }

    private fun openDndSettings() {
        val intent = Intent(Settings.ACTION_NOTIFICATION_POLICY_ACCESS_SETTINGS)
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)

        val pendingIntent = PendingIntent.getActivity(
            this,
            0,
            intent,
            PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_UPDATE_CURRENT
        )

        if (Build.VERSION.SDK_INT >= 34) {
            startActivityAndCollapse(pendingIntent)
        } else {
            @Suppress("DEPRECATION")
            startActivityAndCollapse(intent)
        }
        
        Toast.makeText(this, "Please allow 'Do Not Disturb' access for Silent mode", Toast.LENGTH_LONG).show()
    }
}
