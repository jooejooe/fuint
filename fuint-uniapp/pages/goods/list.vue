<template>
  <mescroll-body ref="mescrollRef" :sticky="true" @init="mescrollInit" :down="{ native: true }" @down="downCallback"
    :up="upOption" @up="upCallback">
    <!-- 页面头部 -->
    <view class="header">
      <search class="search" :tips="options.search ? options.search : '搜索卡券'" @event="handleSearch" />
    </view>

    <!-- 排序标签 -->
    <view class="store-sort">
      <view class="sort-item" :class="{ active: sortType === 'all' }" @click="handleSortType('all')">
        <text>综合</text>
      </view>
      <view class="sort-item" :class="{ active: sortType === 'sales' }" @click="handleSortType('sales')">
        <text>领取数</text>
      </view>
      <view class="sort-item sort-item-price" :class="{ active: sortType === 'price' }" @click="handleSortType('price')">
        <text>面额</text>
        <view class="price-arrow">
          <view class="icon up" :class="{ active: sortType === 'price' && !sortPrice }">
            <text class="iconfont icon-arrow-up"></text>
          </view>
          <view class="icon down" :class="{ active: sortType === 'price' && sortPrice }">
            <text class="iconfont icon-arrow-down"></text> </view>
        </view>
      </view>
    </view>

    <!-- 商品列表 -->
    <view class="goods-list clearfix" :class="['column-1']">
      <view class="goods-item" v-for="(item, index) in list.data" :key="index" @click="onTargetDetail(item.goods_id, item.type)">
        <!-- 单列显示 -->
        <view v-if="showView" class="dis-flex">
          <!-- 商品图片 -->
          <view class="goods-item_left">
            <image class="image" :src="item.goods_image"></image>
          </view>
          <view class="goods-item_right">
            <!-- 商品名称 -->
            <view class="goods-name twolist-hidden">
              <text>{{ item.goods_name }}</text>
            </view>
            <view class="goods-item_desc">
              <!-- 商品卖点 -->
              <view class="desc-selling_point dis-flex">
                <text class="onelist-hidden">{{ item.selling_point }}</text>
              </view>
              <view class="coupon-attr">
				  <view class="attr-l">
					  <!-- 卡券销量 -->
					  <view class="desc-goods_sales dis-flex">
					    <text>已领取{{ item.goods_sales }}张</text>
					  </view>
					  <!-- 卡券价格 -->
					  <view class="desc_footer">
					    <text class="price_x">¥{{ item.goods_price_min }}</text>
					    <text class="price_y col-9" v-if="item.line_price_min > 0">¥{{ item.line_price_min }}</text>
					  </view>
				  </view>
				  <view class="attr-r">
					  <!--领券按钮-->
					  <view class="receive" v-if="item.type === 'C'" @click="receive(item.goods_id)">
						<text>立即领取</text>
					  </view>
					  <view v-else-if="item.type === 'P'" class="receive">
						<text>立即预存</text>
					  </view>
					  <view v-else-if="item.type === 'T'" class="receive state">
						<text>已领取</text>
					  </view>
				  </view>
              </view>
            </view>
          </view>
        </view>
      </view>
    </view>
  </mescroll-body>
</template>

