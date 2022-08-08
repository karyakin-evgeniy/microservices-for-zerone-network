<template lang="pug">
.friends.inner-page
    .inner-page__main
        .friends__header
            h2.friends__title {{ $t("myFriends") }}
            .friends__search
                .friends__search-icon
                    simple-svg(:filepath='"/static/img/search.svg"')
                input.friends__search-input(:placeholder='$t("enterFriend")', v-model='first_name')
        .friends__list
            template(v-if="friends.length > 0")
                friends-block(friend, v-for='friend in friends', :key='friend.id', :info='friend')
        is-loading(v-if="total > offset", :isLoad='isLoad', v-load="loadFrends")
    .inner-page__aside
        friends-request
        br
        friends-possible
</template>

<script>
import { mapGetters, mapActions, mapMutations } from 'vuex'
import FriendsPossible from '@/components/Friends/Possible'
import FriendsRequest from '@/components/Friends/Request'
import FriendsBlock from '@/components/Friends/Block'
import isLoading from '@/components/isLoading'

export default {
  name: 'Friends',
  components: { FriendsPossible, FriendsRequest, FriendsBlock, isLoading },
  data: () => ({
    first_name: '',
    isLoad: true,
    offset: 0,
    itemPerPage: 10,
    total: 0
  }),
  computed: {
    ...mapGetters('profile/friends', ['getResultById']),
    friends() {
      return this.first_name.length === 0
        ? this.getResultById('friends')
        : this.getResultById('friends').filter(
            el => el.first_name.toLowerCase().indexOf(this.first_name.toLowerCase()) !== -1
          )
    }
  },
  methods: {
    ...mapActions('profile/friends', ['apiFriends', 'apiRequest']),
    ...mapMutations('profile/friends', ['setResult']),

    loadFrends() {
      this.isLoad = true
      this.apiFriends({ offset: this.offset, itemPerPage: this.itemPerPage }).then(response => {
        this.total = response
        this.isLoad = false
        this.offset = this.offset + this.itemPerPage
      })
    }
  },
  mounted() {
    this.setResult({
      id: 'friends',
      value: []
    })
    this.loadFrends()
  },

  i18n: {
    messages: {
      en: {
        myFriends: 'My friends',
        enterFriend: "Start typing your friend's name ..."
      },
      ru: {
        myFriends: 'Мои друзья',
        enterFriend: 'Начните вводить имя друга...'
      }
    }
  }
}
</script>

<style lang="stylus">
@import '../../assets/stylus/base/vars.styl';
</style>
