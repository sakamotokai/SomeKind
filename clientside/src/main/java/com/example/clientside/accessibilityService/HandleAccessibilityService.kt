package com.example.clientside.accessibilityService

import android.accessibilityservice.AccessibilityGestureEvent
import android.accessibilityservice.AccessibilityService
import android.accessibilityservice.AccessibilityServiceInfo
import android.accessibilityservice.FingerprintGestureController
import android.accessibilityservice.FingerprintGestureController.FINGERPRINT_GESTURE_SWIPE_DOWN
import android.accessibilityservice.FingerprintGestureController.FINGERPRINT_GESTURE_SWIPE_LEFT
import android.accessibilityservice.FingerprintGestureController.FINGERPRINT_GESTURE_SWIPE_RIGHT
import android.accessibilityservice.FingerprintGestureController.FINGERPRINT_GESTURE_SWIPE_UP
import android.accessibilityservice.GestureDescription
import android.accessibilityservice.InputMethod
import android.annotation.SuppressLint
import android.content.Intent
import android.graphics.Path
import android.util.Log
import android.view.KeyEvent
import android.view.MotionEvent
import android.view.accessibility.AccessibilityEvent
import android.view.accessibility.AccessibilityNodeInfo
import com.example.clientside.ktorClient.WebSocketClient
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.async
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import org.koin.android.ext.android.inject
import org.koin.java.KoinJavaComponent.inject

class HandleAccessibilityService : AccessibilityService() {

    companion object {
        var handler: MutableList<HandleAccessibilityService?> = mutableListOf(null)
        val scope = CoroutineScope(SupervisorJob())
        var service:HandleAccessibilityService? = null
        var gesture: GestureDescription? = null
        var gestureCompleted = false
    }


    private val client by inject<WebSocketClient>()
    private val scope = CoroutineScope(SupervisorJob())

    override fun onCreate() {
        super.onCreate()
        Log.e("localError", "service is start")
    }

    override fun onServiceConnected() {
        super.onServiceConnected()
        this.serviceInfo.flags = AccessibilityServiceInfo.FLAG_REQUEST_TOUCH_EXPLORATION_MODE
        service = this
    }

    override fun onInterrupt() {}

    override fun onAccessibilityEvent(event: AccessibilityEvent?) {
        Log.e("lofigirl", "Inside")
        if (gesture != null) {
            Log.e("lofigirl", "Inside != null")
            gestureCompleted = false

            val localGesture = GestureDescription.StrokeDescription(Path().apply {
                moveTo(100f, 700f)
                lineTo(700f, 700f)
            },100, 50)

            dispatchGesture(GestureDescription.Builder().addStroke(localGesture).build(), object : GestureResultCallback() {
                override fun onCancelled(gestureDescription: GestureDescription?) {
                    Log.e("lofigirl", "Cancelled")
                    super.onCancelled(gestureDescription)
                }
                override fun onCompleted(gestureDescription: GestureDescription?) {
                    Log.e("localError", "Gesture Completed")
                    Log.e("lofigirl", "Completed")
                    gesture = null
                    gestureCompleted = true
                    super.onCompleted(gestureDescription)
                }
            }, null).apply {
                Log.e("lofigirl", this.toString())
            }
        }
    }

    override fun onUnbind(intent: Intent?): Boolean {
        service = null
        return super.onUnbind(intent)
    }

    override fun onDestroy() {
        this.disableSelf()
        super.onDestroy()
    }
}