<script>
  import MescrollBody from '@/components/mescroll-uni/mescroll-body.vue'
  import MescrollMixin from '@/components/mescroll-uni/mescroll-mixins'
  import * as GoodsApi from '@/api/goods'
  import { getEmptyPaginateObj, getMoreListData } from '@/utils/app'
  import Search from '@/components/search'

  const pageSize = 15
  const showViewKey = 'GoodsList-ShowView';

  export default {
    components: {
      MescrollBody,
      Search
    },

    mixins: [MescrollMixin],

    data() {
      return {
        showView: true, // 列表显示方式 (true列表、false平铺)
        sortType: 'all', // 排序类型
        sortPrice: false, // 价格排序 (true高到低 false低到高)
        options: {}, // 当前页面参数
        list: getEmptyPaginateObj(), // 商品列表数据

        // 上拉加载配置
        upOption: {
          // 首次自动执行
          auto: true,
          // 每页数据的数量; 默认10
          page: { size: pageSize },
          // 数量要大于4条才显示无更多数据
          noMoreSize: 4,
        }
      }
    },

    /**
     * 生命周期函数--监听页面加载
     */
    onLoad(options) {
      // 记录options
      this.options = options
      // 设置默认列表显示方式
      this.setShowView()
    },

    methods: {

      /**
       * 上拉加载的回调 (页面初始化时也会执行一次)
       * 其中page.num:当前页 从1开始, page.size:每页数据条数,默认10
       * @param {Object} page
       */
      upCallback(page) {
        const app = this
        // 设置列表数据
        app.getGoodsList(page.num)
          .then(list => {
            const curPageLen = list.data.length
            const totalSize = list.data.total
            app.mescroll.endBySize(curPageLen, totalSize)
          })
          .catch(() => app.mescroll.endErr())
      },

      // 设置默认列表显示方式
      setShowView() {
        this.showView = uni.getStorageSync(showViewKey) || true
      },

      /**
       * 获取商品列表
       * @param {number} pageNo 页码
       */
      getGoodsList(pageNo = 1) {
        const app = this
        console.log(app.options)
        const param = {
          sortType: app.sortType,
          sortPrice: Number(app.sortPrice),
          categoryId: app.options.categoryId || 0,
          goodsName: app.options.search || '',
          page: pageNo
        }
		
		// 范例数据
		let data = [{
				"goods_sales": 363,
				"goods_id": 10007,
				"goods_name": "五一5元优惠券",
				"type":"C",
				"goods_no": "",
				"selling_point": "",
				"spec_type": 20,
				"goods_price_min": "5.00",
				"goods_price_max": "1699.00",
				"line_price_min": "0.00",
				"line_price_max": "0.00",
				"stock_total": 1988,
				"delivery_id": 10001,
				"is_points_gift": 1,
				"is_points_discount": 1,
				"is_alone_points_discount": 0,
				"points_discount_config": "",
				"is_enable_grade": 1,
				"is_alone_grade": 0,
				"alone_grade_equity": [],
				"status": 10,
				"goods_image": "/static/coupon/3.png",
				"is_user_grade": false
			}, {
				"goods_sales": 0,
				"goods_id": 10006,
				"goods_name": "五一20元优惠券",
				"type":"C",
				"goods_no": "",
				"selling_point": "",
				"spec_type": 20,
				"goods_price_min": "20.00",
				"goods_price_max": "999.00",
				"line_price_min": "0.00",
				"line_price_max": "0.00",
				"stock_total": 845,
				"delivery_id": 10001,
				"is_points_gift": 1,
				"is_points_discount": 1,
				"is_alone_points_discount": 0,
				"points_discount_config": "",
				"is_enable_grade": 1,
				"is_alone_grade": 0,
				"alone_grade_equity": [],
				"status": 10,
				"goods_image": "/static/coupon/3.png",
				"is_user_grade": false
			}, {
				"goods_sales": 0,
				"goods_id": 10006,
				"goods_name": "五一20元优惠券",
				"type":"T",
				"goods_no": "",
				"selling_point": "",
				"spec_type": 20,
				"goods_price_min": "30.00",
				"goods_price_max": "999.00",
				"line_price_min": "0.00",
				"line_price_max": "0.00",
				"stock_total": 845,
				"delivery_id": 10001,
				"is_points_gift": 1,
				"is_points_discount": 1,
				"is_alone_points_discount": 0,
				"points_discount_config": "",
				"is_enable_grade": 1,
				"is_alone_grade": 0,
				"alone_grade_equity": [],
				"status": 10,
				"goods_image": "/static/coupon/3.png",
				"is_user_grade": false
			}, {
				"goods_sales": 0,
				"goods_id": 10006,
				"goods_name": "五一20元优惠券",
				"type":"P",
				"goods_no": "",
				"selling_point": "",
				"spec_type": 20,
				"goods_price_min": "50.00",
				"goods_price_max": "999.00",
				"line_price_min": "0.00",
				"line_price_max": "0.00",
				"stock_total": 845,
				"delivery_id": 10001,
				"is_points_gift": 1,
				"is_points_discount": 1,
				"is_alone_points_discount": 0,
				"points_discount_config": "",
				"is_enable_grade": 1,
				"is_alone_grade": 0,
				"alone_grade_equity": [],
				"status": 10,
				"goods_image": "/static/coupon/3.png",
				"is_user_grade": false
			}]
			let result = {"data":{"list":{"total":2, "per_page":15, "current_page":1, "last_page":1,
			           "data":data,
					   }}}
		
		const newList = result.data.list
		app.list.data = getMoreListData(newList, app.list, pageNo)
		/*
        return new Promise((resolve, reject) => {
          GoodsApi.list(param)
            .then(result => {
              // 合并新数据
              const newList = result.data.list
			  alert(newList)
              app.list.data = getMoreListData(newList, app.list, pageNo)
              resolve(newList)
            })
            .catch(reject)
        })*/
      },

      // 切换排序方式
      handleSortType(newSortType) {
        const app = this
        const newSortPrice = newSortType === 'price' ? !app.sortPrice : true
        app.sortType = newSortType
        app.sortPrice = newSortPrice
        // 刷新列表数据
        app.list = getEmptyPaginateObj()
        app.mescroll.resetUpScroll()
      },

      // 切换列表显示方式
      handleShowView() {
        const app = this
        app.showView = !app.showView
        uni.setStorageSync(showViewKey, app.showView)
      },

      // 跳转商品详情页
      onTargetDetail(goodsId, type) {
        if (type === 'P') {
        	this.$navTo(`pages/goods/detail`, { goodsId })
        } else {
            return
        }
      },
	  
	  // 领取卡券
	  receive(goods_id) {
	  	this.$success("领取成功！")
	  },

      //卡券搜索
      handleSearch() {
        const searchPageUrl = 'pages/search/index'
        // 判断来源页面
        let pages = getCurrentPages()
        if (pages.length > 1 &&
          pages[pages.length - 2].route === searchPageUrl) {
          uni.navigateBack()
          return
        }
        // 跳转到卡券搜索页
        this.$navTo(searchPageUrl)
      }

    },

    /**
     * 设置分享内容
     */
    onShareAppMessage() {
      // 构建分享参数
      return {
        title: "全部分类",
        path: "/pages/category/index?" + this.$getShareUrlParams()
      }
    },

    /**
     * 分享到朋友圈
     * 本接口为 Beta 版本，暂只在 Android 平台支持，详见分享到朋友圈 (Beta)
     * https://developers.weixin.qq.com/miniprogram/dev/framework/open-ability/share-timeline.html
     */
    onShareTimeline() {
      // 构建分享参数
      return {
        title: "全部分类",
        path: "/pages/category/index?" + this.$getShareUrlParams()
      }
    }

  }
