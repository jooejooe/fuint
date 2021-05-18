import request from '@/utils/request'

// api地址
const api = {
  list: 'coupon/list',
  receive: 'coupon/receive',
  detail: 'coupon/detail'
}

// 卡券列表
export const list = (param, option) => {
  const options = {
    isPrompt: true, //（默认 true 说明：本接口抛出的错误是否提示）
    load: true, //（默认 true 说明：本接口是否提示加载动画）
    ...option
  }
  return request.get(api.list, param, options)
}

// 领券接口
export const receive = (couponId) => {
  return request.post(api.receive, { couponId })
}

// 会员卡券详情
export function detail(couponId) {
  return request.get(api.detail, { couponId })
}
