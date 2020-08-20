import service from '@/service/serviceApi.js'
import qs from 'qs'
import { MessageBox } from 'mint-ui'
export default {
  data () {
    return {
      times: 60,
      showButton: true,
      paramObj: {
        mobile: '',
        verifyCode: ''
      },
      jumpUrl: null,
      jumpQuery: null
    }
  },
  mounted () {
    this.jumpUrl = this.$route.query.frompath
    if (this.$route.query.param) {
      this.jumpQuery = JSON.parse(this.$route.query.param)
    }
    console.log(this.jumpQuery)
  },
  created () {
    document.title = '会员中心 - FuInt会员系统'
  },
  methods: {
    toJump (url) {
      this.$router.push({path: url})
    },
    // 输入框重置
    resetParam (objName) {
      this.paramObj[objName] = ''
    },
    // 发送短信验证码
    sendCode () {
      let self = this
      if (this.paramObj.mobile === '') {
        // MessageBox.alert('请填写手机号码', '温馨提示')
        return false
      }
      if (!this.showButton) {
        return false
      }
      service.postWithNoToken('/rest/sms/doSendVeryfiCode', {'mobile': this.paramObj.mobile}).then(
        result => {
          // console.log(result)
          if (result.data.code === 200) {
            self.showButton = false
            let a = setInterval(() => {
              if (self.times === 0) {
                self.showButton = true
                window.clearInterval(a)
                self.times = 60
                return false
              }
              self.times--
            }, 1000);
          } else {
            this.$MessageBox({
              title: '温馨提示',
              message: result.data.message, // 提示的内容，作为参数，传进来
              closeOnClickModal: true	// 表示不只是点击确定按钮才能关闭弹窗，点击页面的任何地方都可以关闭弹窗
            })
          }
        }
      ).catch(error => {
        if (error.response.status === 555) {
          this.$MessageBox({
            title: '温馨提示',
            message: '系统繁忙，请稍后重试', // 提示的内容，作为参数，传进来
            closeOnClickModal: true	// 表示不只是点击确定按钮才能关闭弹窗，点击页面的任何地方都可以关闭弹窗
          })
        }
      })
    },
    // 登录
    login () {
      if (this.paramObj.verifyCode === '') {
        // MessageBox.alert('请填写短信验证码', '温馨提示')
        this.$MessageBox({
          title: '温馨提示',
          message: '请填写短信验证码', // 提示的内容，作为参数，传进来
          closeOnClickModal: true	// 表示不只是点击确定按钮才能关闭弹窗，点击页面的任何地方都可以关闭弹窗
        })
        return false
      }

      service.postWithNoToken('/rest/sign/doSign', this.paramObj).then(
        result => {
          // console.log(result)
          if (result.data.code === 200) {
            let res = result.data.data
            console.log(res)
            this.$cookies.set('mobile', this.paramObj.mobile)
            this.$cookies.set('storeToken', res.token)
            if (this.jumpUrl) {
              this.$router.push({'path': '/' + this.jumpUrl, query: this.jumpQuery})
            } else {
              this.$router.push({'path': '/home'})
            }
          } else {
            this.$MessageBox({
              title: '温馨提示',
              message: result.data.message, // 提示的内容，作为参数，传进来
              closeOnClickModal: true	// 表示不只是点击确定按钮才能关闭弹窗，点击页面的任何地方都可以关闭弹窗
            })
          }
        }
      ).catch(error => {
        this.$MessageBox({
          title: '温馨提示',
          message: '系统繁忙，请稍后重试', // 提示的内容，作为参数，传进来
          closeOnClickModal: true	// 表示不只是点击确定按钮才能关闭弹窗，点击页面的任何地方都可以关闭弹窗
        })
      })
    }
  }
}
