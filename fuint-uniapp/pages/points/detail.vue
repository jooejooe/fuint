<template>
  <view class="container">
    <mescroll-body ref="mescrollRef" :sticky="true" @init="mescrollInit" :down="{ use: false }" :up="upOption"
      @up="upCallback">
      <view class="log-list">
        <view v-for="(item, index) in list.content" :key="index" class="log-item">
          <view class="item-left flex-box">
            <view class="rec-status">
              <text>{{ item.description }}</text>
            </view>
            <view class="rec-time">
              <text>{{ timeStamp(item.createTime) }}</text>
            </view>
          </view>
          <view class="item-right" :class="[item.amount > 0 ? 'col-green' : 'col-6']">
            <text>{{ item.amount > 0 ? '+' : '' }}{{ item.amount }}</text>
          </view>
        </view>
		<empty v-if="!list.content.length" :isLoading="isLoading" :custom-style="{ padding: '180rpx 50rpx' }" tips="暂时没有积分记录">
		</empty>
      </view>
    </mescroll-body>
  </view>
</template>

<script>
  import MescrollBody from '@/components/mescroll-uni/mescroll-body.vue'
  import MescrollMixin from '@/components/mescroll-uni/mescroll-mixins'
  import * as LogApi from '@/api/points/log'
  import { getEmptyPaginateObj, getMoreListData } from '@/utils/app'
  import Empty from '@/components/empty'

  const pageSize = 15

  export default {
    components: {
      MescrollBody,
	  Empty
    },
    mixins: [MescrollMixin],
    data() {
      return {
        // 数据记录
        list: getEmptyPaginateObj(),
		// 正在加载
		isLoading: false,
        // 上拉加载配置
        upOption: {
          // 首次自动执行
          auto: true,
          // 每页数据的数量; 默认10
          page: { size: pageSize },
          // 数量要大于12条才显示无更多数据
          noMoreSize: 12,
          // 空布局
          empty: {
            tip: '亲，暂无相关数据'
          }
        }
      }
    },

    /**
     * 生命周期函数--监听页面加载
     */
    onLoad(options) {},

    methods: {

      /**
       * 上拉加载的回调 (页面初始化时也会执行一次)
       * 其中page.num:当前页 从1开始, page.size:每页数据条数,默认10
       * @param {Object} page
       */
      upCallback(page) {
        const app = this
        // 设置列表数据
        app.getLogList(page.num)
          .then(list => {
            const curPageLen = list.data.length
            const totalSize = list.data.totalPages
            app.mescroll.endBySize(curPageLen, totalSize)
          })
          .catch(() => app.mescroll.endErr())
      },

      // 获取积分明细列表
      getLogList(pageNo = 1) {
        const app = this
        return new Promise((resolve, reject) => {
          LogApi.list({ page: pageNo })
            .then(result => {
              // 合并新数据
              const newList = result.data
              app.list.content = getMoreListData(newList, app.list, pageNo)
              resolve(newList)
            })
        })
      },
	  timeStamp: function(value) {
		  var date = new Date(value);
		  var year = date.getFullYear();
		  var month = ("0" + (date.getMonth() + 1)).slice(-2);
		  var sdate = ("0" + date.getDate()).slice(-2);
		  var hour = ("0" + date.getHours()).slice(-2);
		  var minute = ("0" + date.getMinutes()).slice(-2);
		  var second = ("0" + date.getSeconds()).slice(-2);
		  // 拼接
		  var result = year + "." + month + "." + sdate + " " + hour + ":" + minute //+ ":" + second;
		  // 返回
		  return result;
	  }
    }
  }
</script>

<style lang="scss" scoped>
  .container {
    background: #fff;
  }

  .log-list {
    padding: 0 30rpx;
  }

  .log-item {
    font-size: 28rpx;
    padding: 20rpx 20rpx;
    line-height: 1.8;
    border-bottom: 1rpx solid rgb(238, 238, 238);
    display: flex;
    justify-content: center;
    align-items: center;
  }

  .rec-status {
    color: #333;

    .rec-time {
      color: rgb(160, 160, 160);
      font-size: 26rpx;
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
</style>
