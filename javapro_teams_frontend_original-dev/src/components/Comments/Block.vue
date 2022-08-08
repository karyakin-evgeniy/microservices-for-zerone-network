<template lang="pug">
.comment-block(:class='{ "show-comments": isShowSubComments }')
    template(v-if='admin')
        .edit(v-tooltip.bottom='\'Разблокировать\'', v-if='blocked')
            simple-svg(:filepath='"/static/img/unblocked.svg"')
        .edit(v-tooltip.bottom='\'Заблокировать\'', v-else)
            simple-svg(:filepath='"/static/img/blocked.svg"')
    comment-main(
        :admin='admin',
        :info='info',
        :edit='edit',
        :deleted='deleted',
        @answer-comment='onAnswerMain',
        @edit-comment='onEditMain',
        @delete-comment='onDeleteComment',
        @recover-comment='onRecoverComment'
    )
    .comment-block__reviews
        a.comment-block__reviews-show(href='#', v-if='info.sub_comments.length > 0', @click.prevent='showSubComments') {{ showSubText() }} {{ info.sub_comments.length }} {{ answerText }}
        .comment-block__reviews-list(v-if='isShowSubComments')
            comment-main(
                v-for='i in info.sub_comments',
                :admin='admin',
                :key='i.id',
                :info='i',
                :edit='getInfo.id === i.author.id',
                :deleted='getInfo.id === i.author.id',
                @answer-comment='onAnswerSub',
                @edit-comment='onEditSub',
                @delete-comment='onDeleteComment',
                @recover-comment='onRecoverComment'
            )
            comment-add(
                v-if='!admin',
                ref='addComment',
                :id='info.post_id',
                :photos="photos"
                v-model='commentText',
                @submited='onSubmitComment'
            )
            ul.comments__photos.photos-list
              li.photos-list__item(v-for="photo in photos" :key="photo.id")
                img.photos-list__img(:src="photo.url")
                button.photos-list__btn(@click.prevent="deletePhoto(photo.id)")
                  simple-svg(:filepath='"/static/img/delete.svg"')
</template>

