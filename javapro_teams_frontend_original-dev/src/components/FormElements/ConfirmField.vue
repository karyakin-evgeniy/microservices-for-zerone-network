<template lang="pug">
  .form__group
    input.form__checkbox(type="checkbox" name="confirm" v-model="confirm" :id="id" :class="{invalid: (v.$dirty && !v.sameAs)}")
    label.form__checkbox-label(:for="id") {{ $t('policy') }} &nbsp;
      a(@click="modalOn({ header: $t('header'), link: 'http://31.40.251.201:8086/policy.html' })") {{ $t('policy2') }} &nbsp;
      | {{ $t('policy3') }} &nbsp;
      a(@click="modalOn({ header: $t('header2'), link: 'http://31.40.251.201:8086/personal-data.html' })") {{ $t('policy4') }}.
</template>

<script>
import { mapActions } from 'vuex';

export default {
  name: 'ConfirmField',
  props: {
    value: {
      type: Boolean,
      default: ''
    },
    v: {
      type: Object,
      required: true
    },
    id: {
      type: String,
      required: true
    }
  },
  methods: {
    ...mapActions('auth/api', ['modalOn']),
  },
  computed: {
    confirm: {
      get() {
        return this.value
      },
      set(value) {
        this.$emit('input', value)
      }
    }
  },
  i18n: {
    messages: {
      "en": {
        "policy": "I agree with",
        "policy2": "the privacy policy",
        "policy3": "and the transfer",
        "policy4": "of personal data",
        "header": "Privacy Policy",
        "header2": "Personal Information",
      },
      "ru": {
        "policy": "Я согласен с",
        "policy2": "политикой конфиденциальности",
        "policy3": "и передачей",
        "policy4": "персональных данных",
        "header": "Политика конфиденциальности",
        "header2": "Персональные данные",
      }
    }
  },
}
</script>
