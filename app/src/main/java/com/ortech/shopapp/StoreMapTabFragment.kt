package com.ortech.shopapp

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.app.ActivityCompat
import com.google.android.gms.maps.*
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import java.lang.Exception


/**
 * A simple [Fragment] subclass.
 * Use the [StoreMapTabFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class StoreMapTabFragment : Fragment(), OnMapReadyCallback{
  // TODO: Rename and change types of parameters
  private var param1: String? = null
  private var param2: String? = null

  private lateinit var mMap: GoogleMap
  lateinit var mapView: MapView

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

  override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
    super.onViewCreated(view, savedInstanceState)

    Log.d(TAG, "OnViewCreated")
    val fm = activity?.supportFragmentManager
    var supportMapFragment = fm?.findFragmentById(R.id.mapView) as SupportMapFragment?
    if (supportMapFragment == null) {
      supportMapFragment = SupportMapFragment.newInstance()
      fm?.beginTransaction()?.replace(R.id.mapView, supportMapFragment)?.commit()
    }
    supportMapFragment?.getMapAsync(this)

  }

  override fun onMapReady(p0: GoogleMap?) {
    // Add a marker in Sydney and move the camera
    Log.d(TAG, "MapReady")
    if (p0 != null) {

      mMap = p0
      setupMap()
      val sydney = LatLng(-34.0, 151.0)
      mMap.addMarker(MarkerOptions().position(sydney).title("Marker in Sydney"))
      mMap.moveCamera(CameraUpdateFactory.newLatLng(sydney))
    }

  }

  private fun setupMap() {
    mMap.isMyLocationEnabled = true

  }

  private fun setupMapIfNeeded() {
//    if (mMap == null) {
//
//    }
  }

  companion object {
   const val TAG = "StoreMapTabFragment"
  }
}
