<template lang='pug'>
  VueDragResize.chat(:isActive="dragFunctions.isActive" :minw='minW' :w='minW' :h='minH' :minh='minH' :isDraggable="dragFunctions.isDragon" :isResizable="dragFunctions.isResize" @resizing="dragFunctions.resize" @dragging="dragFunctions.dragon" :y='y' :x="x" :z='100')
    .chat__dragging-block(@mousedown='dragTrue' @mouseup='dragFalse')
    .chat__wraper
      .chat__header
        router-link.chat__link-img(:to="{name: 'ProfileId', params: {id: dialog[0].recipient_id.id}}")
          img.chat__img(v-if='dialog[0].recipient_id.photo' :src='dialog[0].recipient_id.photo' :alt='dialog[0].recipient_id.first_name')
          img.chat__img(v-else src='../../../static/img/user/default_avatar.svg' :alt='dialog[0].recipient_id.first_name')
        .chat__info-container
          p.chat__name {{dialog[0].recipient_id.first_name}} {{dialog[0].recipient_id.last_name}}
          span.user-status(:class="{online: checkOnlineUser(dialog[0].recipient_id.last_online_time)}") {{statusText}}
        button.chat__btn-close(@click='closeChat')
      .chat__main
        virtual-list.chat__infitite_list.scroll-touch(:size="60"
          :keeps="120"
          :data-key="'sid'"
          :data-sources="messagesGrouped"
          :data-component="itemComponent"
          :wrap-class="'chat__message'"
          :root-tag="'section'"
          @totop="onScrollToTop"
          @scroll.passive="onScroll"
          @tobottom="onScrollToBottom"
          ref="vsl"
        )
          .im-chat__loader(slot="header", v-show="fetching")
            .spinner(v-show="!isHistoryEndReached()")
            .finished(v-show="isHistoryEndReached()") {{ $t('nomore') }}
      form.chat__footer(@submit.prevent="onSubmitMessage")
        input.chat__input(@click='focus' type="text" @input='typingMessage' @blur='finishedTypingMessage' :placeholder="$t('placeholder')" v-model="mes")
        button.chat__btn(type='submit')
    .chat__dragging-bottom-block(@mousedown='dragResizeTrue' @mouseup='dragResizeFalse')
      .span
</template>

<script>
import { mapActions, mapGetters, mapMutations } from 'vuex'
import VueDragResize from 'vue-drag-resize'
import ChatMessage from '@/components/Im/ChatMessage'
import moment from 'moment'
import VirtualList from 'vue-virtual-scroll-list'
import { submitFinishTypingMessage, submitTypingMessage } from '../../api/socetIO'

const makeHeader = msgDate => {
  return { sid: `group-${msgDate}`, stubDate: true, date: msgDate }
}

