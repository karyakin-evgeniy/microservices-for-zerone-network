<template lang='pug'>
.modal-n
  .modal-n__close(@click='closeModal')
  .modal-n__content(v-if='this.getFeeds[0]')
      news-block(
        edit,
        deleted,
        :info='this.getFeeds[0]'
        :commentOpen='true'
        :edit='getInfo.id === this.getFeeds[0].author.id',
        :deleted='getInfo.id === this.getFeeds[0].author.id'
      )
</template>

<script>
import { mapActions, mapGetters, mapMutations } from 'vuex'
import NewsBlock from '../News/Block';

export default {
  name: 'NotificationsModal',
  components: {NewsBlock},
  data: () => ({

  }),
  computed:{
    ...mapGetters('profile/notifications', ['getOpenModal', 'getFeeds']),
    ...mapGetters('profile/info', ['getInfo']),
    watchTrigger(){
      return this.getOpenModal[0].components
    },
  },
  methods:{
    ...mapActions('profile/notifications', ['addPostById']),
    ...mapMutations('profile/notifications', ['setOpenModal', 'removeFeeds']),
    closeModal(){
      this.setOpenModal(null);
      this.removeFeeds([]);
    },
  },
  watch:{
    watchTrigger:{
      handler: function(after, before){
        console.log(after)
      },
      deep: true
    },
  },
  mounted() {
    const postId = this.getOpenModal[0].entity_id
    this.addPostById(postId).then(()=>{
      if(this.getOpenModal[0].entity_id){
        console.log(this.getOpenModal[0])
        const id = '' + this.getOpenModal[0].entity_id + '-' + this.getOpenModal[0].parent_id
        const el = document.getElementById(id);
        console.log(el)
        setTimeout(()=>{
          if(el) {
            el.scrollIntoView({block: "center", behavior: "smooth"});
            el.parentElement.classList.add('new-comment')
          }
        },1000)
      }
    });
  }

}
</script>


<style scoped lang='stylus'>

.modal-n{
  position fixed
  top: 0;
  right: 0;
  bottom: 0;
  left: 0;
  z-index: 1060;
  &__close{
      position absolute
      top: 0;
      right: 0;
      bottom: 0;
      left: 0;
      background black
      opacity 0.7
      transition opacity 0.3s

      &:hover{
        opacity 0.5
        cursor pointer !important
        transition opacity 0.3s
      }
  }

  &__content{
    position absolute
    top: 50%;
    left: 50%;
    transform: translate(-50%, -50%);
    background white;
    z-index: 1065;

    padding 25px;
    max-height 80vh;
    overflow auto
  }
}
</style>
