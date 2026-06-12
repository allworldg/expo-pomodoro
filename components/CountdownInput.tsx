import { CountdownSettingInput } from "@/modules/countdown";
import CountdownModule from "@/modules/countdown/src/CountdownModule";
import { Pressable, Text, TextInput, View } from "react-native";

type Props = {
  value: CountdownSettingInput;
  onChange: (setting: CountdownSettingInput) => void;
};

export default function CountdownInput({ value, onChange }: Props) {
  return (
    <View>
      <View className="flex-row mb-6">
        <Text>番茄: </Text>
        <TextInput
          className="border-b py-0 ml-2 w-14  text-center"
          value={value.pomodoro}
          maxLength={4}
          onChangeText={(text) =>
            onChange({
              ...value,
              pomodoro: text,
            })
          }
        ></TextInput>
        <Text className="ml-2">分钟</Text>
      </View>
      <View className="flex-row mb-6">
        <Text>休息: </Text>
        <TextInput
          className="border-b py-0 ml-2 w-14 text-center"
          value={value.rest}
          maxLength={4}
          onChangeText={(text) =>
            onChange({
              ...value,
              rest: text,
            })
          }
        ></TextInput>
        <Text className="ml-2">分钟</Text>
      </View>
      <View className="flex-row mb-6">
        <Text>循环: </Text>
        <TextInput
          className="border-b py-0 ml-2 w-14 text-center"
          value={value.cycles}
          maxLength={4}
          onChangeText={(text) =>
            onChange({
              ...value,
              cycles: text,
            })
          }
        ></TextInput>
        <Text className="ml-2">次</Text>
      </View>
      <Pressable
        className="flex-row items-center active:bg-gray-200"
        onPress={() => CountdownModule.selectMusic()}
      >
        <Text>铃声: </Text>
        <View className="border-b ml-2">
          <Text className="py-0 w-14 text-center leading-none">
            {value.ringtoneName}
          </Text>
        </View>
      </Pressable>
    </View>
  );
}
