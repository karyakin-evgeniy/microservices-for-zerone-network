<template lang="pug">
.support
    h2.support__title.form__title {{ $t("title") }}
    form.support__form(@submit.prevent='submitHandler', ref="form")
        name-field#support-firstName(v-model='firstName', :v='$v.firstName', :label='$t("name")')
        name-field#support-lastName(v-model='lastName', :v='$v.lastName', :label='$t("lastname")')
        email-field#support-email(v-model='email', :v='$v.email')
        comments-field#support-message(v-model='message', :v='$v.message', :label='$t("message")')

        .support__action
            button-hover(tag='button', type='submit', variant='white') {{ $t("send") }}
            router-link.support__link(:to='{ name: "Login" }') {{ $t("login") }}

    p.form__title.support__message(
      v-show="supportMessage"
    ) {{ $t("successMessage") }}
    p.form__title.support__message.support__message_error(
      v-show="isSendError"
    ) {{ $t("errorMessage") }}
</template>

<script>
import { mapActions, mapGetters, mapMutations } from 'vuex'
import { required, email, minLength } from 'vuelidate/lib/validators'
import NameField from '@/components/FormElements/NameField'
import EmailField from '@/components/FormElements/EmailField'
import CommentsField from '@/components/FormElements/CommentsField'
export default {
    name: 'Support',
    components: {
        NameField,
        EmailField,
        CommentsField,
    },
    data: () => ({
        email: '',
        firstName: '',
        lastName: '',
        message: '',
        isSendError: false,
    }),
    computed: {
      ...mapGetters('global/support', ['supportMessage']),
      redirectUrl() {
        return this.$route.query.redirect
      },
    },
    methods: {
        ...mapMutations('global/support', ['updateSupportMessage']),
        ...mapActions('global/support', ['sendMessage']),
        ...mapActions('profile/info', ['apiInfo']),

        submitHandler() {
            if (this.$v.$invalid) {
                this.$v.$touch()
                return
            }
            const { email, firstName, lastName, message } = this;
            const formData = {
              e_mail: email,
              first_name: firstName,
              last_name: lastName,
              message,
            }
            this.sendMessage(formData)
              .then(() => this.resetForm())
              .catch(() => this.isSendError = true);
        },

        resetForm() {
            this.$refs.form.reset();
            this.email = '';
            this.firstName = '';
            this.lastName = '';
            this.message = '';
            this.$v.$reset();
            this.hideSendMessages();
        },

        hideSendMessages() {
          setTimeout(() => {
            this.updateSupportMessage(null);
            this.isSendError = false;
          }, 30000);
        }
    },
    validations: {
        email: { required, email },
        firstName: { required, minLength: minLength(3) },
        lastName: { required, minLength: minLength(3) },
        message: { required, minLength: minLength(10) },
    },
    i18n: {
        messages: {
            en: {
                title: 'Contacting support',
                login: 'Sign in',
                send: 'Send',
                name: 'Name',
                lastname: 'Last name',
                message: 'Message',
                successMessage: "Your message has been sent.",
                errorMessage: "Oops, something went wrong.",
            },
            ru: {
                title: 'Обращение в поддержку',
                login: 'Войти',
                send: 'Отправить',
                name: 'Имя',
                lastname: 'Фамилия',
                message: 'Сообщение',
                successMessage: "Ваше сообщение отправлено.",
                errorMessage: "Упс... Что то пошло не так.",
            },
        },
    },
}
</script>

<style lang="stylus" scoped>
@import '../../assets/stylus/base/vars.styl';

.support {
    height: 100%;
    display: flex;
    flex-direction: column;
    justify-content: center;
}

.support__title {
    margin-bottom: 50px;
}

.support__action {
    display: flex;
    align-items: center;
    margin-top: 50px;
}

.support__link {
    font-size: 13px;
    color: rgba(255, 255, 255, 0.5);
    margin-left: 30px;
    white-space: nowrap;
    transition: all 0.2s;

    &:hover {
        color: #fff;
    }
}

.support__message {
  margin-top: 25px;

  &_error{
    color: #ff5573;
  }
}
</style>