export default {
    name: 'miniChat',
    components: {
      VueDragResize,
      VirtualList
    },
    props: {
      chatID:{
        type: Number
      },
      close:{
        type: Function
      }
    },
    data: () => ({
      x: window.innerWidth - 350,
      y: window.innerHeight - 400,
      minW: 300,
      minH: 350,
      dragStatus: false,
      resizeStatus: false,

      mes: '',
      itemComponent: ChatMessage,
      isUserViewHistory: false,
      fetching: false,
      offset: 0,
      itemPrePage: 10,
    }),
    computed:{
      ...mapGetters('profile/dialogs', ['getDialogs', 'getMessages']),
      ...mapGetters('profile/info', ['getInfo']),
        dialog(){
          return this.getDialogs.filter(i => i.id == this.chatID)
        },
        dialogMessages(){
          return this.getMessages[this.chatID] ? this.getMessages[this.chatID] : []
        },
        statusText() {
        const time = this.getDialogs[0].recipient_id.last_online_time;
          return this.checkOnlineUser(time)
            ? this.$t('online')
            : this.$t('was') + moment(time).fromNow()
        },
        messagesGrouped() {
        const message = this.dialogMessages;
        let groups = []
        let headerDate = null
        for (const msg of message) {
          let msgDate = moment(msg.time).format('YYYY-MM-DD')
          if (msgDate !== headerDate) {
            headerDate = msgDate
            groups.push(makeHeader(headerDate))
          }
          groups.push(msg)
        }
        return groups
      },
        checkTypingMessage() {
        let user
        this.getTypeMessage.forEach(el=> {
          if (el.dialog == this.getActiveDialogId) {
            user = el.author
          }
        })
        return user
      },
        dragFunctions(){
          if(this.dragStatus){
            return {
              isActive: true,
              isDragon: true,
              isResize: false,
              dragon: this.resize,
              resize: function(){
                return
              }
            }
          }
          if(this.resizeStatus){
            return {
              isActive: true,
              isDragon: false,
              isResize: true,
              dragon: function(){
              return
            },
              resize: this.resize,
            }
          }
          return {
            isActive: false,
            isDragon: false,
            isResize: false,
            dragon: function(){
              return
            },
            resize: function(){
              return
            },
          }
        }
      },
    methods: {
      ...mapActions('profile/dialogs', ['loadOlderMessages', 'postMessage', 'apiLoadAllDialogs', 'loadFreshMessages']),
      ...mapMutations('profile/dialogs', ['setUnreadedDialogs']),
      ...mapGetters('profile/dialogs', ['isHistoryEndReached']),

      onSubmitMessage() {
        if(this.mes == '') return
        this.postMessage({ id: this.chatID, message_text: this.mes, })
        this.mes = ''
        this.finishedTypingMessage()
      },
      async onScrollToTop() {
        if (this.$refs.vsl) {
          if (this.dialogMessages.length < this.dialogMessages[0].total) {
            let oldest = this.messagesGrouped[0]

            this.fetching = true
            await this.loadOlderMessages({itemPerPage: this.itemPrePage, offset: this.offset + this.itemPrePage}).then(()=>{
              this.offset += this.itemPrePage;
            })
            this.setVirtualListToOffset(1)

            this.$nextTick(() => {
              const scrollSize = this.$refs.vsl.getScrollSize()
              const scrollOffset = scrollSize / this.offset;
              this.setVirtualListToOffset(scrollOffset * this.itemPrePage)
              this.fetching = false
            })
          }
        }
      },
      onScroll() {
        this.follow = false
      },
      onScrollToBottom() {
        this.follow = true
      },
      setVirtualListToOffset(offset) {
        if (this.$refs.vsl) {
          this.$refs.vsl.scrollToOffset(offset)
        }
      },
      setVirtualListToBottom() {
        if (this.$refs.vsl) {
          this.$refs.vsl.scrollToBottom()
        }
      },
      typingMessage(){
        if(this.mes){
          const data = {
            author: this.getInfo.id,
            dialog: this.getActiveDialogId
          }
          submitTypingMessage(data)
        }
      },
      finishedTypingMessage(){
        const data = {
          author: Number(this.getInfo.id),
          dialog: Number(this.chatID)
        }
        submitFinishTypingMessage(data)
      },
      resetCounterUnreadedDialogs(){
        const params = {
          id: this.getActiveDialogId,
          unread_count: 0
        }
        this.setUnreadedDialogs(params)
      },

      checkOnlineUser(time) {
        return moment().diff(moment(time), 'seconds') <= 60
      },
      closeChat(){
        this.close(this.chatID)
      },
      resize(newRect) {
        this.width = newRect.width;
        this.height = newRect.height;
        this.top = newRect.top;
        this.left = newRect.left;
      },
      dragTrue(){
        this.dragStatus = true
      },
      dragFalse(){
        this.dragStatus = false
      },
      dragResizeTrue(){
        this.resizeStatus = true
      },
      dragResizeFalse(){
        this.resizeStatus = false
      },
      focus(e){
        e.target.focus()
      }
    },
    watch: {
      dialogMessages() {
        if (this.follow) this.setVirtualListToBottom()
      },
    },

    mounted() {
      const params = {
        id: this.chatID,
        unread_count: 0
      }
      this.setUnreadedDialogs(params)
      this.setVirtualListToBottom()

      this.apiLoadAllDialogs()
      this.loadFreshMessages({id:this.chatID})
    },
    i18n: {
      messages: {
        en: {
          nomore: 'No more messages',
          placeholder: 'Your message...',
          online: 'Online',
          was: 'was online '
        },
        ru: {
          nomore: 'Больше сообщений нет',
          placeholder: 'Ваше сообщение...',
          online: 'Онлайн',
          was: 'был в сети '
        }
      }
    }
  }

