<template lang="pug">
  .select-location(v-click-outside='hideOptions')
    input.select-location__input(
      type='text',
      :value='value',
      :placeholder='placeholder',
      @focus='showOptions',
      @input='inputHandler($event.target.value)',
      @keyup.enter='inputHandler($event.target.value)'
      ref="input"
    )

    ul.custom-select__options.options__list(
      v-if='isShowOptions'
    )
      li.options__item(
        v-for='option in optionsList',
        :key='option.id',
        @click='selectHandler(option)',
        @keyup.enter='selectHandler(option)',
        tabindex=0
      ) {{ option.title }}
      li.options__item.options__item_error(v-if='!optionsList.length') Значение не найдено
</template>

<script>
import ClickOutside from 'vue-click-outside'

export default {
  name: 'SelectLocation',

  directives: {
    ClickOutside
  },

  props: {
    value: {
      type: [String, Number],
      default: '',
    },
    placeholder: {
      type: String,
      default: '',
    },
    optionsList: {
      type: Array,
      default() {
        return [];
      },
    },
  },

  data: () => ({
    isShowOptions: false,
    inputTimerId: null,
    inputTimer: 450,
    options: [],
  }),

  methods: {
    showOptions() {
      this.isShowOptions = true;
    },
    hideOptions() {
      this.isShowOptions = false;
    },
    inputHandler(value) {
      clearTimeout(this.inputTimerId);

      this.inputTimerId = setTimeout(() => {
        this.$emit('input', value.trim());
      }, this.inputTimer);
    },
    selectHandler(option) {
      this.$emit('selectOoption', option);
      this.hideOptions();
    },
  },
};
</script>

<style lang="stylus">
.select-location {
    position: relative;

    &__input {
      width: 100%;
      height: 45px;
      border: 1px solid #E3E3E3;
      padding: 0 20px;
      font-size: 15px;
      color: #000;
      cursor: pointer;
      display: flex;
      align-items: center;
      white-space: nowrap;
      overflow: hidden;
      position: relative;

      &::placeholder {
        color: #B0B0BC;
      }
    }

    .options__list {
        position: absolute;
        top: 50px;
        width: 100%;
        max-height: 150px;
        z-index: 10;
        overflow-y: auto;
        background-color: #FFFFFF;
        border: 1px solid #c4c4c4;
        -webkit-box-shadow: 4px 5px 6px 0px rgba(34, 60, 120, 0.5);
        -moz-box-shadow: 4px 5px 6px 0px rgba(34, 60, 80, 0.5);
        box-shadow: 4px 5px 6px 0px rgba(34, 60, 80, 0.5);
    }

    .options__item {
        padding: 5px 20px;

        &:hover, &:focus {
            background-color: rgba(0, 0, 0, 0.3);
            cursor: pointer;
        }

        &_error {
          padding 20px;
        }
    }
}
</style>
