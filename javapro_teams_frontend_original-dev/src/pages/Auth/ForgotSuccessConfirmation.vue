<template lang="pug">
  .forgot
    h2.forgot__title.form__title {{ $t('title') }}
    form.forgot__form(@submit.prevent="submitHandler")
      code-field(id="forgot-code" v-model="code" :v="$v.code")
      .forgot__action
        button-hover(tag="button" type="submit" variant="white") {{ $t('send') }}
</template>

<script>
import { required, minLength } from 'vuelidate/lib/validators'
import CodeField from '@/components/FormElements/CodeField'
import { mapActions, mapGetters } from 'vuex'
export default {
  name: 'Forgot',
  components: {
    CodeField
  },
  data: () => ({
    code: ''
  }),
  computed: {
    ...mapGetters('profile/account', ['getMail'])
  },
  methods: {
    ...mapActions('profile/account', ['passwordRecovery']),

    submitHandler() {
      if (this.$v.$invalid) {
        this.$v.$touch()
        return
      }

      this.passwordRecovery({ email: this.getMail, key: this.code })
        .then(resp => {
          if (resp.status === 200) {
            this.$router.push({ name: 'ForgotSuccess' })
          }
        })
        .catch(err => {
          this.$router.push({ name: 'ChangePasswordFailed' })
        })
    }
  },
  validations: {
    code: { required, minLength: minLength(4) }
  },

  i18n: {
    messages: {
      en: {
        title: 'Enter the received code',
        send: 'Send'
      },
      ru: {
        title: 'Введите код',
        send: 'Отправить'
      }
    }
  }
}
</script>

<style lang="stylus">
.forgot {
  display: flex;
  justify-content: center;
  flex-direction: column;
  height: 100%;
}

.forgot__title {
  margin-bottom: 60px;
}

.forgot__action {
  margin-top: 90px;
}
</style>
