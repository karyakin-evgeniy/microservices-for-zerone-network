<template lang="pug">
  form.friends-possible(@submit.prevent="clerAndSearchUser" action="#")
    h4.friends-possible__title Параметры поиска
    .friends-search
      .friends-search__row
        .friends-search__block
          label.search__label(for="friends-search-name") Имя:
          input.search__input(type="text" id="friends-search-name" v-model="first_name")
        .friends-search__block
          label.search__label(for="friends-search-lastname") Фамилия:
          input.search__input(type="text" id="friends-search-lastname" v-model="last_name")

      .friends-search__block.age
        label.search__label Возраст:
        .search__row
            input#search-age-from.search__input.search__input_age(type='number', placeholder="От" v-model.number='age_from')
            span.search__age-defis —
            input#search-age-to.search__input.search__input_age(type='number', placeholder="До" v-model.number='age_to')

      .friends-search__block.region
        label.search__label Регион:
        .search__row
            select-location.search-filter__select(
              v-model="country",
              placeholder='Страна',
              :options-list="countries"
              @selectOoption="setCountry"
            )

            select-location.search-filter__select.city(
              v-model="city",
              placeholder='Город',
              :options-list="cities"
              @selectOoption="setCity"
            )
    button.friends-possible__btn(type="submit")
      simple-svg(:filepath="'/static/img/search.svg'")
      span.friends-possible__link Искать друзей
</template>

<script>
import { mapGetters, mapActions, mapMutations } from 'vuex'
import SelectLocation from '@/components/FormElements/SelectLocation.vue'
export default {
  name: 'FriendsSearch',
  components: { SelectLocation },
  data: () => ({
    first_name: null,
    last_name: null,
    age_from: null,
    age_to: null,
    country: null,
    city: null,
    offset: 0,
    itemPerPage: 10
  }),
  computed: {
    ...mapGetters('profile/country_city', ['getCountries', 'getCities', 'getLoadUsers']),
    ...mapGetters('global/search', ['getLoadUsers']),
    countries() {
      return this.getCountries.data || [];
    },
    cities() {
      return this.getCities.data || [];
    },
    selectedCountryId() {
      const country = this.countries.find(
        (item) => item.title.toUpperCase() === this.country.toUpperCase()
      )
      return country ? country.id : 0;
    },
    getCityFilter() {
      if (!this.country || this.country === 'null') {
        return this.getCities
      } else {
        return this.getCities.filter(el => el.city === this.country)
      }
    },
    searchData() {
      const { first_name, last_name, age_from, age_to, country, city, offset, itemPerPage } = this
      console.log(offset);
      return { first_name, last_name, age_from, age_to, country, city, offset, itemPerPage }
    }
  },

  created() {
    this.apiCountries()
    this.apiCities({ countryId: 1 })
  },
  beforeDestroy() {
    this.clearSearchUsers()
    this.offset = 0
    this.setOffsetUsers(this.offset)
  },
  watch: {
    country(newVal) {
      this.apiCountries({ country: newVal });
      this.apiCities({ countryId: this.selectedCountryId });
    },
    city(newVal) {
      this.apiCities({
        countryId: this.selectedCountryId,
        city: newVal,
      });
    },
    getLoadUsers(val) {
      if (val) {
        this.onSearchUsers()
      }
    },
  },
  methods: {
    ...mapActions('global/search', ['searchUsers', 'clearSearchUsers']),
    ...mapMutations('global/search', ['setOffsetUsers']),
    ...mapActions('profile/country_city', ['apiCountries', 'apiCities']),
    onSearchUsers() {
      this.searchUsers(this.searchData)
        .then(() => {
          this.offset += this.itemPerPage
        })
        .then(() => {
          this.setOffsetUsers(this.offset)
        })
    },
    clerAndSearchUser() {
      this.offset = 0
      this.setOffsetUsers(this.offset)
      setTimeout(() => {
        this.onSearchUsers()
      }, 0)
    },
    setCountry(countryData) {
      this.country = countryData.title;
    },
    setCity(value) {
      this.city = value.title
    }
  },
}
</script>

<style lang="stylus">
@import '../../assets/stylus/base/vars.styl';

.friends-search {
  margin-top: 25px;
  padding-top: 20px;
  margin-bottom: 30px;
  border-top: 1px solid #E6E6E6;
}

.search__row{
  justify-content: space-between;
}

.city{
  max-width: 200px;
}

.friends-search__row {
  @media (max-width: breakpoint-xl) {
    display: flex;


    .friends-search__block {
      flex: auto;
    }

    .friends-search__block + .friends-search__block {
      margin-top: 0;
      margin-left: 12px;
    }
  }
}

.friends-search__row + .friends-search__block {
  margin-top: 15px;
}

.friends-search__block {
  &+& {
    margin-top: 15px;
  }
}

.friends-search__block input{
    border: 1px solid #cbcbcb !important;
}

.friends-search__select {
  display: block;
  width: 100%;

  &+& {
    margin-left: 12px;
  }
}
</style>
