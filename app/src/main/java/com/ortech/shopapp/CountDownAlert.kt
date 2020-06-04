package com.ortech.shopapp

import android.os.Bundle
import android.os.CountDownTimer
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.ortech.shopapp.Adapters.AllCouponListAdapter
import kotlinx.android.synthetic.main.fragment_count_down_alert.*
import java.util.*
import java.util.concurrent.TimeUnit


private const val ARG_MILLIS = "millis"


class CountDownAlert : Fragment() {
  // TODO: Rename and change types of parameters
  private var millis: Long? = null
  private var countDownTimer: CountDownTimer? = null

  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    arguments?.let {
      millis = it.getLong(ARG_MILLIS)
    }
  }

  override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
    super.onViewCreated(view, savedInstanceState)
    buttonAlertOk.setOnClickListener {
      countDownTimer?.cancel()
      activity?.supportFragmentManager?.popBackStack()
    }
    addCountDownTimer()
  }


  private fun addCountDownTimer() {
    millis?: return

      countDownTimer = object : CountDownTimer(millis!!, 1000) {
      override fun onFinish() {
        countDownTimer?.cancel()
        activity?.supportFragmentManager?.popBackStack()
      }

      override fun onTick(millisUntilFinished: Long) {
        textViewAlertCountDown.text = String.format("%02d:%02d:%02d",
          TimeUnit.MILLISECONDS.toHours(millisUntilFinished),
          TimeUnit.MILLISECONDS.toMinutes(millisUntilFinished) - TimeUnit.HOURS.toMinutes(TimeUnit.MILLISECONDS.toHours(millisUntilFinished)),
          TimeUnit.MILLISECONDS.toSeconds(millisUntilFinished) - TimeUnit.MINUTES.toSeconds(
            TimeUnit.MILLISECONDS.toMinutes(millisUntilFinished))
        )
      }
    }.start()
  }

  override fun onCreateView(
    inflater: LayoutInflater, container: ViewGroup?,
    savedInstanceState: Bundle?
  ): View? {
    // Inflate the layout for this fragment

    return inflater.inflate(R.layout.fragment_count_down_alert, container, false)
  }

  override fun onPause() {
    super.onPause()
    countDownTimer?.cancel()
  }

  override fun onDestroy() {
    super.onDestroy()
    countDownTimer?.cancel()
  }

  override fun onDestroyView() {
    super.onDestroyView()
    countDownTimer?.cancel()
  }

  companion object {

    @JvmStatic
    fun newInstance(millis: Long) =
      CountDownAlert().apply {
        arguments = Bundle().apply {
          putLong(ARG_MILLIS, millis)
        }
      }
  }
}
