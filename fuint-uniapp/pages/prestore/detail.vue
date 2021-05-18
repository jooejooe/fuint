<template>
  <view v-if="!isLoading" class="container b-f p-b">
    <view class="coupon-title">
      <text class="title">{{ detail.name }}</text>
	  <view class="amount">
		  <view>原始金额：<b>￥{{ detail.amount }}</b></view>
		  <view>剩余金额：<b>￥{{ detail.balance }}</b></view>
	  </view>
	  <view class="time">有效期：{{ detail.effectiveDate }}</view>
    </view>
    <view class="coupon-qr">
      <view>
         <image class="image" :src="detail.qrCode"></image>
      </view>
      <view class="qr-code">
          <p class="code">卡号：{{ detail.code }}</p>
		  <p class="tips">请出示以上卡号给核销人员</p>
      </view>
    </view>
    <view class="coupon-content m-top20">
		<view class="title">使用须知</view>
        <view class="content"><jyf-parser :html="detail.description"></jyf-parser></view>
    </view>
    <!-- 快捷导航 -->
    <shortcut />
  </view>
</template>

<script>
  import jyfParser from '@/components/jyf-parser/jyf-parser'
  import Shortcut from '@/components/shortcut'
  import * as myCouponApi from '@/api/myCoupon'

  export default {
    components: {
      Shortcut
    },
    data() {
      return {
        // 卡券ID
        couponId: null,
        // 加载中
        isLoading: true,
        // 卡券详情
        detail: null,
		qrCode: ""
      }
    },

    /**
     * 生命周期函数--监听页面加载
     */
    onLoad(options) {
      // 记录ID
      this.userCouponId = options.id
      // 获取卡券详情
      this.getCouponDetail()
    },

    methods: {

      // 获取卡券详情
      getCouponDetail() {
        const app = this
        myCouponApi.detail(app.userCouponId)
          .then(result => {
            app.detail = result.data
          })
          .finally(() => app.isLoading = false)
      }

    }
  }
</script>

<style lang="scss" scoped>
  .container {
    min-height: 100vh;
    padding: 20rpx;
    background: #fff;
  }
  .coupon-title {
	  border: dashed 5rpx #cccccc;
	  padding: 30rpx;
	  border-radius: 10rpx;
	  .title{
		  font-weight: bold;
		  font-size: 30rpx;
	  }
	  .amount{
		  margin-top:30rpx;
		  view{
			  margin-top: 10rpx;
			  font-size: 30rpx;
		  }
	  }
	  .time {
		  margin-top: 20rpx;
		  font-size: 25rpx;
		  color: #666666;
	  }
  }
  .coupon-qr{
	  border: dashed 5rpx #cccccc;
	  border-radius: 10rpx;
	  margin-top: 20rpx;
	  text-align: center;
	  padding-top: 80rpx;
	  padding-bottom: 30rpx;
	  .image{
		  width: 360rpx;
		  height: 360rpx;
		  margin: 0 auto;
	  }
	  .qr-code{
		  .code{
			  font-weight: bold;
			  font-size: 30rpx;
			  line-height: 50rpx;
		  }
		  .tips{
			  font-size: 25rpx;
			  color:#C0C4CC;
		  }
	  }
  }

  .coupon-content {
    font-size: 28rpx;
  }
</style>
