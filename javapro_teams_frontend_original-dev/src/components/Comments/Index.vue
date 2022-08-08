<template lang="pug">
.comments(:class='{ open: isOpenComments, "comments--admin": admin }')
    h4.comments__title
        span {{ $t("title") }} ({{ info.total }})
        a.comments__show(@click.prevent='showComments', href='#', v-if='info.data.length > 1') {{ showText }}
    .comments__list(v-if='getInfo')
        comment-block(
            v-for='i in info.data',
            :admin='admin',
            :key='i.id',
            :info='i',
            :offset='commentOffset',
            :perPage='commentPerPage',
            :edit='getInfo.id === i.author.id',
            :deleted='getInfo.id === i.author.id',
            @edit-comment='onEditMain'
        )
        .div(v-if='isOpenComments && commentOffset < info.total && info.perPage < info.total')
            button.btn-load-comments-text(type='button', v-if='!isLoad', @click.prevent='loadComments') Показать следующие комментарии
            is-loading(:isLoad='isLoad')
        .comments__add(v-if='!admin')
            comment-add(ref='addComment', :id='id', :photos="photos" v-model='commentText', @updatePhotos='updatePhotos', @submited='onSubmitComment')
            ul.comments__photos.photos-list
              li.photos-list__item(v-for="photo in photos" :key="photo.id")
                img.photos-list__img(:src="photo.url")
                button.photos-list__btn(@click.prevent="deletePhoto(photo.id)")
                  simple-svg(:filepath='"/static/img/delete.svg"')
</template>

<script>
import { mapGetters, mapActions } from 'vuex'
import CommentBlock from '@/components/Comments/Block'
import CommentAdd from '@/components/Comments/Add'
import isLoading from '@/components/isLoading'
export default {
  name: 'Comments',
  props: {
    admin: Boolean,
    info: Object,
    id: Number,
    edit: Boolean,
    deleted: Boolean,
    commentOpen: Boolean,
  },
  components: { CommentBlock, CommentAdd, isLoading },
  data: () => ({
    isOpenComments: false,
    commentText: '',
    commentEdit: false,
    commentEditInfo: null,
    commentPerPage: 5,
    commentOffset: 0,
    isLoad: false,
    photos: [],
  }),
  computed: {
    ...mapGetters('profile/info', ['getInfo']),
    ...mapGetters('global/storage', ['getStorage']),
    showText() {
      if (localStorage.getItem('lang') === 'en') {
        return this.isOpenComments ? 'hide' : 'show'
      }
      return this.isOpenComments ? 'скрыть' : 'показать'
    }
  },
  methods: {
    ...mapActions('profile/comments', ['commentActions']),
    ...mapActions('profile/comments', ['addCommentsById']),
    ...mapActions('global/storage', ['deleteFile']),
    showComments() {
      this.isOpenComments = !this.isOpenComments
    },
    updatePhotos(photos) {
      this.photos = photos;
    },
    deletePhoto(photoId) {
      this.deleteFile(photoId)
        .then(() => {
          this.photos = this.photos.filter((photo) => photo.id !== photoId)
        })
        .catch((err) => console.log('Error delete file:', err))
    },
    onEditMain({ commentInfo, commentText }) {
      this.commentEdit = true
      this.commentText = commentText
      this.commentEditInfo = commentInfo
      this.$refs.addComment.$refs.addInput.focus()
    },
    onSubmitComment() {
      if (this.commentText || this.photos.length) {
        this.commentActions({
          edit: this.commentEdit,
          post_id: this.id,
          text: this.commentText,
          id: this.commentEdit ? this.commentEditInfo.id : null,
          parent_id: null,
          offset: 0,
          images: this.photos,
          perPage:
            this.commentOffset + this.commentPerPage === 5
              ? this.commentPerPage * 2
              : this.commentOffset + this.commentPerPage
        }).then(() => {
          this.commentOffset + this.commentPerPage === 5
            ? (this.commentOffset = this.commentPerPage * 2)
            : (this.commentOffset += this.commentPerPage)
          this.commentText = ''
          this.commentEdit = false
          this.commentEditInfo = null
          this.photos = []
        })
      }
    },
    loadComments() {
      if (this.info.offset < this.info.total) {
        this.isLoad = true
        const data = {
          post_id: this.id,
          offset: 0,
          perPage:
            this.commentOffset + this.commentPerPage === 5
              ? this.commentPerPage * 2
              : this.commentOffset + this.commentPerPage
        }
        this.addCommentsById(data).then(() => {
          this.commentOffset + this.commentPerPage === 5
            ? (this.commentOffset = this.commentPerPage * 2)
            : (this.commentOffset += this.commentPerPage)
          this.isLoad = false
        })
      }
    }
  },
  i18n: {
    messages: {
      en: {
        title: 'Comments'
      },
      ru: {
        title: 'Комментарии'
      }
    }
  },
  mounted() {
    if(this.commentOpen){
      this.isOpenComments = this.commentOpen
    }
  }
}
</script>

<style lang="stylus">
@import '../../assets/stylus/base/vars.styl';

.comments {
    .comment-block:not(:first-child) {
        display: none;
    }

    &.open {
        .comment-block, .comment-block__reviews {
            display: block;
        }
    }

    &.comments--admin {
        padding-bottom: 40px;

        .comment-block {
            &:first-child {
                display: none;
            }

            .edit {
                display: none;
            }
        }

        &.open {
            .comment-block:first-child {
                display: block;
            }
        }
    }
}

.comments__title {
    display: flex;
    align-items: center;
    margin-bottom: 15px;

    span {
        font-family: font-exo;
        font-weight: bold;
        font-size: 15px;
        color: #000;
        display: block;
        margin-right: 5px;
    }
}

.comments__show {
    font-size: 13px;
    color: eucalypt;
}

.comments__list {
    width: 100%;
    max-width: 580px;
}

.btn-load-comments-text {
    color: #21a45d;
    font-size: 13px;
    font-weight: 600;
    display: -webkit-box;
    display: -ms-flexbox;
    display: flex;
    -webkit-box-align: center;
    -ms-flex-align: center;
    align-items: center;
    background-color: transparent;
    cursor: pointer;
}

.btn-load-comments-container {
    display: grid;
    grid-template-columns: repeat(3, 10px);
    grid-column-gap: 4px;

    &_icon {
        display: block;
        width: 100%;
        height: 5px;
        background-color: #21a45d;
        opacity: 0.3;
        transition: opacity 0.3s;
        animation-name: loadAnimations;
        animation-duration: 3s;
        animation-timing-function: linear;
        animation-iteration-count: infinite;

        &:nth-child(1) {
            -webkit-animation-delay: 0.5s;
        }

        &:nth-child(2) {
            -webkit-animation-delay: 1s;
        }

        &:nth-child(3) {
            -webkit-animation-delay: 1.5s;
        }
    }
}

.photos-list {
    margin-top: 10px;
    padding-bottom: 10px;
    .photos-list__item {
        display: flex;
        align-items: center;
        justify-content: flex-start;
        &:not(:last-child) {
          margin-bottom: 10px;
        }
    }

    .photos-list__img {
        flex: 0 0 56px;
        margin-right 15px;
    }

    .photos-list__btn {
      flex: 0 0 22px;
      padding: 0;
      background-color: transparent;
      cursor: pointer;
        .simple-svg-wrapper {
            width: 22px;
            height: 22px;
        }
    }
}

@keyframes loadAnimations {
    0% {
        opacity: 0.3;
        transition: opacity 0.3s;
    }

    66% {
        opacity: 1;
        transition: opacity 0.3s;
    }
}
</style>
