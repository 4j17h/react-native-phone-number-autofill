package com.phonenumberautofill

import com.facebook.react.BaseReactPackage
import com.facebook.react.bridge.NativeModule
import com.facebook.react.bridge.ReactApplicationContext
import com.facebook.react.module.model.ReactModuleInfo
import com.facebook.react.module.model.ReactModuleInfoProvider
import java.util.HashMap

class PhoneNumberAutofillPackage : BaseReactPackage() {
  override fun getModule(name: String, reactContext: ReactApplicationContext): NativeModule? {
    return if (name == PhoneNumberAutofillModule.NAME) {
      PhoneNumberAutofillModule(reactContext)
    } else {
      null
    }
  }

  override fun getReactModuleInfoProvider(): ReactModuleInfoProvider {
    return ReactModuleInfoProvider {
      val moduleInfos: MutableMap<String, ReactModuleInfo> = HashMap()
      moduleInfos[PhoneNumberAutofillModule.NAME] = ReactModuleInfo(
        PhoneNumberAutofillModule.NAME,
        PhoneNumberAutofillModule.NAME,
        false,  // canOverrideExistingModule
        false,  // needsEagerInit
        false,  // isCxxModule
        true // isTurboModule
      )
      moduleInfos
    }
  }
}
