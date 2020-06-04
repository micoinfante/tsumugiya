package com.ortech.shopapp

import android.content.Context
import android.graphics.Bitmap
import android.graphics.Canvas
import android.os.Build
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.RequiresApi
import androidx.fragment.app.commit
import com.bumptech.glide.Glide
import com.bumptech.glide.request.target.SimpleTarget
import com.google.android.gms.maps.*
import com.google.android.gms.maps.model.BitmapDescriptor
import com.google.android.gms.maps.model.BitmapDescriptorFactory
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.ortech.shopapp.Models.Branch
import kotlinx.android.synthetic.main.fragment_map_pin.view.*



class StoreMapTabFragment : Fragment(), OnMapReadyCallback{

  private var mMap: GoogleMap? = null
  private var mapView: MapView? = null
  private var branches = ArrayList<Branch>()

  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    retainInstance = true
    if (mMap == null) {
      val fm = childFragmentManager
      var supportMapFragment = fm?.findFragmentById(R.id.mapView) as SupportMapFragment?
      if (supportMapFragment == null) {
        supportMapFragment = SupportMapFragment.newInstance()
        fm?.beginTransaction()?.replace(R.id.mapView, supportMapFragment)?.commit()
      }
      supportMapFragment?.getMapAsync(this)
      if (branches.count() == 0) {
        getBranches()
      }
    }

  }


  override fun onResume() {
    super.onResume()
    setupMapIfNeeded()
  }

  override fun onCreateView(
    inflater: LayoutInflater, container: ViewGroup?,
    savedInstanceState: Bundle?
  ): View? {
    // Inflate the layout for this fragment


    return inflater.inflate(R.layout.fragment_store_map_tab, container, false)
  }

  private fun setupMapIfNeeded() {
    if (mMap == null) {
      val fm = childFragmentManager
      var supportMapFragment = fm?.findFragmentById(R.id.mapView) as SupportMapFragment?
      if (supportMapFragment == null) {
        supportMapFragment = SupportMapFragment.newInstance()
        fm?.beginTransaction()?.replace(R.id.mapView, supportMapFragment)?.commit()
      }
      supportMapFragment?.getMapAsync(this)
      if (branches.count() == 0) {
        getBranches()
      }
    }
  }

  override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
    super.onViewCreated(view, savedInstanceState)
    Log.d(TAG, "ViewCreated")
    val fm = childFragmentManager
    var supportMapFragment = fm?.findFragmentById(R.id.mapView) as SupportMapFragment?
    if (supportMapFragment == null) {
      supportMapFragment = SupportMapFragment.newInstance()
      fm?.beginTransaction()?.replace(R.id.mapView, supportMapFragment)?.commit()
    }
    supportMapFragment?.getMapAsync(this)
    getBranches()
}

  @RequiresApi(Build.VERSION_CODES.LOLLIPOP)
  override fun onMapReady(p0: GoogleMap?) {
    // Add a marker in Sydney and move the camera
    Log.d(TAG, "MapReady")
    if (p0 != null) {
      mMap = p0
      setupMap()

    }

  }

  private fun setupMap() {
//    mMap?.isMyLocationEnabled ?: return = true

  }

  private fun getMarkerIcon(context: Context, url: String, listener: (BitmapDescriptor) -> Unit) {
    val markerView = View.inflate(context, R.layout.fragment_map_pin, null)
    Glide.with(context)
      .asBitmap()
      .circleCrop()
      .load(url)
      .into(object : SimpleTarget<Bitmap>() {

        override fun onResourceReady(
          resource: Bitmap,
          transition: com.bumptech.glide.request.transition.Transition<in Bitmap>?
        ) {
          markerView.imageViewMapPin.setImageBitmap(resource)
          listener.invoke(BitmapDescriptorFactory.fromBitmap(getBitmapFromView(markerView)))
        }
      })
  }

  private fun getBitmapFromView(view: View): Bitmap {
    view.measure(View.MeasureSpec.UNSPECIFIED, View.MeasureSpec.UNSPECIFIED)
    val bitmap = Bitmap.createBitmap(view.measuredWidth, view.measuredHeight, Bitmap.Config.ARGB_8888)
    val canvas = Canvas(bitmap)
    view.layout(0, 0, view.measuredWidth, view.measuredHeight)
    view.draw(canvas)
    return bitmap
  }

  private fun getBranches() {
    Log.d(TAG, "Getting branches")
    val db = Firebase.firestore
    db.collection("CMSBranches").get()
      .addOnSuccessListener { result ->
        for (document in result) {
          val newBranch = document.toObject(Branch::class.java)
          addNewMarker(newBranch)
        }
      }
      .addOnFailureListener {exception ->
        Log.w(TAG, "Error getting documents.", exception)
      }
  }

  private fun addNewMarker(branch: Branch) {
    if (branch.latitude == 0.0 || branch.longitude == 0.0) {return}
    val position = LatLng(branch.latitude, branch.longitude)
    getMarkerIcon(context!!, branch.branchURLImages) {
      val options = MarkerOptions()
        .position(position)
        .icon(it)
      mMap?.addMarker(options)
      mMap?.moveCamera(CameraUpdateFactory.newLatLng(position))
      this.branches.add(branch)
    }
  }

  override fun onDestroyView() {
    super.onDestroyView()

    val fm = childFragmentManager
    var supportMapFragment = fm?.findFragmentById(R.id.mapView) as SupportMapFragment?
//    if (supportMapFragment == null) {
//      supportMapFragment = SupportMapFragment.newInstance()
//      fm?.beginTransaction()?.replace(R.id.mapView, supportMapFragment)?.commit()
//    }
    if (supportMapFragment != null) {
      fm?.beginTransaction()?.remove(supportMapFragment)
      fm?.commit {
        commitAllowingStateLoss()
      }
    }
  }


  companion object {
   const val TAG = "StoreMapTabFragment"
  }
}
