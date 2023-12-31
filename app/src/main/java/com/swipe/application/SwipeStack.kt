package com.swipe.application

import android.content.Context
import android.database.DataSetObserver
import android.os.Build
import android.os.Parcelable
import android.util.AttributeSet
import android.view.View
import android.view.ViewGroup
import android.widget.Adapter
import android.widget.FrameLayout
import java.util.Random
import android.os.Bundle
import android.util.Log
import android.view.Gravity
import androidx.annotation.Nullable
import androidx.core.content.ContentProviderCompat.requireContext
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.lifecycleScope
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.launch

class SwipeStack @JvmOverloads constructor(
    context : Context?,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0,

) : ViewGroup(context, attrs, defStyleAttr)   {
    private var mAdapter: SwipeAdapter? = null
    private var mRandom: Random? = null
    var allowedSwipeDirections = 0
    private var mAnimationDuration = 0
    private var mCurrentViewIndex = 0
    private var mNumberOfStackedViews = 0
    private var mViewSpacing = 0
    private var mViewRotation = 0
    private var mSwipeRotation = 0f
    private var mSwipeOpacity = 0f
    private var mScaleFactor = 0f
    private var mDisableHwAcceleration = false
    private var mIsFirstLayout = true
    var topView : View? = null
        private set
    private lateinit var saveGame : (Games) -> Unit
    private lateinit var onSwipe : (Int, MutableSet<String>) -> Unit
    private var mSwipeHelper: SwipeHelper?= null
    private var mDataObserver : DataSetObserver?= null
    private lateinit var mListener: MySwipeStackListener
    private var mProgressListener: SwipeProgressListener ?= null
    private lateinit var userSession: UserSession
    init {
        readAttributes(attrs)
        initialize()
        userSession = UserSession(context!!)
    }
    fun setSaveGame(saveGame : (Games) -> Unit){
        this@SwipeStack.saveGame = saveGame
        mListener= MySwipeStackListener(saveGame)
    }
    fun setOnSwipe(onSwipe : (Int, MutableSet<String>) -> Unit){
        this@SwipeStack.onSwipe = onSwipe

    }



    private fun readAttributes(attributeSet: AttributeSet?){
        val attrs = context.obtainStyledAttributes(attributeSet, R.styleable.SwipeStack)
        try{
            allowedSwipeDirections = attrs.getInt(R.styleable.SwipeStack_allowed_swipe_directions, SWIPE_DIRECTION_BOTH)
            mAnimationDuration = attrs.getInt(R.styleable.SwipeStack_animation_duration, DEFAULT_ANIMATION_DURATION)
            mNumberOfStackedViews = attrs.getInt(R.styleable.SwipeStack_stack_size, DEFAULT_STACK_SIZE)
            mViewSpacing = attrs.getDimensionPixelSize(R.styleable.SwipeStack_stack_spacing, resources.getDimensionPixelSize(R.dimen.default_stack_spacing))
            mViewRotation = attrs.getInt(R.styleable.SwipeStack_stack_rotation, DEFAULT_STACK_ROTATION)
            mSwipeRotation = attrs.getFloat(R.styleable.SwipeStack_swipe_rotation, DEFAULT_SWIPE_ROTATION)
            mSwipeOpacity = attrs.getFloat(R.styleable.SwipeStack_swipe_opacity, DEFAULT_SWIPE_OPACITY)
            mScaleFactor = attrs.getFloat(R.styleable.SwipeStack_scale_factor, DEFAULT_SCALE_FACTOR)
            mDisableHwAcceleration = attrs.getBoolean(R.styleable.SwipeStack_disable_hw_accleration, DEFAULT_DISABLE_HW_ACCLERATION)

        } finally {
            attrs.recycle()
        }
    }

    private fun initialize(){
        mRandom = Random()
        clipToPadding = false
        clipChildren = false
        mSwipeHelper = SwipeHelper(this)
        mSwipeHelper!!.setAnimationDuration(mAnimationDuration)
        mSwipeHelper!!.setRotation(mSwipeRotation)
        mSwipeHelper!!.setOpacityEnd(mSwipeOpacity)
        mDataObserver = object : DataSetObserver() {
            override fun onChanged() {
                super.onChanged()
                invalidate()
                requestLayout()
            }
        }
    }

    public override fun onSaveInstanceState(): android.os.Parcelable? {
        val bundle = Bundle()
        bundle.putParcelable(KEY_SUPER_STATE, super.onSaveInstanceState())
        bundle.putInt(KEY_CURRENT_INDEX, mCurrentViewIndex - childCount)
        return bundle
    }

    public override fun onRestoreInstanceState(state: Parcelable) {
        var state: Parcelable? = state
        if (state is Bundle){
            val bundle = state
            mCurrentViewIndex = bundle.getInt(KEY_CURRENT_INDEX)
            state = bundle.getParcelable(KEY_SUPER_STATE)
        }
        super.onRestoreInstanceState(state)
    }

    override fun onLayout(changed: Boolean, l: Int, t: Int, r: Int, b:Int){
        if(mAdapter == null || mAdapter!!.isEmpty){
            mCurrentViewIndex = 0
            removeAllViewsInLayout()
            return
        }
        var x = childCount
        while (x < mNumberOfStackedViews && mCurrentViewIndex < mAdapter!!.count){
            addNextView()
            x++
        }
        reorderItems()
        mIsFirstLayout= false

    }
    private fun addNextView() {
        if (mCurrentViewIndex < mAdapter!!.count) {
            val bottomView = mAdapter!!.getView(mCurrentViewIndex, this, this)
            if (bottomView != null) {
                bottomView.setTag(R.id.new_view, true)
            }

            if (!mDisableHwAcceleration) {
                if (bottomView != null) {
                    bottomView.setLayerType(LAYER_TYPE_HARDWARE, null)
                }
            }

            if (mViewRotation > 0) {
                if (bottomView != null) {
                    bottomView.rotation = (mRandom!!.nextInt(mViewRotation) - mViewRotation / 2).toFloat()
                }
            }

            val widthMeasureSpec = MeasureSpec.makeMeasureSpec(width - (paddingLeft + paddingRight), MeasureSpec.AT_MOST)
            val heightMeasureSpec = MeasureSpec.makeMeasureSpec(height - (paddingTop + paddingBottom), MeasureSpec.AT_MOST)

            if (bottomView != null) {
                bottomView.measure(widthMeasureSpec, heightMeasureSpec)
            }

            val params = FrameLayout.LayoutParams(
                LayoutParams.WRAP_CONTENT,
                LayoutParams.WRAP_CONTENT,

            )
            params.gravity=Gravity.CENTER_VERTICAL

            addViewInLayout(bottomView, 0, params, true)
            mCurrentViewIndex++
        }
    }
    private fun reorderItems(){
        for(x in 0 until childCount){
            val childView = getChildAt(x)
            val topViewIndex= childCount -1
            val distanceToViewAbove = topViewIndex * mViewSpacing - x * mViewSpacing
            val newPositionX = (width - childView.measuredWidth) / 2
            val newPositionY = distanceToViewAbove + 150
            childView.layout(
                newPositionX,
                paddingTop,
                newPositionX + childView.measuredWidth,
                paddingTop + childView.measuredHeight
            )
            if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP){
                childView.translationZ = x.toFloat()
            }
            var isNewView= false
            if(childView.getTag(R.id.new_view) != null){
                isNewView = childView.getTag(R.id.new_view) as Boolean
            }


            val scaleFactor = mScaleFactor
            if(x==topViewIndex){
                mSwipeHelper!!.unregisterObservedView()
                topView=childView
                mSwipeHelper!!.registerObservedView(
                    topView,
                    newPositionX.toFloat(),
                    newPositionY.toFloat()
                )
            }
            if(!mIsFirstLayout){
                if(isNewView) {
                    childView.setTag(R.id.new_view, false)
                    childView.alpha = 0f
                    childView.y=newPositionY.toFloat()
                    childView.scaleY=scaleFactor
                    childView.scaleX=scaleFactor
                }
                childView.animate()
                    .y(newPositionY.toFloat())
                    .scaleX(scaleFactor)
                    .scaleY(scaleFactor)
                    .alpha(1f).duration = mAnimationDuration.toLong()
            } else{
                childView.setTag(R.id.new_view, false)
                childView.y = newPositionY.toFloat()
                childView.x = newPositionX.toFloat()
                childView.scaleY = scaleFactor
                childView.scaleX = scaleFactor
            }
        }
    }
    private fun removeTopView(){
        if(topView!=null){
            removeView(topView)
            topView = null
        }
        if(childCount == 0){
            if(mListener!=null) mListener!!.onStackEmpty()
        }
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        val width = MeasureSpec.getSize(widthMeasureSpec)
        val height = MeasureSpec.getSize(heightMeasureSpec)
        setMeasuredDimension(width, height)
    }

    fun onSwipeStart(){
        if(mProgressListener !=null) mProgressListener!!.onSwipeStart(currentPosition)
    }
    fun onSwipeProgress(progress: Float){
        if(mProgressListener !=null) mProgressListener!!.onSwipeProgress(currentPosition, progress)
    }

    fun onSwipeEnd(){
        if(mProgressListener!=null) mProgressListener!!.onSwipeEnd(currentPosition)
    }

    fun onViewSwipedToLeft(){
        if(mListener != null){
            mListener!!.onViewSwipedToLeft(0)

            onSwipe(6, userSession.likedGameIds!!)
        }
        removeTopView()
        if(mCurrentViewIndex>0){
            mCurrentViewIndex--
        }
        mAdapter!!.removeItem(currentPosition)

    }
    fun onViewSwipedToRight(){

        if(mListener != null){

            mListener!!.onViewSwipedToRight(mAdapter?.getItem(currentPosition) as Games)

            onSwipe(6, userSession.likedGameIds!!)

        }
        removeTopView()
        if(mCurrentViewIndex>0){
            mCurrentViewIndex--
        }

        mAdapter!!.removeItem(currentPosition)

    }


    val currentPosition: Int
        get() = mCurrentViewIndex - childCount

    var adapter: Adapter?
        get() = mAdapter
        set(adapter){
            if(mAdapter!=null) mAdapter!!.unregisterDataSetObserver((mDataObserver))
            mAdapter = adapter as SwipeAdapter?
            mAdapter!!.registerDataSetObserver(mDataObserver )
            mAdapter!!.onDataChanged = { updateViewsFromAdapter() }
        }
    fun updateViewsFromAdapter() {

    }
    fun setListener(@Nullable listener: SwipeStackListener?){
        mListener = (listener as MySwipeStackListener?)!!
    }
    fun setSwipeProgressListener(@Nullable listener: SwipeProgressListener?){
        mProgressListener = listener
    }

    fun swipeTopViewToRight(){
        if(childCount == 0) return
        mSwipeHelper!!.swipeViewToRight()
    }

    fun swipeTopViewToLeft (){
        if(childCount == 0) return
        mSwipeHelper!!.swipeViewToRight()
    }

    fun resetStack(){
        mCurrentViewIndex = 0
        removeAllViewsInLayout()
        requestLayout()
    }

    fun addGames(retrieveGames: List<Games>) {
        mAdapter?.addGames(retrieveGames)
    }

    interface SwipeStackListener {
        fun onViewSwipedToLeft(position: Int){

        }
        fun onViewSwipedToRight(games: Games)

        fun onStackEmpty()
    }

    interface SwipeProgressListener{
        fun onSwipeStart(position: Int)
        fun onSwipeProgress(position: Int, progress: Float){}
        fun onSwipeEnd(position: Int)
    }

    companion object {
        const val SWIPE_DIRECTION_BOTH = 0
        const val SWIPE_DIRECTION_ONLY_LEFT= 1
        const val SWIPE_DIRECTION_ONLY_RIGHT = 2
        const val DEFAULT_ANIMATION_DURATION = 300
        const val DEFAULT_STACK_SIZE = 3
        const val DEFAULT_STACK_ROTATION = 8
        const val DEFAULT_SWIPE_ROTATION = 30f
        const val DEFAULT_SWIPE_OPACITY = 1f
        const val DEFAULT_SCALE_FACTOR = 1.25f
        const val DEFAULT_DISABLE_HW_ACCLERATION = true
        private const val KEY_SUPER_STATE="superState"
        private const val KEY_CURRENT_INDEX="currentIndex"
    }
    class MySwipeStackListener(val saveGame: (Games) -> Unit) : SwipeStackListener {

        override fun onViewSwipedToLeft(position: Int) {
            // Implementation of what should happen when a view is swiped left

        }

        override fun onViewSwipedToRight(games: Games) {
            // Implementation of what should happen when a view is swiped right


            saveGame(games)
        }

        override fun onStackEmpty() {
            // Implementation of what should happen when the stack is empty
            println("The stack is empty")
        }
    }
}


