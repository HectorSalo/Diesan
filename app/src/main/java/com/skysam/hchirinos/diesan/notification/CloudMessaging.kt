package com.skysam.hchirinos.diesan.notification

import android.util.Log
import com.google.firebase.messaging.FirebaseMessaging
import com.skysam.hchirinos.diesan.common.Constants

/**
 * Created by Hector Chirinos on 20/02/2022.
 */

object CloudMessaging {
 private fun getInstance(): FirebaseMessaging {
  return FirebaseMessaging.getInstance()
 }
 
 fun subscribeToTopicUpdateApp() {
  getInstance().subscribeToTopic(Constants.TOPIC_NOTIFICATION_UPDATE_APP)
   .addOnSuccessListener {
    Log.e("MSG OK", "subscribe")
   }
 }
 
 fun unsubscribeToTopicUpdateApp() {
  getInstance().unsubscribeFromTopic(Constants.TOPIC_NOTIFICATION_UPDATE_APP)
 }
}