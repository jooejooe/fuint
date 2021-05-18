<template>
  <view class="container">
    <mescroll-body ref="mescrollRef" :sticky="true" @init="mescrollInit" :down="{ use: false }" :up="upOption"
      @up="upCallback">

      <!-- tab栏 -->
      <u-tabs :list="tabs" :is-scroll="false" :current="curTab" active-color="#FA2209" :duration="0.2" @change="onChangeTab" />

      <!-- 卡券列表 -->
	  <view class="goods-list">
		  <view class="goods-item" v-for="(item, index) in list.content" :key="index">
			<!-- 单列卡券 -->
			<view class="dis-flex">
			    <!-- 卡券图片 -->
			    <view class="goods-item_left">
			      <image class="image" :src="item.image"></image>
			    </view>
			    <view class="goods-item_right">
			      <!-- 卡券名称 -->
			      <view class="goods-name twolist-hidden">
			        <text>{{ item.name }}</text>
			      </view>
			      <view class="goods-item_desc">
			        <!-- 卡券卖点 -->
			        <view class="desc-selling_point dis-flex">
			          <text class="onelist-hidden">{{ item.tips }}</text>
			        </view>
			        <view class="coupon-attr">
							  <view class="attr-l">
								  <view class="desc-goods_sales dis-flex">
								    <text>{{ item.effectiveDate }}</text>
								  </view>
								  <view v-if="item.amount > 0" class="desc_footer">
								    <text class="price_x">¥{{ item.amount }}</text>
								  </view>
							  </view>
							  <view class="attr-r">
								  <!--领券按钮-->
								  <view v-if="item.canUse" class="receive" @click="onDetail(item.id, item.type)">
									<text>立即使用</text>
								  </view>
								  <view v-if="!item.canUse" class="receive state">
								  	<text>不可使用</text>
								  </view>
							  </view>
			        </view>
			      </view>
			    </view>
			  </view>
          </view>
		  <empty v-if="!list.content.length" :isLoading="isLoading" :custom-style="{ padding: '180rpx 50rpx' }" tips="当前没有卡券, 快去逛逛吧">
		    <view slot="slot" class="empty-ipt" @click="onTargetIndex">
		      <text>去逛逛</text>
		    </view>
		  </empty>
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
  import Empty from '@/components/empty'

  const color = ['red', 'blue', 'violet', 'yellow']
  const pageSize = 15
  const tabs = [{
    name: `未使用`,
    value: 'A'
  }, {
    name: `已使用`,
    value: 'B'
  }, {
    name: `已过期`,
    value: 'C'
  }]

  export default {
    components: {
      MescrollBody,
	  Empty
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
		// 卡券类型
		type: "",
        // 优惠券列表数据
        list: getEmptyPaginateObj(),
		// 正在加载
		isLoading: false,
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
            tip: '亲，暂无相关卡券'
          }
        }
      }
    },

    /**
     * 生命周期函数--监听页面加载
     */
    onLoad(options) {
	   let type = options.type
	   this.type = type;
       uni.setNavigationBarTitle({
         title: "我的" + CouponTypeEnum[type].name
       })
    },

    methods: {
		
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
	  
	  // 点击跳转到首页
	  onTargetIndex() {
	    this.$navTo('pages/index/index')
	  },
	  
	  // 卡券详情
	  onDetail(id, type) {
		  if (type === 'C') {
			  this.$navTo(`pages/coupon/detail`, { id })
		  } else if(type === 'T'){
		      this.$navTo(`pages/timer/detail`, { id })
		  } else if(type === 'P') {
			  this.$navTo(`pages/prestore/detail`, { id })
		  }
	  },
	  
      /**
       * 获取卡券列表
       */
      getCouponList(pageNo = 1) {
        const app = this
        return new Promise((resolve, reject) => {
          MyCouponApi.list({ type: app.type, status: app.getTabValue(), page: pageNo }, { load: false })
            .then(result => {
              // 合并新数据
              const newList = result.data
              app.list.content = getMoreListData(newList, app.list, pageNo)
              resolve(newList)
            })
        })
      },

      // 类型
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
  		 width: 68%;
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
 // 空数据按钮
 .empty-ipt {
   width: 220rpx;
   margin: 10rpx auto;
   font-size: 28rpx;
   height: 64rpx;
   line-height: 64rpx;
   text-align: center;
   color: #fff;
   border-radius: 50rpx;
   background: linear-gradient(to right, #00acac, #00acac);
 }
}
</style>
