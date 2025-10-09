import PhoneNumberAutofill from './NativePhoneNumberAutofill';

export function requestPhoneNumber(): Promise<string> {
  return PhoneNumberAutofill.requestPhoneNumber();
}
