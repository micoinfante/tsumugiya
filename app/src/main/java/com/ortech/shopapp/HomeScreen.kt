package com.ortech.shopapp

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.ortech.shopapp.Adapters.HomeScreenAdapter
import com.ortech.shopapp.Views.TopSpacingDecoration
import kotlinx.android.synthetic.main.activity_home_screen.*

class HomeScreen : Fragment() {

  override fun onCreateView(
    inflater: LayoutInflater,
    container: ViewGroup?,
    savedInstanceState: Bundle?
  ): View? {
    Log.d("HomeScreen", "Setup Adapter")
    val db = Firebase.firestore

    return inflater.inflate(R.layout.activity_home_screen, container, false)
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
//          actionBar?.show()
//          supportActionBar?.show()
//          (activity as AppCompatActivity).supportActionBar?.show()
          showNavigation()
        }

        if (dy <= 0) {
          Log.d("RecyclerView", "OnTop S")
//          (activity as AppCompatActivity).supportActionBar?.show()

//          actionBar?.show()
//          supportActionBar?.show()
          showNavigation()
        }
        if (dy > 0){
          //                    actionBar?.hide()
          //                    supportActionBar?.hide()
//          (activity as AppCompatActivity).supportActionBar?.hide()
          hideNavigation()
      }
        super.onScrolled(recyclerView, dx, dy)
      }
    })
  }


  private fun hideNavigation() {
//    if (homeRecyclerView.scrollState == RecyclerView.SCROLL_STATE_DRAGGING) {
      navigationCardView.visibility = View.GONE
//    }
//    val transition = object:Fade() {
//      override fun setDuration(duration: Long): androidx.transition.Transition {
//        return super.setDuration(600)
//      }
//
//      override fun addTarget(targetId: Int): androidx.transition.Transition {
//        return super.addTarget(navigationCardView.id)
//      }
//    }

  }

  private fun showNavigation() {
//    if (homeRecyclerView.scrollState == RecyclerView.SCROLL_STATE_SETTLING || homeRecyclerView.scrollState == RecyclerView.SCROLL_STATE_IDLE) {
      navigationCardView.visibility = View.VISIBLE
//    }

  }

}