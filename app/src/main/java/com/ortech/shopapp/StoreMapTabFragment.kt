package com.ortech.shopapp

import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.drawable.Drawable
import android.net.Uri
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
import com.bumptech.glide.load.DataSource
import com.bumptech.glide.load.engine.GlideException
import com.bumptech.glide.request.RequestListener
import com.bumptech.glide.request.target.SimpleTarget
import com.bumptech.glide.request.target.Target
import com.google.android.gms.maps.*
import com.google.android.gms.maps.model.*
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.ortech.shopapp.Models.Branch
import kotlinx.android.synthetic.main.custom_marker_info_window.*
import kotlinx.android.synthetic.main.custom_marker_info_window.view.*
import kotlinx.android.synthetic.main.fragment_map_pin.view.*



class StoreMapTabFragment : Fragment(), OnMapReadyCallback, GoogleMap.OnMarkerClickListener, GoogleMap.OnInfoWindowClickListener{

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
      mMap!!.setOnMarkerClickListener(this)
      setupCustomInfoWindow()
      setupMap()

    }

  }

  private fun setupMap() {
//    mMap?.isMyLocationEnabled ?: return = true

  }

  private fun setupCustomInfoWindow () {

    val customView = layoutInflater.inflate(R.layout.custom_marker_info_window, null)
    mMap!!.setInfoWindowAdapter(CustomInfoWindow(this.context!!))
    mMap!!.setOnInfoWindowClickListener(this)
//    mMap!!.setInfoWindowAdapter(object: GoogleMap.InfoWindowAdapter {
//
//      override fun getInfoContents(p0: Marker?): View {
//        val title = customView.textViewInfoWindowTitle
//        val image = customView.imageViewInfoWindowImage
//        val branch = branches[p0!!.tag as Int]
//
//        Glide.with(customView)
//          .load(Uri.parse(branch.branchURLImages))
//          .centerCrop()
//          .into(image)
//        title.text = branch.branch
//        return customView
//      }
//
//      override fun getInfoWindow(p0: Marker?): View {
//        val title = customView.textViewInfoWindowTitle
//        val image = customView.imageViewInfoWindowImage
//        val branch = branches[p0!!.tag as Int]
//
//        Glide.with(context!!)
//          .load(Uri.parse(branch.branchURLImages))
//          .centerCrop()
//          .into(image)
//        title.text = branch.branch
//        return customView
//      }
//
//    })
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

      val marker = mMap?.addMarker(options)
      if (marker != null) {
        marker.tag = this.branches.count()
      }
      mMap?.moveCamera(CameraUpdateFactory.newLatLng(position))
      this.branches.add(branch)
    }
  }

  override fun onMarkerClick(p0: Marker?): Boolean {
    p0?.showInfoWindow()

    return false
  }

  override fun onInfoWindowClick(p0: Marker?) {
        val tag = p0?.tag
    val branch = this.branches[tag as Int]

    if (branch.latitude == 0.toDouble() || branch.longitude == 0.toDouble()) {
      val uri = Uri.parse("https://www.google.com.ph/maps/search/${branch.location}")
      val intent = Intent(Intent.ACTION_VIEW, uri)
      intent.setPackage("com.google.android.apps.maps")
      activity?.baseContext?.startActivity(intent)
    } else {
      val geoUri = Uri.parse("geo:${branch.latitude},${branch.longitude}")
      var intent = Intent(Intent.ACTION_VIEW, geoUri)
      intent.setPackage("com.google.android.apps.maps")
      if (activity?.baseContext?.packageManager?.let { intent.resolveActivity(it) } !== null) {
        activity?.baseContext?.startActivity(intent)
      } else {
        val uri = Uri.parse("http://maps.google.com/maps?saddr=${branch.latitude},${branch.longitude}")
        intent = Intent(Intent.ACTION_VIEW, uri)
        intent.setPackage("com.google.android.apps.maps")
        activity?.baseContext?.startActivity(intent)
      }
    }
  }



  override fun onDestroyView() {
    super.onDestroyView()
//
//    val fm = childFragmentManager
//    var supportMapFragment = fm?.findFragmentById(R.id.mapView) as SupportMapFragment?
////    if (supportMapFragment == null) {
////      supportMapFragment = SupportMapFragment.newInstance()
////      fm?.beginTransaction()?.replace(R.id.mapView, supportMapFragment)?.commit()
////    }
//    if (supportMapFragment != null) {
//      fm?.beginTransaction()?.remove(supportMapFragment)
//      fm?.commit {
//        commitAllowingStateLoss()
//      }
//    }
  }


  companion object {
   const val TAG = "StoreMapTabFragment"
  }

  inner class CustomInfoWindow(private var context: Context): GoogleMap.InfoWindowAdapter {

    private lateinit var customView: View


    override fun getInfoContents(p0: Marker?): View {
      val inflater = context.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
      val view = inflater.inflate(R.layout.custom_marker_info_window, null)
      val title = view.textViewInfoWindowTitle
      val image = view.imageViewInfoWindowImage
      val branch = branches[p0!!.tag as Int]

      Log.d("MapTab", "Loading ${branch.branchURLImages}")

      Glide.with(context)
        .load(Uri.parse(branch.branchURLImages))
        .centerCrop()
        .listener(object: RequestListener<Drawable>{
          override fun onLoadFailed(
            e: GlideException?,
            model: Any?,
            target: Target<Drawable>?,
            isFirstResource: Boolean
          ): Boolean {
            Log.d("MapTab", "Loading Failed ${branch.branchURLImages}")
            return false
          }

          override fun onResourceReady(
            resource: Drawable?,
            model: Any?,
            target: Target<Drawable>?,
            dataSource: DataSource?,
            isFirstResource: Boolean
          ): Boolean {
            Log.d("MapTab", "Loading Success ${branch.branchURLImages}")
            image.setImageDrawable(resource)

            return false
          }

        })
        .into(image)

      title.text = branch.branch
      return view
    }

    override fun getInfoWindow(p0: Marker?): View? {
      return null
    }

  }


}
