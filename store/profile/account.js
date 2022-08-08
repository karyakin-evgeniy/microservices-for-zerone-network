import axios from 'axios'

export default {
  namespaced: true,
  state: {
    notifications: {
      ru: [
        {
          icon: 'comments',
          name: 'О новых комментариях к моим публикациям',
          type: 'POST_COMMENT',
          enable: false
        },
        {
          icon: 'reviews',
          name: 'О ответах на мои комментарии',
          type: 'COMMENT_COMMENT',
          enable: false
        },
        {
          icon: 'friends',
          name: 'О заявках в дузья',
          type: 'FRIEND_REQUEST',
          enable: false
        },
         //{
         //  icon: 'messages',
         //  name: 'О новых личных сообщениях',
         //  type: 'MESSAGE',
         //  enable: false
         //},
         {
           icon: 'birthdays',
           name: 'О дне рождения друга',
           type: 'FRIEND_BIRTHDAY',
           enable: false
         }
      ],
      en: [
        {
          icon: 'comments',
          name: 'About new comments to my publications',
          type: 'POST_COMMENT',
          enable: false
        },
        {
          icon: 'reviews',
          name: 'About replies to my comments',
          type: 'COMMENT_COMMENT',
          enable: false
        },
        {
          icon: 'friends',
          name: 'About friend requests',
          type: 'FRIEND_REQUEST',
          enable: false
        },
        // {
        //   icon: 'messages',
        //   name: 'About new private messages',
        //   type: 'MESSAGE',
        //   enable: false
        // },
         {
           icon: 'birthdays',
           name: 'Birthday friend',
           type: 'FRIEND_BIRTHDAY',
           enable: false
         }
      ]
    },
    email: ''
  },
  getters: {
    getNotificationsSettings: s => s.notifications[localStorage.getItem('lang')],
    getMail: s => s.email
  },
  mutations: {
    setNotificationsSettings: (s, notifications) =>
      s.notifications[localStorage.getItem('lang')].map(
        el => (el.enable = notifications.find(n => n.notification_type === el.type).enable)
      ),
    setEmail: (s, mail) => (s.email = mail)
  },
  actions: {
    async passwordRecoveryConfirmation({}, email) {
      return axios({
        url: 'account/recovery',
        method: 'PUT',
        data: email
      })
        .then(resp => {
          return resp
        })
        .catch(error => {})
    },
    async passwordRecovery({}, data) {
      console.log(data)
      return axios({
        url: `account/recovery_complete?key=${data.key}&eMail=${data.email}`,
        method: 'GET'
      })
        .then(resp => {
          return resp
        })
        .catch(error => {})
    },
    async passwordSet({ rootState }, value) {
      let data = {
        token: value.token,
        password: value.password
      }
      await axios({
        url: 'account/password/set',
        method: 'PUT',
        data
      })
        .then(response => {})
        .catch(error => {})
    },
    async changeEmail({ rootState }, value) {
      await axios({
        url: 'account/email',
        method: 'PUT',
        data: value
      })
        .then(resp => {})
        .catch(error => {})
    },
    changeNotifications({ dispatch }, data) {
      axios({
        url: 'account/notifications',
        method: 'PUT',
        data
      })
        .then(response => {
          dispatch(
            'global/alert/setAlert',
            {
              status: 'success',
              text: 'Настройки уведомления изменены'
            },
            {
              root: true
            }
          )
          dispatch('apiNotificationsSettings')
        })
        .catch(error => {})
    },
    async apiNotificationsSettings({ commit }) {
      await axios({
        url: 'account/notifications',
        method: 'GET'
      })
        .then(response => {
          console.log(response.data.data)
          commit('setNotificationsSettings', response.data.data)
        })
        .catch(error => {})
    }
  }
}
