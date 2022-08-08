<template lang="pug">
.comment-main(:id='postId')
    template(v-if='info.is_deleted')
        p.comment-main__text {{ $t("del") }}.
            a(href='#', @click.prevent='onRecoverComment', v-if='edit') {{ $t("restore") }}
    template(v-else)
        .edit.edit--small(v-if='edit || deleted')
            .edit__icon(v-if='deleted', @click='onDeleteComment')
                simple-svg(:filepath='"/static/img/delete-news.svg"')
            .edit__icon(v-if='edit', @click='editComment')
                simple-svg(:filepath='"/static/img/edit.svg"')
        router-link.comment-main__pic(:to='{ name: "ProfileId", params: { id: info.author.id } }')
            img(:src='info.author.photo', :alt='info.author.first_name')
        .comment-main__main
            router-link.comment-main__author(:to='{ name: "ProfileId", params: { id: info.author.id } }') {{ info.author.first_name + " " + info.author.last_name }}
            p.comment-main__text
                template(v-if='commentTexts.id && commentTexts.name')
                    router-link.comment-main__author-res(:to='{ name: "ProfileId", params: { id: commentTexts.id } }') {{ commentTexts.name }}:
                span {{ commentTexts.messages ? commentTexts.messages : info.comment_text }}
                span(v-for='image in info.images', :key='image.id')
                  img.comment-main__img(:src="image.url")
            .comment-main__actions
                span.comment-main__time {{ info.time | moment("from") }}
                template(v-if='!admin')
                    a.comment-main__review(href='#', @click.prevent='$emit("answer-comment", info)') {{ $t("answer") }}
                    like-comment(
                        fill,
                        :quantity='info.likes',
                        :active='info.my_like',
                        :id='info.id',
                        @liked='likeAction'
                    )
</template>

<script>
import { mapActions } from 'vuex'
import LikeComment from '@/components/LikeComment'
export default {
  name: 'CommentMain',
  props: {
    admin: Boolean,
    info: Object,
    edit: Boolean,
    deleted: Boolean
  },
  components: { LikeComment },
  methods: {
    ...mapActions('global/likes', ['putLike', 'deleteLike']),
    likeAction(active) {
      active
        ? this.deleteLike({ item_id: this.info.id, post_id: this.info.post_id, type: 'Comment' })
        : this.putLike({ item_id: this.info.id, post_id: this.info.post_id, type: 'Comment' })
    },
    onDeleteComment() {
      this.$emit('delete-comment', this.info.id)
    },
    editComment() {
      this.$emit('edit-comment', {
        id: this.info.id,
        commentText: this.commentTexts.messages ? this.commentTexts.messages : this.info.comment_text,
        parentId: this.info.parent_id,
        respongindLink:
          this.commentTexts.id && this.commentTexts.name
            ? `id:${this.commentTexts.id}, name:${this.commentTexts.name}`
            : ''
      })
    },
    onRecoverComment() {
      this.$emit('recover-comment', this.info.id)
    }
  },
  computed: {
    commentTexts() {
      const resp = this.info.comment_text.split(',')
      const str = this.info.comment_text
      const arr = {}

      resp.forEach(el => {
        if (el.includes('id:')) {
          const i = el.indexOf('id:') + 3
          arr.id = el.substr(i).trim()
        } else if (el.includes('name:')) {
          const i = el.indexOf('name:') + 5
          arr.name = el.substr(i).trim()
        }
      })
      if (str.indexOf(',message:') !== -1) {
        const i = str.indexOf('message:') + 8
        arr.messages = str.substr(i)
      } else {
        arr.messages = str
      }

      return arr
    },
    postId(){
      const id = '' + this.info.post_id + '-' + this.info.id;
      return id;
    },
    commText() {
      return this.info.comment_text
    }
  },

  watch: {
    commText(val) {
      this.commentTexts
    }
  },

  i18n: {
    messages: {
      en: {
        del: 'Comment has been deleted',
        restore: 'Restore',
        answer: 'To answer'
      },
      ru: {
        del: 'Комментарий удален',
        restore: 'Восстановить',
        answer: 'Ответить'
      }
    }
  }
}
</script>

<style lang="stylus">
@import '../../assets/stylus/base/vars.styl';

.comment-main {
    z-index 1;
    display: flex;
    font-size: 13px;
    position: relative;
}

.comment-main__pic {
    flex: none;
    align-self: flex-start;
    width: 40px;
    height: 40px;
    border-radius: 50%;
    overflow: hidden;
    margin-right: 10px;

    img {
        width: 100%;
        height: 100%;
        background-position: center;
        background-repeat: no-repeat;
        background-size: content;
    }
}

.comment-main__main {
    width: 100%;
}

.comment-main__author {
    font-weight: 600;
    color: #000;
    margin-bottom: 5px;
    display: inline-block;
}

.comment-main__author-res {
    font-weight: 600;
    color: #21a45d;
    margin-right: 5px;
    border-bottom: 1px solid transparent;
    transition: border-color 0.3s;

    &:hover, &:focus {
        border-color: #21a45d;
        transition: border-color 0.3s;
    }
}

.comment-main__text {
    line-height: 21px;
    color: #6A6A80;
    margin-bottom: 5px;
}

.comment-main__actions {
    display: flex;
    align-items: center;
}

.comment-main__time {
    color: santas-gray;
    display: block;
    margin-right: 20px;
}

.comment-main__review {
    color: eucalypt;
    margin-right: auto;
}

.comment-main__img {
  margin-bottom 5px;
}
</style>
