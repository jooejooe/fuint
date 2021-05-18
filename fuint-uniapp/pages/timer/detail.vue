<template>
  <view v-if="!isLoading" class="container b-f p-b">
    <view class="coupon-title">
      <text class="name f-32">{{ detail.name }}</text>
	  <view class="time">有效期：{{ detail.effectiveDate }}</view>
    </view>
	<view class="coupon-timer">
	  <view class="tips">完成情况</view>
	  <view class="time active">1√</view>
	  <view class="time active">2√</view>
	  <view class="time active">3√</view>
	  <view class="time">4×</view>
	  <view class="time">5×</view>
	  <view class="time">6×</view>
	  <view class="time">7×</view>
	  <view class="time">8×</view>
	</view>
    <view class="coupon-qr">
      <view>
         <image class="image" :src="detail.qrCode"></image>
      </view>
      <view class="qr-code">
          <p class="code">兑换码：{{ detail.code }}</p>
		  <p class="tips">请出示以上券码给核销人员</p>
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
        // 当前会员卡券ID
        userCouponId: null,
        // 加载中
        isLoading: true,
        // 当前卡券详情
        detail: null
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
        app.isLoading = false
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
	color:#666666;
  }
  .coupon-title {
	  border: dashed 5rpx #cccccc;
	  padding: 30rpx;
	  border-radius: 10rpx;
	  .name {
	  	font-weight: bold;
	  }
	  .tips {
	  	margin-top:20rpx;
	  	color:#e49a3d;
	  	font-size: 20rpx;
	  }
	  .time {
		margin-top: 20rpx;
		font-size: 20rpx;
		color: #666666;
	  }
  }
  .coupon-timer {
	  border: dashed 5rpx #cccccc;
	  margin-top:20rpx;
	  padding: 5px 30rpx 5rpx 30rpx;
	  border-radius: 10rpx;
	  overflow:auto;
	  .tips{
		  margin-bottom: 10rpx;
	  }
	  .time {
		  width: 72rpx;
		  height: 72rpx;
		  border: solid 1px #cccccc;
		  float: left;
		  margin-right: 20rpx;
		  margin-bottom: 20rpx;
		  text-align: center;
		  padding-top: 20rpx;
		  color: #ffffff;
	  }
	  .active{
		  background: #73D661;
	  }
	  min-height: 160rpx;
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
