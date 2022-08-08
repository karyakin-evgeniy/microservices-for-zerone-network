import axios from 'axios'

export default {
  namespaced: true,
  state: {
    feeds: [],
    feedsId: [],
  },
  getters: {
    getFeeds(state) {
      return state.feeds
    }
  },
  mutations: {
    setFeeds: (s, payload) => {
      if (payload.length > 0) {
        if (s.feeds.length == 0) {
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
          s.feeds = sort
        } else {
          console.log(payload)
          if (payload.post_id) {
            let post = s.feeds[s.feeds.indexOf(s.feeds.find(el => el.id === payload.post_id))]
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

            s.feeds[s.feeds.indexOf(s.feeds.find(el => el.id === payload.post_id))].comments.data = [...sort]
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
            s.feeds = sort
          }
        }
      } else {
        s.feeds = payload
      }
    },
    setFeedsCommentsById(s, payload) {
      s.feeds.forEach(el => {
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
  },
  actions: {
    async apiFeeds({ getters, commit }, payload) {
      let query = []
      payload &&
        Object.keys(payload).map(el => {
          payload[el] && query.push(`${el}=${payload[el]}`)
        })

      return await axios({
        url: `feeds?${query.join('&')}`,
        method: 'GET'
      })
        .then(response => {
          const previousPost = getters.getFeeds
          const post = response.data.data
          //влив подгруженых постов + существующих
          const newsPost = [...previousPost, ...post]
          //удаление повторяющихся постов
          const chechcDoblePost = newsPost.filter((v, i, a) => a.findIndex(t => t.id === v.id) === i)
          commit('setFeeds', chechcDoblePost)
          return response.data.total
        })
        .catch(() => {})
    },
    async apiFeedsById({ commit }, post_id) {
      await axios({
        url: `post/${post_id}`,
        method: 'GET'
      })
        .then(response => {
          console.log('TCL: apiFeeds -> response', response)
          commit('setFeedsById', response.data)
          return response.data.total
        })
        .catch(() => {})
    },
    async actionsFeed({ dispatch }, payload) {
      console.log('TCL: payload', payload)
      let url = payload.edit ? `post/${payload.post_id}` : `users/${payload.id}/wall`
      let method = payload.edit ? 'PUT' : 'POST'
      if (payload.publish_date) url += '?publish_date=' + payload.publish_date
      await axios({
        url,
        method,
        data: {
          title: payload.title,
          post_text: payload.post_text,
          tags: payload.tags
        }
      })
        .then(response => {
          if (payload.edit) {
            dispatch('users/info/apiWallById', payload.post_id, {
              root: true
            })
            payload.route === 'News'
              ? dispatch('apiFeeds')
              : dispatch(
                  'users/info/apiWall',
                  {
                    id: payload.id
                  },
                  {
                    root: true
                  }
                )
          } else {
            payload.route === 'News'
              ? dispatch('apiFeeds')
              : dispatch(
                  'users/info/apiWall',
                  {
                    id: payload.id
                  },
                  {
                    root: true
                  }
                )
          }
        })
        .catch(() => {})
    },
    async deleteFeeds({ dispatch }, payload) {
      await axios({
        url: `post/${payload.post_id}`,
        method: 'DELETE'
      })
        .then(response => {

          payload.route === 'News'
            ? dispatch('apiFeeds')
            : dispatch(
                'users/info/apiWall',
                {
                  id: payload.id
                },
                {
                  root: true
                }
              )
        })
        .catch(() => {})
    },
    async recoverFeeds({ dispatch }, payload) {
      await axios({
        url: `post/${payload.post_id}/recover`,
        method: 'PUT'
      })
        .then(response => {
          payload.route === 'News'
            ? dispatch('apiFeeds')
            : dispatch(
                'users/info/apiWall',
                {
                  id: payload.id
                },
                {
                  root: true
                }
              )
        })
        .catch(() => {})
    }
  }
}
