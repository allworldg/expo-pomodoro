export const EventType = {
    TICK:"tick",
    STATECHANGE:"stateChange",
    STOP:"stop"
} as const;

export const MILLISECOND = 1;
export const SECOND = 1000 *MILLISECOND;
export const MINUTE = 60 * SECOND;
