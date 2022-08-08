<template lang="pug">
.news.inner-page
    .inner-page__main
        .news__add
            news-add(user)
        .news__list(v-if='getInfo')      
            news-block(
                v-for='feed in getFeeds',
                :key='feed.id',
                :info='feed',
                :edit='getInfo.id === feed.author.id',
                :deleted='getInfo.id === feed.author.id'
            )
        is-loading(v-if="total > offset" :isLoad='isLoad', v-load="loadFeeds")

    .inner-page__aside
        friends-request
        br
        friends-possible
</template>

<script>
import { mapGetters, mapActions, mapMutations } from 'vuex'
import FriendsPossible from '@/components/Friends/Possible'
import FriendsRequest from '@/components/Friends/Request'
import NewsBlock from '@/components/News/Block'
import NewsAdd from '@/components/News/Add'
import isLoading from '@/components/isLoading'
export default {
  name: 'News',
  components: { FriendsPossible, FriendsRequest, NewsBlock, NewsAdd, isLoading },
  data: () => ({
    offset: 0,
    itemPerPage: 5,
    isLoad: true,
    total: 0
  }),
  computed: {
    ...mapGetters('profile/feeds', ['getFeeds']),
    ...mapGetters('profile/info', ['getInfo'])
  },
  methods: {
    ...mapActions('profile/feeds', ['apiFeeds']),
    ...mapMutations('profile/feeds', ['setFeeds']),
    loadFeeds() {
      this.isLoad = true

      this.apiFeeds({ offset: this.offset, itemPerPage: this.itemPerPage }).then(response => {
        console.log(response)
        this.total = response
        this.isLoad = false
        this.offset = this.offset + this.itemPerPage
      })
    }
  },
  mounted() {
    this.setFeeds([])
    this.loadFeeds()
  }
}
</script>

<style lang="stylus">
@import '../../assets/stylus/base/vars.styl';

.news__add {
    margin-bottom: 30px;
}
</style>
