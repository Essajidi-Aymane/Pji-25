const base64 = {
  encode: (bytes) => btoa(String.fromCharCode(...new Uint8Array(bytes))),
};

const JsonUtil = {
  encodeElementArrayMap(map) {
    const encoded = {};
    for (const [key, arr] of Object.entries(map)) {
      encoded[key] = arr.map(e => base64.encode(e));
    }
    return encoded;
  },

  encodeElementArray(arr) {
    return arr.map(e => base64.encode(e));
  }
};

export default JsonUtil;
