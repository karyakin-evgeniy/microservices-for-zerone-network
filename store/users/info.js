import axios from 'axios'
import moment from 'moment'
import router from '@/router'

export default {
  namespaced: true,
  state: {
    info: null,
    wall: [],
    users: null
  },
  getters: {
    getInfo(state) {
      if (!state.info) return
      let result = {
        ...state.info
      }
      // если понадобиться то добавить склонение (для публикаций, но нужен или пол или отчество)
      // библиотека - petrovich
      result.fullName = result.first_name + ' ' + result.last_name
      result.ages = moment().diff(result.birth_date, 'years')
      result.is_onlined = moment().diff(moment(result.last_online_time), 'seconds') <= 60
      result.photo = result.photo || '../static/img/user/default_avatar.svg'
      return result
    },
    getUsersInfo(state, getters, rootState) {
      if (!state.users) return
      let result = {
        ...state.users
      }

      // если понадобиться то добавить склонение (для публикаций, но нужен или пол или отчество)
      // библиотека - petrovich
      result.fullName = result.first_name + ' ' + result.last_name
      result.ages = moment().diff(result.birth_date, 'years')
      result.is_onlined = moment().diff(moment(result.last_online_time), 'seconds') <= 60
      result.photo = result.photo || '../static/img/user/default_avatar.svg'
      result.is_friend = rootState.profile.friends.result.friends.find(el => el.id === result.id) ? true : false
      return result
    },
    getWall(state) {
      return state.wall
    },
    getWallPostedLength: s => s.wall.filter(el => el.type === 'POSTED').length,
    getWallQueuedLength: s => s.wall.filter(el => el.type === 'QUEUED').length,
    getWallDeletedLength: s => s.wall.filter(el => el.type === 'DELETED').length
  },
  mutations: {
    setInfo: (s, info) => (s.info = info),
    setWall: (s, payload) => {
      if (payload.length > 0) {
        if (s.wall.length == 0) {
          const sort = payload
          sort.forEach(el => {
            el.comments.data.sort(function(a, b) {
              return new Date(a.time) - new Date(b.time)
            })
            el.comments.data.forEach(elSub => {
              elSub.sub_comments.sort(function(a, b) {
                return new Date(a.time) - new Date(b.time)
              })
            })
          })
          s.wall = sort
        } else {
          console.log(payload)
          if (payload.post_id) {
            let post = s.wall[s.wall.indexOf(s.wall.find(el => el.id === payload.post_id))]
            let newPost = [...payload.value.data, ...post.comments.data]
            let filteredStrings = newPost.filter(
              (thing, index, self) => index === self.findIndex(t => t.id === thing.id)
            )
            let sort = filteredStrings.sort(function(a, b) {
              return new Date(a.time) - new Date(b.time)
            })

            sort.forEach(el => {
              if (el.sub_comments.length !== 0) {
                el.sub_comments = el.sub_comments.sort(function(a, b) {
                  return new Date(a.time) - new Date(b.time)
                })
              }
            })

            s.wall[s.wall.indexOf(s.wall.find(el => el.id === payload.post_id))].comments.data = [...sort]
          } else {
            const sort = payload
            sort.forEach(el => {
              el.comments.data.sort(function(a, b) {
                return new Date(a.time) - new Date(b.time)
              })
              el.comments.data.forEach(elSub => {
                elSub.sub_comments.sort(function(a, b) {
                  return new Date(a.time) - new Date(b.time)
                })
              })
            })
            s.wall = sort
          }
        }
      } else {
        s.wall = payload
      }
    },
    setCommentsById: (s, payload) => {
      s.wall.forEach(el => {
        if (el.id == payload.post_id) {
          let newPost = [...payload.value.data, ...el.comments.data]
          newPost = newPost.filter((thing, index, self) => index === self.findIndex(t => t.id === thing.id))
          let sortMainComments = payload.value
          sortMainComments.data = newPost
          sortMainComments.data = payload.value.data.sort(function(a, b) {
            return new Date(a.time) - new Date(b.time)
          })
          sortMainComments.data.forEach(elSub => {
            elSub.sub_comments.sort(function(a, b) {
              return new Date(a.time) - new Date(b.time)
            })
          })
          el.comments = sortMainComments
        }
      })
    },
    setUsersInfo: (s, info) => {
      return (s.users = info)
    }
  },
  actions: {
    async apiInfo({ commit }, id) {
      await axios({
        url: `users/${id}`,
        method: 'GET'
      })
        .then(response => {
          console.log(response)
          commit('setInfo', response.data.data)
        })
        .catch(error => {})
    },

    async apiWall({ getters, commit }, data) {
      let query = []

      console.log('apiWall');

      data.payload &&
        Object.keys(data.payload).map(el => {
          data.payload[el] && query.push(`${el}=${data.payload[el]}`)
        })
      return await axios({
        url: `users/${data.id}/wall?${query.join('&')}`,
        method: 'GET'
      })
        .then(response => {
          const previousWall = getters.getWall
          console.log(previousWall)
          const wall = response.data.data
          console.log(wall)
          const newsWall = [...previousWall, ...wall]
          const chechcDobleWall = wall.filter((v, i, a) => a.findIndex(t => t.id === v.id) === i)
          console.log(chechcDobleWall)

          commit('setWall', chechcDobleWall)

          return response.data.total
        })
        .catch(error => {})
    },

    async apiWallById({ commit }, id) {
      console.log('apiWallById');
      await axios({
        url: `post/${id}`,
        method: 'GET'
      })
        .then(response => {
          console.log(response.data.data);
          commit('setWall', {
            id,
            value: response.data.data
          })
        })
        .catch(error => {})
    },

    async apiCommentsById({ commit }, id) {
      await axios({
        url: `post/${id}/comments`,
        method: 'GET'
      })
        .then(response => {
          commit('setWall', response.data.data)
        })
        .catch(error => {})
    },

    async userInfoId({ commit, dispatch }, data) {
      return await axios({
        url: `users/${data.id}`,
        method: 'GET'
      })
        .then(async response => {
          let total
          commit('setUsersInfo', response.data.data)
          await dispatch('apiWall', data).then(resp => {
            total = resp
          })
          return total
        })
        .catch(error => {})
    }
  }
}
