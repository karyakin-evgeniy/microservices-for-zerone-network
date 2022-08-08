<template lang="pug">
.settings-main
    user-info-form-block(:label='$t("name")', :placeholder='$t("entName")', v-model.trim='name', :class="{invalid: ($v.name.$dirty && !$v.name.required)}" )

    .user-error-form__block
      span.form__error(v-if="$v.name.$dirty && !$v.name.required") {{ $t('entName') }}

    user-info-form-block(:label='$t("lastName")', :placeholder='$t("entLastName")', v-model.trim='lastName', :class="{invalid: ($v.lastName.$dirty && !$v.lastName.required)}")

    .user-error-form__block
      span.form__error(v-if="$v.lastName.$dirty && !$v.lastName.required") {{ $t('entLastName') }}

    user-info-form-block(:label='$t("tel")', :placeholder='$t("entTel")', v-model.trim='phone', phone, :class="{invalid: ($v.phone.$dirty && !$v.phone.required) || ($v.phone.$dirty && !$v.phone.checkphone)}")

    .user-error-form__block
      span.form__error(v-if="($v.phone.$dirty && !$v.phone.required) || ($v.phone.$dirty && !$v.phone.checkphone)") {{ $t('entTel') }}

    .user-info-form__block
        span.user-info-form__label {{ $t("country") }}

        select-location.user-info-form__wrap.countries(
          v-model.trim="country",
          :placeholder='$t("entCountry")',
          :options-list="countries"
          @selectOoption="setCountry"
        )

    .user-info-form__block
        span.user-info-form__label {{ $t("city") }}

        select-location.user-info-form__wrap.countries(
          v-model.trim="city",
          :placeholder='$t("entCity")',
          :options-list="cities"
          @selectOoption="setCity"
        )

    .user-info-form__block
        span.user-info-form__label {{ $t("birthDay") }}
        .user-info-form__wrap
            select.select.user-info-form__select.day(v-model='day')
                option(v-for='d in days', :key='d', :value='d') {{ d }}
            select.select.user-info-form__select.month(v-model='month')
                option(v-for='m in months', :key='m.val', :value='m') {{ m.text }}
            select.select.user-info-form__select.year(v-model='year')
                option(v-for='i in years', :key='i', :value='i') {{ i }}

    .user-info-form__block.user-info-form__block--photo
        span.user-info-form__label {{ $t("photo") }}
        .user-info-form__wrap
            .user-info-form__photo-wrap
                input#photo.user-info-form__input-photo(
                    type='file',
                    ref='photo',
                    @change='processFile($event)',
                    accept='image/*'
                )
                label.user-info-form__input.user-info-form__input--photo(for='photo')
                    span.setting-main__photo-delete(v-if='photo') {{ photo.name }}
                        simple-svg(:filepath='"/static/img/delete.svg"', @click.native.prevent='deletePhoto')
                button-hover(variant='fill', bordered, @click.native='loadPhoto') {{ $t("download") }}
            .user-info-form__photo-pic(v-if='src')
                img(:src='src', :alt='name + " " + lastName')
    user-info-form-block(:label='$t("myself")', v-model='about', about)
    .user-info-form__block.user-info-form__block--actions
        span.user-info-form__label
        .user-info-form__wrap
            button-hover(@click.native.prevent='submitHandler') {{ $t("save") }}
            router-link.settings-main__back(:to='{ name: "Profile" }')
                button-hover(variant='red', bordered) {{ $t("cancel") }}
</template>

<script>
import { mapGetters, mapActions } from 'vuex'
import {required, minLength} from 'vuelidate/lib/validators'
import moment from 'moment'
import ClickOutside from 'vue-click-outside'
import UserInfoFormBlock from '@/components/Settings/UserInfoForm/Block.vue'
import SelectLocation from '@/components/FormElements/SelectLocation.vue'

