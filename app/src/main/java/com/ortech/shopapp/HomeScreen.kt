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
import com.ortech.shopapp.Adapters.HeaderListAdapter
import com.ortech.shopapp.Adapters.HomeScreenAdapter
import com.ortech.shopapp.Adapters.StoreListAdapter
import com.ortech.shopapp.Views.HeaderItemDecoration
import com.ortech.shopapp.Views.TopSpacingDecoration
import kotlinx.android.synthetic.main.activity_home_screen.*

class HomeScreen : Fragment() {

  private val mainAdapter = HomeScreenAdapter()
  override fun onCreateView(
    inflater: LayoutInflater,
    container: ViewGroup?,
    savedInstanceState: Bundle?
  ): View? {
    val db = Firebase.firestore

    return inflater.inflate(R.layout.activity_home_screen, container, false)
  }

  override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
    super.onViewCreated(view, savedInstanceState)

    val recyclerView: RecyclerView? = view.findViewById(R.id.homeRecyclerView)

    recyclerView?.apply {
      layoutManager = LinearLayoutManager(this@HomeScreen.context)
      val topSpacingDecoration = TopSpacingDecoration(10)
      addItemDecoration(topSpacingDecoration)
      addItemDecoration(HeaderItemDecoration(recyclerView, false, isHeader()))
      recyclerView.adapter = mainAdapter
    }

  }

  private fun isHeader() : (itemPosition: Int) -> Boolean {
    return {
      itemPosition ->
      itemPosition == 0
    }
  }





}