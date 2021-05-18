import request from '@/utils/request'

// api地址
const api = {
  submit: 'settlement/submit',
}

// 结算台订单提交
export const submit = (couponId, selectNum, type, remark) => {
  return request.post(api.submit, { couponId, selectNum, type, remark})
}