const checkphone = (phone) => {
  if (phone.trim()[0]!='+') {
    phone = '+7' + phone.trim();
  }
  let regex = /^(\s*)?(\+)?([- _():=+]?\d[- _():=+]?){11,14}(\s*)?$/;
  return regex.test(phone.trim());
}

export default {
  name: 'SettingsMain',
  components: { UserInfoFormBlock, SelectLocation },
  directives: {
    ClickOutside
  },
  i18n: {
    messages: {
      en: {
        tel: 'Phone:',
        entTel: 'Enter your phone',
        lastName: 'Last name:',
        entLastName: 'Enter last name',
        name: 'Name:',
        entName: 'Enter name',
        country: 'Country:',
        entCountry: 'Enter country',
        city: 'City:',
        entCity: 'Enter city',
        birthDay: 'Date of Birth:',
        photo: 'Photo:',
        myself: 'About myself:',
        download: 'Download',
        cancel: 'Cancel',
        save: 'Save'
      },
      ru: {
        tel: '*Телефон:',
        entTel: 'Введите телефон',
        lastName: '*Фамилия:',
        entLastName: 'Введите фамилию',
        name: '*Имя:',
        entName: 'Введите имя',
        country: 'Страна:',
        entCountry: 'Введите страну',
        city: 'Город:',
        entCity: 'Введите город',
        birthDay: 'Дата рождения:',
        photo: 'Фотография:',
        myself: 'О себе:',
        download: 'Загрузить',
        cancel: 'Отмена',
        save: 'Сохранить'
      }
    }
  },
  data: () => ({
    name: '',
    lastName: '',
    phone: '',
    about: '',
    day: 1,
    month: {},
    year: 2000,
    photo: null,
    src: '',
    country: '',
    city: '',
    country_city: {
      country: '',
      city: ''
    }
  }),
  validations: {
    name: {required, minLength: minLength(3)},
    lastName: {required,  minLength: minLength(3)},
    phone: {required, checkphone},
  },
  computed: {
    ...mapGetters('global/storage', ['getStorage']),
    ...mapGetters('profile/info', ['getInfo']),
    ...mapGetters('profile/country_city', ['getCountries', 'getCities']),

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

      console.log('country: ', country);
      return country ? country.id : 0;
    },

    phoneNumber() {
      return this.phone.replace(/\D+/g, '')
    },
    years() {
      return Array.from({ length: 60 }, (value, index) => 1970 + index)
    },
    months() {
      if (localStorage.getItem('lang') === 'en') {
        return [
          { val: 0, text: 'January' },
          { val: 1, text: 'February' },
          { val: 2, text: 'March' },
          { val: 3, text: 'April' },
          { val: 4, text: 'May' },
          { val: 5, text: 'June' },
          { val: 6, text: 'July' },
          { val: 7, text: 'August' },
          { val: 8, text: 'September' },
          { val: 9, text: 'October' },
          { val: 10, text: 'November' },
          { val: 11, text: 'December' }
        ]
      }

      return [
        { val: 0, text: 'Января' },
        { val: 1, text: 'Февраля' },
        { val: 2, text: 'Марта' },
        { val: 3, text: 'Апреля' },
        { val: 4, text: 'Мая' },
        { val: 5, text: 'Июня' },
        { val: 6, text: 'Июля' },
        { val: 7, text: 'Августа' },
        { val: 8, text: 'Сентября' },
        { val: 9, text: 'Октября' },
        { val: 10, text: 'Ноября' },
        { val: 11, text: 'Декабря' }
      ]
    },
    /*namesError() {
      const errors = [];
      if( !$v.name.required) errors.push('обязательно для заполнения');
      return errors;
    },*/
    days() {
      switch (this.month.val) {
        case 0:
          return 31
        case 1:
          return this.years % 4 == 0 ? 29 : 28
        case 2:
          return 31
        case 3:
          return 30
        case 4:
          return 31
        case 5:
          return 30
        case 6:
          return 31
        case 7:
          return 31
        case 8:
          return 30
        case 9:
          return 31
        case 10:
          return 30
        case 11:
          return 31
        default:
          return 31
      }
    }
  },
  mounted() {
    this.getInfo ? this.setInfo() : (this.month = this.months[0])

    this.apiCountries()
    this.apiCities({ countryId: 1 })
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
    }
  },
  methods: {
    ...mapActions('global/storage', ['apiStorage']),
    ...mapActions('profile/info', ['apiChangeInfo']),
    ...mapActions('profile/country_city', ['apiCountries', 'apiCities']),

    submitHandler() {
      if( this.$v.$invalid) {
        console.log('не верно заведены данные');
        this.$v.$touch()
        return
      }
      if (this.src !== this.getInfo.photo && this.src !== '') {
        this.apiStorage({ file: this.photo }).then(() => {
          this.apiChangeInfo({
            photo: this.getStorage && this.getStorage.url,
            first_name: this.name,
            last_name: this.lastName,
            birth_date: new Date(Date.UTC(this.year, this.month.val, this.day, 0, 0, 0)),
            phone: this.phoneNumber === '' ? null : this.phoneNumber,
            about: this.about,
            country: this.country === '' ? null : this.country,
            city: this.city === '' ? null : this.city
          })
        })
      } else {
        this.apiChangeInfo({
          first_name: this.name,
          last_name: this.lastName,
          birth_date: new Date(Date.UTC(this.year, this.month.val, this.day, 0, 0, 0)),
          phone: this.phoneNumber === '' ? null : this.phoneNumber,
          photo: this.getInfo.photo,
          about: this.about,
          country: this.country === '' ? null : this.country,
          city: this.city === '' ? null : this.city
        })
      }
    },
    processFile(event) {
      this.photo = event.target.files[0]
      const reader = new window.FileReader()
      reader.onload = e => (this.src = e.target.result)
      reader.readAsDataURL(this.photo)
    },
    loadPhoto() {
      this.$refs.photo.click()
    },
    deletePhoto() {
      this.photo = null
      this.src = ''
    },
    setBritishData() {
      const data = new Date(this.getInfo.birth_date)
      const m = data.getMonth()
      const result = {
        year: data.getFullYear(),
        month: this.months[m],
        day: data.getDate()
      }

      this.day = result.day
      this.month = result.month
      this.year = result.year
    },
    setInfo() {
      this.name = this.getInfo.first_name
      this.lastName = this.getInfo.last_name
      this.src = this.getInfo.photo
      this.phone = this.getInfo.phone !== null ? this.getInfo.phone.replace(/^[+]?[78]/, '') : ''
      this.setBritishData()
      this.about = this.getInfo.about
      this.country = this.getInfo.country !== null ? this.getInfo.country : ''
      this.city = this.getInfo.city !== null ? this.getInfo.city : ''
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

.settings-main {
    background: #fff;
    box-shadow: standart-boxshadow;
    padding: 40px 10px 50px;

    .user-info-form__label {
        white-space: pre-wrap;
    }

    @media (max-width: breakpoint-xl) {
        padding: 40px 20px;
    }
}

.settings-main__back {
    margin-left: 20px;
}

.countries {
    position: relative;

    &__list {
        position: absolute;
        top: 50px;
        width: 100%;
        max-height: 150px;
        z-index: 10;
        overflow-y: scroll;
        background-color: #FFFFFF;
        border: 1px solid #c4c4c4;
    }

    &__item {
        padding: 5px 20px;

        &:hover, &:focus {
            background-color: rgba(0, 0, 0, 0.3);
            cursor: pointer;
        }
    }
}

.invalid{
  border: 1px solid red !important;
}

.form__error{
 color:red;
 right:0;
 left:unset;
 bottom:15px;
}

.user-error-form__block{
    display: flex;
    max-width: 680px;
    margin: 0 auto;
    position: relative;
    margin-top:30px;
}
</style>
