/**
 * BPM 业务表单插件初始化
 * 在应用启动时注册默认的业务表单配置
 */

import {App} from 'vue'
import {initDefaultBusinessForms} from '@/utils/bpm/businessFormManager'

export default {
  install(app: App) {
    // 初始化默认业务表单配置
    initDefaultBusinessForms()

    // 可以在这里添加全局BPM相关的配置
  }
}
