<template lang="pug">
  .aside-filter
    h2.aside-filter__title {{ $t('title') }}
    ul.aside-filter__list
      li.aside-filter__item(v-for="item in list" :key="item.component" :class="{active: item.component === value}" @click="changeComponent(item)") {{item.text}}
</template>

<script>
export default {
  name: 'SettingsSidebar',
  props: {
    value: String
  },
  computed: {
    list() {
      if (localStorage.getItem('lang') === 'en') {
        return [
        { component: 'settings-main', text: 'Main' },
        { component: 'settings-push', text: 'Configuring alerts' },
        { component: 'settings-security', text: 'Security' },
        { component: 'settings-lang', text: 'Change language' },
        { component: 'settings-delete', text: 'Delete profile' }
      ]
      }
      return [
        { component: 'settings-main', text: 'Основные' },
        { component: 'settings-push', text: 'Настройка оповещений' },
        { component: 'settings-security', text: 'Безопасность' },
        { component: 'settings-lang', text: 'Изменить язык' },
        { component: 'settings-delete', text: 'Удалить профиль' }
      ]
    },
  },
  methods: {
    changeComponent(item) {
      this.$emit('change-component', item)
    }
  },
  i18n: {
    messages: {
      "en": {
        "title": "Settings",
      },
      "ru": {
        "title": "Настройки",
      }
    }
  },
}
</script>
