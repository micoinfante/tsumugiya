package com.ortech.shopapp.Adapters

import android.content.Intent
import android.net.Uri
import android.os.Build
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.annotation.RequiresApi
import androidx.recyclerview.widget.RecyclerView
import com.ortech.shopapp.Models.Branch
import com.ortech.shopapp.Models.UserSingleton
import com.ortech.shopapp.R
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.branch_details_section.view.*
import kotlinx.android.synthetic.main.fragment_branch_details.view.*
import kotlinx.android.synthetic.main.fragment_home_screen_header.view.*

class BranchDetailsAdapter(private val branch: Branch): RecyclerView.Adapter<RecyclerView.ViewHolder>() {
  private var itemCount = 2

  override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
    val inflater = LayoutInflater.from(parent.context)
    return if (viewType==0) {
        BranchHeaderHolder(inflater.inflate(R.layout.fragment_home_screen_header, parent, false))
    } else BranchDetailsHolder(inflater.inflate(R.layout.branch_details_section, parent, false))
  }

  override fun getItemViewType(position: Int): Int {
    return if(position==0) {
      TYPE_HEADER
    } else TYPE_DETAIL
  }

  override fun getItemCount(): Int {
    return itemCount
  }

  @RequiresApi(Build.VERSION_CODES.LOLLIPOP)
  override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
    when(holder) {
      is BranchHeaderHolder -> holder.bind()
      is BranchDetailsHolder -> holder.bind()
    }
  }

  inner class BranchHeaderHolder constructor(itemView: View): RecyclerView.ViewHolder(itemView) {
    private val imageView: ImageView = itemView.imageViewHeader

    @RequiresApi(Build.VERSION_CODES.LOLLIPOP)
    fun bind() {
      Picasso.get()
        .load(Uri.parse(branch.branchURLImages))
        .into(imageView)
    }

  }

  inner class BranchDetailsHolder constructor(itemView: View): RecyclerView.ViewHolder(itemView) {
    private val location = itemView.textViewBranchDetailsLocation
    private val contact = itemView.textViewBranchDetailsContact
    private val storeHours = itemView.textViewBranchDetailsHours
    private val holiday = itemView.textViewRegularHoliday
    private val creditCard = itemView.textViewCreditCard
    private val smartPayment = itemView.textViewSmartPayment
    private val directions = itemView.textViewDirections
    private val customerCapacity = itemView.textViewCustomerCapacity
    private val parking = itemView.textViewCustomerParking
    private val buttonMap = itemView.buttonBranchDetailsDirection
    private val buttonUseCoupon = itemView.buttonBranchDetailsUseCoupon
    private val currentPoints = itemView.textViewBranchDetailsCurrentPoints
    private val res = itemView.context

    fun bind() {
      location.text = branch.location
      contact.text = branch.phone
      storeHours.text = res.getString(R.string.dash_store_hours, branch.opening , branch.closing)
      holiday.text = branch.holiday
      creditCard.text = branch.credit
      smartPayment.text = branch.smartPhone
      directions.text = branch.access
      customerCapacity.text = branch.capacity
      parking.text = branch.exclusive

      currentPoints.text = UserSingleton.instance.getCurrentPoints().toString()

      //  TODO use coupon/points
      buttonMap.setOnClickListener {

        var uri: Uri?

        if (branch.latitude == 0.toDouble() || branch.longitude == 0.toDouble()) {
          uri = Uri.parse("https://www.google.com.ph/maps/search/${branch.location}")
          val intent = Intent(Intent.ACTION_VIEW, uri)
          intent.setPackage("com.google.android.apps.maps")
        } else {
          val geoUri = Uri.parse("geo:${branch.latitude},${branch.longitude}")
          var intent = Intent(Intent.ACTION_VIEW, geoUri)
          intent.setPackage("com.google.android.apps.maps")
          if (intent.resolveActivity(itemView.context.packageManager) !== null) {
            itemView.context.startActivity(intent)
          } else {
            uri = Uri.parse("http://maps.google.com/maps?saddr=${branch.latitude},${branch.longitude}")
            intent = Intent(Intent.ACTION_VIEW, uri)
            intent.setPackage("com.google.android.apps.maps")
            itemView.context.startActivity(intent)
          }
        }


      }
    }

  }

  companion object {
    const val TYPE_HEADER = 0
    const val TYPE_DETAIL = 1
  }

}