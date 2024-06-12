package com.example.clientside.accessibilityService

import android.accessibilityservice.AccessibilityGestureEvent
import android.accessibilityservice.AccessibilityService
import android.accessibilityservice.AccessibilityServiceInfo
import android.accessibilityservice.FingerprintGestureController
import android.accessibilityservice.FingerprintGestureController.FINGERPRINT_GESTURE_SWIPE_DOWN
import android.accessibilityservice.FingerprintGestureController.FINGERPRINT_GESTURE_SWIPE_LEFT
import android.accessibilityservice.FingerprintGestureController.FINGERPRINT_GESTURE_SWIPE_RIGHT
import android.accessibilityservice.FingerprintGestureController.FINGERPRINT_GESTURE_SWIPE_UP
import android.annotation.SuppressLint
import android.util.Log
import android.view.KeyEvent
import android.view.accessibility.AccessibilityEvent
import com.example.clientside.ktorClient.WebSocketClient
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.launch
import org.koin.android.ext.android.inject
import org.koin.java.KoinJavaComponent.inject

class HandleAccessibilityService : AccessibilityService() {

    companion object{
        var handler:MutableList<HandleAccessibilityService?> = mutableListOf(null)
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
        handler += this
        scope.launch {
            client.gestureAccess.collect{

            }
        }
    }

    override fun onInterrupt() {}

    override fun onAccessibilityEvent(event: AccessibilityEvent?) {
        //Log.e("localError", "Event is occurred")
        //Log.e("localError", "Event is ${event?.className}")
    }

    override fun onGesture(gestureEvent: AccessibilityGestureEvent): Boolean {
        Log.e("localError", "inside on gesture")
        return super.onGesture(gestureEvent)
    }

    override fun onGesture(gestureId: Int): Boolean {
        Log.e("localError", "Gesture Code IS $gestureId")
        if(gestureId == FINGERPRINT_GESTURE_SWIPE_DOWN){
            Log.e("localError", "DOWN")
        } else {
            Log.e("localError", "Another Gesture")
        }
        return super.onGesture(gestureId)
    }

    override fun onKeyEvent(event: KeyEvent?): Boolean {
        if(event?.action == KeyEvent.ACTION_UP){
            Log.e("localError","UP BUTTON")
        } else if (event?.action == KeyEvent.ACTION_DOWN) {Log.e("localError","DOWN BUTTON")}
        return super.onKeyEvent(event)
    }

    override fun onDestroy() {
        this.disableSelf()
        super.onDestroy()
    }
}