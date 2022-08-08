<template lang="pug">
.load-block(:class="{isLoad:'load-block'}")
    .load-anial-container(v-if='isLoad')
        span.load-anial-icon
        span.load-anial-icon
        span.load-anial-icon
</template>
<script>
export default {
  name: 'isLoading',
  props: {
    isLoad: Boolean
  }
}
</script>

<style lang="stylus">

.load-block {
    display: block;
    width: 100%;
    height: 0px;
    position: relative;
    z-index: -1;
}

.load-block{
    height: 10px;
    z-index: 1;
}

.load-anial-container {
    margin-top: 15px;
    display: grid;
    grid-template-columns: repeat(3, 20px);
    grid-column-gap: 5px;
}

.load-anial-icon {
    display: block;
    height: 8px;
    background-color: #21a45d;
    opacity: 0.3;
    transition: opacity 0.3s;
    border-radius: 2px;
    animation-name: loadAnimations;
    animation-duration: 1.6s;
    animation-timing-function: linear;
    animation-iteration-count: infinite;

    &:nth-child(1) {
        -webkit-animation-delay: 0.4s;
    }

    &:nth-child(2) {
        -webkit-animation-delay: 0.8s;
    }

    &:nth-child(3) {
        -webkit-animation-delay: 1.2s;
    }
}

@keyframes loadAnimations {
    0% {
        opacity: 0.3;
        transition: opacity 0.3s;
    }

    70% {
        opacity: 1;
        transition: opacity 0.3s;
    }
}
</style>
