import { NativeModule, registerWebModule } from 'expo';

import { ChangeEventPayload } from './Countdown.types';

type CountdownModuleEvents = {
  onChange: (params: ChangeEventPayload) => void;
}

class CountdownModule extends NativeModule<CountdownModuleEvents> {
  PI = Math.PI;
  async setValueAsync(value: string): Promise<void> {
    this.emit('onChange', { value });
  }
  hello() {
    return 'Hello world! 👋';
  }
};

export default registerWebModule(CountdownModule, 'CountdownModule');
