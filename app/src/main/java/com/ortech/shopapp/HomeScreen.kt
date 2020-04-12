package com.ortech.shopapp

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.ortech.shopapp.Adapters.HomeScreenAdapter
import com.ortech.shopapp.Views.TopSpacingDecoration
import kotlinx.android.synthetic.main.fragment_home.*

class HomeScreen : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        Log.d("HomeScreen", "Setup Adapter")
        val db = Firebase.firestore

        val view = inflater.inflate(R.layout.activity_home_screen, container, false)

        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val recyclerView: RecyclerView? = view?.findViewById(R.id.homeRecyclerView)
        recyclerView?.apply {
            layoutManager = LinearLayoutManager(this@HomeScreen.context)
            val topSpacingDecoration = TopSpacingDecoration(10)
            addItemDecoration(topSpacingDecoration)
            recyclerView.adapter = HomeScreenAdapter()
        }
        Log.d("RecyclerPosition", recyclerView?.scrollY.toString())
        recyclerView?.addOnScrollListener(object: RecyclerView.OnScrollListener(){
            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                //                Log.d("RecyclerView", "Scrolled $dx $dy")
                val layoutManager = recyclerView.layoutManager
                val visibleItemCount = recyclerView.childCount
                val totalItemCount = recyclerView.itemDecorationCount

                if (recyclerView.getChildAt(0).top == 0) {
                    Log.d("RecyclerView", "OnTop C")
                    //                    actionBar?.show()
                    //                    supportActionBar?.show()
                }

                if (dy <= 0) {
                    Log.d("RecyclerView", "OnTop S")
                    //                    actionBar?.show()
                    //                    supportActionBar?.show()
                }
                if (dy > 0){
                    //                    actionBar?.hide()
                    //                    supportActionBar?.hide()
                }
                super.onScrolled(recyclerView, dx, dy)
            }
        })
    }

//    override fun onCreate(savedInstanceState: Bundle?) {
//        super.onCreate(savedInstanceState)
//
//    }

}