</script>

<style lang='stylus'>
@import '../../assets/stylus/base/vars.styl';
.chat{
  position fixed
  background white
  border-radius 10px
  overflow hidden

  border 1px solid #f1f3f9
  box-shadow: 0px 2px 8px rgb(0 0 0 / 8%);

  &__dragging-block{
    width 100%
    height 20px
    cursor move
    background #21a45d
  }

  &__wraper{
    position relative
    padding 5px
    display grid
    grid-template-rows 50px 1fr 40px
    grid-row-gap 5px
    height 95%
  }

  &__header{
    display flex
    background #f2f5f9
    padding 5px
    border-radius 5px
  }

  &__img{
    width auto
    height 100%
    border-radius 100%
    margin-right 20px
  }

  &__info-container{
    margin-right auto
  }

  &__name{
    margin-bottom 3px
    font-weight 600
  }

  &__onlie {
    text-align right
    color: #9EA4AD;
  }

  &__btn-close{
    width 20px
    height 20px
    background transparent
    border none
    outline none

    position relative
    cursor pointer

    transition transform 0.3s

    &:before,
    &:after{
      content ''
      width 100%
      height 1px
      display block
      background black
      position absolute
      top 50%
      left 50%

      transition background 0.3s
    }

    &:before{
      transform translate(-50%, -50%) rotate(45deg)
    }

    &:after{
      transform translate(-50%, -50%) rotate(-45deg)
    }

    &:hover:before,
    &:hover:after,
    &:focus:before,
    &:focus:after{
      background red
      transition background 0.3s
    }

    &:active{
      transform scale(0.8)
      transition transform 0.3s
    }
  }

  &__dragging-bottom-block{
    position absolute
    bottom 0
    right 0
    width 15px
    height 15px
    background transparent

    &:after,
    &:before{
      content ''
      position absolute
      display block
      width 100%
      height 1px
      background black
      overflow hidden
    }

    &:after{
      width 100%

      top 40%
      left 40%
      transform translate(-50%, -50%) rotate(-45deg)
    }

    &:before{
      width 60%

      top 60%
      left 60%
      transform translate(-50%, -50%) rotate(-45deg)
    }
  }

  &__main{
    overflow hidden
    background #f2f5f9
    padding 5px
    border-radius 5px
  }

  &__footer{
    background #f2f5f9
    padding 5px
    border-radius 5px
    position relative
  }

  &__input{
    width 100%
    border-radius 5px
    height 100%
    padding-left 5px
    padding-right 40px
  }

  &__btn{
    position absolute
    cursor pointer
    top 50%
    right 15px
    transform translateY(-50%) rotate(-90deg)

    width 25px
    height 25px
    background url("../../../static/img/miniChat/btn.png")
    background-position center
    background-size contain
    background-repeat no-repeat
  }
}

.online {
  color: eucalypt;
}

.chat__infitite_list{
  overflow-y auto
  height 100%
}

.chat__message > div {
  &:not(:last-child){
    margin-bottom 10px
  }
}
.chat__message > div .im-chat__message-text{
  padding 5px 15px
}
.chat__message > div .im-chat__message-time{
    margin-left 0
  }
</style>
