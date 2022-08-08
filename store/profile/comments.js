import axios from 'axios'
import router from '@/router'

export default {
  namespaced: true,
  actions: {
    async commentsById({ commit }, post_id) {
      await axios({
        url: `post/${post_id}`,
        method: 'GET'
      })
        .then(response => {
          let dataComments = {
            post_id: data.post_id,
            value: response.data.data
          }

          router.history.current.name === 'News'
            ? commit('profile/feeds/setFeedsById', dataComments, {
                root: true
              })
            : commit('users/info/setCommentsById', dataComments, {
                root: true
              })
        })
        .catch(() => {})
    },
    async newComment({ dispatch, rootState }, payload) {
      await axios({
        url: `post/${payload.post_id}/comments`,
        method: 'POST',
        data: {
          parent_id: payload.parent_id,
          comment_text: payload.text,
          images: payload.images
        }
      })
        .then(() => {
          dispatch('addCommentsById', payload)
          if(rootState.profile.notifications.feeds.length > 0){
            dispatch('profile/notifications/addCommentsById', payload.post_id, {root: true})
          }
        })
        .catch(() => {})
    },
    async editComment({ dispatch, rootState }, payload) {
      await axios({
        url: `post/${payload.post_id}/comments/${payload.id}`,
        method: 'PUT',
        data: {
          comment_text: payload.text
        }
      })
        .then(() => {
          dispatch('addCommentsById', payload)
          if(rootState.profile.notifications.feeds.length > 0){
            dispatch('profile/notifications/addCommentsById', payload.post_id, {root: true})
          }
        })
        .catch(() => {})
    },
    async deleteComment({ dispatch,rootState }, payload) {
      await axios({
        url: `post/${payload.post_id}/comments/${payload.id}`,
        method: 'DELETE'
      })
        .then(() => {
          dispatch('addCommentsById', payload)
          if(rootState.profile.notifications.feeds.length > 0){
            dispatch('profile/notifications/addCommentsById', payload.post_id, {root: true})
          }
        })
        .catch(() => {})
    },
    async recoverComment({ dispatch,rootState }, payload) {
      await axios({
        url: `post/${payload.post_id}/comments/${payload.id}/recover`,
        method: 'PUT'
      })
        .then(() => {
          dispatch('addCommentsById', payload)
          if(rootState.profile.notifications.feeds.length > 0){
            dispatch('profile/notifications/addCommentsById', payload.post_id, {root: true})
          }
        })
        .catch(() => {})
    },
    async commentActions({ dispatch }, payload) {
      payload.edit ? await dispatch('editComment', payload) : await dispatch('newComment', payload)
    },
    async addCommentsById({ commit, dispatch,rootState }, data) {
      await axios({
        url: `post/${data.post_id}/comments?offset=${data.offset}&itemPerPage=${data.perPage}`,
        method: 'GET'
      })
        .then(response => {
          let dataComments = {
            post_id: data.post_id,
            value: response.data
          }

          router.history.current.name === 'News'
            ? commit('profile/feeds/setFeedsCommentsById', dataComments, {
                root: true
              })
            : commit('users/info/setCommentsById', dataComments, {
                root: true
              })
          if(rootState.profile.notifications.feeds.length > 0){
            dispatch('profile/notifications/addCommentsById', payload.post_id, {root: true})
          }
        })
        .catch(() => {})
    },
  }
}
