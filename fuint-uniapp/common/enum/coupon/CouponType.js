import Enum from '../enum'

/**
 * 枚举类：卡券类型
 * CouponTypeEnum
 */
export default new Enum([
  { key: 'C', name: '优惠券', value: 1000 },
  { key: 'P', name: '预存卡', value: 2000 },
  { key: 'T', name: '集次卡', value: 3000 }
])
