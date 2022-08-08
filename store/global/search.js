import axios from 'axios'
import router from '@/router'

export default {
  namespaced: true,
  state: {
    loadUsers: false,
    totalUsers: 0,
    offsetUsers: 0,
    loadNews: false,
    totalNews: 0,
    offsetNews: 0,
    searchText: '',
    tabs: [
      {
        id: 'users',
        text: 'Люди'
      },
      {
        id: 'news',
        text: 'Новости'
      }
    ],
    tabSelect: 'users',
    result: {
      users: [],
      news: []
    },
    status: '',
    tagForSearch: ''
  },
  getters: {
    searchText: s => s.searchText,
    tabs: s => s.tabs,
    tabSelect: s => s.tabSelect,
    getResult: s => s.result,
    getResultById: s => id => s.result[id],
    getStatus: s => s.status,
    getLoadUsers: s => s.loadUsers,
    getTotalUsers: s => s.totalUsers,
    getOffsetUsers: s => s.offsetUsers,
    getLoadNews: s => s.loadNews,
    getTotalNews: s => s.totalNews,
    getOffsetNews: s => s.offsetNews,
    tagForSearch: s => s.tagForSearch,
  },
  mutations: {
    setSearchText: (s, value) => (s.searchText = value),
    setTabSelect: (s, id) => (s.tabSelect = id),
    routePushWithQuery(state, id) {
      let query = {}
      query.tab = id
      if (state.searchText) query.text = state.searchText
      router.push({
        name: 'Search',
        query
      })
    },
    setResult: (s, result) => (s.result[result.id] = result.value),
    setLoadUsers: (s, state) => (s.loadUsers = state),
    setTotalUsers: (s, total) => (s.totalUsers = total),
    setOffsetUsers: (s, offsetUsers) => (s.offsetUsers = offsetUsers),
    setloadNews: (s, state) => (s.loadNews = state),
    setTotalNews: (s, total) => (s.totalNews = total),
    setOffsetNews: (s, offsetNews) => (s.offsetNews = offsetNews),
    setTagForSearch: (s, tag) => (s.tagForSearch = tag),
  },
  actions: {
    changeTab({ commit }, id) {
      commit('setTabSelect', id)
      commit('routePushWithQuery', id)
    },
    async searchUsers({ commit, state }, payload) {
      const { totalUsers, offsetUsers } = state;

      if (totalUsers > 0 && offsetUsers >= totalUsers) {
        return
      }

      let query = []
      payload &&
        Object.keys(payload).map(el => {
          payload[el] && query.push(`${el}=${payload[el]}`)
        })

      await axios({
        url: `users/search?${query.join('&')}`,
        method: 'GET'
      })
        .then(response => {
          const { result } = state;
          const users = totalUsers !== response.data.total ? response.data.data : [...result.users, ...response.data.data];
          const uniqUsers = users.filter((v, i, a) => a.findIndex(t => t.id === v.id) === i);

          commit('setResult', {
            id: 'users',
            value: uniqUsers
          }),
            commit('setTotalUsers', response.data.total),
            commit('setLoadUsers', false)
        })
        .catch(error => {})
    },
    clearSearchUsers({ commit }) {
      commit('setSearchText', '')
      commit('setResult', {
        id: 'users',
        value: []
      })
    },
    async searchNews({ commit, state }, payload) {
      const { totalNews, offsetNews } = state;

      if (totalNews > 0 && offsetNews >= totalNews) {
        return
      }

      let query = []
      payload &&
        Object.keys(payload).map(el => {
          payload[el] && query.push(`${el}=${payload[el]}`)
        })
      await axios({
        url: `post?${query.join('&')}`,
        method: 'GET'
      })
        .then(response => {
          const { result } = state;
          const news = totalNews !== response.data.total ? response.data.data : [...result.news, ...response.data.data];
          const uniqNews = news.filter((v, i, a) => a.findIndex(t => t.id === v.id) === i);

          commit('setResult', {
            id: 'news',
            value: uniqNews
          }),
            commit('setTotalNews', response.data.total),
            commit('setloadNews', false)
        })
        .catch(error => {})
    },
    clearSearchNews({ commit }) {
      commit('setSearchText', '')
      commit('setResult', {
        id: 'news',
        value: []
      })
      commit('setTagForSearch', '')
      commit('setTabSelect', 'users')
    }
  }
}
