<template lang="pug">
  .push(:class="{open: isOpen}")
    .push__overlay(@click.stop="closePush")
    .push__wrap(:class="{open: isOpen}" ref="wrap")
      .push__list(ref="list")
        .push__item(v-for="info in getNotifications.slice(0,10)" :key="info.id")
          .push__img
            router-link(:to='{ name: "ProfileId", params: { id: info.entity_author.id } }')
              img(:src="info.entity_author.photo" :alt="info.entity_author.first_name")
          p.push__content
            button.push__content-name(@click='routerLink(info)')
              |{{info.entity_author.first_name + ' ' + info.entity_author.last_name}}
              |
              |{{getNotificationsTextType(info.event_type)}}
          span.push__time {{info.sent_time | moment('from')}}
          .push__del(@click.stop="readNotifications(info.id)")
            simple-svg(:filepath="'/static/img/delete.svg'")
      router-link.push__btn(:to="{name: 'Push'}" v-if="getNotificationsLength > 1") {{ $t('show') }} ({{getNotificationsLength}})
      a.push__btn(href="#" v-if="getNotificationsLength > 1" @click="readNotifications()") {{ $t('delete') }} ({{getNotificationsLength}})
</template>

<script>
import { mapGetters, mapActions, mapMutations } from 'vuex'
import { getRouteByNotification } from '../../utils/notifications.utils'
export default {
  name: 'Push',
  props: {
    isOpen: Boolean
  },
  computed: {
    ...mapGetters('profile/notifications', ['getNotifications', 'getNotificationsLength', 'getNotificationsTextType'])
  },
  watch: {
    isOpen(val) {
      if (!val) {
        this.$refs.list.scrollTop = 0
      }
    },
  },
  methods: {
    ...mapActions('profile/notifications', ['apiNotifications', 'readNotifications']),
    ...mapMutations('profile/notifications', ['setOpenModal']),
    ...mapMutations('profile/dialogs', ['selectDialog']),
    getRouteByNotification,
    closePush() {
      if (!this.isOpen) return
      this.$emit('close-push')
    },
    routerLink(info){
      switch (info.event_type) {
        case 'MESSAGE':
          this.selectDialog(info.parent_entity_id)
          this.$router.push({ name: 'Im', params: { id: info.entity_id } })
          return
        case 'FRIEND_REQUEST':
          this.$router.push({ name: 'ProfileId', params: { id: info.entity_id } })
          return
        case 'POST_COMMENT':
        case 'COMMENT_COMMENT':
          console.log(info)
          console.log({entity_id :info.entity_id, parent_id: info.parent_entity_id })
          this.setOpenModal([{entity_id :info.entity_id, parent_id: info.parent_entity_id }])
          return
        default:
          return { name: 'ProfileId', params: { id: info.entity_id } }
      }

    },
    setActiveDialog(item){
      if(item.event_type == "MESSAGE"){
        this.selectDialog(item.parent_entity_id)
      }
    }
  },
  mounted() {
    if (this.getNotificationsLength === 0) this.apiNotifications()
    if (window.innerHeight - this.$refs.wrap.getBoundingClientRect().top - this.$refs.wrap.offsetHeight < 0) {
      this.$refs.wrap.style.maxHeight = `${window.innerHeight - this.$refs.wrap.getBoundingClientRect().top}px`
    }
    window.onscroll = () => {
      this.closePush()
    }
  },
  i18n: {
    messages: {
      "en": {
        "show": "Show all",
        "delete": "Delete all"
      },
      "ru": {
        "show": "Показать все",
        "delete": "Удалить все"
      }
    }
  },
}
</script>

<style lang="stylus">
@import '../../assets/stylus/base/vars.styl';

.push {
  .push__overlay {
    display: none;
  }

  &.open {
    .push__overlay {
      display: block;
    }
  }
}

.push__overlay {
  position: fixed;
  top: 0;
  left: 0;
  right: 0;
  bottom: 0;
  z-index: -1;
  cursor: default;
}

.push__wrap {
  position: fixed;
  background: #FFf;
  box-shadow: 0px 2px 60px rgba(0, 0, 0, 0.1);
  right: 50px;
  top: header-height;
  width: 100%;
  max-width: 710px;
  max-height: 675px;
  z-index: 100;
  opacity: 0;
  visibility: hidden;
  transform: translateY(-20px);
  transition: all 0.2s;
  overflow-y: auto;

  &.open {
    transform: translateY(0);
    opacity: 1;
    visibility: visible;
  }

  &:before, &:after {
    content: '';
    display: block;
    width: 19px;
    height: 38px;
    position: absolute;
    top: -16px;
  }

  &:before {
    background-image: linear-gradient(115deg, transparent 50%, #fff 50%);
    right: 223px;
  }

  &:after {
    background-image: linear-gradient(245deg, transparent 50%, #fff 50%);
    right: 205px;
  }
}

.push__list {
  overflow-y: auto;
  max-height: 543px;
}

.push__item {
  display: flex;
  align-items: center;
  padding: 10px 0;
  margin: 0 20px;

  &+& {
    border-top: 1px solid #E7E7E7;
  }
}

.push__content-name{
  cursor pointer
  background transparent
}

.push__content-name:hover {
  text-decoration: underline #000000;
}

.push__btn {
  display: flex;
  align-items: center;
  justify-content: center;
  font-weight: bold;
  font-size: 15px;
  letter-spacing: 0.01em;
  color: eucalypt;
  border-top: 1px solid #E7E7E7;
  height: 50px;
}

.push__btn:hover {
  text-decoration: underline eucalypt;
}

.push__del {
  width: 40px;
  height: 40px;
  margin-left: 20px;
  padding: 10px;
  transition: transform 0.4s;
}

.push__del:hover {
  transform: rotate(360deg)
}

</style>
