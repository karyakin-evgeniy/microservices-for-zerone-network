<template lang="pug">
  .im-chat(@click='resetCounterUnreadedDialogs')
    .im-chat__user
      router-link.im-chat__user-pic(:to="{name: 'ProfileId', params: {id: info.recipient_id.id}}")
        img(v-if='info.recipient_id.photo' :src="info.recipient_id.photo" :alt="info.recipient_id.first_name")
        img(v-else src="../../../static/img/user/default_avatar.svg" :alt="info.recipient_id.first_name")
      router-link.im-chat__user-name(:to="{name: 'ProfileId', params: {id: info.recipient_id.id}}") {{info.recipient_id.first_name + ' ' + info.recipient_id.last_name}}
      .typing(v-if='checkTypingMessage' )
        .typing__animations-block
          .typing__animations-item
          .typing__animations-item
          .typing__animations-item
        p.typing__text {{checkTypingMessage}} набирает сообщение
      span.user-status(:class="{online}") {{statusText}}
    .im-chat__infitite_list_wrapper(v-if='messages')
      virtual-list.im-chat__infitite_list.scroll-touch(:size="60"
        :keeps="120"
        :data-key="'sid'"
        :data-sources="messagesGrouped"
        :data-component="itemComponent"
        :wrap-class="'im-chat__message'"
        :root-tag="'section'"
        @totop="onScrollToTop"
        @scroll.passive="onScroll"
        @tobottom="onScrollToBottom"
        ref="vsl"
      )
        .im-chat__loader(slot="header", v-show="fetching")
          .spinner(v-show="!isHistoryEndReached()")
          .finished(v-show="isHistoryEndReached()") {{ $t('nomore') }}
    form.im-chat__enter(action="#" @submit.prevent="onSubmitMessage")
      input.im-chat__enter-input(type="text" @input='typingMessage' @blur='finishedTypingMessage' :placeholder="$t('placeholder')" v-model="mes")
      button-hover(@click.native="onSubmitMessage") {{ $t("Отправить") }}
</template>

<script>
import moment from 'moment'
import { mapActions, mapGetters, mapMutations } from 'vuex'
import ChatMessage from '@/components/Im/ChatMessage'
import VirtualList from 'vue-virtual-scroll-list'
import { submitFinishTypingMessage, submitTypingMessage } from '../../api/socetIO'

