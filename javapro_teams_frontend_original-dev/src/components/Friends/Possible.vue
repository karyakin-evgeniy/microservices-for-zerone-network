<template lang="pug">
  .friends-possible(v-if="possibleFriends.length > 0")
    h4.friends-possible__title {{ $t('title') }}
    ul.friends-possible__list
      li.friends-possible__item(v-for="user in possibleFriends" :key="user.id")
        .friends-possible__pic
          img(:src="user.photo" :alt="user.first_name")
        router-link.friends-possible__name(:to="{name: 'ProfileId', params: {id: user.id}}") {{user.first_name + ' ' + user.last_name}}
        a.friends-possible__link(href="#" @click.prevent="apiAddFriends(user.id)") {{ $t('add') }}
    router-link.friends-possible__btn(:to="{name: 'FriendsFind'}")
      simple-svg(:filepath="'/static/img/search.svg'")
      span.friends-possible__link {{ $t('find') }}
</template>

<script>
import { mapGetters, mapActions } from 'vuex'
export default {
  name: 'FriendsPossible',
  computed: {
    ...mapGetters('profile/friends', ['getResultById']),
    possibleFriends() {
      return this.getResultById('recommendations')
    }
  },
  methods: {
    ...mapActions('profile/friends', ['apiAddFriends', 'apiRecommendations'])
  },
  mounted() {
    if (this.possibleFriends.length === 0) this.apiRecommendations()
  },
  i18n: {
    messages: {
      en: {
        title: 'Maybe you know them',
        add: 'Add',
        find: 'Find friends'
      },
      ru: {
        title: 'Возможно вы их знаете',
        add: 'Добавить',
        find: 'Искать друзей'
      }
    }
  }
}
</script>
