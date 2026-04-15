import NativePhoneNumberAutofill from './NativePhoneNumberAutofill';

export function requestPhoneNumber(): Promise<string> {
  return NativePhoneNumberAutofill.requestPhoneNumber();
}

const PhoneNumberAutofill = {
  requestPhoneNumber,
};

export default PhoneNumberAutofill;