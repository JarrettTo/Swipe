package com.swipe.application
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View


class MainActivity : AppCompatActivity() {
    private lateinit var mData:ArrayList<Int>
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        mData = ArrayList()
        val swipeStack = findViewById<View>(R.id.swipeStack) as SwipeStack
        swipeStack.adapter = SwipeAdapter(mData)
        getImgData()
    }

    private fun getImgData(){
        mData.add(R.drawable.lol)
        mData.add(R.drawable.mlbb)
        mData.add(R.drawable.starcraft)
    }
}