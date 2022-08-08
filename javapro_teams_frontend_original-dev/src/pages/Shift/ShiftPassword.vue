<template lang="pug">
.shift-password
    form.shift-password__form(@submit.prevent='submitHandler')
        .form__block
            h4.form__subtitle {{ $t("password") }}
            password-field#shift-password(
                v-model='password',
                :v='$v.password',
                info,
                registration,
                :class='{ checked: $v.password.required && $v.passwordTwo.sameAsPassword }'
            )
            password-repeat-field#shift-repeat-password(
                v-model='passwordTwo',
                :v='$v.passwordTwo',
                :class='{ checked: $v.password.required && $v.passwordTwo.sameAsPassword }'
            )
        .shift-password__action
            button-hover(tag='button', type='submit', variant='white') {{ $t("change") }}
</template>

<script>
import { mapActions } from 'vuex'
import { required, sameAs, minLength } from 'vuelidate/lib/validators'
import PasswordField from '@/components/FormElements/PasswordField'
import PasswordRepeatField from '@/components/FormElements/PasswordRepeatField'

export default {
    name: 'ShiftPassword',
    components: {
        PasswordField,
        PasswordRepeatField,
    },
    data: () => ({
        password: '',
        passwordTwo: '',
    }),
    methods: {
        ...mapActions('profile/info', ['deleteInfo']),
        ...mapActions('auth/api', ['logout']),
        ...mapActions('profile/account', ['passwordSet']),

        submitHandler() {
            if (this.$v.$invalid) {
                this.$v.$touch()
                return
            }
            this.passwordSet({
                password: this.passwordTwo,
                token: this.$store.state.auth.api.token,
            }).then(() => {
                this.logout().then(() => {
                    this.$router.push({ name: 'Login' })
                })
            })
        },
    },
    validations: {
        password: { required, minLength: minLength(8) },
        passwordTwo: { required, minLength: minLength(8), sameAsPassword: sameAs('password') },
    },
    i18n: {
        messages: {
            en: {
                password: 'Change password',
                change: 'Change',
            },
            ru: {
                password: 'Смена пароля',
                change: 'Сменить',
            },
        },
    },
}
</script>

<style lang="stylus">
.shift-password {
    display: flex;
    justify-content: center;
    flex-direction: column;
    height: 100%;
}

.shift-password__title {
    margin-bottom: 20px;
}

.shift-password__action {
    margin-top: 40px;
}
</style>
