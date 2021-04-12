<template>
  <view class="container">
    <mescroll-body ref="mescrollRef" :sticky="true" @init="mescrollInit" :down="{ use: false }" :up="upOption"
      @up="upCallback">

      <!-- tab栏 -->
      <u-tabs :list="tabs" :is-scroll="false" :current="curTab" active-color="#FA2209" :duration="0.2" @change="onChangeTab" />

      <!-- 优惠券列表 -->
	  <view class="goods-list">
		  <view class="goods-item" v-for="(dataItem, index) in list.data" :key="index">
			<!-- 单列卡券 -->
			<block>
			  <view class="dis-flex">
				<!-- 商品图片 -->
				<view class="goods-item_left">
				  <image class="image" :src="dataItem.goods_image"></image>
				</view>
				<view class="goods-item_right">
				  <!-- 卡券名称 -->
				  <view class="goods-name twolist-hidden">
					<text>{{ dataItem.goods_name }}</text>
				  </view>
				  <view class="goods-item_desc">
					<!-- 卡券卖点 -->
					<view class="desc-selling_point dis-flex">
					  <text class="onelist-hidden">{{ dataItem.selling_point }}</text>
					</view>
				  <view class="coupon-attr">
					  <view class="attr-l">
						  <!-- 卡券销量 -->
						  <view class="desc-goods_sales dis-flex">
							<text>已领取{{ dataItem.goods_sales }}张</text>
						  </view>
						  <!-- 卡券价格 -->
						  <view class="desc_footer">
							<text class="price_x">¥{{ dataItem.goods_price_min }}</text>
							<text class="price_y col-9">¥{{ dataItem.line_price_min }}</text>
						  </view>
					  </view>
					  <view class="attr-r">
						  <!--领券按钮-->
						  <view class="receive" v-if="dataItem.type === 'C'" @click="onDetail(dataItem.goods_id, dataItem.type)">
							<text>立即使用</text>
						  </view>
						  <view v-else-if="dataItem.type === 'P'" class="receive" @click="onDetail(dataItem.goods_id, dataItem.type)">
							<text>立即使用</text>
						  </view>
						  <view v-else-if="dataItem.type === 'T'" class="receive state" @click="onDetail(dataItem.goods_id, dataItem.type)">
							<text>已使用</text>
						  </view>
					  </view>
				  </view>
				  </view>
				</view>
			  </view>
			</block>
		  </view>
	  </view>

    </mescroll-body>
  </view>
</template>

<script>
  import MescrollBody from '@/components/mescroll-uni/mescroll-body.vue'
  import MescrollMixin from '@/components/mescroll-uni/mescroll-mixins'
  import { getEmptyPaginateObj, getMoreListData } from '@/utils/app'
  import * as MyCouponApi from '@/api/myCoupon'
  import { CouponTypeEnum } from '@/common/enum/coupon'

  const color = ['red', 'blue', 'violet', 'yellow']
  const pageSize = 15
  const tabs = [{
    name: `未使用`,
    value: 'isUsable'
  }, {
    name: `已使用`,
    value: 'isUse'
  }, {
    name: `已过期`,
    value: 'isExpire'
  }]

  export default {
    components: {
      MescrollBody
    },
    mixins: [MescrollMixin],
    data() {
      return {
        // 枚举类
        CouponTypeEnum,
        // 颜色组
        color,
        // 标签栏数据
        tabs,
        // 当前标签索引
        curTab: 0,
        // 优惠券列表数据
        list: getEmptyPaginateObj(),
        // 上拉加载配置
        upOption: {
          // 首次自动执行
          auto: true,
          // 每页数据的数量; 默认10
          page: { size: pageSize },
          // 数量要大于4条才显示无更多数据
          noMoreSize: 4,
          // 空布局
          empty: {
            tip: '亲，暂无相关优惠券'
          }
        }
      }
    },

    /**
     * 生命周期函数--监听页面加载
     */
    onLoad(options) {
	   let type = options.type
       uni.setNavigationBarTitle({
         title: "我的" + CouponTypeEnum[type].name
       })
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
        app.getCouponList(page.num)
          .then(list => {
            const curPageLen = list.data.length
            const totalSize = list.data.total
            app.mescroll.endBySize(curPageLen, totalSize)
          })
          .catch(() => app.mescroll.endErr())
      },
	  // 卡券详情
	  onDetail(goodsId, type) {
		  if (type === 'C') {
			  this.$navTo(`pages/coupon/detail`, { goodsId })
		  } else if(type === 'T'){
		      this.$navTo(`pages/timer/detail`, { goodsId })
		  } else {
			  this.$navTo(`pages/prestore/detail`, { goodsId })
		  }
	  },
      /**
       * 获取优惠券列表
       */
      getCouponList(pageNo = 1) {
        const app = this
		const data = [{
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
		let result = {"data":{"list":{"total":2, "per_page":15, "current_page":1, "last_page":1,
		           "data":data}}}
		const newList = result.data.list
		
		app.list.data = getMoreListData(newList, app.list, pageNo)
        /*return new Promise((resolve, reject) => {
          MyCouponApi.list({ dataType: app.getTabValue(), page: pageNo }, { load: false })
            .then(result => {
              // 合并新数据
              const newList = result.data.list
              app.list.data = getMoreListData(newList, app.list, pageNo)
              resolve(newList)
            })
        })*/
      },

      // 评分类型
      getTabValue() {
        return this.tabs[this.curTab].value
      },

      // 切换标签项
      onChangeTab(index) {
        const app = this
        // 设置当前选中的标签
        app.curTab = index
        // 刷新优惠券列表
        app.onRefreshList()
      },

      // 刷新优惠券列表
      onRefreshList() {
        this.list = getEmptyPaginateObj()
        setTimeout(() => {
          this.mescroll.resetUpScroll()
        }, 120)
      },

    }
  }
</script>

<style lang="scss" scoped>
.goods-list {
  padding: 4rpx;
  box-sizing: border-box;

  .goods-item {
	box-sizing: border-box;
	padding: 6rpx;
	
  .goods-item_left {
    display: flex;
    width: 40%;
    align-items: center;
    background: #fff;
    .image {
      display: block;
      width: 240rpx;
      height: 240rpx;
    }
  }
  
  .goods-item_right {
    position: relative;
    width: 60%;
	background: #fff;
  
    .goods-name {
      margin-top: 20rpx;
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
 }
</style>
