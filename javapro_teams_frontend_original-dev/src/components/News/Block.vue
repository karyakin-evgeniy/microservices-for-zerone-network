<template lang="pug">
.news-block(:class='{ deffered, "news-block--admin": admin }')
    add-form(v-if='isEditNews', :info='info', edit, :deffered='deffered', @submit-complete='toggleEditNews')
    template(v-else)
        template(v-if='!admin')
            .edit
                .edit__icon(v-if='deleted && info.type === "POSTED"', @click='deleteNews')
                    simple-svg(:filepath='"/static/img/delete-news.svg"')
                .edit__recover(v-if='deleted && info.type === "DELETED"', @click='recoverNews')
                    a(href='#', @click.prevent='onRecoverComment') {{ $t("restore") }}
                .edit__icon(v-if='edit', @click='toggleEditNews')
                    simple-svg(:filepath='"/static/img/edit.svg"')
        template(v-else)
            .edit(v-tooltip.bottom='\'Разблокировать\'', v-if='blocked')
                simple-svg(:filepath='"/static/img/unblocked.svg"')
            .edit(v-tooltip.bottom='\'Заблокировать\'', v-else)
                simple-svg(:filepath='"/static/img/blocked.svg"')
        .news-block__deffered(v-if='deffered')
            span.news-block__deffered-text {{ $t("date") }}: {{ info.time | moment("DD.MM.YYYY, HH:mm") }} ({{ diffTime(info.time * 1000) }})
        .news-block__author(v-if='!deffered')
            router-link.news-block__author-pic(:to='{ name: "ProfileId", params: { id: info.author.id } }')
                img(:src='info.author.photo || "../static/img/user/default_avatar.svg"', :alt='info.author.first_name')
            .news-block__author-info
                router-link.news-block__author-name(:to='{ name: "ProfileId", params: { id: info.author.id } }') {{ info.author.first_name + " " + info.author.last_name }}
                span.news-block__author-time {{ info.time | moment("from") }}
        .news-block__content
            .news-block__content-main
                h3.news-block__content-title {{ info.title }}
                p.news-block__content-text(
                    ref='text',
                    :class='{ lotText: isLotText, open: openText }',
                    v-html='info.post_text'
                )
                a.news-block__content-more(href='#', v-if='isLotText', @click.prevent='toggleText')
                    template(v-if='openText') {{ $t("hide") }}
                    template(v-else) {{ $t("read") }}

                .news-block__tags-block(v-if='info.tags.length > 0')
                    //- h3.news-block__tags-title тэги
                    ul.news-block__content-tags
                        template(v-if='!isOpenTags')
                            li.news-block__item-tag(v-for='(tag, index) in info.tags.slice(0, 5)', :key='index')
                                a.news-block__content-tag(
                                  :class='{ "news-block__content-tag_close": !isOpenTags }'
                                  @click.prevent="goToSearch(tag)"
                                ) {{ "#" + tag }}
                        template(v-else)
                            li.news-block__item-tag(v-for='(tag, index) in info.tags', :key='index')
                                a.news-block__content-tag(
                                  :class='{ "news-block__content-tag_close": !isOpenTags }'
                                  @click.prevent="goToSearch(tag)"
                                ) {{ "#" + tag }}

                        li.news-block__item-tag(v-if='info.tags.length > 5')
                            a.news-block__content-tag(@click.prevent='openCloseAllTags') {{ showTextClose }}

        .news-block__actions(v-if='!deffered && !admin')
            .news-block__actions-block
                like-comment(
                    :quantity='info.likes',
                    width='16px',
                    height='16px',
                    font-size='15px',
                    @liked='likeAction',
                    :active='info.my_like',
                    :id='info.id',
                    :isPost='true'
                )
            .news-block__actions-block
                like-comment(:quantity='commentsLength', width='16px', height='16px', font-size='15px', comment)

        .news-block__comments(v-if='!deffered')
            comments(:admin='admin', :info='info.comments', :id='info.id', :edit='edit', :deleted='deleted' :commentOpen='commentOpen')
</template>

