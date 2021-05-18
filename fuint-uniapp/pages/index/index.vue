<template>
  <view class="container">
	  <block>
	      <Search :itemStyle="options.searchStyle" :params="options.searchParam" />
	  </block>
	  <block>
	      <Banner :itemStyle="options.bannerStyle" :params="options.bannerParam" :dataList="banner" />
	  </block>
	  <block>
	      <Blank :itemStyle="options.blankStyle" />
	  </block>
	  <block>
	      <NavBar :itemStyle="options.navStyle" :params="{}" :dataList="options.navBar" />
	  </block>
	  <block>
	      <Blank :itemStyle="options.blankStyle" />
	  </block>
	  <block>
	      <Coupon :itemStyle="options.goodsStyle" :params="options.goodsParams" :dataList="coupon" />
	  </block>
  </view>
</template>

<script>
  import { setCartTabBadge } from '@/utils/app'
  import Search from '@/components/page/search'
  import Banner from '@/components/page/banner'
  import NavBar from '@/components/page/navBar'
  import Blank from '@/components/page/blank'
  import Coupon from '@/components/page/coupon'
  import * as Api from '@/api/page'

  const App = getApp()
  
  export default {
	components: {
	   Search,
	   Banner,
	   NavBar,
	   Blank,
	   Coupon
	},
    data() {
      return {
        options: {
			"searchStyle": {
				"textAlign": "left",
				"searchStyle": "radius",
			},
			"searchParam": {
				"placeholder": "搜索卡券",
			},
			"blankStyle": {
				"height": "5",
				"background": "#ffffff",
			},
			"navBar": [{
						"imgUrl": "/static/nav/1.png",
						"imgName": "icon-1.png",
						"linkUrl": "pages\/coupon\/list?type=C",
						"text": "优惠券",
						"color": "#666666"
					}, {
						"imgUrl": "/static/nav/3.png",
						"imgName": "icon-1.png",
						"linkUrl": "pages\/coupon\/list?type=P",
						"text": "预存卡",
						"color": "#666666"
					}, {
						"imgUrl": "/static/nav/2.png",
						"imgName": "icon-1.png",
						"linkUrl": "pages\/coupon\/list?type=T",
						"text": "集次卡",
						"color": "#666666"}],
			"goodsStyle": {
				"background": "#F6F6F6",
				"display": "list",
				"column": 1,
				"show": ["goodsName", "goodsPrice", "linePrice", "sellingPoint", "goodsSales"]
			},
			"goodsParams": {
				"source": "auto",
				"auto": {
					"category": 0,
					"goodsSort": "all",
					"showNum": 40
				}
			},
			"bannerStyle": {
				"btnColor": "#ffffff",
				"btnShape": "round",
			},
			"bannerParam": {
				"interval": 2800
			},
			"navStyle": {
				"background": "#ffffff",
				"rowsNum": "3",
			}
		},
        banner: [],
		coupon: []
		}
	},

    /**
     * 生命周期函数--监听页面加载
     */
    onLoad() {
      this.getPageData();
    },

    /**
     * 生命周期函数--监听页面显示
     */
    onShow() {
      // empty
    },

    methods: {
		/**
		 * 加载页面数据
		 * @param {Object} callback
		 */
		getPageData(callback) {
		  const app = this
		  Api.home()
		    .then(result => {
		      app.banner = result.data.banner
			  app.coupon = result.data.coupon.content
		    })
		    .finally(() => callback && callback())
		},
		
		/**
		 * 下拉刷新
		 */
		onPullDownRefresh() {
		  // 获取数据
		  this.getPageData(() => {
		     uni.stopPullDownRefresh()
		  })
		}
    },

    /**
     * 分享当前页面
     */
    onShareAppMessage() {
      const app = this
      return {
         title: "FuInt会员卡券",
         path: "/pages/index/index?" + app.$getShareUrlParams()
      }
    },

    /**
     * 分享到朋友圈
     * 本接口为 Beta 版本，暂只在 Android 平台支持，详见分享到朋友圈 (Beta)
     * https://developers.weixin.qq.com/miniprogram/dev/framework/open-ability/share-timeline.html
     */
    onShareTimeline() {
      const app = this
      const { page } = app
      return {
        title: page.params.share_title,
        path: "/pages/index/index?" + app.$getShareUrlParams()
      }
    }

  }
</script>

<style lang="scss" scoped>
  .container {
    background: #fff;
  }
</style>
