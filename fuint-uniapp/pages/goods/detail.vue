<template>
  <view v-show="!isLoading" class="container">
    <!-- 商品图片轮播 -->
    <SlideImage v-if="!isLoading" :images="goods.goods_images" />

    <!-- 商品信息 -->
    <view v-if="!isLoading" class="goods-info m-top20">
      <!-- 价格、销量 -->
      <view class="info-item info-item__top dis-flex flex-x-between flex-y-end">
        <view class="block-left dis-flex flex-y-end">
          <!-- 商品售价 -->
          <text class="floor-price__samll">￥</text>
          <text class="floor-price">{{ goods.goods_price_min }}</text>
          <!-- 划线价 -->
          <text class="original-price">￥{{ goods.line_price_min }}</text>
        </view>
        <view class="block-right dis-flex">
          <!-- 销量 -->
          <view class="goods-sales">
            <text>已领取{{ goods.goods_sales }}张</text>
          </view>
        </view>
      </view>
      <!-- 标题、分享 -->
      <view class="info-item info-item__name dis-flex flex-y-center">
        <view class="goods-name flex-box">
          <text class="twolist-hidden">{{ goods.goods_name }}</text>
        </view>
        <!-- #ifdef MP-WEIXIN -->
        <view class="goods-share__line"></view>
        <view class="goods-share">
          <button class="share-btn dis-flex flex-dir-column" open-type="share">
            <text class="share__icon iconfont icon-fenxiang"></text>
            <text class="f-24">分享</text>
          </button>
        </view>
        <!-- #endif -->
      </view>
      <!-- 商品卖点 -->
      <view v-if="goods.selling_point" class="info-item info-item_selling-point">
        <text>{{ goods.selling_point }}</text>
      </view>
    </view>

    <!-- 选择商品规格 -->
    <view v-if="goods.spec_type == 20" class="goods-choice m-top20 b-f" @click="onShowSkuPopup(1)">
      <view class="spec-list">
        <view class="flex-box">
          <text class="col-8">选择：</text>
          <text class="spec-name" v-for="(item, index) in goods.specList" :key="index">{{ item.spec_name }}</text>
        </view>
        <view class="f-26 col-9 t-r">
          <text class="iconfont icon-xiangyoujiantou"></text>
        </view>
      </view>
    </view>

    <!-- 商品服务 -->
    <Service v-if="!isLoading" :goods-id="goodsId" />

    <!-- 商品SKU弹窗 -->
    <SkuPopup v-if="!isLoading" v-model="showSkuPopup" :skuMode="skuMode" :goods="goods" @addCart="onAddCart" />

    <!-- 商品描述 -->
    <view v-if="!isLoading" class="goods-content m-top20">
      <view class="item-title b-f">
        <text>卡券描述</text>
      </view>
      <block v-if="goods.content != ''">
        <view class="goods-content__detail b-f">
          <jyf-parser :html="goods.content"></jyf-parser>
        </view>
      </block>
      <empty v-else tips="亲，暂无卡券描述" />
    </view>

    <!-- 底部选项卡 -->
    <view class="footer-fixed">
      <view class="footer-container">
        <!-- 导航图标 -->
        <view class="foo-item-fast">
          <!-- 首页 -->
          <view class="fast-item fast-item--home" @click="onTargetHome">
            <view class="fast-icon">
              <text class="iconfont icon-shouye"></text>
            </view>
            <view class="fast-text">
              <text>首页</text>
            </view>
          </view>
          <!-- 客服 (仅微信小程序端显示) -->
          <!-- #ifdef MP-WEIXIN -->
          <button class="btn-normal" open-type="contact">
            <view class="fast-item">
              <view class="fast-icon">
                <text class="iconfont icon-kefu1"></text>
              </view>
              <view class="fast-text">
                <text>客服</text>
              </view>
            </view>
          </button>
          <!-- #endif -->
          <!-- 购物车 (非微信小程序端显示) -->
          <!-- #ifndef MP-WEIXIN -->
          <view class="fast-item fast-item--cart" @click="onTargetCart">
            <view v-if="cartTotal > 0" class="fast-badge fast-badge--fixed">{{ cartTotal > 99 ? '99+' : cartTotal }}</view>
            <view class="fast-icon">
              <text class="iconfont icon-gouwuche"></text>
            </view>
            <view class="fast-text">
              <text>卡券包</text>
            </view>
          </view>
          <!-- #endif -->
        </view>
        <!-- 操作按钮 -->
        <view class="foo-item-btn">
          <view class="btn-wrapper">
            <view class="btn-item btn-item-deputy" @click="onShowSkuPopup(2)">
              <text>加入卡券包</text>
            </view>
            <view class="btn-item btn-item-main" @click="onShowSkuPopup(3)">
              <text>立即购买</text>
            </view>
          </view>
        </view>
      </view>
    </view>

    <!-- 快捷导航 -->
    <!-- <shortcut bottom="120rpx" /> -->

  </view>
