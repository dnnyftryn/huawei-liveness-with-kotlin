package com.aplikasi.huaweiliveness

import android.Manifest
import android.app.Activity
import android.app.AlertDialog
import android.app.Dialog
import android.content.DialogInterface
import android.graphics.Color
import android.os.Build
import android.os.Bundle
import android.util.Log
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.DialogFragment
import com.aplikasi.huaweiliveness.databinding.ActivityMainBinding
import com.huawei.hms.mlsdk.livenessdetection.MLLivenessCapture
import com.huawei.hms.mlsdk.livenessdetection.MLLivenessCaptureResult

class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding

    companion object {
        const val REQUEST_CAMERA_PERMISSION = 1

        const val FRAGMENT_DIALOG = "dialog"
    }

    private val callback: MLLivenessCapture.Callback = object : MLLivenessCapture.Callback {
        override fun onSuccess(result: MLLivenessCaptureResult) {
            binding.textView.text = if (result.isLive) "Live" else "Not Live"
            binding.textView.setBackgroundColor(if (result.isLive) Color.GREEN else Color.RED)
        }

        override fun onFailure(errorCode: Int) {
            Log.d("MainActivity", "onFailure: $errorCode")
        }

    }

    @RequiresApi(Build.VERSION_CODES.M)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.check.setOnClickListener {
            val capture = MLLivenessCapture.getInstance()
            capture.startDetect(this, callback)
        }
        requestCameraPermission()
    }

    @RequiresApi(Build.VERSION_CODES.M)
    private fun requestCameraPermission() {
        if (shouldShowRequestPermissionRationale(Manifest.permission.CAMERA)) {
            ConfirmationDialog().show(supportFragmentManager, FRAGMENT_DIALOG)
        } else {
            requestPermissions(arrayOf(Manifest.permission.CAMERA), REQUEST_CAMERA_PERMISSION)
        }
    }

    /**
     * Shows OK/Cancel confirmation dialog about camera permission.
     */
    class ConfirmationDialog : DialogFragment() {
        override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
            val parent = parentFragment
            return AlertDialog.Builder(activity)
                .setMessage("Camera permission is needed to show camera preview")
                .setPositiveButton(
                    android.R.string.ok
                ) { _, _ ->
                    parent?.requestPermissions(
                        arrayOf(Manifest.permission.CAMERA),
                        REQUEST_CAMERA_PERMISSION
                    )
                }
                .setNegativeButton(android.R.string.cancel,
                    DialogInterface.OnClickListener { _, _ ->
                        val activity: Activity? = parent!!.activity
                        activity?.finish()
                    })
                .create()
        }
    }
}