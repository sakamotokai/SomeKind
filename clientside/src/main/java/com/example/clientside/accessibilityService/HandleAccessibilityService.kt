package com.example.clientside.accessibilityService


import android.accessibilityservice.AccessibilityService
import android.accessibilityservice.AccessibilityServiceInfo
import android.accessibilityservice.GestureDescription
import android.content.Intent
import android.graphics.Path
import android.view.accessibility.AccessibilityEvent

class HandleAccessibilityService : AccessibilityService() {

    companion object {
        var service: HandleAccessibilityService? = null
        var gesture: GestureDescription? = null
        var gestureCompleted = false
    }

    override fun onServiceConnected() {
        super.onServiceConnected()
        this.serviceInfo.flags = AccessibilityServiceInfo.FLAG_REQUEST_TOUCH_EXPLORATION_MODE
        service = this
    }

    override fun onInterrupt() {}

    override fun onAccessibilityEvent(event: AccessibilityEvent?) {
        if (gesture != null) {
            gestureCompleted = false

            val localGesture = GestureDescription.StrokeDescription(Path().apply {
                moveTo(100f, 700f)
                lineTo(700f, 700f)
            }, 100, 50)

            dispatchGesture(
                GestureDescription.Builder().addStroke(localGesture).build(),
                object : GestureResultCallback() {
                    override fun onCancelled(gestureDescription: GestureDescription?) {
                        super.onCancelled(gestureDescription)
                    }

                    override fun onCompleted(gestureDescription: GestureDescription?) {
                        gesture = null
                        super.onCompleted(gestureDescription)
                    }
                },
                null
            ).apply {
                gestureCompleted = this
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