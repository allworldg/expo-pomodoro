import { Event } from "@/modules/countdown/src/CountdownConstant";
import type { StyleProp, ViewStyle } from "react-native";

export type OnLoadEventPayload = {
  url: string;
};

export type CountdownData = {
  pomodoro: number;
  rest: number;
  cycles: number;
};
export type CountdownSetting = {
  pomodoro: number;
  rest: number;
  cycles: number;
};

export enum StateEnum {
  FOCUSING = "FOCUSING",
  RESTING = "RESTING",
  STOP = "STOP",
}
export type StateChangeData = {
  state: StateEnum;
  curCycle: number;
  cycles: number;
};

export type FocusRecord = {
  focusCount: number;
  focusDuration: number;
  totalDuration: number;
};

export type CountdownModuleEvents = {
  onChange: (params: ChangeEventPayload) => void;
  [Event.TICK]: (data: { remainTime: number }) => void;
  [Event.STATECHANGE]: (data: StateChangeData) => void;
  [Event.RECORD]: (data: {
    focusCount: number;
    focusDuration: number;
    totalDuration: number;
  }) => void;
};

export type ChangeEventPayload = {
  value: string;
};

export type CountdownViewProps = {
  url: string;
  onLoad: (event: { nativeEvent: OnLoadEventPayload }) => void;
  style?: StyleProp<ViewStyle>;
};
