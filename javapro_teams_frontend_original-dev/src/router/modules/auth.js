export default [
  {
    path: '/login',
    name: 'Login',
    meta: {
      layout: 'form',
      notRequiresAuth: true,
      notWithoutAuth: true,
      title: 'Авторизация'
    },
    component: () => import('@/pages/Auth/Login.vue')
  },
  {
    path: '/registration',
    name: 'Registration',
    meta: {
      layout: 'form',
      notRequiresAuth: true,
      title: 'Регистрация'
    },
    component: () => import('@/pages/Auth/Registration.vue')
  },
  {
    path: '/registration-failed',
    name: 'RegisterFailed',
    meta: {
      layout: 'form',
      notRequiresAuth: true,
      title: 'Такой пользователь уже существует'
    },
    component: () => import('@/pages/Auth/RegisterFailed.vue')
  },
  {
    path: '/registration-letter-sent',
    name: 'RegisterLetter',
    meta: {
      layout: 'form',
      notRequiresAuth: true,
      title: 'Подтвердите регистрацию в письме'
    },
    component: () => import('@/pages/Auth/RegisterLetter.vue')
  },
  {
    path: '/registration/complete',
    name: 'RegisterConfirm',
    meta: {
      layout: 'form',
      notRequiresAuth: true,
      title: 'Подтверждение регистрации'
    },
    component: () => import('@/pages/Auth/RegisterConfirm.vue')
  },
  {
    path: '/registration-success',
    name: 'RegisterSuccess',
    meta: {
      layout: 'form',
      notRequiresAuth: true,
      title: 'Успешная регистрация'
    },
    component: () => import('@/pages/Auth/RegisterSuccess.vue')
  },
  {
    path: '/registration-confirmation-failed',
    name: 'RegisterConfirmationFailed',
    meta: {
      layout: 'form',
      notRequiresAuth: true,
      title: 'Не удалось подтвердить регистрацию'
    },
    component: () => import('@/pages/Auth/RegisterConfirmationFailed.vue')
  },
  {
    path: '/forgot',
    name: 'Forgot',
    meta: {
      layout: 'form',
      notRequiresAuth: true,
      title: 'Востановление'
    },
    component: () => import('@/pages/Auth/Forgot.vue')
  },
  {
    path: '/forgot-success-confirmation',
    name: 'ForgotSuccessConfirmation',
    meta: {
      layout: 'form',
      notRequiresAuth: true,
      title: 'Подтверждение востановление'
    },
    component: () => import('@/pages/Auth/ForgotSuccessConfirmation.vue')
  },
  {
    path: '/forgot-success',
    name: 'ForgotSuccess',
    meta: {
      layout: 'form',
      notRequiresAuth: true,
      title: 'Успешное востановление'
    },
    component: () => import('@/pages/Auth/ForgotSuccess.vue')
  },
  {
    path: '/change-password',
    name: 'ChangePassword',
    meta: {
      layout: 'form',
      notRequiresAuth: true,
      title: 'Изменить пароль'
    },
    component: () => import('@/pages/Auth/ChangePassword.vue')
  },
  {
    path: '/change-password-success',
    name: 'ChangePasswordSuccess',
    meta: {
      layout: 'form',
      notRequiresAuth: true,
      title: 'Успешное изменение пароля'
    },
    component: () => import('@/pages/Auth/ChangePasswordSuccess.vue')
  },
  {
    path: '/change-password-failed',
    name: 'ChangePasswordFailed',
    meta: {
      layout: 'form',
      notRequiresAuth: true,
      title: 'Не удалось изменить пароль'
    },
    component: () => import('@/pages/Auth/ChangePasswordFailed.vue')
  }
]
