<template lang="pug">
  .settings-security
    .settings-security__block
      h3.settings-security__title E-mail:
      span.settings-security__value {{getInfo.email}}
      button-hover(@click.native="submitHandler('email')") {{ $t('change') }}
    .settings-security__block
      h3.settings-security__title {{ $t('password') }}:
      span.settings-security__value ********
      button-hover(@click.native="submitHandler('password')") {{ $t('change') }}
</template>

<script>
import { mapGetters } from 'vuex'
import router from '@/router'

export default {
  name: 'SettingsSecurity',
  computed: {
    ...mapGetters('profile/info', ['getInfo'])
  },
  methods: {
    submitHandler(id) {
      if (id === 'email') {
        router.push({name: 'ShiftEmail', params: {id: this.getInfo.id}});
      } else {
        router.push({name: 'ShiftPassword', params: {id: this.getInfo.id}});
      }
    }
  },
  i18n: {
    messages: {
      "en": {
        "password": "Password",
        "change": "Change"
      },
      "ru": {
        "password": "Пароль",
        "change": "Изменить"
      }
    }
  },
}
</script>

<style lang="stylus">
@import '../../assets/stylus/base/vars.styl';

.settings-security__block {
  background: #fff;
  box-shadow: standart-boxshadow;
  display: flex;
  align-items: center;
  height: 95px;
  padding: 0 33px 0 50px;
  font-size: 15px;

  &+& {
    margin-top: 20px;
  }
}

.settings-security__title {
  color: #5F5E7A;
  width: 100px;
}

.settings-security__value {
  color: #414141;
  display: block;
  margin-right: auto;
}
</style>
