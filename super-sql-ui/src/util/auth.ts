const TOKEN_KEY = 'satoken';

const isLogin = () => {
  return !!localStorage.getItem(TOKEN_KEY);
};

const getToken = () => {
  return localStorage.getItem(TOKEN_KEY);
};

const setToken = (token: string) => {
  localStorage.setItem(TOKEN_KEY,token);
};

const clearToken = () => {
  localStorage.removeItem(TOKEN_KEY);
};

export { isLogin, getToken, setToken, clearToken };