</template>

<script>
  import * as GoodsApi from '@/api/goods'
  import * as CartApi from '@/api/cart'
  import jyfParser from '@/components/jyf-parser/jyf-parser'
  import Shortcut from '@/components/shortcut'
  import SlideImage from './components/SlideImage'
  import SkuPopup from './components/SkuPopup'
  import Service from './components/Service'

  export default {
    components: {
      jyfParser,
      Shortcut,
      SlideImage,
      SkuPopup,
      Service
    },
    data() {
      return {
        // 正在加载
        isLoading: true,
        // 当前商品ID
        goodsId: null,
        // 商品详情
        goods:  {
			"goods_id": 10006,
			"goods_name": "五一预存有大礼，你还等什么！",
			"goods_no": "",
			"selling_point": "送100积分",
			"spec_type": 20,
			"goods_price_min": "1000.00",
			"goods_price_max": "999.00",
			"line_price_min": "2000.00",
			"line_price_max": "0.00",
			"stock_total": 845,
			"content": "<p>1、凭本卡券到适用网点可享受车辆洗车服务一次，每日限用1张，若超标使用导致账户锁定我司不予处理。</p></br><p>2、使用本卡券前，需点击查看最新的“适用网点”，并致电网点预约服务时间，错峰到店。因服务网点实时变动，如您未查看【适用网点】，而前往不合作的网点使用服务，或未提前预约导致您行程耽误的，期间产生的损失与费用，我司概不负责。</p></br><p>3、 本卡券禁止外借或转赠他人，若发现我司有权回收权益，卡券使用最终解释权归平安产险海南分公司所有。</p></br><p><b>内容介绍</b></p><p>【先预约，再服务】春节期间（2021年1月21日—2021年2月27日），网点洗车人数剧增，排队等待时间较长，使用服务前请先查询可适用网点，致电网点预约服务时间，错峰到店（点开任意一家门店后可在“电话按钮”处点开服务电话），由此给您造成不便，敬请谅解！</p>",
			"delivery_id": 10001,
			"is_points_gift": 1,
			"is_points_discount": 1,
			"is_alone_points_discount": 0,
			"points_discount_config": "",
			"is_enable_grade": 1,
			"is_alone_grade": 0,
			"alone_grade_equity": [],
			"status": 10,
			"goods_images": [{
				"file_id": 10073,
				"group_id": 0,
				"channel": 10,
				"storage": "qiniu",
				"domain": "http:\/\/static.yoshop.xany6.com",
				"file_type": 10,
				"file_name": "2018071716364761fab9153.jpg",
				"file_path": "2018071716364761fab9153.jpg",
				"file_size": 317269,
				"file_ext": "jpg",
				"cover": "",
				"uploader_id": 0,
				"is_recycle": 0,
				"is_delete": 0,
				"update_time": "2021-03-01 08:00:00",
				"preview_url": "/static/goods/1.png?v=1",
				"external_url": "/static/goods/1.png?v=1"
			}],
			"goods_image": "/static/banner/2.png",
			"goods_sales": 1980,
			"is_user_grade": false,
			"specList": [{
				"spec_id": 10001,
				"spec_name": "预存金额",
				"key": 0,
				"valueList": [{
					"key": 0,
					"groupKey": 0,
					"spec_value_id": 10001,
					"spec_value": "￥1000"
				}, {
					"key": 1,
					"groupKey": 0,
					"spec_value_id": 10002,
					"spec_value": "￥2000"
				}, {
					"key": 2,
					"groupKey": 0,
					"spec_value_id": 10003,
					"spec_value": "￥5000"
				}, {
					"key": 3,
					"groupKey": 0,
					"spec_value_id": 10004,
					"spec_value": "￥10000"
				}]
			}, {
				"spec_id": 10002,
				"spec_name": "适用范围",
				"key": 1,
				"valueList": [{
					"key": 0,
					"groupKey": 1,
					"spec_value_id": 10005,
					"spec_value": "餐食"
				},{
					"key": 1,
					"groupKey": 1,
					"spec_value_id": 10005,
					"spec_value": "住宿"
				}]
			}],
			"skuList": [{
				"id": 10198,
				"goods_sku_id": "10001_10005",
				"goods_id": 10006,
				"image_id": 0,
				"goods_sku_no": "",
				"goods_price": "749.00",
				"line_price": "0.00",
				"stock_num": 250,
				"goods_weight": 0.15,
				"goods_props": [{
					"group": {
						"name": "颜色",
						"id": 10001
					},
					"value": {
						"name": "流沙金",
						"id": 10001
					}
				}, {
					"group": {
						"name": "版本",
						"id": 10002
					},
					"value": {
						"name": "全网通(3G RAM+32G ROM)",
						"id": 10005
					}
				}],
				"spec_value_ids": [10001, 10005],
				"image_url": null
			}, {
				"id": 10199,
				"goods_sku_id": "10002_10005",
				"goods_id": 10006,
				"image_id": 0,
				"goods_sku_no": "",
				"goods_price": "999.00",
				"line_price": "0.00",
				"stock_num": 200,
				"goods_weight": 0.15,
				"goods_props": [{
					"group": {
						"name": "颜色",
						"id": 10001
					},
					"value": {
						"name": "巴厘蓝",
						"id": 10002
					}
				}, {
					"group": {
						"name": "版本",
						"id": 10002
					},
					"value": {
						"name": "全网通(3G RAM+32G ROM)",
						"id": 10005
					}
				}],
				"spec_value_ids": [10002, 10005],
				"image_url": null
			}, {
				"id": 10200,
				"goods_sku_id": "10003_10005",
				"goods_id": 10006,
				"image_id": 0,
				"goods_sku_no": "",
				"goods_price": "739.00",
				"line_price": "0.00",
				"stock_num": 200,
				"goods_weight": 0.15,
				"goods_props": [{
					"group": {
						"name": "颜色",
						"id": 10001
					},
					"value": {
						"name": "樱花粉",
						"id": 10003
					}
				}, {
					"group": {
						"name": "版本",
						"id": 10002
					},
					"value": {
						"name": "全网通(3G RAM+32G ROM)",
						"id": 10005
					}
				}],
				"spec_value_ids": [10003, 10005],
				"image_url": null
			}, {
				"id": 10201,
				"goods_sku_id": "10004_10005",
				"goods_id": 10006,
				"image_id": 0,
				"goods_sku_no": "",
				"goods_price": "739.00",
				"line_price": "0.00",
				"stock_num": 200,
				"goods_weight": 0.15,
				"goods_props": [{
					"group": {
						"name": "颜色",
						"id": 10001
					},
					"value": {
						"name": "铂银灰",
						"id": 10004
					}
				}, {
					"group": {
						"name": "版本",
						"id": 10002
					},
					"value": {
						"name": "全网通(3G RAM+32G ROM)",
						"id": 10005
					}
				}],
				"spec_value_ids": [10004, 10005],
				"image_url": null
			}]
		},
        // 购物车总数量
        cartTotal: 0,
        // 显示/隐藏SKU弹窗
        showSkuPopup: false,
        // 模式 1:都显示 2:只显示购物车 3:只显示立即购买
        skuMode: 1
      }
    },

    /**
     * 生命周期函数--监听页面加载
     */
    onLoad(options) {
      // 记录商品ID
      this.goodsId = parseInt(options.goodsId)
      // 加载页面数据
      this.onRefreshPage()
    },

    methods: {

      // 刷新页面数据
      onRefreshPage() {
        const app = this
        app.isLoading = true
        Promise.all([app.getGoodsDetail(), app.getCartTotal()])
          .finally(() => app.isLoading = false)
      },

      // 获取商品信息
      getGoodsDetail() {
        const app = this
        return new Promise((resolve, reject) => {
          GoodsApi.detail(app.goodsId)
            .then(result => {
              app.goods = result.data.detail
              resolve(result)
            })
            .catch(err => reject(err))
        })
      },

      // 获取购物车总数量
      getCartTotal() {
        const app = this
        return new Promise((resolve, reject) => {
          CartApi.total()
            .then(result => {
              app.cartTotal = result.data.cartTotal
              resolve(result)
            })
            .catch(err => reject(err))
        })
      },

      // 更新购物车数量
      onAddCart(total) {
        this.cartTotal = total
      },

      /**
       * 显示/隐藏SKU弹窗
       * @param {skuMode} 模式 1:都显示 2:只显示购物车 3:只显示立即购买
       */
      onShowSkuPopup(skuMode = 1) {
        this.skuMode = skuMode
        this.showSkuPopup = !this.showSkuPopup
      },

      // 跳转到首页
      onTargetHome(e) {
        this.$navTo('pages/index/index')
      },

      // 跳转到购物车页
      onTargetCart() {
        this.$navTo('pages/cart/index')
      },

    },

    /**
     * 分享当前页面
     */
    onShareAppMessage() {
      const app = this
      // 构建页面参数
      const params = app.$getShareUrlParams({
        goodsId: app.goodsId,
      })
      return {
        title: app.goods.goods_name,
        path: `/pages/goods/detail?${params}`
      }
    },

    /**
     * 分享到朋友圈
     * 本接口为 Beta 版本，暂只在 Android 平台支持，详见分享到朋友圈 (Beta)
     * https://developers.weixin.qq.com/miniprogram/dev/framework/open-ability/share-timeline.html
     */
    onShareTimeline() {
      const app = this
      // 构建页面参数
      const params = app.$getShareUrlParams({
        goodsId: app.goodsId,
      })
      return {
        title: app.goods.goods_name,
        path: `/pages/goods/detail?${params}`
      }
    }
  }
</script>

<style>
  page {
    background: #fafafa;
  }
</style>
<style lang="scss" scoped>
  @import "./detail.scss";
</style>
