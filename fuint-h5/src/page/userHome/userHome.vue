<template>
  <transition name="slide-fade">
    <div class="wrap">
      <div class="baise">
        <div class="member-info">
             <div class="member-type">钻石会员</div>
             <span class="member-no">898293200</span>
        </div>
        <div class="member-amount">
             <div class="amount-tip">余额（元）</div>
             <div class="amount">899.00</div>
        </div>
      </div>
      <div class="main-tool">
            <div class="tool" @click="toJump('/')">
                <div class="number">6</div>
                <span class="text">我的卡券</span>
            </div>
            <div class="tool"  @click="toJump('/increaseDetail')">
                <div class="number">2</div>
                <span class="text">转赠记录</span>
            </div>
            <div class="tool"  @click="toJump('/')">
                <div class="number">18</div>
                <span class="text">可领取券</span>
            </div>
      </div>
    </div>
  </transition>
</template>
<style lang="sass" scoped>
  @import "userHome"
</style>
<script type="text/javascript" src="./userHomeController.js"></script>
