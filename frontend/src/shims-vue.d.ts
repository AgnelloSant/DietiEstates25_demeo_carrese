// Questo dice a TypeScript: “Ogni file .vue può essere importato come un componente Vue”.

declare module '*.vue' {
  import { DefineComponent } from 'vue'
  const component: DefineComponent<{}, {}, any>
  export default component
}
