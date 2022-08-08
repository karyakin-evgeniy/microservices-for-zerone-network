import io from 'socket.io-client'

const url = 'http://195.161.114.18:1111';

export const socket = io(url, {
  transports: ['websocket'],
})

export function authorizationIo(){
  const token = localStorage.getItem('user-token');

  if(!token){
    setTimeout(()=>{
      authorizationIo();
    },5000)
  }

  socket.connect();
  socket.emit('auth', {
    token: token
  })
}

socket.on('disconnect', (response)=>{
  console.log(response)
  socket.connect();
  authorizationIo()
})

function ask(name, callback){
  if(typeof callback !== 'function') throw new TypeError('callback is not function');
  socket.emit('newListener')

  socket.on('auth-response', response=>{
    if(response == 'not'){
      authorizationIo();
    }else{
      socket.removeListener(name);
      socket.on(name, response=>{
        callback(response);
      });
    }
  });
}

export function disconnectIo(){
  socket.disconnect();
}

export async function getMessage(callback){
  ask('message', callback)
}

export function submitTypingMessage(data){
  socket.emit('start-typing', data)
}

export function submitFinishTypingMessage(data){
  socket.emit('stop-typing', data)
}

export function checkTypingMessage(callback){
  ask('start-typing-response', callback)
}

export function checkFinishTypingMessage(callback){
  ask('stop-typing-response', callback)
}

export function unreadCount(callback){
  ask('unread-response', callback)
}

export function readMessages (idDialogs){
  socket.emit('read-messages', idDialogs)
}

export function responseNotification(callback){
  ask('comment-notification-response', callback)
}

export function responseNotificationAddFriends(callback){
  ask('friend-notification-response', callback)
}
