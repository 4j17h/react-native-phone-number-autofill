package com.phonenumberautofill

import android.app.Activity
import android.content.Intent
import android.content.IntentSender
import com.facebook.react.bridge.*
import com.facebook.react.module.annotations.ReactModule
import com.google.android.gms.auth.api.credentials.Credential
import com.google.android.gms.auth.api.credentials.Credentials
import com.google.android.gms.auth.api.credentials.HintRequest

@ReactModule(name = PhoneNumberAutofillModule.NAME)
class PhoneNumberAutofillModule(
  reactContext: ReactApplicationContext
) : NativePhoneNumberAutofillSpec(reactContext) {

  private var promise: Promise? = null

  override fun getName(): String = NAME

  override fun requestPhoneNumber(promise: Promise) {
    this.promise = promise
    val activity = reactApplicationContext.currentActivity

    if (activity == null) {
      promise.reject(ACTIVITY_NULL_ERROR_TYPE, ACTIVITY_NULL_ERROR_MESSAGE)
      return
    }

    try {
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
      promise.reject(SEND_INTENT_ERROR_TYPE, e.message)
    } catch (e: Exception) {
      promise.reject(SEND_INTENT_ERROR_TYPE, e.message)
    }
  }

  private val activityEventListener = object : BaseActivityEventListener() {
    override fun onActivityResult(
      activity: Activity,
      requestCode: Int,
      resultCode: Int,
      data: Intent?
    ) {
      if (requestCode == REQUEST_PHONE_NUMBER_REQUEST_CODE) {
        if (resultCode == Activity.RESULT_OK && data != null) {
          try {
            val credential: Credential? =
              data.getParcelableExtra(Credential.EXTRA_KEY)
            val phoneNumber = credential?.id

            if (!phoneNumber.isNullOrEmpty()) {
              promise?.resolve(phoneNumber)
            } else {
              promise?.reject(
                NO_PHONE_NUMBERS_ERROR_TYPE,
                NO_PHONE_NUMBERS_ERROR_MESSAGE
              )
            }
          } catch (e: Exception) {
            promise?.reject(
              ACTIVITY_RESULT_NOOK_ERROR_TYPE,
              e.message
            )
          }
        } else {
          promise?.reject(
            ACTIVITY_RESULT_NOOK_ERROR_TYPE,
            ACTIVITY_RESULT_NOOK_ERROR_MESSAGE
          )
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
    private const val ACTIVITY_RESULT_NOOK_ERROR_MESSAGE =
      "There was an error trying to get the phone number."
    private const val NO_PHONE_NUMBERS_ERROR_MESSAGE =
      "No phone numbers found on this device."
  }
}