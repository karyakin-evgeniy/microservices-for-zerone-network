<template lang="pug">
  .main-layout(:class='{"open-modal":getOpenModal}')
    main-layout-sidebar
    .main-layout__main
      main-layout-header
      main.main-layout__page
        router-view
    real-time-updater
    notifications-messages(:openChat='openChat'  @updateOpenChat="openChat = $event")
    .mini-chat(v-if='openAllChats.length > 0')
      mini-chat(v-for='item in openAllChats' :key='item' :chatID='item' :close='closeMiniChat')
    template(v-if='getOpenModal')
      transition(name="modal")
        notifications-modal
</template>

<script>
import MainLayoutHeader from '@/components/MainLayout/Header'
import MainLayoutSidebar from '@/components/MainLayout/Sidebar'
import RealTimeUpdater from '@/components/RealTimeUpdater'
import NotificationsMessages from '../components/Im/NotificationsMessages'
import { mapActions, mapGetters, mapMutations } from 'vuex'
import MiniChat from '../components/Im/MiniChat'
import NotificationsModal from '../components/Notifications/NotificationsModal'
export default {
  components: {
    NotificationsModal,
    MiniChat,
    NotificationsMessages,
    MainLayoutHeader,
    MainLayoutSidebar,
    RealTimeUpdater
  },
  data: () => ({
    openChat:[],
  }),
  methods:{
    ...mapActions('profile/dialogs', [
      'checkUnreadCount',
      'apiLoadAllDialogs',
      'loadMessages',
      'checkTypingMessage',
      'checkFinishTypingMessage',]),

    ...mapMutations('profile/notifications', ['setOpenModal']),

    closeMiniChat(chatID){
      this.openChat = this.openChat.filter(el => el !== chatID);
    },
    closeModal(){
      this.setOpenModal(null)
    }
  },
  computed:{
    ...mapGetters('profile/notifications', ['getOpenModal']),
    openAllChats(){
      return this.openChat.filter((e,i,a)=>a.indexOf(e)==i)
    },
  },
  mounted() {
    this.checkUnreadCount();
    this.apiLoadAllDialogs();
    this.loadMessages();
    this.checkTypingMessage();
    this.checkFinishTypingMessage();
  }
}
</script>

<style lang="stylus">
@import '../assets/stylus/base/vars.styl';

.main-layout {
  position relative
  display: flex;
  height: 100%;
}

.main-layout__main {
  width: 100%;
  margin-left: sidebar-width;
  height: 100%;

  @media (max-width: breakpoint-xxl) {
    margin-left: sidebar-width-xl;
  }
}

.main-layout__page {
  padding-top: header-height;
  background-color: white-lilac;
  min-height: 100%;
  position: relative;
  z-index: 1;
}
.open-modal{
  overflow hidden
}
.modal-enter-active,
.modal-leave-active {
  transition: opacity 0.5s ease;
}

.modal-enter-from,
.modal-leave-to {
  opacity: 0;
}
</style>
