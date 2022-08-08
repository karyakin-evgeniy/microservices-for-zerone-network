import axios from 'axios'

export default {
  namespaced: true,
  state: {
    result: {
      friends: [],
      request: [],
      recommendations: []
    }
  },
  getters: {
    getResult: s => s.result,
    getResultById: s => id => s.result[id]
  },
  mutations: {
    setResult: (s, payload) => {
      payload.value.forEach(el => (el.photo = el.photo || '../static/img/user/default_avatar.svg'))
      return (s.result[payload.id] = payload.value)
    },
    delRequest: (s, id) => {
      const index = s.result.request.findIndex(el => el.id === id)
      if (index === -1) return
      return s.result.request.splice(index, 1)
    }
  },
  actions: {
    async apiFriends({ getters, commit }, payload) {
      let query = []
      payload &&
        Object.keys(payload).map(el => {
          payload[el] && query.push(`${el}=${payload[el]}`)
        })
      return await axios({
        url: `friends?${query.join('&')}`,
        method: 'GET'
      })
        .then(response => {
          const previousPost = getters.getResult['friends']
          const post = response.data.data
          //влив подгруженых постов + существующих
          const newsPost = [...previousPost, ...post]
          //удаление повторяющихся постов
          const chechcDoblePost = newsPost.filter((v, i, a) => a.findIndex(t => t.id === v.id) === i)

          commit('setResult', {
            id: 'friends',
            value: chechcDoblePost
          })
          return response.data.total
        })

        .catch(error => {})
    },
    apiDeleteFriends({ dispatch }, id) {
      axios({
        url: `friends/${id}`,
        method: 'DELETE'
      })
        .then(response => {
          console.log('TCL: response', response)
          dispatch(
            'global/alert/setAlert',
            {
              status: 'success',
              text: 'Пользователь удален из друзей'
            },
            {
              root: true
            }
          )
          dispatch('apiFriends')
        })
        .catch(error => {})
    },
    apiAddFriends({ commit, dispatch }, id) {
      console.log('apiAddFriends', id)
      axios({
        url: `friends/${id}`,
        method: 'POST'
      })
        .then(response => {
          console.log('TCL: response', response)
          commit('delRequest', id)
          dispatch('apiFriends')
          dispatch(
            'global/alert/setAlert',
            {
              status: 'success',
              text: 'Заявка отправлена'
            },
            {
              root: true
            }
          )
        })
        .catch(error => {})
    },
    async apiRequest({ commit }, payload) {
      let query = []
      payload &&
        Object.keys(payload).map(el => {
          payload[el] && query.push(`${el}=${payload[el]}`)
        })
      await axios({
        url: `friends/request?${query.join('&')}`,
        method: 'GET'
      })
        .then(response => {
          console.log('TCL: request', response)
          commit('setResult', {
            id: 'request',
            value: response.data.data
          })
        })
        .catch(error => {})
    },
    async apiRecommendations({ commit }, payload) {
      let query = []
      payload &&
        Object.keys(payload).map(el => {
          payload[el] && query.push(`${el}=${payload[el]}`)
        })
      await axios({
        url: `friends/recommendations?${query.join('&')}`,
        method: 'GET'
      })
        .then(response => {
          commit('setResult', {
            id: 'recommendations',
            value: response.data.data
          })
        })
        .catch(error => {})
    }
  }
}
