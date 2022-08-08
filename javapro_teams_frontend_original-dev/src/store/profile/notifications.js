import axios from 'axios'
import { responseNotification, responseNotificationAddFriends } from '../../api/socetIO'

export default {
  namespaced: true,
  state: {
    notifications: [],
    feeds: [],
    openModal: null,
  },
  getters: {
    getNotifications: s => s.notifications,
    getNotificationsLength: s => s.notifications.length,
    getNotificationsTextType: s => type => {
      const lang = localStorage.getItem('lang') || 'ru'
      if (lang === 'ru') {
        switch (type) {
          case 'POST':
            return 'опубликовал новую запись'
          case 'POST_COMMENT':
            return 'оставил комментарий'
          case 'COMMENT_COMMENT':
            return 'ответил на ваш комментарий'
          case 'FRIEND_REQUEST':
            return 'добавил в друзья нового пользователя'
          case 'FRIEND_BIRTHDAY':
            return 'день рождение'
          case 'MESSAGE':
            return 'прислал сообщение'
        }
      }
      if (lang === 'en') {
        switch (type) {
          case 'POST':
            return 'posted a new post'
          case 'POST_COMMENT':
            return 'left a comment'
          case 'COMMENT_COMMENT':
            return 'replied to your comment'
          case 'FRIEND_REQUEST':
            return 'added a new user as a friend'
          case 'FRIEND_BIRTHDAY':
            return 'birthday'
          case 'MESSAGE':
            return 'sent a message'
        }
      }
    },
    getOpenModal: s => s.openModal,
    getFeeds: s => s.feeds,
  },
  mutations: {
    setNotifications: (s, value) => {s
      const notifications = [...value];
      notifications.reverse();
      s.notifications = [...notifications]
    },
    setOncNotifications: (s,value) => {
      console.log(value[0])
      value[0].sent_time = new Date(value[0].sent_time * 1000)
      const result = [...value, ...s.notifications]
      s.notifications = result;
    },
    setOpenModal: (s, value) => s.openModal = value,
    setFeedsById(s, payload){
      const feeds = [payload.data];
      feeds[0].id = s.openModal[0].parent_entity_id;
      s.feeds = [...feeds];
    },
    setFeedsByIdComments(s, payload){
      s.feeds[0].comments = payload;
    },
    removeFeeds(s, payload){
      s.feeds = payload
    }
  }
  ,
  actions: {
    async apiNotifications({ state, commit, }) {
      console.log('apiNotifications')
      await axios({
        url: 'notifications',
        method: 'GET'
      }).then(response => {
        console.log(response)
        if (`${response.data.data.map(z => z.sent_time)}` !== `${state.notifications.map(z => z.sent_time)}`) {
          const result = response.data.data.map(el => {
            if (!el.entity_author) el.entity_author = {}
            if (!el.entity_author.photo) el.entity_author.photo = '../static/img/user/default_avatar.svg'
            if (!el.entity_author.first_name) el.entity_author.first_name = `Имя автора с ID: ${el.id}`
            if (!el.entity_author.last_name) el.entity_author.last_name = `Фамилия автора с ID: ${el.id}`
            if (!el.event_type) el.event_type = 'POST'
            return el;
          });
          commit('setNotifications', result)
        }
      }).catch(() => {})
    },
    async readNotifications({ state, commit, dispatch }, notificationId) {
      if (!notificationId) {
        await axios({
          url: 'notifications?all=true',
          method: 'PUT'
        }).then(()=> {
          dispatch('apiNotifications')
        }).catch(() => {})
      } else {
        await axios({
          url: `notifications?id=${notificationId}`,
          method: 'PUT'
        }).then(()=> {
          dispatch('apiNotifications')
        }).catch(() => {})
      }
    },
    socketNotifications({commit}){
      function callback(response){
        const res = [response]
        const result = res.map(el => {
          if (!el.entity_author) el.entity_author = {}
          if (!el.entity_author.photo) el.entity_author.photo = '../static/img/user/default_avatar.svg'
          if (!el.entity_author.first_name) el.entity_author.first_name = `Имя автора с ID: ${el.id}`
          if (!el.entity_author.last_name) el.entity_author.last_name = `Фамилия автора с ID: ${el.id}`
          if (!el.event_type) el.event_type = 'POST'
          return el;
        });
        commit('setOncNotifications', result)
      }
      responseNotification(callback)
    },
    socketNotificationsAddFriends({commit}){
      function callback(response){
        const res = [response]
        const result = res.map(el => {
          if (!el.entity_author) el.entity_author = {}
          if (!el.entity_author.photo) el.entity_author.photo = '../static/img/user/default_avatar.svg'
          if (!el.entity_author.first_name) el.entity_author.first_name = `Имя автора с ID: ${el.id}`
          if (!el.entity_author.last_name) el.entity_author.last_name = `Фамилия автора с ID: ${el.id}`
          if (!el.event_type) el.event_type = 'POST'
          return el;
        });
        commit('setOncNotifications', result)
      }
      responseNotificationAddFriends(callback)
    },
    async addPostById({commit, dispatch}, id){
      await axios({
        url: `post/${id}`,
        method: 'GET'
      })
        .then(async response => {
          await commit('setFeedsById', response.data)
          await dispatch('addCommentsById',id)
        })
        .catch(() => {})
    },
    async addCommentsById({ commit, dispatch }, id) {
      await axios({
        url: `post/${id}/comments?offset=0&itemPerPage=10000`,
        method: 'GET'
      })
        .then(response => {
          console.log(response)
          commit('setFeedsByIdComments', response.data)
        })
        .catch(() => {})
    },
  }
}
