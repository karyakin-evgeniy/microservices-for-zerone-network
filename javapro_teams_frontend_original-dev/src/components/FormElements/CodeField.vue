<template lang="pug">
  .form__group(:class="{fill: code.length > 0}")
    input.form__input(:id="id" v-model="code" name="code" :class="{invalid: (v.$dirty && !v.required) || (v.$dirty && !v.minLength)}" @change="v.$touch()")
    label.form__label(:for="id") {{placeholder}}
    span.form__error(v-if="v.$dirty && !v.code") {{ $t('enterCode') }}
    span.form__error(v-else-if="v.$dirty && !v.minLength") {{ $t('correctCode') }}
</template>

<script>
export default {
  name: 'CodeField',
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
      default: 'Code'
    }
  },
  computed: {
    code: {
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
      en: {
        enterCode: 'Enter Code',
        correctCode: 'Enter correct Code'
      },
      ru: {
        enterCode: 'Введите Код',
        correctCode: 'Введите корректный код'
      }
    }
  }
}
</script>
