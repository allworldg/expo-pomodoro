import { NativeModule, requireNativeModule } from 'expo';

import { CountdownModuleEvents } from './Countdown.types';

declare class CountdownModule extends NativeModule<CountdownModuleEvents> {
  PI: number;
  hello(): string;
  fker():void;
  setValueAsync(value: string): Promise<void>;
}

// This call loads the native module object from the JSI.
export default requireNativeModule<CountdownModule>('Countdown');
