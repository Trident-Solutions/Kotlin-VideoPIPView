package com.trident.disablescreenshot

import android.Manifest
import android.Manifest.permission.READ_PHONE_STATE
import android.content.pm.ActivityInfo
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.view.WindowManager
import android.widget.MediaController
import kotlinx.android.synthetic.main.activity_main.*
import android.content.Intent
import android.content.res.Configuration
import android.util.Rational
import android.app.PictureInPictureParams
import android.content.pm.PackageManager
import android.provider.Settings
import android.util.Log
import androidx.appcompat.app.AlertDialog
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat

/**
 * This class consists of picture-in-picture (PIP) mode for video playback, restrict the user to take
 * screenshot as well as the recorded screen becomes black.
 * @author SURYA DEVI
 */

class MainActivity : AppCompatActivity() {

    companion object {
        lateinit var instance: MainActivity
    }

    private val PHONE_STATE_REQUEST_CODE = 101
    private val TAG = "TAG"
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        instance = this
        setupPermissions()

    }

    /*
    This method is used to restrict the screen recorder as well as screenshot while watching the video
     */
    private fun disableScreenShotRecorder() {
        window.setFlags(
            WindowManager.LayoutParams.FLAG_SECURE,
            WindowManager.LayoutParams.FLAG_SECURE
        )
    }

    /*
    This method is used to view the video in landscape mode
     */
    private fun getFullscreenVideoView() {
        checkbox.setOnClickListener(View.OnClickListener {
            if (checkbox.isChecked) {
                checkbox.setButtonDrawable(R.drawable.ic_fullscreen_exit)
                window.setFlags(
                    WindowManager.LayoutParams.FLAG_FULLSCREEN,
                    WindowManager.LayoutParams.FLAG_FULLSCREEN
                );
                requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE;
            } else {
                checkbox.setButtonDrawable(R.drawable.ic_fullscreen)
                requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_PORTRAIT;
            }
        })
    }

    private fun setVideoView() {
        val mediaController = MediaController(this);
        mediaController.setAnchorView(videoView)
        val uri = Uri.parse("android.resource://" + packageName + "/" + R.raw.sample_video)
        videoView.setMediaController(mediaController)
        videoView.setVideoURI(uri)
        videoView.requestFocus()
        videoView.start()

    }

    /*
    This method is used to allows activities to launch in picture-in-picture (PIP) mode.
     */
    public override fun onUserLeaveHint() {
        if (!isInPictureInPictureMode) {
            val aspectRatio = Rational(192, 108)
            val mParams = PictureInPictureParams.Builder()
                .setAspectRatio(aspectRatio)
                .build()
            enterPictureInPictureMode(mParams)
        }
    }

    /*
    When the activity enters or exits picture-in-picture mode the system calls this method
     */
    override fun onPictureInPictureModeChanged(
        isInPictureInPictureMode: Boolean,
        newConfig: Configuration
    ) {
        if (isInPictureInPictureMode) {
            videoView.start()
        } else {
            videoView.start()
        }
    }

    public override fun onNewIntent(i: Intent) {
        super.onNewIntent(intent)
        setVideoView()
    }

    /*
    purpose of read phone state permission is used to pause the video while user receiving an incoming call
     */
    private fun setupPermissions() {
        val permission = ContextCompat.checkSelfPermission(
            this,
            Manifest.permission.READ_PHONE_STATE
        )

        setDeniedPermission(permission)
    }

    private fun setDeniedPermission(permission: Int) {
        if (permission != PackageManager.PERMISSION_GRANTED) {
            if (ActivityCompat.shouldShowRequestPermissionRationale(
                    this,
                    READ_PHONE_STATE
                )
            ) {
                val builder = AlertDialog.Builder(this)
                builder.setMessage(R.string.message)
                    .setTitle(R.string.permission_required)
                builder.setPositiveButton(R.string.ok) { dialog, id ->
                    requestPermission()
                }

                val dialog = builder.create()
                dialog.show()
            } else {
                requestPermission()
            }
        }
    }


    private fun requestPermission() {
        ActivityCompat.requestPermissions(
            this,
            arrayOf(Manifest.permission.READ_PHONE_STATE),
            PHONE_STATE_REQUEST_CODE
        )
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<String>, grantResults: IntArray
    ) {
        var neverAsk = false

        when (requestCode) {
            PHONE_STATE_REQUEST_CODE -> {


                for (allowedPermissions in permissions) {
                    if (ActivityCompat.shouldShowRequestPermissionRationale(
                            this,
                            allowedPermissions
                        )
                    ) {
                        requestPermission()
                        Log.e(TAG, getString(R.string.user_denied_msg))
                    } else if (grantResults[0] != PackageManager.PERMISSION_GRANTED) {
                        Log.e(TAG, getString(R.string.user_force_denied_msg))
                        neverAsk = true
                        break
                    } else {
                        Log.e(TAG, getString(R.string.permission_allowed))
                        disableScreenShotRecorder()
                        checkbox.setButtonDrawable(R.drawable.ic_fullscreen)
                        setVideoView()
                        getFullscreenVideoView()
                    }
                }


                if (!neverAsk) {
                    Log.e(TAG, getString(R.string.force_denied_msg_false))
                } else {
                    Log.e(TAG, getString(R.string.force_denied_msg_true));
                    val builder = AlertDialog.Builder(this)
                    builder.setMessage(R.string.message)
                        .setTitle(R.string.permission_required)
                    builder.setPositiveButton(R.string.ok) { dialog, id ->
                        val intent = Intent(
                            Settings.ACTION_APPLICATION_DETAILS_SETTINGS,
                            Uri.fromParts(getString(R.string.scheme), packageName, null)
                        )
                        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                        startActivityForResult(intent, PHONE_STATE_REQUEST_CODE)
                    }

                    val dialog = builder.create()
                    dialog.show()
                }
            }
        }
    }
}
