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
export type CountdownSetting={
  pomodoro:number;
  rest:number;
  cycles:number;
}

export enum StateEnum {
    FOCUSING = "FOCUSING",
    RESTING = "RESTING",
    STOP = "STOP"
}
export type StateChangeData = {
  state: StateEnum;
  curCycle:number;
  cycles:number;
}

export type CountdownModuleEvents = {
  onChange: (params: ChangeEventPayload) => void;
  [EventType.TICK]: (data:{remainTime:number}) => void;
  [EventType.STATECHANGE]:(data:StateChangeData)=>void;
};



export type ChangeEventPayload = {
  value: string;
};

export type CountdownViewProps = {
  url: string;
  onLoad: (event: { nativeEvent: OnLoadEventPayload }) => void;
  style?: StyleProp<ViewStyle>;
};
