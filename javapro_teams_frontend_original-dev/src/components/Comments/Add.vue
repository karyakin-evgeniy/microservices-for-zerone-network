<template lang="pug">
form.comment-add(action='#', @submit.prevent='onSubmitComment')
    .comment-add__pic(v-if='getInfo')
        img(:src='getInfo.photo', :alt='getInfo.fullName')
    input.comment-add__input(type='text', :placeholder='$t("placeholder")', ref='addInput', v-model='commentText')
    label.comment-add__icon.photo
        input#photo.user-info-form__input-photo(
            type='file',
            ref='photo',
            accept='image/*'
            @change="addImage($event)"
        )
        simple-svg(:filepath='"/static/img/photo.svg"')
    .comment-add__icon.add(@click='onSubmitComment')
        simple-svg(:filepath='"/static/img/add.svg"')
</template>

<script>
import { mapGetters, mapActions } from 'vuex'
export default {
    name: 'CommentAdd',
    props: {
        value: String,
        id: [Number, String],
        photos: Array,
    },
    data: () => ({
      src: '',
    }),
    computed: {
        ...mapGetters('profile/info', ['getInfo']),
        ...mapGetters('global/storage', ['getStorage']),

        commentText: {
            get() {
                return this.value
            },
            set(value) {
                this.$emit('input', value)
            },
        },
    },
    methods: {
        ...mapActions('global/storage', ['apiStorage']),
        onSubmitComment() {
            this.$emit('submited')
        },
        addImage($event) {
          const photos = this.photos;
          const photo = $event.target.files[0]
          const reader = new window.FileReader()

          reader.onload = e => this.src = e.target.result
          reader.readAsDataURL(photo)

          this.apiStorage({
            file: photo,
            typeImage: 'COMMENTIMAGE',
          })
            .then(() => {
              photos.push(this.getStorage);
              this.$emit('updatePhotos', this.photos);
            })
            .catch((err) => {
              console.log('error photo: ', err);
            })
        }
    },
    i18n: {
        messages: {
            en: {
                placeholder: 'Write a comment...',
            },
            ru: {
                placeholder: 'Написать комментарий...',
            },
        },
    },
}
</script>

<style lang="stylus">
.comment-add {
    display: grid;
    grid-template-columns: 36px 1fr 20px 15px;
    grid-column-gap: 14px;
    align-items: center;
    height: 60px;
    border-top: 1px solid #e7e7e7;
    margin-top: 20px;
}

.comment-add__pic {
    width: 36px;
    height: 36px;
    border-radius: 50%;
    overflow: hidden;
    margin-right: 15px;

    img {
        width: 100%;
    }
}

.comment-add__input {
    font-size: 13px;
    line-height: 21px;
    color: #6A6A80;
    display: block;
    width: 100%;

    &::placeholder {
        color: #B0B0BC;
    }
}

.comment-add__icon {
    cursor: pointer;

    &+& {
        margin-left: 10px;
    }

    &.photo {
      width: 22px;
        .simple-svg-wrapper {
            width: 22px;
            height: 22px;
        }
    }

    &.add {
        width: 15px;
        margin-top: -5px;

        .simple-svg-wrapper {
            width: 15px;
            height: 15px;
        }
    }
}
</style>
