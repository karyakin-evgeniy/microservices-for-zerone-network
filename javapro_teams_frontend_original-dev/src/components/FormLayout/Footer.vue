<template lang="pug">
  .form-layout__footer
    router-link.form-layout__footer-support(:to="{ name: 'Support' }") {{ $t('support') }}
    .form-layout__footer-language {{ $t('lang') }}: &nbsp;
      span.active(@click="toggleLanguageBlock") {{getActiveLang}}
    span.form-layout__footer-copyright © Copyright {{year}} ZERONE
</template>


<script>
import { mapGetters, mapMutations } from 'vuex'


export default {
  name: 'FormLayoutFooter',
  computed: {
    ...mapGetters('auth/languages', ['getActiveLanguage']),
    getActiveLang() {
      const lang = localStorage.getItem('lang')
      if (lang === 'ru') return 'Русский'
      if (lang === 'en') return 'English'
    },
    year() {
      let date = new Date()
      return date.getFullYear()
    }
  },
  methods: {
    ...mapMutations('auth/languages', ['setActiveLanguage', 'toggleLanguageBlock'])
  },
  i18n: {
    messages: {
      "en": {
        "lang": "Language",
        "support": "Support"
      },
      "ru": {
        "lang": "Язык",
        "support": "Поддержка"
      }
    }
  },
}
</script>

<style lang="stylus">
@import '../../assets/stylus/base/vars.styl';

.form-layout__footer {
  display: flex;
  align-items: center;
  flex-wrap: wrap;
  color: gun-powder;
  font-size: 13px;
  line-height: 18px;
}

.form-layout__footer-support {
  color: comet;
  margin-right: 8.3%;
}

.form-layout__footer-language {
  margin-right: 5.8%;

  .active {
    font-weight: 600;
    cursor: pointer;
    transition: all 0.2s;

    &:hover {
      color: #fff;
    }
  }
}
</style>
