import axios from 'axios'
import store from '@/store'
import moment from 'moment'
import { url, urlLocal } from '@/utils/url.js'

const NODE_ENV = process.env.NODE_ENV ? process.env.NODE_ENV : 'development'

axios.defaults.headers['content-type'] = 'application/json'
axios.defaults.withCredentials = false
switch (NODE_ENV) {
  case 'development':
    axios.defaults.baseURL = url + 'api/v1/'
    break
  case 'production':
    axios.defaults.baseURL = url + 'api/v1/'
    break
  case 'build':
    axios.defaults.baseURL = url + 'api/v1/'
    break
  default:
    axios.defaults.baseURL = 'https://virtserver.swaggerhub.com/andrewleykin/social/1.0.4/api/v1/'
}

const token = localStorage.getItem('user-token')
if (token) axios.defaults.headers.common['Authorization'] = token

let timeLastStart = null

axios.interceptors.response.use(null, error => {
  // добавить проверку на законченный токен и сделать выход из приложения
  // store.dispatch('auth/api/logout')
  if (error.response.status === 401) {
    store.dispatch('auth/api/logout')
    localStorage.removeItem('user-token')
  }

  const message = error.response.data.error_description || error.response.data.path + ' - ' + error.response.data.error

  const currentTime = moment()
  let timeCount = !timeLastStart ? 0 : 3500 - currentTime.diff(timeLastStart, 'milliseconds')
  timeCount = timeCount < 0 ? 0 : timeCount

  setTimeout(() => {
    store.dispatch('global/alert/setAlert', {
      status: 'error',
      text: message
    })
  }, timeCount)

  timeLastStart = currentTime

  return Promise.reject(error)
})
