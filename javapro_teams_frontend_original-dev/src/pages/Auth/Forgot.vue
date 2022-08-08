<template lang="pug">
  .forgot
    h2.forgot__title.form__title {{ $t('title') }}
    form.forgot__form(@submit.prevent="submitHandler")
      email-field(id="forgot-email" v-model="email" :v="$v.email")
      .forgot__action
        button-hover(tag="button" type="submit" variant="white") {{ $t('send') }}
</template>

<script>
import { required, email } from 'vuelidate/lib/validators'
import EmailField from '@/components/FormElements/EmailField'
import { mapActions, mapMutations } from 'vuex'
export default {
  name: 'Forgot',
  components: {
    EmailField
  },
  data: () => ({
    email: ''
  }),
  methods: {
    ...mapActions('profile/account', ['passwordRecoveryConfirmation']),
    ...mapMutations('profile/account', ['setEmail']),

    submitHandler() {
      if (this.$v.$invalid) {
        this.$v.$touch()
        return
      }

      this.passwordRecoveryConfirmation({ email: this.email }).then(resp => {
        if (resp.status == 200) {
          this.setEmail(this.email)
          this.$router.push({ name: 'ForgotSuccessConfirmation' })
        }
      })
    }
  },
  validations: {
    email: { required, email }
  },
  i18n: {
    messages: {
      en: {
        title: 'Write your e-mail',
        send: 'Send'
      },
      ru: {
        title: 'Напишите ваш e-mail',
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
