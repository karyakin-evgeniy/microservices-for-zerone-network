<template lang="pug">
  .push-block
    .push__img
      img(:src="info.entity_author.photo" :alt="info.entity_author.first_name")
    p.push__content
      router-link.push__content-name(:to="getRouteByNotification(info)")
        | {{info.entity_author.first_name + ' ' + info.entity_author.last_name}}
        |
        | {{getNotificationsTextType(info.event_type)}}
    span.push__time {{info.sent_time | moment('from')}}
    .push__del(@click="readNotifications(info.id)")
      simple-svg(:filepath="'/static/img/delete.svg'")
</template>

<script>
import { mapGetters, mapActions } from 'vuex'
import { getRouteByNotification } from '@/utils/notifications.utils.js'
export default {
  name: 'PushBlock',
  props: {
    info: Object
  },
  computed: {
    ...mapGetters('profile/notifications', ['getNotificationsTextType'])
  },
  methods: {
    ...mapActions('profile/notifications', ['readNotifications']),
    getRouteByNotification
  }
}
</script>

<style lang="stylus">
.push-block {
  background: #fff;
  padding: 25px 30px;
  box-shadow: 0px 2px 60px rgba(0, 0, 0, 0.1);
  display: flex;
  justify-content: space-between;
  align-items: center;

  &+& {
    margin-top: 20px;
  }

  .push__img {
    flex: none;
  }

  .push__content {
    max-width: 650px;
  }

  .push__time {
    flex: none;
  }

  .push__del {
    width: 40px;
    height: 40px;
    margin-left: 40px;
    padding: 10px;
    transition: transform 0.4s;
  }

  .push__del:hover {
    transform: rotate(360deg)
  }
}
</style>
