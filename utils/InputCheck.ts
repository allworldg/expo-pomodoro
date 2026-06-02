export function checkInRange(value: string, min: number, max: number): boolean {
  if (typeof value !== "string" || value.trim() === "" || isNaN(value as any)) {
    return false;
  }
  const number = parseInt(value);
  return number >= min && number <= max;
}
