import "@/global.css";
import { useState } from "react";
import { Text, TextInput, View } from "react-native";
import Svg, { Path } from "react-native-svg";

export default function Index() {
  const [minute, setMinute] = useState(0);
  const [second, setSecond] = useState(0);
  const [isStarted, setIsStarted] = useState(false);
  const [pomodoro, setPomodoro] = useState<string>("1");
  const [rest, setRest] = useState<string>("0");
  const [totalLoops, setTotalLoops] = useState<string>("0");
  const minute_format = minute.toString().padStart(2, "0");
  const second_format = second.toString().padStart(2, "0");

  function start() {
    setIsStarted(!isStarted);
  }
  return (
    <View className="flex-1">
      <View className="items-center py-10">
        <Text className="text-7xl">
          {minute_format}:{second_format}
        </Text>
      </View>
      <View className={`items-center mb-6 ${!isStarted? "hidden" : ""}`}>
        <View>
          <Text>正在专注（状态）</Text>
          <Text>当前第x轮/共x轮</Text>
        </View>
      </View>
      <View className="items-center max-h-16 mb-5">
        {!isStarted ?
          <Svg
            width={"50%"}
            height={"100%"}
            viewBox="0 0 1024 1024"
            onPress={() => {
              start();
            }}
          >
            <Path
              d="M512 938.666667a426.666667 426.666667 0 1 1 426.666667-426.666667 426.666667 426.666667 0 0 1-426.666667 426.666667zM450.133333 305.92A42.666667 42.666667 0 0 0 384 341.333333v341.333334a42.666667 42.666667 0 0 0 66.133333 35.413333l256-170.666667a42.666667 42.666667 0 0 0 0-70.826666z"
              fill="#FFFFFF"
              p-id="1164"
            ></Path>
            <Path
              d="M512 938.666667a426.666667 426.666667 0 1 1 426.666667-426.666667 426.666667 426.666667 0 0 1-426.666667 426.666667zM450.133333 305.92A42.666667 42.666667 0 0 0 384 341.333333v341.333334a42.666667 42.666667 0 0 0 66.133333 35.413333l256-170.666667a42.666667 42.666667 0 0 0 0-70.826666z"
              fill="#0C66FF"
              p-id="1165"
            ></Path>
          </Svg>
        : <Svg
            width={"50%"}
            height={"100%"}
            viewBox="0 0 1024 1024"
            onPress={() => start()}
          >
            <Path
              d="M512 938.666667a426.666667 426.666667 0 1 1 426.666667-426.666667 426.666667 426.666667 0 0 1-426.666667 426.666667zM395.52 356.693333a38.826667 38.826667 0 0 0-38.826667 38.826667v232.533333a38.826667 38.826667 0 0 0 38.826667 38.826667h232.533333a38.826667 38.826667 0 0 0 38.826667-38.826667V395.52a38.826667 38.826667 0 0 0-38.826667-38.826667z"
              fill="#FFFFFF"
              p-id="1014"
            ></Path>
            <Path
              d="M512 938.666667a426.666667 426.666667 0 1 1 426.666667-426.666667 426.666667 426.666667 0 0 1-426.666667 426.666667zM395.52 356.693333a38.826667 38.826667 0 0 0-38.826667 38.826667v232.533333a38.826667 38.826667 0 0 0 38.826667 38.826667h232.533333a38.826667 38.826667 0 0 0 38.826667-38.826667V395.52a38.826667 38.826667 0 0 0-38.826667-38.826667z"
              fill="#0C66FF"
              p-id="1015"
            ></Path>
          </Svg>
        }
      </View>
      <View className="items-center">
        <View>
          <View className="flex-row mb-6">
            <Text>番茄: </Text>
            <TextInput
              className="border-b py-0 ml-2 w-14  text-center"
              value={pomodoro}
              maxLength={4}
              onChangeText={setPomodoro}
            ></TextInput>
            <Text className="ml-2">分钟</Text>
          </View>
          <View className="flex-row mb-6">
            <Text>休息: </Text>
            <TextInput
              className="border-b py-0 ml-2 w-14 text-center"
              value={rest}
              onChangeText={setRest}
              maxLength={4}
            ></TextInput>
            <Text className="ml-2">分钟</Text>
          </View>
          <View className="flex-row mb-6">
            <Text>循环: </Text>
            <TextInput
              className="border-b py-0 ml-2 w-14 text-center"
              value={totalLoops}
              onChangeText={setTotalLoops}
              maxLength={4}
            ></TextInput>
            <Text className="ml-2">次</Text>
          </View>
        </View>
      </View>
      <View className="mt-auto">
        <Text>当日专注次数: x</Text>
        <Text>时长：xx分钟</Text>
        <Text>总时长：x小时x分钟</Text>
      </View>
    </View>
  );
}
