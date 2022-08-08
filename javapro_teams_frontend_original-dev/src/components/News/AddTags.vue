<template lang="pug">
  .add-tags
    .add-tags__field(v-click-outside='hideOptions')
      label.add-tags__label
        input.add-tags__input(
          type="text",
          v-model="tag",
          :placeholder="$t('placeholder')",
          @focus="showOptions",
          @input="eventInput",
          @keyup.enter.prevent="addTag"
          ref="input"
        )
        button.add-tags__btn(type="button" @click.prevent="addTag")
          simple-svg(:filepath='"/static/img/add.svg"')

      ul.custom-select__options.options__list.add-tags__select(
        v-if="isShowOptions"
      )
        li.options__item(
          v-for="option in listTags",
          :key="option.id",
          @click="selectHandler(option)",
          @keyup.enter="selectHandler(option)",
          tabindex=0
        ) {{ `#${option.tag}` }}
        li.options__item.options__item_loading(v-if="listTags.length")
          is-loading(
            v-if="pageParams.total > offset",
            :isLoad='isLoad', v-load="loadTags"
          )
        li.options__item.options__item_error(v-if="!listTags.length") Значение не найдено

    .add-tags__block
      .add-tags__item(v-for="(tag,index) in tags" :key="index") {{'#'+tag}}
        span.add-tags__delete(@click="deleteTag(tag, index)") &#10005;
</template>

<script>
import { mapGetters, mapActions } from 'vuex'
import ClickOutside from 'vue-click-outside'
import isLoading from '@/components/isLoading'

export default {
  name: 'AddTags',

  components: { isLoading },

  directives: {
    ClickOutside
  },

  props: {
    tags: Array
  },

  data: () => ({
    tagsComponent: [],
    tag: '',
    isShowOptions: false,
    inputTimerId: null,
    inputTimer: 450,
    isLoad: true,
    offset: 0,
    itemPerPage: 10,
  }),

  computed: {
    ...mapGetters('profile/tags', {
      listTags: 'tags',
      pageParams: 'pageParams',
    }),
  },

  created() {
    this.loadTags();
  },

  mounted() {
    this.tagsComponent = this.tags
  },

  watch: {
    tags() {
      this.tagsComponent = this.tags
    },
  },

  methods: {
    ...mapActions('profile/tags', ['getTags', 'createTag', 'removeTag']),
    showOptions() {
      this.isShowOptions = true;
    },
    hideOptions() {
      this.isShowOptions = false;
    },

    loadTags() {
      const { tag, offset, itemPerPage } = this;

      this.isLoading = true;
      this.getTags({ tag, offset, itemPerPage })
        .then(() => {
          this.offset = offset + itemPerPage;
          this.isLoading = false;
        })
    },
    deleteTag(tag, index) {
      this.getTags({tag})
        .then(() => {
          const tagId = this.listTags[0].id
          this.removeTag(tagId)
          this.tagsComponent.splice(index, 1)
          this.$emit('change-tags', this.tagsComponent)
        })
    },
    eventInput() {
      this.offset = 0;
      clearTimeout(this.inputTimerId)
      this.inputTimerId = setTimeout(() => {
        this.loadTags()
      }, this.inputTimer)
    },
    addTag() {
      if (!this.tag) return
      this.saveTag()
      this.createNewTag()
      this.hideOptions()
      this.resetTag()
    },
    selectHandler(option) {
      this.tag = option.tag
      this.saveTag()
      this.hideOptions()
      this.resetTag()
    },


    saveTag() {
      if (this.tagsComponent.includes(this.tag)) return

      this.tagsComponent.push(this.tag)
      this.$emit('change-tags', this.tagsComponent)
      this.offset = 0
    },
    createNewTag() {
      const notHasTag = !this.listTags.length || !this.listTags.includes(this.tag);
      if (notHasTag) {
        this.createTag(this.tag)
      } else {
        this.loadTags();
      }
      this.resetTag()
    },
    resetTag() {
      if (!this.tag) return
      this.tag = ''
    },
  },
  i18n: {
    messages: {
      en: {
        placeholder: 'Add tag...'
      },
      ru: {
        placeholder: 'Добавить тег...'
      }
    }
  }
}
</script>

<style lang="stylus">
@import '../../assets/stylus/base/vars.styl';

.add-tags{
  display: grid !important;
}

.add-tags__input {
  border-bottom: 1px solid rgba(0, 0, 0, 0.12);
  padding-bottom: 5px;
  font-size: 13px;
  color: steel-gray;
  margin-bottom: 15px;

  &::placeholder {
    color: #B0B0BC;
  }
}

.add-tags__block {
  margin: 0 -4px 10px;
  min-height: 32px;
}

.add-tags__item {
  display:block;
  align-items: center;
  color: eucalypt;
  font-size: 13px;
  background-color: #F5F7FB;
  padding: 5px;
  margin: 0 5px 10px;
}

.add-tags__delete {
  margin-left: 5px;
  color: #B0B0BC;
  font-size: 10px;
  font-weight: 600;
  cursor: pointer;
}

.add-tags__field {
    position: relative;

    .add-tags__label {
      position: relative;

      .add-tags__btn {
        position: absolute;
        top: 50%;
        right: 0;
        transform: translateY(-50%);
        width: 24px;
        height: 24px;
        padding: 5px;
        font-size: 0;
        line-height: 0;
        background-color: transparent;
        cursor: pointer;
      }

      .simple-svg-wrapper {
        width: 14px;
        height: 14px;
      }
    }

    .add-tags__input {
      width: 100%;
      padding-right: 25px;
      box-sizing: border-box;
    }
    .options__list {
        position: absolute;
        bottom: 10px;
        transform: translateY(100%);
        width: 100%;
        max-height: 150px;
        z-index: 10;
        overflow-y: auto;
        background-color: #FFFFFF;
        border: 1px solid #c4c4c4;
        -webkit-box-shadow: 4px 5px 6px 0px rgba(34, 60, 120, 0.5);
        -moz-box-shadow: 4px 5px 6px 0px rgba(34, 60, 80, 0.5);
        box-shadow: 4px 5px 6px 0px rgba(34, 60, 80, 0.5);
    }

    .options__item {
        padding: 5px 20px;

        &:hover, &:focus {
            background-color: rgba(0, 0, 0, 0.3);
            cursor: pointer;
        }

        &_error {
          padding: 20px;
        }

        .load-anial-container {
          margin-top: 0;
        }
    }
}
</style>
