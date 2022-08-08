<script>
import { mapGetters, mapActions } from 'vuex'

export default {
    mounted() {
      if (this.activeDialog) {
        this.loadFreshMessages(this.activeDialog.id)
      }

      this.apiNotifications()
    },
    computed: {
        ...mapGetters('profile/dialogs', { activeDialog: 'getActiveDialog' }),
    },
    methods: {
        ...mapActions('profile/dialogs', ['loadFreshMessages']),
        ...mapActions('profile/notifications', ['apiNotifications']),
    },
    render: () => null,
}
</script>