const makeHeader = msgDate => {
  return { sid: `group-${msgDate}`, stubDate: true, date: msgDate }
}
export default {
  name: 'ImChat',
  props: {
    info: Object,
    messages: Array,
    online: Boolean,
  },
  components: { VirtualList },
  data: () => ({
    mes: '',
    itemComponent: ChatMessage,
    isUserViewHistory: false,
    fetching: false,
    offset: 0,
    itemPrePage: 10,
  }),
  mounted() {
    this.follow = true;
    this.setVirtualListToBottom();
  },
  watch: {
    messages() {
      if (this.follow) this.setVirtualListToBottom()
    },
  },
  computed: {
    ...mapGetters('profile/dialogs', ['getTotalMessage', 'getActiveDialogId', 'getTypeMessage', 'getMessages']),
    ...mapGetters('profile/info', ['getInfo']),
    statusText() {
      return this.online
        ? this.$t('online')
        : this.$t('was') + moment(this.info.recipient_id.last_online_time).fromNow()
    },
    messagesGrouped() {
      let groups = []
      let headerDate = null
      this.messages.forEach( msg => {
        let msgDate = moment(msg.time).format('YYYY-MM-DD')
        if (msgDate !== headerDate) {
          headerDate = msgDate
          groups.push(makeHeader(headerDate))
        }
        groups.push(msg)
      })
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
    }
  },
  methods: {
    ...mapActions('profile/dialogs', ['postMessage', 'loadOlderMessages']),
    ...mapGetters('profile/dialogs', ['isHistoryEndReached']),
    ...mapMutations('profile/dialogs', ['setUnreadedDialogs']),
    onSubmitMessage() {
      if(this.mes == '') return
      this.postMessage({ id: this.info.id, message_text: this.mes })
      this.mes = ''
      this.finishedTypingMessage()
    },
    async onScrollToTop() {
      if (this.$refs.vsl) {

        if (this.messages.length < this.messages[0].total) {
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
        dialog: Number(this.getActiveDialogId)
      }
      submitFinishTypingMessage(data)
    },
    resetCounterUnreadedDialogs(){
      const params = {
        id: this.getActiveDialogId,
        unread_count: 0
      }
      this.setUnreadedDialogs(params)
    }
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

<style lang="stylus">
@import '../../assets/stylus/base/vars.styl';

.im-chat {
  position: relative;
  display: flex;
  flex-direction: column;
  height: 100%;
}

.im-chat__user {
  border-bottom: 1px solid #E3E8EE;
  display: flex;
  align-items: center;
  height: 60px;
  padding: 30px 20px;
  font-size: 13px;
  background-color: #F8FAFD;
}

.im-chat__user-pic {
  width: 40px;
  height: 40px;
  border-radius: 50%;
  overflow: hidden;
  margin-right: 10px;
}

.im-chat__user-name {
  font-weight: 600;
  color: steel-gray;
  margin-right: auto;
}

.typing{
  margin-right auto
  display flex
  &__animations-block{
    display flex
    align-items flex-end
    padding-bottom 2px
    margin-right 5px
  }

  &__animations-item{
      display block
      width 4px
      height 4px
      background grey
      border-radius 50%

      &:not(:last-child){
        margin-right 2px
      }

      animation-name: loadAnimations;
      animation-duration: 1.8s;
      animation-timing-function: linear;
      animation-iteration-count: infinite;

      &:nth-child(1) {
        -webkit-animation-delay: 0.0s;
      }

      &:nth-child(2) {
        -webkit-animation-delay: 0.6s;
      }

      &:nth-child(3) {
        -webkit-animation-delay: 1.2s;
      }
  }

  &__text{
    &:first-letter{
      text-transform uppercase
    }
  }
}

.im-chat__user-status {
  color: #9EA4AD;

  &.online {
    color: eucalypt;
  }
}

.im-chat__infitite_list_wrapper {
  position: relative;
  flex: 1;
}

.im-chat__infitite_list {
  position: absolute;
  top: 0;
  bottom: 0;
  left: 0;
  right: 0;
  overflow-y: auto;
}

.im-chat__message > div {
  padding-left: 30px;
  padding-right: 30px;
  padding: 20px;
}

.im-chat__enter {
  display: flex;
  width: 100%;
}

.im-chat__enter-input {
  width: 100%;
  background-color: #fff;
  padding: 0 40px;
  font-size: 15px;
  color: steel-gray;
  height: 60px;

  &::placeholder {
    color: #B0B0BC;
  }
}


.im-chat__loader {
  padding: 1em;
  .finished {
    font-size: 14px;
    text-align: center;
    color: #bfbfbf;
  }
  .spinner {
    font-size: 10px;
    margin: 0px auto;
    text-indent: -9999em;
    width: 25px;
    height: 25px;
    border-radius: 50%;
    background: #ffffff;
    background: linear-gradient(to right, eucalypt 10%, rgba(255, 255, 255, 0) 42%);
    position: relative;
    animation: load3 1.4s infinite linear;
    transform: translateZ(0);
  }
  .spinner:before {
    width: 50%;
    height: 50%;
    background: eucalypt;
    border-radius: 100% 0 0 0;
    position: absolute;
    top: 0;
    left: 0;
    content: '';
  }
  .spinner:after {
    background: #f8fafd;
    width: 75%;
    height: 75%;
    border-radius: 50%;
    content: '';
    margin: auto;
    position: absolute;
    top: 0;
    left: 0;
    bottom: 0;
    right: 0;
  }
  @-webkit-keyframes load3 {
    0% {
      transform: rotate(0deg);
    }
    100% {
      transform: rotate(360deg);
    }
  }
  @keyframes load3 {
    0% {
      transform: rotate(0deg);
    }
    100% {
      transform: rotate(360deg);
    }
  }
}

@keyframes loadAnimations {
  0% {
    opacity: 0.3;
    transition: opacity 0.3s;
  }

  70% {
    opacity: 1;
    transition: opacity 0.3s;
  }
}
</style>