<script>
import AddForm from '@/components/News/AddForm'
import { mapActions, mapGetters, mapMutations } from 'vuex'
import moment from 'moment'
import Comments from '@/components/Comments/Index'
import LikeComment from '@/components/LikeComment'
import reportComment from '@/components/reportComment'
export default {
  name: 'NewsBlock',
  components: { Comments, LikeComment, AddForm, reportComment },
  props: {
    info: {
      type: Object,
      default: () => ({
        title: 'Дизайн привычных вещей',
        post_text:
          'А вот и «Книга недели от Skillbox и МИФ». Сегодня делимся с вами книгой «Дизайн привычных вещей» автора Дональда Нормана. В ней Дональд рассказывает об основополагающих принципах, которым нужно следовать, чтобы избежать проблем и превратить привычные вещи в приятные товары, доставляющие нам удовольствие А вот и «Книга недели от Skillbox и МИФ». Сегодня делимся с вами книгой «Дизайн привычных вещей» автора Дональда Нормана. В ней Дональд рассказывает об основополагающих принципах, которым нужно следовать, чтобы избежать проблем и превратить привычные вещи в приятные товары, доставляющие нам удовольствие',
        time: 1559751301818,
        likes: 44,
        id: 1,
        tags: ['tag1']
      })
    },
    edit: Boolean,
    deffered: Boolean,
    admin: Boolean,
    blocked: Boolean,
    deleted: Boolean,
    commentOpen: Boolean,
  },
  data: () => ({
    isLotText: false,
    openText: false,
    isEditNews: false,
    isOpenTags: false
  }),
  computed: {
    ...mapGetters('profile/info', ['getInfo']),
    commentsLength() {
      let result = 0
      this.info.comments.data.map(el => {
        !el.is_deleted && result++
        el.sub_comments &&
          el.sub_comments.map(subEl => {
            !subEl.is_deleted && result++
          })
      })
      return result
    },
    showTextClose() {
      if (localStorage.getItem('lang') === 'en') {
        return this.isOpenTags ? 'hide' : 'show'
      }
      return this.isOpenTags ? 'Cкрыть' : 'Показать'
    },
  },
  methods: {
    ...mapActions('global/likes', ['putLike', 'deleteLike', 'reportPost']),
    ...mapActions('profile/feeds', ['deleteFeeds', 'recoverFeeds']),
    ...mapMutations('global/search', ['setTagForSearch']),
    ...mapActions('global/search', ['changeTab']),
    toggleText() {
      this.openText = !this.openText
    },
    diffTime(time) {
      let now = moment()
      let timePost = moment(time)
      return timePost.calendar(null, {
        sameElse: `[через ${timePost.diff(now, 'days')} дней, ${timePost.diff(now, 'hours') % 24} часа]`
      })
    },
    likeAction(active) {
      active
        ? this.deleteLike({ item_id: this.info.id, type: 'Post' })
        : this.putLike({ item_id: this.info.id, type: 'Post' })
    },
    reportAction() {
      console.log('report');
      //active
        //this.reportPost({ item_id: this.info.id, type: 'Post' })
    },
    toggleEditNews() {
      this.isEditNews = !this.isEditNews
    },
    deleteNews() {
      this.deleteFeeds({
        id: this.getInfo.id,
        post_id: this.info.id,
        route: this.$route.name
      })
    },
    recoverNews() {
      this.recoverFeeds({
        id: this.getInfo.id,
        post_id: this.info.id,
        route: this.$route.name
      })
    },
    openCloseAllTags() {
      this.isOpenTags = !this.isOpenTags
    },
    goToSearch(tag) {
      this.setTagForSearch(tag);
      this.changeTab('news');
    }
  },
  mounted() {
    this.$refs.text.offsetHeight > 100 ? (this.isLotText = true) : (this.isLotText = false)
  },
  i18n: {
    messages: {
      en: {
        restore: 'Restore',
        date: 'Date and time of publication',
        hide: 'Hide',
        read: 'Read the entire post'
      },
      ru: {
        restore: 'Восстановить',
        date: 'Дата и время публикации',
        hide: 'Скрыть',
        read: 'Читать весь пост'
      }
    }
  }
}
</script>

