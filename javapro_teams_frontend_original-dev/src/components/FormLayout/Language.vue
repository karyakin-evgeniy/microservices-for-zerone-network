<template lang="pug">
  .form-layout__language(ref="language")
    h4.form-layout__language-title {{ $t('title') }}
    .form-layout__language-search
      simple-svg(:filepath="'/static/img/search.svg'")
      input.form-layout__language-input(type="text" v-model="language" :placeholder=`$t('placeholder')`)
    ul.form-layout__language-list
      li.form-layout__language-item(v-for="lang in filterLanguages" :key="lang.id" @click="setActiveHandler(lang)") {{lang.title}}
</template>

<script>
import { mapGetters, mapMutations, mapActions } from 'vuex'

export default {
  name: 'FormLayoutLanguage',
  data: () => ({
    language: '',
  }),
  computed: {
    ...mapGetters('auth/languages', ['getLanguages', 'getLang']),
    filterLanguages() {
      return this.language.length > 0
        ? this.getLanguages.filter(el => el.title.toLowerCase().indexOf(this.language.toLowerCase()) >= 0)
        : this.getLanguages
    },
  },
  methods: {
    ...mapMutations('auth/languages', ['toggleLanguageBlock']),
    ...mapActions('auth/languages', ['apiLanguages']),
    setActiveHandler(lang) {
      switch (lang.title) {
        case 'Русский':
          localStorage.setItem('lang', 'ru')
          break
        case 'English':
          localStorage.setItem('lang', 'en')
          break
        default:
          localStorage.setItem('lang', 'ru')
      }

      this.toggleLanguageBlock()
      window.location.reload()
    }
  },
  mounted() {
    if (this.getLanguages.length === 0) this.apiLanguages()
  },
  i18n: {
    messages: {
      "en": {
        "title": "Choose language",
        "placeholder": "Start typing the name of the language"
      },
      "ru": {
        "title": "Выберите язык",
        "placeholder": "Начните вводить название языка"
      }
    }
  },
}
</script>

<style lang="stylus">
@import '../../assets/stylus/base/vars.styl';

.form-layout__language {
  height: 0;
  width: 100%;
  background: #fff;
  padding: 35px 20px 20px 11.82%;
  color: charade;
  transition: all 0.2s;

  &.active {
    height: auto;
  }
}

.form-layout__language-title {
  font-family: font-exo;
  font-weight: 200;
  font-size: 30px;
  letter-spacing: -0.01em;
  color: #000000;
  margin-bottom: 15px;
}

.form-layout__language-search {
  margin-bottom: 30px;
  max-width: 345px;
  position: relative;

  .simple-svg-wrapper {
    width: 16px;
    height: 16px;
    position: absolute;
    left: 0;
    top: 0;
  }

  .simple-svg {
    stroke: steel-gray;
    fill: transparent;
  }
}

.form-layout__language-input {
  width: 100%;
  border-bottom: 1px solid #BBBBC7;
  padding: 0 20px 5px;
  cursor: pointer;
  font-weight: 300;
  font-size: 15px;
  color: manatee;
}

.form-layout__language-list {
  display: inline-flex;
  flex-direction: column;
  flex-wrap: wrap;
  height: auto;
  max-height: 350px;
}

.form-layout__language-item {
  font-weight: 300;
  font-size: 13px;
  line-height: 25px;
  color: charade;
  width: 190px;
  transition: all 0.2s;
  cursor: pointer;

  &:hover {
    color: eucalypt;
  }
}
</style>
