import axios from 'axios'
import moment from 'moment'
import { checkFinishTypingMessage, checkTypingMessage, getMessage, readMessages, unreadCount } from '../../api/socetIO'
import {MySet} from '../../api/mySetFunction'

//  [msg 0] [msg 1] .... [msg 10]
//                          ^
//                          oldestLoadedId
// <-newest messages------------------------------------------------------older
export default {
  namespaced: true,
  state: {
    dialogs: [],
    unreadedMessages: 0, // total unreaded
    messages: [], // sorted oldest->newest 0 is oldest
    totalMessages: null,
    dialogsLoaded: false,
    activeId: null,
    oldLastKnownMessageId: null,
    isHistoryEndReached: false,
    newMessage: [],
    total:0,
    typeMessage: [],
  },
  getters: {
    oldestKnownMessageId: s => (s.messages.length > 0 ? s.messages[s.activeId][0]['id'] : null),
    getDialogs: s => {
      const result = [...s.dialogs]

      // result.forEach(dialog => {
      //   // dialog.last_message = dialog.last_message || {}
      //   // dialog.last_message.isSentByMe = dialog.last_message.isSentByMe || true
      //   // dialog.last_message.recipient = dialog.last_message.recipient || {}
      //   // dialog.last_message.recipient.last_online_time = dialog.last_message.recipient.last_online_time * 1000 || 0
      //   // dialog.last_message.recipient.id = dialog.last_message.recipient.id || dialog.last_message.recipient_id
      //   // dialog.last_message.recipient.photo = dialog.last_message.recipient.photo || '../static/img/user/default_avatar.svg'
      //   // dialog.last_message.recipient.first_name = dialog.last_message.recipient.first_name || 'Name'
      //   // dialog.last_message.recipient.last_name = dialog.last_message.recipient.last_name || 'LastName'
      //   // dialog.last_message.time = dialog.last_message.time * 1000
      //   // dialog.last_message.last_online_time = dialog.last_message.last_online_time * 1000
      //   // dialog.last_message.photo = dialog.last_message.photo || '../static/img/user/default_avatar.svg'
      //   dialog.recipient_id.photo = dialog.recipient_id.photo || '../static/img/user/default_avatar.svg'
      //   dialog.recipient_id.last_online_time = dialog.recipient_id.last_online_time * 1000 || 0
      // })

      return result
    },
    getActiveDialog: s => s.dialogs.find(el => el.id == s.activeId),
    getActiveDialogId: s => s.activeId,
    dialogsLoaded: s => s.dialogsLoaded,
    unreadedMessages: s => s.unreadedMessages,
    hasOpenedDialog: s => dialogId => !!s.dialogs.find(el => el.id == dialogId),
    isHistoryEndReached: s => s.isHistoryEndReached,
    getMessages: s => s.messages,
    getNewMessage: s => s.newMessage,
    getTotalMessage: s => s.total,
    getTypeMessage: s => s.typeMessage,
  },
  mutations: {
    setUnreadedMessages: (s, unread) => (s.unreadedMessages = unread),
    setUnreadedDialogs: (s, dialog) => {
      readMessages({dialog:dialog.id})

      if(s.messages[dialog.id]){
        s.messages[dialog.id].forEach(el=>{
          el.read_status = "READ"
        })
      }

      s.dialogs.forEach(el=>{
        if(el.id == dialog.id) el.unread_count = dialog.unread_count
      })
    },
    setDialogs: (s, dialogs) => {
      const dialog = dialogs.sort((a, b) => new Date(a.last_message.time) - new Date(b.last_message.time)).reverse();
      s.activeId = s.activeId ? s.activeId : Number(dialog[0].id)
      s.dialogs = dialog
    },
    dialogsLoaded: s => (s.dialogsLoaded = true),
    addMessages: (s, { messages, total }) => {
      const mn = messages;
      if(!mn[0]) return

      mn.forEach(el=>{
        el.sid = '' + el.dialog_id + '-' + el.id;
      })

      const dialogId = mn[0].dialog_id + '';
      const messArr = [...s.messages];
      const messIdArr = [];
      const newMessIdArr = [];

      if(messArr[dialogId]){
        messArr[dialogId].forEach(el=>{
          messIdArr.push(el)
        })
      }
      mn.forEach(el=>{
        let status = true;

        for(let i = 0; i < messIdArr.length; i++){
          if(el.dialog_id == messIdArr[i].dialog_id
          && el.id == messIdArr[i].id){
            status = false;
            return;
          }
        }
        if(status) newMessIdArr.push(el);
      })

      newMessIdArr.reverse();

      if(mn.length > 1) messArr[dialogId] = [...newMessIdArr, ...messIdArr];
      if(mn.length == 1)  messArr[dialogId] = [...messIdArr, ...newMessIdArr];


      messArr[dialogId][0].total = total;

      s.messages = [... messArr];
      s.total = total
    },
    selectDialog: (state, dialogId) => {
      state.activeId = dialogId
    },
    markEndOfHistory: s => (s.isHistoryEndReached = true),
    setNewMessage: (s, messages) => {
      s.newMessage = [...s.newMessage, ...messages];
      s.newMessage.sort((a, b) => a.time - b.time);
    },
    removeNewMessage: (s, messages) => {
        s.newMessage = messages;
    },
    setTotalMessage: (s, total) => s.total = total,
    setTypeMessage: (s, typeMessage) => {
      let typeArr = [...s.typeMessage, typeMessage];
      const result = typeArr.filter((item, index, self) =>
          index === self.findIndex((t) => (
            t.authorId === item.authorId && t.dialog === item.dialog
          ))
      )
      s.typeMessage = [...result];
    },
    removeTypeMessage: (s, typeMessage) =>{
      s.typeMessage = [...s.typeMessage].filter(i => i.authorId !== typeMessage.authorId && i.dialog !== typeMessage.dialog )
    }
  },
  actions: {
    closeDialog({ commit }) {
      commit('selectDialog', null)
    },

    async switchDialog({ state, getters, commit, dispatch }, dialogId) {
      if (!state.dialogsLoaded) {
        await dispatch('apiLoadAllDialogs')
      }
      if (getters.hasOpenedDialog(dialogId)) {
        commit('selectDialog', dialogId)
        await dispatch('loadFreshMessages', dialogId)
      } else {
        console.log('what to do with this dialog???? ' + dialogId)
      }
    },

    async apiLoadAllDialogs({ commit }, payload) {
      let query = []
      payload &&
        Object.keys(payload).map(el => {
          payload[el] && query.push(`${el}=${payload[el]}`)
        })
      return await axios({
        url: `dialogs?${query.join('&')}`,
        method: 'GET'
      })
        .then(response => {
          if(response.data.total == 0) return
          commit('setDialogs', response.data.data)
          commit('dialogsLoaded')
          return response.data
        })
        .catch(error => {
          console.error(error)
        })
    },

    async createDialogWithUser({ dispatch, commit }, userId) {
      await axios({
        url: 'dialogs',
        method: 'POST',
        data: {
          users_ids: [userId]
        }
      })
        .then(async response => {
          const dialogId = response.data.data.id
          await dispatch('apiLoadAllDialogs', dialogId)
          await dispatch('switchDialog', dialogId)
        })
        .catch(error => {
          console.error(error)
        })
    },

    async loadFreshMessages({ commit, state, dispatch }, {itemPerPage=10, offset=0, id=state.activeId}) {
      await axios({
        url: `dialogs/${id}/messages`,
        method: 'GET',
        params: {
          itemPerPage: itemPerPage,
          offset: offset
        }
      })
        .then(response => {
          commit('addMessages', { messages: response.data.data, total: response.data.total })
        })
        .catch(error => {
          console.error(error)
        })
    },

    async loadOlderMessages({ commit, getters, state }, {itemPerPage=10, offset=0, id=state.activeId}) {
      await axios({
        url: `dialogs/${id}/messages`,
        params: {
          fromMessageId: getters.oldestKnownMessageId,
          offset: offset,
          itemPerPage: itemPerPage
        },
        method: 'GET'
      })
        .then(response => {
          commit('addMessages', {messages:response.data.data, total:response.data.total})
        })
        .catch(error => {
          console.error(error)
        })
    },

    async postMessage({ dispatch }, payload) {
      await axios({
        url: `dialogs/${payload.id}/messages`,
        method: 'POST',
        data: {
          message_text: payload.message_text
        }
      })
        .then(response => {
          dispatch('loadFreshMessages', {itemPerPage:1, offset:0, id:payload.id})
        })
        .catch(error => {
          console.error(error)
        })
    },

    async apiUnreadedMessages({ commit }) {
      await axios({
        url: 'dialogs/unreaded',
        method: 'GET'
      })
        .then(response => {
          commit('setUnreadedMessages', response.data.data.count)
        })
        .catch(error => {
          console.error(error)
        })
    },

    loadMessages({state, commit, rootState, dispatch }){
      function callback(response){
      console.log(response.data.author_id + " ___ " +  rootState.profile.info.info.id);
        if(response.data.author_id == rootState.profile.info.info.id)
        console.log(0);
        return dispatch('apiLoadAllDialogs').then(()=>{
          const data = new Object(response.data);
          data.sendByMe = !data.sendByMe;
          data.time = new Date(data.time * 1000);
          data.sid = '' + data.id;
          const messages = [];
          messages.push(data);

          const total = state.total ? state.total + 1 : 1;

          let recipient = state.dialogs.find(el => el.id == data['dialog_id']);

          const params = {
            messages,
            total,
          }
          const newMessage = [];
          data.recipient = recipient['recipient_id'];
          newMessage.push(data);

          commit('addMessages', params)
          commit('setNewMessage', newMessage)
        })
      }
      getMessage(callback);
    },

    checkTypingMessage({commit, state}){
      function callback (response){
        //const dialogId = response.dialog;
        commit('setTypeMessage', response);
        //if(state.dialogs[dialogId]){
         //commit('setTypeMessage', response)
        //}
      };
      checkTypingMessage(callback)
    },

    checkFinishTypingMessage({commit, state}){
      function callback (response){
        //const dialogId = response.dialog;
        commit('removeTypeMessage', response);

       // if(state.dialogs[dialogId]){
       //   commit('removeTypeMessage', response)
        //}
      };
      checkFinishTypingMessage(callback)
    },

    checkUnreadCount({commit}){
      function callback(response){
        commit('setUnreadedMessages', response)
      }
      unreadCount(callback)
    }
  },
}
