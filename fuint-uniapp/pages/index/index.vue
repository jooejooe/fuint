<template>
  <view class="container">
	  <block>
	    <Search :itemStyle="search.style" :params="search.params" />
	  </block>
	  <block>
	      <Banner :itemStyle="banner.style" :params="banner.params" :dataList="banner.data" />
	  </block>
	  <block>
	      <Blank :itemStyle="{'height':'5', 'background':'#ffffff'}" />
	  </block>
	  <block>
	      <NavBar :itemStyle="navBar.style" :params="navBar.params" :dataList="navBar.data" />
	  </block>
	  <block>
	      <Blank :itemStyle="{'height':'5', 'background':'#ffffff'}" />
	  </block>
	  <block>
	    <Goods :itemStyle="goods.style" :params="goods.params" :dataList="goods.data" />
	  </block>
  </view>
</template>

<script>
  import { setCartTabBadge } from '@/utils/app'
  import Search from '@/components/page/search'
  import Banner from '@/components/page/banner'
  import NavBar from '@/components/page/navBar'
  import Blank from '@/components/page/blank'
  import Goods from '@/components/page/goods'

  const App = getApp()
  
  export default {
	  components: {
		Search,
	    Banner,
		NavBar,
		Blank,
		Goods
	},
    data() {
      return {
        // 页面参数
        options: {},
        banner: {
				"style": {
					"btnColor": "#ffffff",
					"btnShape": "round"
				},
				"params": {
					"interval": 2800
				},
				"data": [{
					"imgUrl": "/static/banner/2.png",
					"linkUrl": "pages\/goods\/detail?goods_id=10002",
					"imgName": "1000.png"
				}, {
					"imgUrl": "/static/banner/1.png",
					"linkUrl": "pages\/goods\/detail?goods_id=10003",
					"imgName": "2000.png"
				}]
			},
			navBar: {
				"style": {
					"background": "#ffffff",
					"rowsNum": "3"
				},
				"data": [{
					"imgUrl": "/static/nav/1.png",
					"imgName": "icon-1.png",
					"linkUrl": "pages\/goods\/list?type=C",
					"text": "优惠券",
					"color": "#666666"
				}, {
					"imgUrl": "/static/nav/3.png",
					"imgName": "icon-1.png",
					"linkUrl": "pages\/goods\/list?type=P",
					"text": "预存卡",
					"color": "#666666"
				}, {
					"imgUrl": "/static/nav/2.png",
					"imgName": "icon-1.png",
					"linkUrl": "pages\/goods\/list?type=T",
					"text": "集次卡",
					"color": "#666666"
				}]
			},
			search: {
				"params": {
					"placeholder": "搜索卡券"
				},
				"style": {
					"textAlign": "left",
					"searchStyle": "radius"
				}
			},
			goods: {
				"params": {
					"source": "auto",
					"auto": {
						"category": 0,
						"goodsSort": "all",
						"showNum": 40
					}
				},
				"style": {
					"background": "#F6F6F6",
					"display": "list",
					"column": 1,
					"show": ["goodsName", "goodsPrice", "linePrice", "sellingPoint", "goodsSales"]
				},
				"data": [{
					"goods_id": 10020,
					"goods_name": "五一20元优惠券",
					"selling_point": "",
					"type":"C",
					"goods_image": "/static/coupon/3.png",
					"goods_price_min": "5.00",
					"goods_price_max": "100.00",
					"line_price_min": "200.00",
					"line_price_max": "200.00",
					"goods_sales": 1050
				}, {
					"goods_id": 10021,
					"goods_name": "五一美食5元无门槛券",
					"selling_point": "",
					"type":"P",
					"goods_image": "/static/coupon/3.png",
					"goods_price_min": "10.00",
					"goods_price_max": "80.00",
					"line_price_min": "80.00",
					"line_price_max": "80.00",
					"goods_sales": 1223
				},{
					"goods_id": 10019,
					"goods_name": "龙湖汽车美容店5月份洗车集次卡",
					"selling_point": "",
					"type":"T",
					"goods_image": "/static/coupon/3.png",
					"goods_price_min": "20",
					"goods_price_max": "1000",
					"line_price_min": "0.00",
					"line_price_max": "0.00",
					"goods_sales": 1720
				}]
			}
		}
	},

    /**
     * 生命周期函数--监听页面加载
     */
    onLoad(options) {
      // 当前页面参数
      this.options = options
    },

    /**
     * 生命周期函数--监听页面显示
     */
    onShow() {
      // 更新购物车角标
      setCartTabBadge()
    },

    methods: {
      /**
       * 设置顶部导航栏
       */
      setPageBar() {
        // 设置页面标题
        uni.setNavigationBarTitle({
          title: "fuint首页"
        });
        // 设置navbar标题、颜色
        uni.setNavigationBarColor({
          frontColor: '#ffffff',
          backgroundColor: "#41d684"
        })
      },
    },

    /**
     * 分享当前页面
     */
    onShareAppMessage() {
      const app = this
      return {
        title: "首页",
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
