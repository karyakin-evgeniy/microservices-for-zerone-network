<template lang="pug">
  .success-register
    simple-svg(:filepath="'/static/img/success-register.svg'")
</template>

<script>
import { mapActions } from 'vuex';

export default {
  name: 'RegisterSuccess',

  watch: {
    $route: {
      handler(newVal) {
        this.completeRegister(newVal.query);
      },
      immediate: true,
    }
  },

  methods: {
    ...mapActions('auth/api', ['completeRegister']),
  },
}
</script>

<style lang="stylus">
.success-register {
  height: 100%;
  display: flex;
  justify-content: center;
  align-items: center;

  .simple-svg-wrapper {
    width: 130px;
  }
}
</style>
