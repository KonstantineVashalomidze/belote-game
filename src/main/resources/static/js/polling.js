export function startPolling(fn, interval = 2000) {
  fn();
  const id = setInterval(fn, interval);
  return () => clearInterval(id);
}
