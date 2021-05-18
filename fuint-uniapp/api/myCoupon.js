import request from '@/utils/request'

// api地址
const api = {
  list: 'myCoupon/list',
  detail: 'userCouponApi/detail'
}

// 我的卡券列表
export const list = (param, option) => {
  const options = {
    isPrompt: true, //（默认 true 说明：本接口抛出的错误是否提示）
    load: true, //（默认 true 说明：本接口是否提示加载动画）
    ...option
  }
  return request.get(api.list, param, options)
}

// 我的卡券详情
export function detail(userCouponId) {
  return request.get(api.detail, { userCouponId })
}