</script>

<style lang="scss" scoped>
  // 页面头部
  .header {
    display: flex;
    align-items: center;
    background-color: #fff;

    // 搜索框
    .search {
      flex: 1;
    }

    // 切换显示方式
    .show-view {
      width: 60rpx;
      height: 60rpx;
      line-height: 60rpx;
      font-size: 36rpx;
      color: #505050;
    }
  }

  // 排序组件
  .store-sort {
    position: sticky;
    top: var(--window-top);
    display: flex;
    padding: 20rpx 0;
    font-size: 28rpx;
    background: #fff;
    color: #000;
    z-index: 99;

    .sort-item {
      flex-basis: 33.3333%;
      display: flex;
      justify-content: center;
      align-items: center;
      height: 50rpx;

      &.active {
        color: #e49a3d;
      }
    }

    .sort-item-price .price-arrow {
      margin-left: 20rpx;
      font-size: 24rpx;
      color: #000;

      .icon {
        &.active {
          color: #e49a3d;
        }

        &.up {
          margin-bottom: -16rpx;
        }

        &.down {
          margin-top: -16rpx;
        }
      }

    }

  }

  // 商品列表
  .goods-list {
    padding: 4rpx;
    box-sizing: border-box;
  }

  // 单列显示
  .goods-list.column-1 {
    .goods-item {
      width: 100%;
      height: 280rpx;
      margin-bottom: 12rpx;
      padding: 20rpx;
      box-sizing: border-box;
      background: #fff;
      line-height: 1.6;

      &:last-child {
        margin-bottom: 0;
      }
    }

    .goods-item_left {
      display: flex;
      width: 300rpx;
      background: #fff;
      align-items: center;

      .image {
        display: block;
        width: 240rpx;
        height: 240rpx;
      }
    }

    .goods-item_right {
      position: relative;
      // width: 450rpx;
      flex: 1;

      .goods-name {
        margin-top: 10rpx;
        height: 64rpx;
        line-height: 1.3;
        white-space: normal;
        color: #484848;
        font-size: 26rpx;
      }
    }

    .goods-item_desc {
      margin-top: 8rpx;
	  .coupon-attr {
		 .attr-l {
			 float:left;
			 width: 60%;
		 }
		 .attr-r {
			 margin-top:20rpx;
			 float:left;
		 }
	  }
    }

    .desc-selling_point {
      width: 400rpx;
      font-size: 24rpx;
      color: #e49a3d;
    }
	.receive {
	  height: 46rpx;
	  width: 128rpx;
	  line-height: 46rpx;
	  text-align: center;
	  border: 1px solid #f8df00;
	  border-radius: 20rpx;
	  color: #f86d48;
	  background: #f8df98;
	  font-size: 22rpx;
	  &.state {
	    border: none;
		color: #cccccc;
		background: #F5F5F5;
	  }
	}

    .desc-goods_sales {
      color: #999;
      font-size: 24rpx;
    }

    .desc_footer {
      font-size: 24rpx;

      .price_x {
        margin-right: 16rpx;
        color: #f03c3c;
        font-size: 30rpx;
      }

      .price_y {
        text-decoration: line-through;
      }
    }
  }

  // 平铺显示
  .goods-list.column-2 {
    .goods-item {
      width: 50%;
    }
  }

  .goods-item {
    float: left;
    box-sizing: border-box;
    padding: 6rpx;

    .goods-image {
      position: relative;
      width: 100%;
      height: 0;
      padding-bottom: 100%;
      overflow: hidden;
      background: #fff;

      &:after {
        content: '';
        display: block;
        margin-top: 100%;
      }

      .image {
        position: absolute;
        width: 100%;
        height: 100%;
        top: 0;
        left: 0;
        -o-object-fit: cover;
        object-fit: cover;
      }
    }

    .detail {
      padding: 8rpx;
      background: #fff;

      .goods-name {
        height: 64rpx;
        line-height: 32rpx;
        white-space: normal;
        color: #484848;
        font-size: 26rpx;
      }

      .detail-price {
        .goods-price {
          margin-right: 8rpx;
        }

        .line-price {
          text-decoration: line-through;
        }
      }
    }
  }
</style>
