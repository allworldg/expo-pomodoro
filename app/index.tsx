import "@/global.css";
import CountdownModule from "@/modules/countdown/src/CountdownModule";
import { useState } from "react";
import { Button, Text, View } from "react-native";

export default function Index() {
  const[minute,setMinute] = useState(0);
  const[second,setSecond] = useState(0)
  return (
    <View
    
      style={{
        flex: 1,
        justifyContent: "center",
        alignItems: "center",
      }}
    >
    <Text className="text-3xl">{CountdownModule.hello()}</Text>
    <Button title="click me" onPress={()=>{
      CountdownModule.fker()
    }}></Button>
    <Button title="asyncbtn" onPress={()=>{
      CountdownModule.setValueAsync("hello")
      CountdownModule.addListener("onChange",(event)=>{
        console.log("onChange",event.value)
      })
    }}></Button>
    </View>
  );
}
