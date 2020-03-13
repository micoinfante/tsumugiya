package com.ortech.shopapp.Adapters

import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.ortech.shopapp.R

class HomeScreenAdapter: RecyclerView.Adapter<RecyclerView.ViewHolder>(){
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        Log.d("HomeScreen", "Setting Up Sections")
        return when(viewType) {
            0 -> {
                Log.d("HomeScreen", "Setup Header")
                HomeScreenHeader(
                    inflater.inflate(R.layout.fragment_home_screen_header, parent, false)
                )
            }
            1 -> {
                Log.d("HomeScreen", "Setup Points")
                HomeScreenPoints(
                    inflater.inflate(R.layout.fragment_home_screen_points, parent, false)
                )
            }
            else -> {
                HomeScreenItem(
                    inflater.inflate(R.layout.fragment_home_screen_item, parent, false)
                )
            }
        }

    }

    override fun getItemViewType(position: Int): Int {
        return when(position) {
            0 -> 0
            1 -> 1
            else -> position
        }
    }

    override fun getItemCount(): Int {
        Log.d("HomeScreen", "Number Of Sections: 5")
       return 5
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {

    }


    class HomeScreenItem constructor(itemView: View): RecyclerView.ViewHolder(itemView) {

    }

    class HomeScreenHeader constructor(itemView: View): RecyclerView.ViewHolder(itemView) {

    }

    class HomeScreenPoints constructor(itemView: View): RecyclerView.ViewHolder(itemView) {

    }
}


