package com.ortech.shopapp

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import com.ortech.shopapp.Adapters.BranchDetailsAdapter
import com.ortech.shopapp.Models.Branch
import com.ortech.shopapp.Views.HeaderItemDecoration
import kotlinx.android.synthetic.main.fragment_branch_details.*

const val ARGS_BRANCH = "branch"

class BranchDetails : Fragment() {

  private var branch: Branch? = null

  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)

    arguments?.let {
      branch = it.getSerializable(ARGS_BRANCH) as Branch
    }
  }

  override fun onCreateView(
    inflater: LayoutInflater, container: ViewGroup?,
    savedInstanceState: Bundle?
  ): View? {
    // Inflate the layout for this fragment
    return inflater.inflate(R.layout.fragment_branch_details, container, false)
  }

  override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
    super.onViewCreated(view, savedInstanceState)
    setup()
    setupToolBar()
  }

  private fun setup() {
    branch?.let {
      textViewBranchDetailsTitle.text = it.branch
      val adapter = BranchDetailsAdapter(it)
      val recyclerView = recyclerViewBranchDetails
      recyclerView.apply {
        recyclerView.adapter = adapter
        addItemDecoration(HeaderItemDecoration(recyclerView!!, false, isHeader()))
        layoutManager = LinearLayoutManager(this.context)
      }
    }

  }

  private fun isHeader() : (itemPosition: Int) -> Boolean {
    return {
        itemPosition ->
      itemPosition == 0
    }
  }

  private fun setupToolBar() {
    val toolbar = toolbarBranchDetails
    toolbar.setNavigationOnClickListener {
      activity?.supportFragmentManager?.popBackStack()
    }
  }

  companion object {
    const val TAG = "BranchList"

    @JvmStatic
    fun newInstance(branch: Branch) = BranchDetails().apply {
      arguments = Bundle().apply {
        putSerializable(ARGS_BRANCH, branch)
      }
    }
  }

}