<script>
import CommentMain from '@/components/Comments/Main'
import CommentAdd from '@/components/Comments/Add'
import { mapGetters, mapActions } from 'vuex'
export default {
  name: 'CommentBlock',
  props: {
    admin: Boolean,
    blocked: Boolean,
    info: Object,
    edit: Boolean,
    deleted: Boolean,
    offset: Number,
    perPage: Number
  },
  components: { CommentMain, CommentAdd },
  data: () => ({
    isShowSubComments: false,
    commentText: '',
    commentEdit: false,
    commentEditId: null,
    commentEditParentId: null,
    respongindLink: '',
    commentName: '',
    photos: [],
  }),
  computed: {
    ...mapGetters('profile/info', ['getInfo']),
    ...mapGetters('global/storage', ['getStorage']),
    answerText() {
      if (!this.info) return 'ответ'
      return this.info.sub_comments == null ? 'ответа' : 'ответ'
    },
    thisScrollY(){
      const el = document.querySelector('.modal-n__content');
      return el.scrollTop;
    }

  },
  methods: {
    ...mapActions('profile/comments', ['commentActions', 'deleteComment', 'recoverComment']),
    ...mapActions('global/storage', ['deleteFile']),
    showSubComments() {
      this.isShowSubComments = !this.isShowSubComments
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
    showSubText() {
      if (localStorage.getItem('lang') === 'en') {
        return this.isShowSubComments ? 'hide' : 'show'
      }
      return this.isShowSubComments ? 'скрыть' : 'показать'
    },
    saveLink(info) {
      this.commentName = info.author.first_name + ': '
      this.$refs.addComment.$refs.addInput.value = info.author.first_name + ': '
      this.respongindLink = `id:${info.author.id}, name:${info.author.first_name}`
    },
    ansfer() {
      this.commentEdit = false
      this.$refs.addComment.$refs.addInput.focus()
    },
    onAnswerSub(info) {
      this.ansfer()
      if (info) {
        this.saveLink(info)
      }
    },
    onAnswerMain() {
      this.showSubComments()
      this.$nextTick(() => this.ansfer())
    },
    onEditMain({ commentText }) {
      this.commentEdit = true
      this.$emit('edit-comment', {
        commentInfo: this.info,
        commentText
      })
    },
    onEditSub({ parentId, id, commentText, respongindLink }) {
      this.commentEdit = true
      this.commentText = commentText
      this.$refs.addComment.$refs.addInput.value = commentText
      this.commentEditId = id
      this.commentEditParentId = parentId
      this.respongindLink = respongindLink
    },
    onDeleteComment(id) {
      this.deleteComment({
        id,
        post_id: this.info.post_id,
        offset: 0,
        perPage: this.offset == 0 ? this.perPage + this.offset : this.offset
      })
    },
    onRecoverComment(id) {
      this.recoverComment({
        id,
        post_id: this.info.post_id,
        offset: 0,
        perPage: this.offset == 0 ? this.perPage + this.offset : this.offset
      })
    },
    onSubmitComment() {
      if (this.commentText || this.photos.length) {

        const nameLength = this.commentName.length
        const nameCheck = this.commentText.substr(0, nameLength)

        if (this.respongindLink !== '' && nameCheck == this.commentName) {
          this.commentText = this.commentText.substr(nameLength)
        }

        const target = event.target.parentElement.parentElement.parentElement

        this.commentActions({
          edit: this.commentEdit,
          post_id: this.info.post_id,
          parent_id: this.info.id,
          text: this.respongindLink + `,message:${this.commentText}`,
          id: this.commentEditId,
          offset: 0,
          perPage: this.perPage + this.offset,
          images: this.photos,
        }).then(() => {
          this.commentText = ''
          this.respongindLink = ''
          this.commentEdit = false
          this.commentEditInfo = null
          this.commentEditParentId = null
          this.photos = []
          target.scrollIntoView({block: "center", behavior: "smooth"});
        })
      }
    }
  },
  mounted() {},
  i18n: {
    messages: {
      en: {
        show: 'show',
        hide: 'hide'
      },
      ru: {
        show: 'показать',
        hide: 'скрыть'
      }
    }
  }
}
</script>

<style lang="stylus">
@import '../../assets/stylus/base/vars.styl';

.comment-block {
    padding-top: 20px;
    position: relative;

    &:after {
        content: '';
        display: none;
        height: 1px;
        width: calc(100% - 50px);
        background-color: #e7e7e7;
        position: absolute;
        top: 0;
        right: 0;
    }

    &+& {
        margin-top: 20px;

        &:after {
            display: block;
        }
    }

    &.show-comments {
        & + .comment-block {
            margin-top: 0;

            &:after {
                width: 100%;
            }
        }

        .comment-block__reviews {
            border-top: 1px solid #e7e7e7;
        }
    }

    .comment-add {
        height: 50px;
    }
}

.comment-block__reviews {
    position relative
    z-index 1
    margin-top: 10px;
    max-width: calc(100% - 50px);
    margin-left: auto;
}

.comment-block__reviews-show {
    color: eucalypt;
    font-size: 13px;
    font-weight: 600;
    display: flex;
    align-items: center;
    padding-top: 5px;

    &:before {
        content: '';
        display: block;
        width: 7px;
        height: 7px;
        margin-right: 7px;
        border: 1.5px solid transparent;
        border-radius: 2px;
        border-top-color: eucalypt;
        border-right-color: eucalypt;
        transform: rotate(45deg);
    }
}

.comment-block__reviews-list {
    padding-top: 20px;

    .comment-main + .comment-main {
        margin-top: 15px;
        padding-top: 15px;
        border-top: 1px solid #e7e7e7;

        .edit--small {
            top: 15px;
            z-index: 1;
        }
    }

    .comment-main__pic {
        width: 30px;
        height: 30px;
    }
}
.new-comment{
  position relative;
  background transparent

  &:after{
    content '';
    display block
    position absolute
    top 14px
    left 50%
    transform translateX(-50%)
    width 106%;
    height 100% + 18px
    background grey
    opacity 0.5
    z-index 0

  }
}
</style>
