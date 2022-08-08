<template lang="pug">
  .profile.inner-page(v-if="getUsersInfo")
    .inner-page__main
      .profile__info
        profile-info(:info="getUsersInfo" :blocked="getUsersInfo.is_blocked" :friend="getUsersInfo.is_friend" :online="getUsersInfo.is_onlined")
      .profile__news
        .profile__tabs
          span.profile__tab.active {{ $t('posted') }} {{getUsersInfo.first_name}} ({{total}})
        .profile__news-list
          news-block(v-for="news in getWall" :key="news.id" :info="news")
        is-loading(v-if="total > offset" :isLoad='loading', v-load="loadWall")
    .inner-page__aside
      friends-request
      br
      friends-possible
</template>

<script>
import FriendsPossible from '@/components/Friends/Possible'
import ProfileInfo from '@/components/Profile/Info'
import FriendsRequest from '@/components/Friends/Request'
import NewsBlock from '@/components/News/Block'
import { mapActions, mapGetters, mapMutations } from 'vuex'
import isLoading from '@/components/isLoading'
export default {
  name: 'ProfileId',
  components: { FriendsPossible, FriendsRequest, ProfileInfo, NewsBlock, isLoading },
  data: () => ({
    loading: false,
    offset: 0,
    itemPerPage: 5,
    total: 0
  }),
  computed: {
    ...mapGetters('users/info', ['getUsersInfo', 'getWall'])
  },
  methods: {
    ...mapActions('users/info', ['userInfoId']),
    ...mapActions('profile/friends', ['apiFriends']),
    ...mapActions('users/info', ['apiWall']),
    ...mapMutations('users/info', ['setWall']),
    loadWall() {
      this.loading = true
      const data = {
        id: this.$route.params.id,
        payload: {
          offset: this.getWall.length,
          itemPerPage: this.itemPerPage
        }
      }

      this.userInfoId(data).then(resp => {
        this.total = resp
        this.offset = this.getWall.length
        this.loading = false
      })
    }
  },
  beforeRouteUpdate (to, from, next) {
    this.setWall([])
    this.loading = true
    const data = {
      id: to.params.id,
      payload: {
        offset: 0,
        itemPerPage: 5
      }
    }
    this.userInfoId(data).then(resp => {
      this.total = resp
      this.offset = this.getWall.length
      this.loading = false
      this.apiFriends()
    })
    next()
  },
  created() {
    this.setWall([])
    this.loadWall()
    this.apiFriends()
  },

  i18n: {
    messages: {
      en: {
        posted: 'Publications'
      },
      ru: {
        posted: 'Публикации'
      }
    }
  }
}
</script>
