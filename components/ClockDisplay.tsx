import { MINUTE, SECOND } from "@/constants/Constants";
import { Text, View } from "react-native";
type Props = {
  remainTime: number;
};

export default function ClockDisplay({ remainTime }: Props) {
  const minute_format = Math.floor(remainTime / MINUTE)
    .toString()
    .padStart(2, "0");
  const second_format = Math.floor((remainTime % MINUTE) / SECOND)
    .toString()
    .padStart(2, "0");
  return (
    <View>
      <Text className="text-7xl">
        {minute_format}:{second_format}
      </Text>
    </View>
  );
}
