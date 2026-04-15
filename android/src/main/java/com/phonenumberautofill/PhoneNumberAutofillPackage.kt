package com.phonenumberautofill

import com.facebook.react.TurboReactPackage
import com.facebook.react.bridge.NativeModule
import com.facebook.react.bridge.ReactApplicationContext
import com.facebook.react.module.model.ReactModuleInfo
import com.facebook.react.module.model.ReactModuleInfoProvider

class PhoneNumberAutofillPackage : TurboReactPackage() {

  override fun getModule(
    name: String,
    reactContext: ReactApplicationContext
  ): NativeModule? {
    return if (name == PhoneNumberAutofillModule.NAME) {
      PhoneNumberAutofillModule(reactContext)
    } else {
      null
    }
  }

  override fun getReactModuleInfoProvider(): ReactModuleInfoProvider {
    return ReactModuleInfoProvider {
      mapOf(
        PhoneNumberAutofillModule.NAME to ReactModuleInfo(
          PhoneNumberAutofillModule.NAME,
          PhoneNumberAutofillModule.NAME,
          false, // canOverrideExistingModule
          false, // needsEagerInit
          false, // hasConstants
          true   // ✅ isTurboModule
        )
      )
    }
  }
}