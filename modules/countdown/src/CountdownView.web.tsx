import * as React from 'react';

import { CountdownViewProps } from './Countdown.types';

export default function CountdownView(props: CountdownViewProps) {
  return (
    <div>
      <iframe
        style={{ flex: 1 }}
        src={props.url}
        onLoad={() => props.onLoad({ nativeEvent: { url: props.url } })}
      />
    </div>
  );
}
