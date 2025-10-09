import { TurboModuleRegistry, type TurboModule } from 'react-native';

export interface Spec extends TurboModule {
  requestPhoneNumber(): Promise<string>;
}

export default TurboModuleRegistry.getEnforcing<Spec>('PhoneNumberAutofill');
