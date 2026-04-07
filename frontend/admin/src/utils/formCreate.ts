/**
 * 针对 https://github.com/xaboy/form-create-designer 封装的工具类
 */
import {isRef} from 'vue'

// 编码表单 Conf
export const encodeConf = (designerRef: object) => {
  // @ts-ignore
  return JSON.stringify(designerRef.value.getOption())
}

// 编码表单 Fields
export const encodeFields = (designerRef: object) => {
  // @ts-ignore
  const rule = JSON.parse(designerRef.value.getJson())
  const fields: string[] = []
  rule.forEach((item) => {
    fields.push(JSON.stringify(item))
  })
  return fields
}

// 解码表单 Fields
export const decodeFields = (fields: string[]) => {
  const rule: object[] = []
  fields.forEach((item) => {
    rule.push(JSON.parse(item))
  })
  return rule
}

// 设置表单的 Conf 和 Fields，适用 FcDesigner 场景
export const setConfAndFields = (designerRef: object, conf: string, fields: string) => {
  // @ts-ignore
  designerRef.value.setOption(JSON.parse(conf))
  // @ts-ignore
  designerRef.value.setRule(decodeFields(fields))
}

// 设置表单的 Conf 和 Fields，适用 form-create 场景
export const setConfAndFields2 = (
  detailPreview: object,
  conf: string,
  fields: string[],
  value?: object
) => {
  if (isRef(detailPreview)) {
    // @ts-ignore
    detailPreview = detailPreview.value
  }

  // 修复所有函数类型（解决设计器保存后函数变成字符串的问题）。例如说：
  // https://t.zsxq.com/rADff
  // https://t.zsxq.com/ZfbGt
  // https://t.zsxq.com/mHOoj
  // https://t.zsxq.com/BSylB
  const option = JSON.parse(conf)
  const rule = decodeFields(fields)
  // 🔧 修复所有函数类型 - 解决设计器保存后函数变成字符串的问题
  const fixFunctions = (obj: any) => {
    if (obj && typeof obj === 'object') {
      Object.keys(obj).forEach((key) => {
        // 检查是否是函数相关的属性
        if (isFunctionProperty(key)) {
          // 如果不是函数类型，重新构建为函数
          if (typeof obj[key] !== 'function') {
            obj[key] = createDefaultFunction(key)
          }
        } else if (typeof obj[key] === 'object' && obj[key] !== null) {
          // 递归处理嵌套对象
          fixFunctions(obj[key])
        }
      })
    }
  }
  // 判断是否是函数属性
  const isFunctionProperty = (key: string): boolean => {
    const functionKeys = [
      'beforeFetch', // 请求前处理
      'afterFetch', // 请求后处理
      'onSubmit', // 表单提交
      'onReset', // 表单重置
      'onChange', // 值变化
      'onInput', // 输入事件
      'onClick', // 点击事件
      'onFocus', // 获取焦点
      'onBlur', // 失去焦点
      'onMounted', // 组件挂载
      'onCreated', // 组件创建
      'onReload', // 重新加载
      'remoteMethod', // 远程搜索方法
      'parseFunc', // 解析函数
      'validator', // 验证器
      'asyncValidator', // 异步验证器
      'formatter', // 格式化函数
      'parser', // 解析函数
      'beforeUpload', // 上传前处理
      'onSuccess', // 成功回调
      'onError', // 错误回调
      'onProgress', // 进度回调
      'onPreview', // 预览回调
      'onRemove', // 移除回调
      'onExceed', // 超出限制回调
      'filterMethod', // 过滤方法
      'sortMethod', // 排序方法
      'loadData', // 加载数据
      'renderContent', // 渲染内容
      'render' // 渲染函数
    ]
    // 检查是否以函数相关前缀开头
    const functionPrefixes = ['on', 'before', 'after', 'handle']
    return functionKeys.includes(key) || functionPrefixes.some((prefix) => key.startsWith(prefix))
  }
  // 根据函数名创建默认函数
  const createDefaultFunction = (key: string): Function => {
    switch (key) {
      case 'beforeFetch':
        return (config: any) => {
          // 添加 Token 认证头。例如说：
          // https://t.zsxq.com/hK3FO
          const token = localStorage.getItem('token')
          if (token) {
            config.headers = {
              ...config.headers,
              Authorization: 'Bearer ' + token
            }
          }
          // 添加通用请求头
          config.headers = {
            ...config.headers,
            'Content-Type': 'application/json',
            'X-Requested-With': 'XMLHttpRequest'
          }
          // 添加时间戳防止缓存
          config.params = {
            ...config.params,
            _t: Date.now()
          }
          return config
        }
      case 'afterFetch':
        return (data: any) => {
          return data
        }
      case 'onSubmit':
        return (_formData: any) => {
          return true
        }
      case 'onReset':
        return () => {
          return true
        }
      case 'onChange':
        return (_value: any, _oldValue: any) => {}
      case 'remoteMethod':
        return (query: string) => {
          console.log('remoteMethod被调用:', query)
        }
      case 'parseFunc':
        return (data: any) => {
          // 默认解析逻辑：如果是数组直接返回，否则尝试获取list属性
          if (Array.isArray(data)) {
            return data
          }
          return data?.list || data?.data || []
        }
      case 'validator':
        return (_rule: any, _value: any, callback: Function) => {
          callback()
        }
      case 'beforeUpload':
        return (_file: any) => {
          return true
        }
      default:
        // 通用默认函数
        return (...args: any[]) => {
          // 对于事件处理函数，返回true表示继续执行
          if (key.startsWith('on') || key.startsWith('handle')) {
            return true
          }
          // 对于其他函数，返回第一个参数（通常是数据传递）
          return args[0]
        }
    }
  }
  // 修复 option 中的所有函数
  fixFunctions(option)
  // 修复 rule 中的所有函数（包括组件的 props）
  if (Array.isArray(rule)) {
    rule.forEach((item: any) => {
      fixFunctions(item)
    })
  }

  // @ts-ignore
  detailPreview.option = option
  // @ts-ignore
  detailPreview.rule = rule

  if (value) {
    // @ts-ignore
    detailPreview.value = value
  }
}
