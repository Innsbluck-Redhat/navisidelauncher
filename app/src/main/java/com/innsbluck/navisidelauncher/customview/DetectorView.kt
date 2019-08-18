package com.innsbluck.navisidelauncher.customview

import android.content.Context
import android.util.AttributeSet
import android.view.GestureDetector
import android.view.MotionEvent
import android.view.View

class DetectorView : View {
    private var mGestureDetector: GestureDetector? = null

    constructor(context: Context) : super(context)
    constructor(context: Context, attrs: AttributeSet) : super(context, attrs)

    fun onInterceptTouchEvent(ev: MotionEvent): Boolean {
        onTouchEvent(ev)
        return false
    }
}