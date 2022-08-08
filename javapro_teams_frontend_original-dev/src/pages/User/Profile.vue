<template lang="pug">
.profile.inner-page
    .inner-page__main
        .profile__info
            profile-info(me, online, :info='getInfo')
        .profile__news
            .profile__tabs
                span.profile__tab(
                    @click='changeTab("POSTED")',
                    :class='{ active: activeTab === "POSTED" }',
                    v-if='getWallPostedLength > 0'
                ) {{ $t("posted") }} ({{ getWallPostedLength }})
                span.profile__tab(
                    @click='changeTab("QUEUED")',
                    :class='{ active: activeTab === "QUEUED" }',
                    v-if='getWallQueuedLength > 0'
                ) {{ $t("queued") }} ({{ getWallQueuedLength }})
                span.profile__tab(
                    @click='changeTab("DELETED")',
                    :class='{ active: activeTab === "DELETED" }',
                    v-if='getWallDeletedLength > 0'
                ) {{ $t("deleted") }} ({{ getWallDeletedLength }})
            .profile__add
                news-add
            .profile__news-list
                news-block(
                    edit,
                    deleted,
                    :deffered='activeTab === "QUEUED"',
                    v-for='news in activeWall',
                    :key='news.id',
                    :info='news'
                )
            is-loading(v-if="total > offset" :isLoad='isLoad', v-load="loadWall")

    .inner-page__aside
        friends-request
        br
        friends-possible
</template>

<script>
import FriendsPossible from '@/components/Friends/Possible'
import FriendsRequest from '@/components/Friends/Request'
import ProfileInfo from '@/components/Profile/Info'
import NewsAdd from '@/components/News/Add'
import NewsBlock from '@/components/News/Block'
import { mapGetters, mapActions, mapMutations } from 'vuex'
import isLoading from '@/components/isLoading'
export default {
  name: 'Profile',
  components: { FriendsPossible, FriendsRequest, ProfileInfo, NewsAdd, NewsBlock, isLoading },
  data: () => ({
    activeTab: 'POSTED',
    offset: 0,
    itemPerPage: 5,
    isLoad: true,
    total: 0
  }),
  computed: {
    ...mapGetters('profile/info', ['getInfo']),
    ...mapGetters('users/info', ['getWall', 'getWallPostedLength', 'getWallQueuedLength', 'getWallDeletedLength']),
    activeWall() {
      let result = []
      for (let key in this.getWall) {
        if (this.getWall[key].type === this.activeTab) {
          result.push(this.getWall[key])
        }
      }
      return result //this.getWall.filter(el => el.type === this.activeTab)
    }
  },
  methods: {
    ...mapMutations('users/info', ['setWall']),
    ...mapActions('users/info', ['apiWall']),

    changeTab(tab) {
      this.activeTab = tab
    },

    loadWall() {
      this.isLoad = true
      const data = {
        id: this.getInfo.id,
        payload: {
          offset: this.offset,
          itemPerPage: this.itemPerPage
        }
      }

      this.apiWall(data).then(response => {
        this.total = response
        this.isLoad = false
        this.offset = this.offset + this.itemPerPage
      })
    }
  },

  watch: {
    getInfo() {
      this.loadWall()
    }
  },
  mounted() {
    if (this.getInfo) {
      this.setWall([])
      this.loadWall()
    }
  },
  i18n: {
    messages: {
      en: {
        posted: 'My publications',
        queued: 'Queued publication',
        deleted: 'Deleted publications'
      },
      ru: {
        posted: 'Мои публикации',
        queued: 'Отложенные публикации',
        deleted: 'Удаленные публикации'
      }
    }
  }
}
</script>
