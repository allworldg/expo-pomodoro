import { requireNativeView } from 'expo';
import * as React from 'react';

import { CountdownViewProps } from './Countdown.types';

const NativeView: React.ComponentType<CountdownViewProps> =
  requireNativeView('Countdown');

export default function CountdownView(props: CountdownViewProps) {
  return <NativeView {...props} />;
}
