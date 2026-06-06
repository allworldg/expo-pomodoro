export const Event = {
  TICK: "tick",
  STATECHANGE: "stateChange",
  RECORD: "record",
} as const;

export const MILLISECOND = 1;
export const SECOND = 1000 * MILLISECOND;
export const MINUTE = 60 * SECOND;
