<template lang="pug">
  // Please avoid to use margin between messages block
  // or it will glitter on scroll
  h5.im-chat__message-title(v-if='source.stubDate') {{source.date | moment('DD MMMM YYYY')}}
  .im-chat__message-block(v-else :class="{me: source.sendByMe}")
      p.im-chat__message-text {{source.message_text}}
      span.im-chat__message-time {{source.time | moment('YYYY-MM-DD hh:mm')}}
</template>

<script>
import moment from 'moment'

export default {
  name: 'infinite-loading-item',
  props: {
    source: {
      type: Object,
      default () {
        return {}
      }
    }
  },
}
</script>

<style lang="stylus" scoped>
@import '../../assets/stylus/base/vars.styl';


.im-chat__message-day {
  &+& {
    margin-top: 50px;
  }
}

.im-chat__message-title {
  color: #8A94A4;
  font-size: 15px;
  display: block;
  width: 100%;
}

.im-chat__message-block {
  display: flex;
  flex-direction: column !important;
  max-width: 460px;

  &:_me{
      align-items: start;
  }

  &:_interlocutor{
    align-items: end;
  }

  &+& {
    margin-top: 20px;
  }

  &.me + & {
    margin-top: 30px;
  }

  &.me {
    flex-direction: row-reverse;
    max-width: 450px;
    margin-left: auto;

    .im-chat__message-text {
      display: inline;
      background-color: eucalypt;
      color: #fff;
      box-shadow: 0px 4px 15px #D4E8DD;
      margin-left: auto !important;
      margin-right: 0;
      text-align: end;
    }

    .im-chat__message-time {
      margin-top: 5px;
      margin-left: auto !important;
    }
  }
}

.im-chat__message-text {
  background-color: #FFFFFF;
  box-shadow: 0px 4px 15px #EFF1F6;
  border-radius: 20px;
  padding: 15px 20px;
  color: steel-gray;
  font-size: 15px;
  line-height: 23px;
  margin-right: auto;
}

.im-chat__message-time {
              margin-top: 5px;
  font-size: 13px;
  color: #BAC3D0;
  margin-left: 15px;
}
</style>
