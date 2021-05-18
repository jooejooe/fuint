<template>
  <view class="container">
    <view class="info-list">
      <view class="info-item">
        <view class="contacts">
          <text class="name">姓名</text>
          <text class="value">{{userInfo.realName ? userInfo.realName : '未知'}}</text>
        </view>
      </view>
	  <view class="info-item">
	    <view class="contacts">
	      <text class="name">手机</text>
	      <text class="value">{{userInfo.mobile ? userInfo.mobile : '未设置'}}</text>
	    </view>
	  </view>
	  <view class="info-item">
	    <view class="contacts">
	      <text class="name">性别</text>
	      <text class="value">{{userInfo.sex === 1 ? '女' : '男'}}</text>
	    </view>
	  </view>
	  <view class="info-item">
	    <view class="contacts">
	      <text class="name">生日</text>
	      <text class="value">{{userInfo.birthday ? userInfo.birthday : '未知'}}</text>
	    </view>
	  </view>
    </view>
    <!-- 底部操作按钮 -->
    <view class="footer-fixed">
      <view class="btn-wrapper">
        <view class="btn-item btn-item-main" @click="logout()">退出登录</view>
      </view>
    </view>
  </view>
</template>

<script>
  import * as UserApi from '@/api/user'
  import store from '@/store'

  export default {
    data() {
      return {
        //当前页面参数
        options: {},
        // 正在加载
        isLoading: true,
        userInfo: {},
      }
    },

    /**
     * 生命周期函数--监听页面加载
     */
    onLoad(options) {
      // 当前页面参数
      this.options = options
	  this.getUserInfo()
    },

    methods: {
	  /**
	   * 用户信息
	   * */
      getUserInfo() {
        const app = this
        app.isLoading = true
        UserApi.info()
          .then(result => {
            app.userInfo = result.data.userInfo
            app.isLoading = false
          })
      },
	  
      /**
       * 退出
       */
      logout() {
        store.dispatch('Logout')
		this.$navTo('pages/user/index')
      }
    }
  }
</script>

<style lang="scss" scoped>
  .info-list {
    padding-bottom: 120rpx;
	margin-top: 25rpx;
  }

  // 项目内容
  .info-item {
    margin: 20rpx auto 20rpx auto;
    padding: 30rpx 40rpx;
    width: 94%;
    box-shadow: 0 1rpx 5rpx 0px rgba(0, 0, 0, 0.05);
    border-radius: 16rpx;
    background: #fff;
  }

  .contacts {
    font-size: 30rpx;
    .name {
      margin-left:0px;
    }
	.value {
		float:right;
		color:#999999;
	}
  }

  .item-option {
    display: flex;
    justify-content: space-between;
    height: 48rpx;
  }


  // 底部操作栏
  .footer-fixed {
    height: 96rpx;
    z-index: 11;
    box-shadow: 0 -4rpx 40rpx 0 rgba(144, 52, 52, 0.1);
    .btn-wrapper {
      height: 100%;
      display: flex;
      align-items: center;
      padding: 0 20rpx;
    }

    .btn-item {
      flex: 1;
      font-size: 28rpx;
      height: 72rpx;
      line-height: 72rpx;
      text-align: center;
      color: #fff;
      border-radius: 50rpx;
    }

    .btn-item-main {
      background: linear-gradient(to right, #f9211c, #ff6335);
    }

  }
</style>
