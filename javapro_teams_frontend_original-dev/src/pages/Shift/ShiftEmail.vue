<template lang="pug">
.shift-email
    form.shift-email__form(@submit.prevent='submitHandler')
        .form__block
            h4.form__subtitle {{ $t("email") }}
            email-field#shift-email(
                v-model='email',
                :v='$v.email',
                :class='{ checked: $v.email.required && $v.email.email }',
                :placeholder='$t("placeholder")'
            )
        .shift-email__action
            button-hover(tag='button', type='submit', variant='white') {{ $t("change") }}
</template>

<script>
import { mapActions } from 'vuex'
import { required, email } from 'vuelidate/lib/validators'
import EmailField from '@/components/FormElements/EmailField'

export default {
    name: 'ShiftEmail',
    components: {
        EmailField,
    },
    data: () => ({
        email: '',
    }),
    methods: {
        ...mapActions('profile/info', ['deleteInfo']),
        ...mapActions('auth/api', ['logout']),
        ...mapActions('profile/account', ['changeEmail']),

        submitHandler() {
            if (this.$v.$invalid) {
                this.$v.$touch()
                return
            }
            this.changeEmail({ email: this.email }).then(() => {
                this.deleteInfo().then(() => {
                    this.logout().then(() => {
                        this.$router.push({ name: 'Login' })
                    })
                })
            })
        },
    },
    validations: {
        email: { required, email },
    },
    i18n: {
        messages: {
            en: {
                email: 'Change mail',
                placeholder: 'New mail',
                change: 'Change',
            },
            ru: {
                email: 'Смена почтового ящика',
                placeholder: 'Новый почтовый ящик',
                change: 'Сменить',
            },
        },
    },
}
</script>

<style lang="stylus">
.shift-email {
    display: flex;
    justify-content: center;
    flex-direction: column;
    height: 100%;
}

.shift-email__title {
    margin-bottom: 20px;
}

.shift-email__action {
    margin-top: 40px;
}
</style>
