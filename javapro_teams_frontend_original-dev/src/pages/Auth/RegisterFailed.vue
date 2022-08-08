<template lang="pug">
  .success-register
    simple-svg(:filepath="'/static/img/delete.svg'")
</template>

<script>
export default {
  name: 'RegisterFailed'
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
