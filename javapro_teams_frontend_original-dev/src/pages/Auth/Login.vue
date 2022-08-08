<template lang="pug">
.login
    h2.login__title.form__title {{ $t("title") }}
    form.login__form(@submit.prevent='submitHandler')
        email-field#login-email(v-model='email', :v='$v.email')
        password-field#login-password(v-model='password', :v='$v.password', autocomplete='current-password')
        .login__action
            button-hover(tag='button', type='submit', variant='white') {{ $t("login") }}
            router-link.login__link(:to='{ name: "Forgot" }') {{ $t("forgot") }}
</template>

<script>
import { mapActions } from 'vuex'
import { required, email, minLength } from 'vuelidate/lib/validators'
import PasswordField from '@/components/FormElements/PasswordField'
import EmailField from '@/components/FormElements/EmailField'
import { authorizationIo } from '../../api/socetIO'
export default {
    name: 'Login',
    components: {
        PasswordField,
        EmailField,
    },
    data: () => ({
        email: '',
        password: '',
    }),
    computed: {
        redirectUrl() {
            return this.$route.query.redirect
        },
    },
    methods: {
        ...mapActions('auth/api', ['login']),
        ...mapActions('profile/info', ['apiInfo']),

        submitHandler() {
            if (this.$v.$invalid) {
                this.$v.$touch()
                return
            }

            this.login({ email: this.email, password: this.password })
                .then(() => {
                    this.apiInfo().then(async () => {
                        await authorizationIo();
                        this.$router.push({ name: this.redirectUrl || 'News' });
                    })
                })
                .catch((error) => {})
        },
    },
    validations: {
        email: { required, email },
        password: { required, minLength: minLength(8) },
    },
    i18n: {
        messages: {
            en: {
                title: 'Log in',
                login: 'Sign in',
                forgot: 'Forgot your password?',
            },
            ru: {
                title: 'Войдите в аккаунт',
                login: 'Войти',
                forgot: 'Забыли пароль?',
            },
        },
    },
}
</script>

<style lang="stylus" scoped>
@import '../../assets/stylus/base/vars.styl';

.login {
    height: 100%;
    display: flex;
    flex-direction: column;
    justify-content: center;
}

.login__title {
    margin-bottom: 50px;
}

.login__action {
    display: flex;
    align-items: center;
    margin-top: 50px;
}

.login__link {
    font-size: 13px;
    color: rgba(255, 255, 255, 0.5);
    margin-left: 30px;
    white-space: nowrap;
    transition: all 0.2s;

    &:hover {
        color: #fff;
    }
}
</style>
