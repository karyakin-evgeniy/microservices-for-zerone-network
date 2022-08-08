<template lang="pug">
.registration
    form.registration__form(@submit.prevent='submitHandler')
        .form__block
            h4.form__subtitle {{ $t("account") }}
            email-field#register-email(
                v-model='email',
                :v='$v.email',
                :class='{ checked: $v.email.required && $v.email.email }'
            )
            password-field#register-password(
                v-model='passwd1',
                :v='$v.passwd1',
                info,
                registration,
                :class='{ checked: $v.passwd1.required && $v.passwd2.sameAsPassword && $v.passwd1.minLength }',
                autocomplete='new-password'
            )
            password-repeat-field#register-repeat-password(
                v-model='passwd2',
                :v='$v.passwd2',
                :class='{ checked: $v.passwd1.required && $v.passwd2.sameAsPassword && $v.passwd2.minLength }',
                autocomplete='new-password'
            )
        .form__block
            h4.form__subtitle {{ $t("personal") }}
            name-field#register-firstName(v-model='firstName', :v='$v.firstName', :label='$t("name")')
            name-field#register-lastName(v-model='lastName', :v='$v.lastName', :label='$t("lastname")')
        .form__block.captcha
            vue-hcaptcha(sitekey='f9aa335c-f77d-455b-aef2-97a151181df7', :language='getLang', @verify='onVerify')
        .form__block
            confirm-field#register-confirm(v-model='confirm', :v='$v.confirm')
        .registration__action
            button-hover(tag='button', type='submit', variant='white') {{ $t("registration") }}
</template>

<script>
import { mapActions } from 'vuex'
import { required, email, minLength, sameAs } from 'vuelidate/lib/validators'
import PasswordField from '@/components/FormElements/PasswordField'
import PasswordRepeatField from '@/components/FormElements/PasswordRepeatField'
import EmailField from '@/components/FormElements/EmailField'
import NameField from '@/components/FormElements/NameField'
import ConfirmField from '@/components/FormElements/ConfirmField'
import VueHcaptcha from '@hcaptcha/vue-hcaptcha'

export default {
    name: 'Registration',
    components: {
        PasswordField,
        EmailField,
        NameField,
        PasswordRepeatField,
        ConfirmField,
        VueHcaptcha,
    },
    data: () => ({
        email: '',
        passwd1: '',
        passwd2: '',
        firstName: '',
        lastName: '',
        verified: false,
        token: null,
        eKey: null,
        confirm: false,
    }),
    computed: {
        getLang() {
            return localStorage.getItem('lang')
        },
    },
    methods: {
        onVerify(token) {
            this.verified = true
            this.token = token
        },
        ...mapActions('auth/api', ['register']),
        submitHandler() {
            if (this.$v.$invalid) {
                this.$v.$touch()
                return
            }
            // проверка прохождения hCaptcha
            // if (!this.verified) {
            //     document.querySelector('.captcha').classList.add('error')
            //     return
            // }

            const { email, passwd1, firstName, lastName, token } = this
            this.register({ email, passwd1, firstName, lastName, token })
        },
    },
    validations: {
        confirm: { sameAs: sameAs(() => true) },
        email: { required, email },
        passwd1: { required, minLength: minLength(8) },
        passwd2: { required, minLength: minLength(8), sameAsPassword: sameAs('passwd1') },
        firstName: { required, minLength: minLength(3) },
        lastName: { required, minLength: minLength(3) },
    },
    i18n: {
        messages: {
            en: {
                account: 'Account',
                personal: 'Personal data',
                registration: 'Registration',
                name: 'Name',
                lastname: 'Last name',
            },
            ru: {
                account: 'Аккаунт',
                personal: 'Личные данные',
                registration: 'Зарегистрироваться',
                name: 'Имя',
                lastname: 'Фамилия',
            },
        },
    },
}
</script>

<style lang="stylus">
@import '../../assets/stylus/base/vars.styl';

.registration {
    min-height: 100%;
    display: flex;
    flex-direction: column;
    justify-content: center;
}

.registration__action {
    margin-top: 40px;

    @media (max-width: breakpoint-xxl) {
        margin-top: 20px;
    }
}

.captcha.error {
    display: inline-block;
    outline: 1px #ff5573 solid;
}
</style>
