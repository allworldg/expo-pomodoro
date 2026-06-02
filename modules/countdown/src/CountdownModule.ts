import { NativeModule, requireNativeModule } from "expo";

import {
  CountdownData,
  CountdownModuleEvents,
  CountdownSetting,
} from "./Countdown.types";

declare class CountdownModule extends NativeModule<CountdownModuleEvents> {
  PI: number;
  setValueAsync(value: string): Promise<void>;
  startCountdown(value: CountdownData): void;
  stopCountdown(): Promise<void>;
  requestNotificationPermission(): Promise<void>;
  initCountdownSetting(): CountdownSetting;
  updateSetting(value: CountdownSetting): void;
  getCountdownSetting(): CountdownSetting;
}

// This call loads the native module object from the JSI.
export default requireNativeModule<CountdownModule>("Countdown");
