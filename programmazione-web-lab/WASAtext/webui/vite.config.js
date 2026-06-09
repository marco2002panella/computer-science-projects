import {fileURLToPath, URL} from 'node:url'
import {defineConfig} from 'vite'
import vue from '@vitejs/plugin-vue'

// https://vitejs.dev/config/
export default defineConfig(({command, mode, ssrBuild}) => {
    const ret = {
        plugins: [vue()],
        resolve: {
            alias: {
                '@': fileURLToPath(new URL('./src', import.meta.url))
            }
        },
    };
    
    ret.define = {
        // La costante del professore
        "__API_URL__": JSON.stringify("http://localhost:3000"),
        // Aggiungi questa riga per risolvere il warning di Vue
        "__VUE_PROD_HYDRATION_MISMATCH_DETAILS__": JSON.stringify(false)
    };
    
    return ret;
})