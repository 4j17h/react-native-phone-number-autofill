# @4j17h/react-native-phone-number-autofill

Android-only phone number autofill using Google Credentials API. Shows Google account phone numbers AND SIM numbers for better UX.

## Why This Library?

Google deprecated the old Credentials API in favor of the new Phone Number Hint API. However:

- **Old Credentials API (This Library)**: Shows Google account numbers + SIM numbers ✅
- **New Phone Number Hint API**: Only shows SIM-based numbers ❌

This library uses the deprecated-but-working Credentials API (like Zomato does) to provide better UX.

## Installation

```bash
npm install @4j17h/react-native-phone-number-autofill
# or
yarn add @4j17h/react-native-phone-number-autofill
```

### Android Setup

Add to your project's `android/build.gradle`:

```gradle
allprojects {
    configurations.all {
        resolutionStrategy {
            force 'com.google.android.gms:play-services-auth:20.7.0'
        }
    }
}
```

## Usage

```typescript
import { requestPhoneNumber } from '@4j17h/react-native-phone-number-autofill';

const handlePhoneNumberPicker = async () => {
  try {
    const phoneNumber = await requestPhoneNumber();
    console.log('Selected:', phoneNumber); // e.g., "+919876543210"
  } catch (error) {
    console.log('User cancelled or no numbers available');
  }
};
```

### Example

```tsx
import React, { useState } from 'react';
import { TextInput, Platform } from 'react-native';
import { requestPhoneNumber } from '@4j17h/react-native-phone-number-autofill';

const LoginScreen = () => {
  const [phone, setPhone] = useState('');

  const handleFocus = async () => {
    if (Platform.OS !== 'android') return;
    
    try {
      const number = await requestPhoneNumber();
      setPhone(number?.slice(-10) || ''); // Last 10 digits
    } catch (error) {
      // User cancelled
    }
  };

  return (
    <TextInput
      value={phone}
      onChangeText={setPhone}
      onFocus={handleFocus}
      placeholder="Phone Number"
      keyboardType="phone-pad"
    />
  );
};
```

## API

### `requestPhoneNumber(): Promise<string>`

Shows Google phone number picker (Android only).

**Returns:** Phone number in E.164 format (e.g., `"+919876543210"`)

**Error Codes:**
- `ACTIVITY_NULL_ERROR` - Activity not available
- `ACTIVITY_RESULT_NOOK_ERROR` - User cancelled
- `NO_PHONE_NUMBERS_ERROR` - No numbers found
- `SEND_INTENT_ERROR` - Failed to show picker
- `IOS_NOT_SUPPORTED` - Called on iOS

## Platform Support

- ✅ **Android** (minSdk 24+)
- ❌ **iOS** (use `textContentType="telephoneNumber"` instead)

## Requirements

- React Native >= 0.60
- Android minSdkVersion 24+
- `com.google.android.gms:play-services-auth:20.7.0`

## Technical Details

Uses deprecated Google Credentials API:
- `com.google.android.gms.auth.api.credentials.CredentialsClient`
- `com.google.android.gms.auth.api.credentials.HintRequest`

Even though deprecated, these APIs still work in `play-services-auth:20.7.0` and provide better UX than the new API.

## License

MIT

## Credits

Inspired by phone number picker in Zomato and other popular apps.
