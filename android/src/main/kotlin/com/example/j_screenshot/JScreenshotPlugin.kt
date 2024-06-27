package com.example.j_screenshot

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.os.Build
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.view.WindowManager.LayoutParams
import androidx.annotation.NonNull
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import io.flutter.embedding.android.FlutterActivity
import io.flutter.embedding.engine.plugins.FlutterPlugin
import io.flutter.embedding.engine.plugins.activity.ActivityAware
import io.flutter.embedding.engine.plugins.activity.ActivityPluginBinding
import io.flutter.plugin.common.MethodCall
import io.flutter.plugin.common.MethodChannel
import io.flutter.plugin.common.MethodChannel.MethodCallHandler
import io.flutter.plugin.common.MethodChannel.Result
import io.flutter.plugin.common.BinaryMessenger
import io.flutter.plugin.common.PluginRegistry

private const val LOG_NAME = "AndroidScreenShot"
private const val PERMISSIONS_SCREENSHOT = 200

/** JScreenshotPlugin */
class JScreenshotPlugin : FlutterPlugin, MethodCallHandler, ActivityAware, PluginRegistry.RequestPermissionsResultListener {
    /// The MethodChannel that will the communication between Flutter and native Android
    ///
    /// This local reference serves to register the plugin with the Flutter Engine and unregister it
    /// when the Flutter Engine is detached from the Activity
    private lateinit var channel: MethodChannel

    private lateinit var _activity: FlutterActivity

    private lateinit var handler: Handler

    private var _result: Result? = null

    private var detector: ScreenshotDetector? = null

    private var applicationContext: Context? = null

    private var lastScreenshotName: String? = null

    override fun onAttachedToEngine(@NonNull flutterPluginBinding: FlutterPlugin.FlutterPluginBinding) {
        onAttachedToEngine(
            flutterPluginBinding.applicationContext,
            flutterPluginBinding.binaryMessenger
        )
    }

    private fun onAttachedToEngine(applicationContext: Context, messenger: BinaryMessenger) {
        this.applicationContext = applicationContext
        channel = MethodChannel(messenger, "j_screenshot")
        channel.setMethodCallHandler(this)
    }

    override fun onMethodCall(@NonNull call: MethodCall, @NonNull result: Result) {
        when (call.method) {
            "permission" -> handleHasPermission(result)
            "initialize" -> handleInitialize(result)
            "dispose" -> handleDispose(result)
            else -> result.notImplemented()
        }
    }

    private fun handleHasPermission(result: Result) {
        if (hasRecordPermission()) {
            screenshotOn()
            result.success(true)
        } else {
            _result = result
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                ActivityCompat.requestPermissions(
                    _activity,
                    arrayOf(Manifest.permission.READ_MEDIA_IMAGES),
                    PERMISSIONS_SCREENSHOT
                )
            } else {
                ActivityCompat.requestPermissions(
                    _activity,
                    arrayOf(
                        Manifest.permission.WRITE_EXTERNAL_STORAGE,
                        Manifest.permission.READ_EXTERNAL_STORAGE,
                    ),
                    PERMISSIONS_SCREENSHOT
                )
            }
        }
    }

    private fun hasRecordPermission(): Boolean {
        // if after [Marshmallow], we need to check permission on runtime
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            val canReadImages = ContextCompat.checkSelfPermission(
                _activity,
                Manifest.permission.READ_MEDIA_IMAGES
            ) == PackageManager.PERMISSION_GRANTED

            canReadImages
        } else {
            val canWriteStorage = ContextCompat.checkSelfPermission(
                _activity,
                Manifest.permission.WRITE_EXTERNAL_STORAGE
            ) == PackageManager.PERMISSION_GRANTED

            val canReadStorage = ContextCompat.checkSelfPermission(
                _activity,
                Manifest.permission.READ_EXTERNAL_STORAGE
            ) == PackageManager.PERMISSION_GRANTED

            canWriteStorage && canReadStorage
        }
    }

    private fun handleInitialize(result: Result) {
        handler = Handler(Looper.getMainLooper())
        detector = applicationContext?.let {
            ScreenshotDetector(it) { screenshotName: String ->
                if (screenshotName != lastScreenshotName) {
                    lastScreenshotName = screenshotName
                    handler.post {
                        channel.invokeMethod(
                            "onCallback",
                            lastScreenshotName
                        )
                    }
                }
            }
        }
        detector?.start()
        result.success("initialize")
    }

    private fun handleDispose(result: Result) {
        detector?.stop()
        detector = null
        lastScreenshotName = null
        result.success("dispose")
    }


    override fun onDetachedFromEngine(@NonNull binding: FlutterPlugin.FlutterPluginBinding) {
        applicationContext = null
        channel.setMethodCallHandler(null)
    }

    // for ActivityAware

    override fun onAttachedToActivity(binding: ActivityPluginBinding) {
        _activity = binding.activity as FlutterActivity
        binding.addRequestPermissionsResultListener(this)
    }

    override fun onDetachedFromActivityForConfigChanges() {
        Log.d(LOG_NAME, "onDetachedFromActivityForConfigChanges")
    }

    override fun onReattachedToActivityForConfigChanges(binding: ActivityPluginBinding) {
        binding.addRequestPermissionsResultListener(this)
    }

    override fun onDetachedFromActivity() {
        Log.d(LOG_NAME, "onDetachedFromActivity")
    }

    // for PluginRegistry.RequestPermissionsResultListener

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ): Boolean {
        if (grantResults.isEmpty()) {
            screenshotOff()
            _result?.success(false)
            return false
        }
        val status = grantResults[0] == 0
        if (status) {
            screenshotOn()
        } else {
            screenshotOff()
        }
        _result?.success(status)
        _result = null
        return status
    }

    private  fun screenshotOn() {
        _activity.window.clearFlags(LayoutParams.FLAG_SECURE)
    }

    private fun screenshotOff() {
        _activity.window.addFlags(LayoutParams.FLAG_SECURE)
    }
}
