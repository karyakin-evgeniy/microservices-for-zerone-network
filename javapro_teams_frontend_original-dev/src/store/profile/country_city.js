import axios from 'axios'

export default {
  namespaced: true,
  state: {
    countries: [],
    cities: [],
  },
  getters: {
    getCountries: s => s.countries,
    getCities: s => s.cities,
  },
  mutations: {
    setCountries: (s, value) => s.countries = value,
    setCities: (s, value) => s.cities = value,
  },
  actions: {
    async apiCountries({
      commit
    }, params) {
      await axios({
        url: 'platform/countries',
        method: 'GET',
        params,
      }).then(response => {
        commit('setCountries', response.data)
      }).catch(() => {})
    },
    async apiCities({
      commit
    }, params) {
      await axios({
        url: 'platform/cities',
        method: 'GET',
        params,
      }).then(response => {
        commit('setCities', response.data)
      }).catch(() => {})
    },
    async apiAllCities({
      commit
    }) {
      await axios({
        url: 'platform/getAllCountriesWithTowns',
        method: 'GET'
      }).then(response => {
        commit('setCities', response.data.data)
      }).catch(() => {})
    },
  }
}
