package com.example.youtube_clone

import android.content.Context
import android.graphics.Rect
import android.util.AttributeSet
import android.view.GestureDetector
import android.view.MotionEvent
import androidx.constraintlayout.motion.widget.MotionLayout
import androidx.constraintlayout.widget.ConstraintLayout

@Suppress("UNREACHABLE_CODE")
class CustomMotionLayout(context: Context, attributeSet: AttributeSet? = null): MotionLayout(context, attributeSet) {

    private var motionTouchStarted = false
    private val mainContainerLayout: ConstraintLayout by lazy {
        findViewById<ConstraintLayout>(R.id.mainContainerLayout)
    }
    private val hitRect = Rect()

    init {
        setTransitionListener(object: TransitionListener {
            override fun onTransitionStarted(
                motionLayout: MotionLayout?,
                startId: Int,
                endId: Int
            ) {

            }

            override fun onTransitionChange(
                motionLayout: MotionLayout?,
                startId: Int,
                endId: Int,
                progress: Float
            ) {

            }

            override fun onTransitionCompleted(motionLayout: MotionLayout?, currentId: Int) {
                motionTouchStarted = false
            }

            override fun onTransitionTrigger(
                motionLayout: MotionLayout?,
                triggerId: Int,
                positive: Boolean,
                progress: Float
            ) {

            }

        })
    }

    override fun onTouchEvent(event: MotionEvent): Boolean {

        when (event.actionMasked) {
            MotionEvent.ACTION_UP, MotionEvent.ACTION_CANCEL -> {
                motionTouchStarted = false
                // custom이 아닌 기존의 터치이벤를 return
                return super.onTouchEvent(event)
            }
        }

        if (!motionTouchStarted) {
            mainContainerLayout.getHitRect(hitRect)
            motionTouchStarted = hitRect.contains(event.x.toInt(), event.y.toInt())
        }

        return super.onTouchEvent(event) && motionTouchStarted
    }

    private val gestureListener by lazy {
        object : GestureDetector.SimpleOnGestureListener() {
            override fun onScroll(
                e1: MotionEvent,
                e2: MotionEvent,
                distanceX: Float,
                distanceY: Float
            ): Boolean {
                mainContainerLayout.getHitRect(hitRect)
                return hitRect.contains(e1.x.toInt(), e2.y.toInt())
            }
        }
    }

    private val gestureDetector by lazy {
        GestureDetector(context, gestureListener)
    }


    override fun onInterceptTouchEvent(event: MotionEvent?): Boolean {
        return gestureDetector.onTouchEvent(event)
    }
}