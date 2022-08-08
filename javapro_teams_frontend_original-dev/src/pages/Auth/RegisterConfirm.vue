<template lang="pug">
  .success-register
    simple-svg(:filepath="'/static/img/success-register.svg'")
</template>

<script>
import { mapActions } from 'vuex'

export default {
  name: 'RegisterConfirm',
  data() {
      return {
          userId: "",
          confirmationToken: ""
      };
  },
  methods: {
    ...mapActions('auth/api', ['confirmRegistration']),
    collectQueryParams() {
      if (this.$route.query.userId) {
        this.userId = this.$route.query.userId
      }
      if (this.$route.query.token) {
        this.confirmationToken = this.$route.query.token
      }
    }
  },
  mounted() {
    this.collectQueryParams();
    this.confirmRegistration({ userId: this.userId, token: this.confirmationToken });
  }
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