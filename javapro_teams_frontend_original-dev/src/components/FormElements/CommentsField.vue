<template lang="pug">
  .form__group(:class="{ fill: comments }")
    textarea.form__input(
      :id="id"
      v-model="comments"
      name="comments"
      :class="{ invalid: (v.$dirty && !v.required) || (v.$dirty && !v.minLength)}"
      @change="v.$touch()"
    )
    label.form__label(:for="id") {{label}}
    span.form__error(v-if="v.$dirty && !v.required") {{ $t('errorRequired') }}
    span.form__error(v-else-if="v.$dirty && !v.minLength") {{ $t('errorMin') }} {{v.$params.minLength.min}}
</template>

<script>
export default {
  name: 'CommentsField',
  props: {
    value: {
      type: String,
      default: ''
    },
    v: {
      type: Object,
      required: true
    },
    label: {
      type: String,
      default: 'Комментарий'
    },
    id: {
      type: String,
      required: true
    }
  },
  computed: {
    comments: {
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
        "errorRequired": "Required field",
        "errorMin": "Minimum number of characters",
      },
      "ru": {
        "errorRequired": "Обязательно поле",
        "errorMin": "Минимальное количество символов",
      }
    }
  },
}
</script>

