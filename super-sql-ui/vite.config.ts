import { defineConfig } from 'vite'
import vue from '@vitejs/plugin-vue'
import * as path from 'path'
// 新增
const resolve = (p: string) => {
  return path.resolve(__dirname, p);
}

export default({command,mode}:any) => {
  return defineConfig({
    resolve: {
      alias: {
        '@': resolve('./src')
      }
    },
    plugins: [vue()],

  })
}