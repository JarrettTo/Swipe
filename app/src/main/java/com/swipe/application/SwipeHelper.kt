package com.swipe.application

import android.animation.Animator
import android.view.MotionEvent
import android.view.View
import android.view.View.OnTouchListener
import android.view.animation.OvershootInterpolator
import com.swipe.application.util.AnimationUtils

class SwipeHelper(private val mSwipeStack: SwipeStack) : OnTouchListener {
    private var mObservedView: View? = null
    private var mListenForTouchEvents = false
    private var mDownX = 0f
    private var mDownY = 0f
    private var mInitialX = 0f
    private var mInitialY = 0f
    private var mPointerId = 0
    private var mRotateDegrees = SwipeStack.DEFAULT_SWIPE_ROTATION
    private var mOpacityEnd = SwipeStack.DEFAULT_SWIPE_OPACITY
    private var mAnimationDuration = SwipeStack.DEFAULT_ANIMATION_DURATION
    override fun onTouch(v: View?, event: MotionEvent?): Boolean {
        if (event != null) {
            when(event.action) {
                MotionEvent.ACTION_DOWN -> {
                    if(!mListenForTouchEvents || !mSwipeStack.isEnabled){
                        return false
                    }
                    v?.parent?.requestDisallowInterceptTouchEvent(true)
                    mSwipeStack.onSwipeStart()
                    if (event != null) {
                        mPointerId = event.getPointerId(0)
                    }
                    if (event != null) {
                        mDownX = event.getX(mPointerId)
                    }
                    if (event != null) {
                        mDownY = event.getY(mPointerId)
                    }
                    return true

                }
                MotionEvent.ACTION_MOVE -> {
                    val pointerIndex= event?.findPointerIndex(mPointerId)
                    if (pointerIndex != null) {
                        if(pointerIndex < 0) return false
                    }
                    val dx= pointerIndex?.let { event.getX(it) }?.minus(mDownX)
                    val dy= pointerIndex?.let { event.getY(it) }?.minus(mDownY)
                    val newX = mObservedView!!.x + dx!!
                    val newY = mObservedView!!.y + dy!!
                    mObservedView!!.x = newX
                    mObservedView!!.y = newY
                    val dragDistanceX = newX - mInitialX
                    val swipeProgress = Math.min(Math.max(dragDistanceX/mSwipeStack.width, -1f), 1f)
                    mSwipeStack.onSwipeProgress(swipeProgress)
                    if(mRotateDegrees>0){
                        val rotation = mRotateDegrees * swipeProgress
                        mObservedView!!.rotation = rotation
                    }
                    if(mOpacityEnd < 1f){
                        val alpha = 1
                        mObservedView!!.alpha = alpha.toFloat()
                    }
                    return true
                }
                MotionEvent.ACTION_UP -> {
                    if (v != null) {
                        v.parent.requestDisallowInterceptTouchEvent(false)
                    }
                    mSwipeStack.onSwipeEnd()
                    checkViewPosition()
                    return true
                }
            }
        }
        return false
    }
    private fun checkViewPosition(){
        if(!mSwipeStack.isEnabled){
            resetViewPosition()
            return
        }
        val viewCenterHorizontal = mObservedView!!.x + mObservedView!!.width/2
        val parentFirstThird = mSwipeStack.width/3f
        val parentLastThird = parentFirstThird * 2
        if(viewCenterHorizontal < parentFirstThird && mSwipeStack.allowedSwipeDirections != SwipeStack.SWIPE_DIRECTION_ONLY_RIGHT){
            swipeViewToLeft(mAnimationDuration/2)
        } else if (viewCenterHorizontal > parentLastThird && mSwipeStack.allowedSwipeDirections != SwipeStack.SWIPE_DIRECTION_ONLY_LEFT){
            swipeViewToRight(mAnimationDuration/2)
        } else{
            resetViewPosition()
        }
    }
    private fun resetViewPosition(){
        mObservedView!!.animate()
            .x(mInitialX)
            .y(mInitialY)
            .rotation(0f)
            .alpha(1f)
            .setDuration(mAnimationDuration.toLong())
            .setInterpolator(OvershootInterpolator(1.4f))
            .setListener(null)
    }
    private fun swipeViewToLeft(duration : Int){
        if(!mListenForTouchEvents) return
        mListenForTouchEvents = false
        mObservedView!!.animate().cancel()
        mObservedView!!.animate()
            .x(-mSwipeStack.width + mObservedView!!.x)
            .rotation(-mRotateDegrees)
            .alpha(0f)
            .setDuration(duration.toLong())
            .setListener(object : AnimationUtils.AnimationEndListener(){
                override fun onAnimationEnd(p0: Animator) {
                    mSwipeStack.onViewSwipedToLeft()
                }
            })
    }

    private fun swipeViewToRight(duration : Int){
        if(!mListenForTouchEvents) return
        mListenForTouchEvents = false
        mObservedView!!.animate().cancel()
        mObservedView!!.animate()
            .x(mSwipeStack.width + mObservedView!!.x)
            .rotation(mRotateDegrees)
            .alpha(0f)
            .setDuration(duration.toLong())
            .setListener(object : AnimationUtils.AnimationEndListener(){
                override fun onAnimationEnd(p0: Animator) {
                    mSwipeStack.onViewSwipedToRight()
                }
            })
    }

    fun registerObservedView(view : View?, initialX : Float, initialY: Float){
        if(view == null) return
        mObservedView = view
        mObservedView!!.setOnTouchListener(this)
        mInitialX = initialX
        mInitialY = initialY
        mListenForTouchEvents = true
    }
    fun unregisterObservedView(){
        if(mObservedView!=null){
            mObservedView!!.setOnTouchListener(null)
        }
        mObservedView = null
        mListenForTouchEvents = false
    }
    fun setAnimationDuration(duration : Int){
        mAnimationDuration = duration
    }
    fun setRotation(rotation : Float){
        mRotateDegrees = rotation
    }
    fun setOpacityEnd(alpha : Float){
        mOpacityEnd = alpha
    }
    fun swipeViewToLeft(){
        swipeViewToLeft(mAnimationDuration)
    }
    fun swipeViewToRight(){
        swipeViewToRight(mAnimationDuration)
    }
}