<style lang="stylus">
@import '../../assets/stylus/base/vars.styl';

.news-block {
    background: #FFFFFF;
    box-shadow: standart-boxshadow;
    padding: 30px 40px 0;
    position: relative;

    &.deffered {
        padding-bottom: 20px;

        .edit {
            top: 30px;
        }
    }

    &--admin {
        .news-block__comments {
            margin-top: 20px;
        }
    }

    &+& {
        margin-top: 30px;
    }

    @media (max-width: breakpoint-xxl) {
        padding: 20px 30px 0;
    }
}

.news-block__deffered {
    position: relative;
    height: 45px;
    margin-bottom: 20px;
    display: flex;

    &:after {
        content: '';
        display: block;
        position: absolute;
        left: -40px;
        right: -40px;
        bottom: 0;
        width: calc(100% + 80px);
        height: 2px;
        background-color: #e7e7e7;
    }

    @media (max-width: breakpoint-xxl) {
        &:after {
            left: -30px;
            width: calc(100% + 60px);
        }
    }
}

.news-block__deffered-text {
    color: #5F5E7A;
    font-size: 16px;
}

.news-block__author {
    margin-bottom: 20px;
    display: flex;
    align-items: center;
}

.news-block__author-pic {
    width: 60px;
    height: 60px;
    border-radius: 50%;
    overflow: hidden;
    margin-right: 15px;

    img {
        width: 100%;
    }
}

.news-block__author-name {
    font-weight: 600;
    font-size: 15px;
    color: #000;
    display: block;
    margin-bottom: 5px;
}

.news-block__author-time {
    font-size: 13px;
    color: santas-gray;
}

.news-block__content {
    display: flex;
}

.news-block__content-main {
    padding-bottom: 10px;
    border-bottom: 1px solid #E7E7E7;
    width: 100%;
}

.news-block__content-title {
    font-family: font-exo;
    font-weight: bold;
    font-size: 24px;
    color: #000;
    margin-bottom: 10px;
}

.news-block__content-text {
    font-size: 15px;
    line-height: 25px;
    color: storm-gray;
    overflow: hidden;
    text-align: justify;
    padding-right: 1em;
    position: relative;
    margin-bottom: 15px;

    &:before {
        content: '\02026';
        position: absolute;
        right: 0;
        bottom: 0;
    }

    &:after {
        content: '';
        position: absolute;
        right: 0;
        width: 1em;
        height: 1em;
        margin-top: 0.2em;
        background: white;
    }

    &.lotText {
        max-height: 100px;
    }

    &.open {
        max-height: 100%;
        padding-right: 0;
    }

    & a{
      color:#21a45d;
      text-decoration: underline #21a45d;
    }

    & strong{
      font-weight:bold !important;
    }

    & em{
      font-style: italic !important;
    }
}

.news-block__content-more {
    display: inline-block;
    margin-top: 10px;
    font-size: 13px;
    color: eucalypt;
}

.news-block__tags-block {
    border-top: 1px solid #e7e7e7;
    padding-top: 10px;
}

.news-block__tags-title {
    font-size: 16px;
}

.news-block__content-tags {
    display: flex;
    flex-wrap: wrap;
}

.news-block__item-tag {
    display: block;
    margin-top: 5px;

    &:not(:last-child) {
        margin-right: 5px;
    }
}

.news-block__content-tag {
    display: inline-block;
    padding: 2px 5px;
    border: 1px solid transparent;
    border-radius: 4px;
    background: #e7e7e7;
    color: eucalypt;
    font-size: 13px;
    line-height: 22px;
    cursor: pointer;
}

.news-block__content-tag_close {
    text-overflow: ellipsis;
    width: 70px;
    white-space: nowrap;
    overflow: hidden;
}

.news-block__actions {
    display: flex;
    align-items: center;
    margin: 25px 0;
}

.news-block__actions-block {
    &+& {
        margin-left: 30px;
    }
}

.edit__recover {
    margin-right: 20px;
}
</style>
