package com.ortech.shopapp.Models

class CMSSettings private constructor() {


  private var couponTimeLimit: Number? = null
  private var qrCodeTimeLimit: Number? = null

  fun setCurrentCouponTimeLimit(timeLimit: Number?) {
    couponTimeLimit = timeLimit
  }

  fun setCurrentQRCodeTimeLimit(timeLimit: Number?) {
    qrCodeTimeLimit = timeLimit
  }

  fun currentCouponTimeLimit(): Number {
    return if (couponTimeLimit != null) {
      couponTimeLimit!!
    } else {
      DEFAULT_COUPON_TIME_LIMIT
    }
  }

  fun currentQRCodeTimeLimit(): Number {
    return if (qrCodeTimeLimit != null) {
      qrCodeTimeLimit!!
    } else {
      DEFAULT_QRCODE_TIME_LIMIT
    }
  }

  private object  HOLDER {
    val INSTANCE = CMSSettings()
  }

  companion object {
    const val DEFAULT_COUPON_TIME_LIMIT = 86400
    const val DEFAULT_QRCODE_TIME_LIMIT = 43200
    val instance: CMSSettings by lazy {HOLDER.INSTANCE}
  }

}