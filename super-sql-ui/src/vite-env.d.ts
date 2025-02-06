// / <reference types="vite/client" />
declare module "*.vue" {
    import { defineComponent } from "vue";
    const component: ReturnType<typeof defineComponent>;
    export default component;
}
interface ImportMetaEnv {
    readonly VITE_API_BASE_URL: string;
}

interface ImportMeta {
    url: string

    readonly env: ImportMetaEnv
}