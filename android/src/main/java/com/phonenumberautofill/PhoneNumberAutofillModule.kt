package com.phonenumberautofill

import android.app.Activity
import android.content.Intent
import android.content.IntentSender
import android.util.Log
import com.facebook.react.bridge.*
import com.google.android.gms.auth.api.credentials.Credential
import com.google.android.gms.auth.api.credentials.Credentials
import com.google.android.gms.auth.api.credentials.HintRequest
import com.facebook.react.module.annotations.ReactModule

@ReactModule(name = PhoneNumberAutofillModule.NAME)
class PhoneNumberAutofillModule(reactContext: ReactApplicationContext) :
  ReactContextBaseJavaModule(reactContext) {

  private var promise: Promise? = null

  override fun getName(): String {
    return NAME
  }

  @ReactMethod
  fun requestPhoneNumber(promise: Promise) {
    this.promise = promise
    val activity = currentActivity
    if (activity == null) {
      promise.reject(ACTIVITY_NULL_ERROR_TYPE, ACTIVITY_NULL_ERROR_MESSAGE)
      return
    }

    try {
      // Use the deprecated but working Credentials API to show Google account phone numbers
      val hintRequest = HintRequest.Builder()
        .setPhoneNumberIdentifierSupported(true)
        .build()
      
      val credentialsClient = Credentials.getClient(activity)
      val intent = credentialsClient.getHintPickerIntent(hintRequest)
      
      activity.startIntentSenderForResult(
        intent.intentSender,
        REQUEST_PHONE_NUMBER_REQUEST_CODE,
        null,
        0,
        0,
        0
      )
    } catch (e: IntentSender.SendIntentException) {
      promise.reject(SEND_INTENT_ERROR_TYPE, "Failed to show phone picker: ${e.message}")
    } catch (e: Exception) {
      promise.reject(SEND_INTENT_ERROR_TYPE, "Unexpected error: ${e.message}")
    }
  }

  private val activityEventListener = object : BaseActivityEventListener() {
    override fun onActivityResult(activity: Activity?, requestCode: Int, resultCode: Int, data: Intent?) {
      if (requestCode == REQUEST_PHONE_NUMBER_REQUEST_CODE) {
        if (resultCode == Activity.RESULT_OK && data != null) {
          try {
            val credential: Credential? = data.getParcelableExtra(Credential.EXTRA_KEY)
            val phoneNumber = credential?.id
            if (phoneNumber != null && phoneNumber.isNotEmpty()) {
              promise?.resolve(phoneNumber)
            } else {
              promise?.reject(NO_PHONE_NUMBERS_ERROR_TYPE, NO_PHONE_NUMBERS_ERROR_MESSAGE)
            }
          } catch (e: Exception) {
            promise?.reject(ACTIVITY_RESULT_NOOK_ERROR_TYPE, "Error extracting phone: ${e.message}")
          }
        } else {
          promise?.reject(ACTIVITY_RESULT_NOOK_ERROR_TYPE, ACTIVITY_RESULT_NOOK_ERROR_MESSAGE)
        }
        promise = null
      }
    }
  }

  init {
    reactContext.addActivityEventListener(activityEventListener)
  }

  companion object {
    const val NAME = "PhoneNumberAutofill"
    private const val REQUEST_PHONE_NUMBER_REQUEST_CODE = 1

    private const val ACTIVITY_NULL_ERROR_TYPE = "ACTIVITY_NULL_ERROR"
    private const val ACTIVITY_RESULT_NOOK_ERROR_TYPE = "ACTIVITY_RESULT_NOOK_ERROR"
    private const val NO_PHONE_NUMBERS_ERROR_TYPE = "NO_PHONE_NUMBERS_ERROR"
    private const val SEND_INTENT_ERROR_TYPE = "SEND_INTENT_ERROR"

    private const val ACTIVITY_NULL_ERROR_MESSAGE = "Activity is null."
    private const val ACTIVITY_RESULT_NOOK_ERROR_MESSAGE = "There was an error trying to get the phone number."
    private const val NO_PHONE_NUMBERS_ERROR_MESSAGE = "No phone numbers found on this device."
    private const val SEND_INTENT_ERROR_MESSAGE = "There was an error trying to send intent."
  }
}
