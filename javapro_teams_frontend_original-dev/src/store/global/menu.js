export default {
  namespaced: true,
  state: {
    en: {
      user: [{
        link: {
          name: 'Profile'
        },
        exact: true,
        icon: 'profile',
        text: 'My page'
      },
      {
        link: {
          name: 'Friends'
        },
        icon: 'friends',
        text: 'Friends'
      },
      {
        link: {
          name: 'Im'
        },
        icon: 'im',
        text: 'Messages'
      },
      {
        link: {
          name: 'News'
        },
        exact: true,
        icon: 'news',
        text: 'News'
      }],
      admin: [{
        link: {
          name: 'AdminStatistics'
        },
        icon: 'statistics',
        text: 'Statistics'
      },
      {
        link: {
          name: 'AdminUsers'
        },
        icon: 'users',
        text: 'Users'
      },
      {
        link: {
          name: 'AdminPosts'
        },
        icon: 'posts',
        text: 'Posts'
      },
      {
        link: {
          name: 'AdminComments'
        },
        icon: 'comments',
        text: 'Comments'
      },
      {
        link: {
          name: 'AdminModerators'
        },
        icon: 'moderators',
        text: 'Administrators and Moderators'
      }]
    },
    ru: {
      user: [{
        link: {
          name: 'Profile'
        },
        exact: true,
        icon: 'profile',
        text: 'Моя страница'
      },
      {
        link: {
          name: 'Friends'
        },
        icon: 'friends',
        text: 'Друзья'
      },
      {
        link: {
          name: 'Im'
        },
        icon: 'im',
        text: 'Сообщения'
      },
      {
        link: {
          name: 'News'
        },
        exact: true,
        icon: 'news',
        text: 'Новости'
      }],
      admin: [{
        link: {
          name: 'AdminStatistics'
        },
        icon: 'statistics',
        text: 'Статистика'
      },
      {
        link: {
          name: 'AdminUsers'
        },
        icon: 'users',
        text: 'Люди'
      },
      {
        link: {
          name: 'AdminPosts'
        },
        icon: 'posts',
        text: 'Публикации'
      },
      {
        link: {
          name: 'AdminComments'
        },
        icon: 'comments',
        text: 'Комментарии'
      },
      {
        link: {
          name: 'AdminModerators'
        },
        icon: 'moderators',
        text: 'Администраторы и модераторы'
      }]
    }
  },
  getters: {
    getSidebarById: state => (id, lang) => state[lang][id],
  }
}
