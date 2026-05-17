import { EventType } from "@/modules/countdown/src/CountdownConstant";
import type { StyleProp, ViewStyle } from "react-native";

export type OnLoadEventPayload = {
  url: string;
};

export type CountdownData = {
  pomodoro: number;
  rest:number;
  cycles:number;
}

export type CountdownModuleEvents = {
  onChange: (params: ChangeEventPayload) => void;
  [EventType.TICK]: (data:{remainTime:number}) => void;
  [EventType.STOP]: () => void;
  [EventType.STATECHANGE]:()=>void;
  CountdownConstant: () => void;
  stateChange: () => void;
  stop: () => void;
};



export type ChangeEventPayload = {
  value: string;
};

export type CountdownViewProps = {
  url: string;
  onLoad: (event: { nativeEvent: OnLoadEventPayload }) => void;
  style?: StyleProp<ViewStyle>;
};
