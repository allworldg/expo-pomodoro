// Reexport the native module. On web, it will be resolved to CountdownModule.web.ts
// and on native platforms to CountdownModule.ts
export { default } from './src/CountdownModule';
export { default as CountdownView } from './src/CountdownView';
export * from  './src/Countdown.types';
