package com.trident.disablescreenshot

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.telephony.TelephonyManager
import android.util.Log
import kotlinx.android.synthetic.main.activity_main.*
/**
 * This class is used to read the phone state using TelephonyManager
 *  @author SURYA DEVI
 */
class CallBroadcastReceiver : BroadcastReceiver() {

    override fun onReceive(context: Context?, intent: Intent?) {
        val state = intent?.getStringExtra(TelephonyManager.EXTRA_STATE)
        val activity = MainActivity.instance
        when (intent?.action) {
            state -> {
                if (state.equals(TelephonyManager.EXTRA_STATE_RINGING)) {
                    activity.videoView.pause()
                    Log.e(R.string.Tag.toString(), R.string.phone_ringing.toString())
                }

                if (state.equals(TelephonyManager.EXTRA_STATE_OFFHOOK)) {
                    Log.e(R.string.Tag.toString(), R.string.call_received.toString())
                }

                if (state.equals(TelephonyManager.EXTRA_STATE_IDLE)) {
                    activity.videoView.start()
                    Log.e(R.string.Tag.toString(), R.string.phone_idle.toString())
                }
            }

        }

    }

}