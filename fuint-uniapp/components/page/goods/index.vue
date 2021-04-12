<template>
  <!-- 卡券组 -->
  <view class="diy-goods" :style="{ background: itemStyle.background }">
    <view class="goods-list" :class="[`display__${itemStyle.display}`, `column__${itemStyle.column}`]">
      <scroll-view :scroll-x="itemStyle.display === 'slide'">
        <view class="goods-item" v-for="(dataItem, index) in dataList" :key="index" @click="onTargetGoods(dataItem.goods_id, dataItem.type)">
          <!-- 单列卡券 -->
          <block>
            <view class="dis-flex">
              <!-- 商品图片 -->
              <view class="goods-item_left">
                <image class="image" :src="dataItem.goods_image"></image>
              </view>
              <view class="goods-item_right">
                <!-- 卡券名称 -->
                <view v-if="itemStyle.show.includes('goodsName')" class="goods-name twolist-hidden">
                  <text>{{ dataItem.goods_name }}</text>
                </view>
                <view class="goods-item_desc">
                  <!-- 卡券卖点 -->
                  <view v-if="itemStyle.show.includes('sellingPoint')" class="desc-selling_point dis-flex">
                    <text class="onelist-hidden">{{ dataItem.selling_point }}</text>
                  </view>
				  <view class="coupon-attr">
					  <view class="attr-l">
						  <!-- 卡券销量 -->
						  <view v-if="itemStyle.show.includes('goodsSales')" class="desc-goods_sales dis-flex">
							<text>已领取{{ dataItem.goods_sales }}张</text>
						  </view>
						  <!-- 卡券价格 -->
						  <view class="desc_footer">
							<text v-if="itemStyle.show.includes('goodsPrice')" class="price_x">¥{{ dataItem.goods_price_min }}</text>
							<text class="price_y col-9" v-if="itemStyle.show.includes('linePrice') && dataItem.line_price_min > 0">¥{{ dataItem.line_price_min }}</text>
						  </view>
					  </view>
					  <view class="attr-r">
						  <!--领券按钮-->
						  <view class="receive" v-if="dataItem.type === 'C'" @click="receive(dataItem.goods_id)">
							<text>立即领取</text>
						  </view>
						  <view v-else-if="dataItem.type === 'P'" class="receive">
						  	<text>立即预存</text>
						  </view>
						  <view v-else-if="dataItem.type === 'T'" class="receive state">
							<text>已领取</text>
						  </view>
					  </view>
				  </view>
                </view>
              </view>
            </view>
          </block>
        </view>
      </scroll-view>
    </view>
  </view>
</template>

<script>

  export default {
    name: "Goods",
    /**
     * 组件的属性列表
     * 用于组件自定义设置
     */
    props: {
      itemIndex: String,
      itemStyle: Object,
      params: Object,
      dataList: Array
    },

    /**
     * 组件的方法列表
     * 更新属性和数据的方法与更新页面数据的方法类似
     */
    methods: {
      /**
       * 跳转卡券详情页
       */
      onTargetGoods(goodsId, type) {
		  if (type === 'P') {
			  this.$navTo(`pages/goods/detail`, { goodsId })
		  } else {
              return
		  }
      },
	  receive(goods_id) {
		  this.$success("领取成功！")
	  }
    }
  }
</script>

<style lang="scss" scoped>
  .diy-goods {
    .goods-list {
      padding: 4rpx;
      box-sizing: border-box;

      .goods-item {
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
            line-height: 1.3;
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

      &.display__slide {
        white-space: nowrap;
        font-size: 0;

        .goods-item {
          display: inline-block;
        }
      }

      &.display__list {
        .goods-item {
          float: left;
        }
      }

      &.column__2 {
        .goods-item {
          width: 50%;
        }
      }

      &.column__3 {
        .goods-item {
          width: 33.33333%;
        }
      }

      &.column__1 {
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
          width: 40%;
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
          width: 60%;

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
  }
</style>
