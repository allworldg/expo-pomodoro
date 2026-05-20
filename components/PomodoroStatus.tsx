import { FOCUSING_STR, RESTING_STR } from "@/constants/Constants";
import { Text, View } from "react-native";
type Props = {
  state: string;
  curCycle: number;
  cycles: number;
};
export default function PomodoroStatus({ state, curCycle, cycles }: Props) {
  const stateStr = state === "focusing" ? FOCUSING_STR : RESTING_STR;

  return (
    <View className="items-center">
      <Text className="text-xl mb-2">{stateStr}</Text>
      <Text>
        当前第 {curCycle} 轮/共 {cycles} 轮
      </Text>
    </View>
  );
}
