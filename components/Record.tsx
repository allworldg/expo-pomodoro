import { HOUR, MINUTE } from "@/constants/Constants";
import { FocusRecord } from "@/modules/countdown";
import { Event } from "@/modules/countdown/src/CountdownConstant";
import CountdownModule from "@/modules/countdown/src/CountdownModule";
import { useEffect, useRef, useState } from "react";
import { Text, View } from "react-native";

export function Record() {
  const subRecord = useRef<any>(null);
  const [focusCount, setFocusCount] = useState<number>(0);
  const [focusDuration, setFocusDuration] = useState<number>(0);
  const [totalDuration, setTotalDuration] = useState<number>(0);
  const minute = Math.floor((focusDuration %HOUR) /MINUTE);
  const hour = Math.floor(focusDuration / HOUR);
  const totalMinute = Math.floor((totalDuration %HOUR) /MINUTE);
  const totalHour = Math.floor(totalDuration / HOUR);
  CountdownModule.getRecord().then((data) => {
    const { focusCount, focusDuration, totalDuration } = data;
    setFocusCount(focusCount);
    setFocusDuration(focusDuration);
    setTotalDuration(totalDuration);
  });
  useEffect(() => {
    subRecord.current = CountdownModule.addListener(
      Event.RECORD,
      (data: FocusRecord) => {
        const { focusCount, focusDuration, totalDuration } = data;
        setFocusCount(focusCount);
        setFocusDuration(focusDuration);
        setTotalDuration(totalDuration);
      }
    );
    return () => {
      subRecord.current.remove();
    };
  }, []);
  return (
    <View className="mt-auto">
      <Text>当日专注次数: {focusCount}</Text>
      <Text>
        {hour}小时{minute}分钟
      </Text>
      <Text>
        总时长：{totalHour}小时{totalMinute}分钟
      </Text>
    </View>
  );
}
