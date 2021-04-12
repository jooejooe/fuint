<template>
  <view v-if="!isLoading" class="container b-f p-b">
    <view class="coupon-title">
      <text class="title">{{ detail.title }}</text>
	  <view class="amount">
		  <view>原始金额：<b>￥1000</b></view>
		  <view>剩余金额：<b>￥600</b></view>
	  </view>
	  <view class="time">有效期：2021-04-09 至 2022-09-10</view>
    </view>
    <view class="coupon-qr">
      <view>
         <image class="image" :src="detail.qr"></image>
      </view>
      <view class="qr-code">
          <p class="code">卡号：202109876309</p>
		  <p class="tips">请出示以上卡号给核销人员</p>
      </view>
    </view>
    <view class="coupon-content m-top20">
		<view class="title">使用须知</view>
        <view class="content"><jyf-parser :html="detail.content"></jyf-parser></view>
    </view>
    <!-- 快捷导航 -->
    <shortcut />
  </view>
</template>

<script>
  import jyfParser from '@/components/jyf-parser/jyf-parser'
  import Shortcut from '@/components/shortcut'

  export default {
    components: {
      Shortcut
    },
    data() {
      return {
        // 当前文章ID
        articleId: null,
        // 加载中
        isLoading: true,
        // 当前文章详情
        detail: null
      }
    },

    /**
     * 生命周期函数--监听页面加载
     */
    onLoad(options) {
      // 记录文章ID
      this.articleId = options.articleId
      // 获取文章详情
      this.getArticleDetail()
    },

    methods: {

      // 获取优惠券详情
      getArticleDetail() {
        const app = this
        app.isLoading = false
		app.detail = {"title":"大众4S店5月份预存1000元卡",
		              "qr":"\/static\/coupon\/qr.png",
		              "content":"<p>1、凭本卡券到适用网点可享受车辆洗车服务一次，每日限用1张，若超标使用导致账户锁定我司不予处理。</p></br><p>2、使用本卡券前，需点击查看最新的“适用网点”，并致电网点预约服务时间，错峰到店。因服务网点实时变动，如您未查看【适用网点】，而前往不合作的网点使用服务，或未提前预约导致您行程耽误的，期间产生的损失与费用，我司概不负责。</p></br><p>3、 本卡券禁止外借或转赠他人，若发现我司有权回收权益，卡券使用最终解释权归平安产险海南分公司所有。</p></br><p><b>内容介绍</b></p><p>【先预约，再服务】春节期间（2021年1月21日—2021年2月27日），网点洗车人数剧增，排队等待时间较长，使用服务前请先查询可适用网点，致电网点预约服务时间，错峰到店（点开任意一家门店后可在“电话按钮”处点开服务电话），由此给您造成不便，敬请谅解！</p>",
					  "show_views":"100",
					  "view_time":"2021-04-09"}
        /*ArticleApi.detail(app.articleId)
          .then(result => {
            app.detail = result.data.detail
          })
          .finally(() => app.isLoading = false)*/
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
