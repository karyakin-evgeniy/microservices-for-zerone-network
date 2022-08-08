<template lang="pug">
#app
    component(:is='layout', v-if='$route.meta.layout')
        router-view
    v-snackbar(:value='getState.show', :timeout='getState.timeout', :color='getState.status', right, bottom) {{ getState.text }}
</template>

<script>
import { VSnackbar } from 'vuetify/lib'
import { mapActions, mapGetters } from 'vuex'
import FormLayout from '@/layouts/FormLayout'
import MainLayout from '@/layouts/MainLayout'
import EmptyLayout from '@/layouts/EmptyLayout'
import { authorizationIo } from './api/socetIO'

export default {
    name: 'App',
    components: {
        FormLayout,
        MainLayout,
        EmptyLayout,
        VSnackbar,
    },
    computed: {
        ...mapGetters('global/alert', ['getState']),
        ...mapGetters('auth/api', ['apiToken']),
        layout() {
            return this.$route.meta.layout + '-layout'
        },
    },
  async created () {
    if(this.apiToken !== ''){
      await authorizationIo();
    }
  }
}
</script>

<style lang="stylus">
@import './assets/stylus/main.styl';

.v-snack__wrapper {
    &.success {
        background-color: eucalypt;
    }

    &.error {
        background-color: wild-watermelon;
    }
}

img {
    width: 100%;
    height: 100%;
    background-position: center;
    background-repeat: no-repeat;
    background-size: cover;
}
</style>
