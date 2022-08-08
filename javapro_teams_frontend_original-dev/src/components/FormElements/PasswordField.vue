<template lang="pug">
.form__group(:class='{ fill: password.length > 0 }')
    label.form__label(:for='id') {{ $t("password") }}
    input.form__input(
        name='password',
        :id='id',
        :type='passwordFieldType',
        :autocomplete='autocomplete',
        v-model.trim='password',
        @change='passwordBlur',
        :class='{ invalid: (v.$dirty && !v.required) || (v.$dirty && !v.minLength) }'
    )
    span.form__error(v-if='v.$dirty && !v.required') {{ $t("enterPassword") }}
    .form__error-block
        template(v-if='registration')
            span.form__password-helper(:class='levelInfo.class')
            span.form__error(v-if='password.length >= 3') {{ levelInfo.text }}
        template(v-else)
            span.form__error(v-if='v.$dirty && !v.minLength') {{ $t("errorPassword1") }} {{ v.$params.minLength.min }} {{ $t("errorPassword2") }} {{ password.length }}
    template(v-if='info')
        .form__password-icon.active
            simple-svg(:filepath='"/static/img/password-info.svg"')
        p.form__password-info {{ $t("infoPassword") }}
    .form__password-icon(:class='{ active: password.length > 0 }', @click='switchVisibility', v-if='!registration')
        simple-svg(:filepath='"/static/img/password-eye.svg"')
</template>

<script>
export default {
    name: 'PasswordField',
    props: {
        value: {
            type: String,
            default: '',
        },
        v: {
            type: Object,
            required: true,
        },
        info: Boolean,
        registration: Boolean,
        id: {
            type: String,
            required: true,
        },
        autocomplete: {
            type: String,
        },
    },
    data: () => ({
        passwordFieldType: 'password',
        passwordHelperShow: true,
    }),
    computed: {
        password: {
            get() {
                return this.value
            },
            set(value) {
                this.$emit('input', value)
            },
        },
        levelInfo() {
            if (!this.passwordHelperShow) return { text: null, class: null }
            return this.password.length >= 3 && this.password.length < 7
                ? { text: this.$t('easy'), class: 'easy' }
                : this.password.length >= 7 && this.password.length < 11
                ? { text: this.$t('middle'), class: 'middle' }
                : this.password.length >= 11 && { text: this.$t('hard'), class: 'hard' }
        },
    },
    methods: {
        switchVisibility() {
            this.passwordFieldType = this.passwordFieldType === 'password' ? 'text' : 'password'
        },
        passwordBlur() {
            this.passwordHelperShow = false
            this.v.$touch()
        },
    },
    i18n: {
        messages: {
            en: {
                password: 'Password',
                enterPassword: 'Enter password',
                errorPassword1: 'Password must be at least',
                errorPassword2: 'characters. He is now',
                infoPassword:
                    'The password must consist of Latin letters, numbers and symbols. Must contain one capital letter, one number and 8 characters.',
                easy: 'Easy',
                middle: 'Middle',
                hard: 'Hard',
            },
            ru: {
                password: 'Пароль',
                enterPassword: 'Введите пароль',
                errorPassword1: 'Пароль должен быть не менее',
                errorPassword2: 'символов. Сейчас он',
                infoPassword:
                    'Пароль должен состоять из латинских букв, цифр и знаков. Обязательно содержать одну заглавную букву, одну цифру и состоять из 8 символов.',
                easy: 'Слабый',
                middle: 'Средний',
                hard: 'Надёжный',
            },
        },
    },
}
</script>

