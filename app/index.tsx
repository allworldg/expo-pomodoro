import ClockDisplay from "@/components/ClockDisplay";
import CountdownInput from "@/components/CountdownInput";
import PomodoroStatus from "@/components/PomodoroStatus";
import Record from "@/components/Record";
import StartStopButton from "@/components/StartStopButton";
import "@/global.css";
import {
  CountdownSettingInput,
  StateChangeData,
  StateEnum,
} from "@/modules/countdown";
import { Event } from "@/modules/countdown/src/CountdownConstant";
import CountdownModule from "@/modules/countdown/src/CountdownModule";
import { checkInRange } from "@/utils/InputCheck";
import { useEffect, useRef, useState } from "react";
import { View } from "react-native";

export default function Index() {
  const [isStarted, setIsStarted] = useState(false);
  const [countdownSetting, setCountdownSetting] =
    useState<CountdownSettingInput>({
      pomodoro: "",
      rest: "",
      cycles: "",
      ringtoneName: "",
    });
  const [state, setState] = useState<string>("");
  const [remainTime, setRemainTime] = useState<number>(0);
  const subTick = useRef<any>(null);
  const subStateChange = useRef<any>(null); //todo: try to update type
  const subRingToneChange = useRef<any>(null);
  const [curCycle, setCurCycle] = useState<number>(1);
  const [stateCycles, setStateCycles] = useState<number>(0);
  useEffect(() => {
    var setting = CountdownModule.getCountdownSetting();
    setCountdownSetting({
      pomodoro: setting.pomodoro.toString(),
      rest: setting.rest.toString(),
      cycles: setting.cycles.toString(),
      ringtoneName: setting.ringtoneName,
    });
    subTick.current = CountdownModule.addListener(
      Event.TICK,
      (data: { remainTime: number }) => {
        const { remainTime } = data;
        setRemainTime(remainTime);
      }
    );
    subRingToneChange.current = CountdownModule.addListener(
      Event.RINGTONE_CHANGE,
      (data: { title: string }) => {
        setCountdownSetting((prev) => ({
          ...prev,
          ringtoneName: data.title,
        }));
      }
    );
    subStateChange.current = CountdownModule.addListener(
      Event.STATECHANGE,
      (data: StateChangeData) => {
        const {
          state: data_state,
          curCycle: data_curCycle,
          cycles: data_cycles,
        } = data;
        setState(data_state);
        setCurCycle(data_curCycle);
        setStateCycles(data_cycles);
        if (data_state == StateEnum.STOP) {
          setIsStarted(false);
        } else {
          setIsStarted(true);
        }
      }
    );

    return () => {
      subStateChange.current.remove();
      subTick.current.remove();
      subRingToneChange.current.remove();
    };
  }, []);

  function updateSetting() {
    const pomodoro = countdownSetting.pomodoro;
    const rest = countdownSetting.rest;
    const cycles = countdownSetting.cycles;
    if (
      checkInRange(pomodoro, 1, 999) &&
      checkInRange(rest, 0, 999) &&
      checkInRange(cycles, 1, 999)
    ) {
      CountdownModule.updateSetting({
        pomodoro: Number(pomodoro),
        rest: Number(rest),
        cycles: Number(cycles),
        ringtoneName: "",
      });
    } else {
      var setting = CountdownModule.getCountdownSetting();
      setCountdownSetting((prev) => ({
        ...prev,
        pomodoro: setting.pomodoro.toString(),
        rest: setting.rest.toString(),
        cycles: setting.cycles.toString(),
      }));
    }
  }

  function start() {
    updateSetting();
    CountdownModule.startCountdown({
      pomodoro: Number(countdownSetting.pomodoro),
      rest: Number(countdownSetting.rest),
      cycles: Number(countdownSetting.cycles),
    });
  }
  function stop() {
    CountdownModule.stopCountdown();
    setRemainTime(0);
    setIsStarted(false);
  }
  return (
    <View className="flex-1">
      <View className="items-center py-10">
        <ClockDisplay remainTime={remainTime}></ClockDisplay>
      </View>
      <View className={`items-center mb-6 ${!isStarted ? "hidden" : ""}`}>
        <PomodoroStatus
          state={state}
          curCycle={curCycle}
          cycles={stateCycles}
        ></PomodoroStatus>
      </View>
      <View className="items-center max-h-16 mb-10">
        {!isStarted ?
          <StartStopButton isStarted={isStarted} onPress={start}></StartStopButton>
        : 
          <StartStopButton isStarted={isStarted} onPress={stop}></StartStopButton>
        }
      </View>
      <View className="items-center">
        <CountdownInput
          value={countdownSetting}
          onChange={setCountdownSetting}
        ></CountdownInput>
      </View>
      <View></View>
      <Record></Record>
    </View>
  );
}
