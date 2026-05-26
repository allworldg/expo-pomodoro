import { NativeModule, requireNativeModule } from "expo";

import { CountdownData, CountdownModuleEvents } from "./Countdown.types";

declare class CountdownModule extends NativeModule<CountdownModuleEvents> {
  PI: number;
  setValueAsync(value: string): Promise<void>;
  startCountdown(value: CountdownData): Promise<void>;
  stopCountdown(): Promise<void>;
  requestNotificationPermission(): Promise<void>;
}

// This call loads the native module object from the JSI.
export default requireNativeModule<CountdownModule>("Countdown");
