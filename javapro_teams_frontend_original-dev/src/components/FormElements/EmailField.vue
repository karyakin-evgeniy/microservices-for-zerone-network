<template lang="pug">
  .form__group(:class="{fill: email.length > 0}")
    input.form__input(:id="id" v-model="email" name="email" :class="{invalid: (v.$dirty && !v.required) || (v.$dirty && !v.email)}" @change="v.$touch()" :autocomplete="autocomplete")
    label.form__label(:for="id") {{placeholder}}
    span.form__error(v-if="v.$dirty && !v.required") {{ $t('enterEmail') }}
    span.form__error(v-else-if="v.$dirty && !v.email") {{ $t('correctEmail') }}
</template>

<script>
export default {
  name: 'EmailField',
  props: {
    value: {
      type: String,
      default: ''
    },
    v: {
      type: Object,
      required: true
    },
    id: {
      type: String,
      required: true
    },
    placeholder: {
      type: String,
      default: 'E-mail'
    },
    autocomplete: {
      type: String,
      default: 'username'
    },
  },
  computed: {
    email: {
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
        "enterEmail": "Enter Email",
        "correctEmail": "Enter correct Email"
      },
      "ru": {
        "enterEmail": "Введите Email",
        "correctEmail": "Введите корректный Email"
      }
    }
  },
}
</script>

