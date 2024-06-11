package com.example.clientside.accessibilityService

import android.accessibilityservice.AccessibilityService
import android.accessibilityservice.FingerprintGestureController
import android.accessibilityservice.FingerprintGestureController.FINGERPRINT_GESTURE_SWIPE_DOWN
import android.accessibilityservice.FingerprintGestureController.FINGERPRINT_GESTURE_SWIPE_LEFT
import android.accessibilityservice.FingerprintGestureController.FINGERPRINT_GESTURE_SWIPE_RIGHT
import android.accessibilityservice.FingerprintGestureController.FINGERPRINT_GESTURE_SWIPE_UP
import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.util.Log
import android.view.KeyEvent
import android.view.accessibility.AccessibilityEvent
import android.view.accessibility.AccessibilityNodeInfo
import android.widget.Toast
import org.koin.android.ext.android.inject


class MyAccessibilityService : AccessibilityService() {

    override fun onServiceConnected() {
        super.onServiceConnected()
        this.serviceInfo.eventTypes = AccessibilityEvent.TYPE_VIEW_CLICKED or AccessibilityEvent.TYPE_VIEW_FOCUSED
    }

    override fun onInterrupt() {}

    override fun onAccessibilityEvent(event: AccessibilityEvent?) {
        Log.e("localError","Event is occurred")
    }

}

/*override fun onKeyEvent(event: KeyEvent?): Boolean {
    if(event?.keyCode == KeyEvent.KEYCODE_VOLUME_UP){
        Log.e("localError", "VOLUME UP")
        showToast("VOLUME UP")
    }
    return super.onKeyEvent(event)
}*/
