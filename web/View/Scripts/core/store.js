const state = {
  session: null,
  route: null,
  theme: "sky",
};

const listeners = new Set();

function getState() {
  return {
    ...state,
    session: state.session ? { ...state.session } : null,
  };
}

function setState(partial) {
  Object.assign(state, partial);
  const snapshot = getState();
  listeners.forEach((listener) => listener(snapshot));
}

function subscribe(listener) {
  listeners.add(listener);
  return () => listeners.delete(listener);
}

export const store = {
  getState,
  setState,
  subscribe,
};
