import type {App} from 'vue'
import {Icon} from './Icon'
import BusinessModuleSelector from './BusinessModuleSelector/index.vue'

export const setupGlobCom = (app: App<Element>): void => {
  app.component('Icon', Icon)
  app.component('BusinessModuleSelector', BusinessModuleSelector)
}
