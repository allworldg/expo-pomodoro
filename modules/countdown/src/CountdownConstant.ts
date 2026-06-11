export const Event = {
  TICK: "tick",
  STATECHANGE: "stateChange",
  RECORD: "record",
  RINGTONE_CHANGE:"ringtoneChange",
} as const;

export const MILLISECOND = 1;
export const SECOND = 1000 * MILLISECOND;
export const MINUTE = 60 * SECOND;
