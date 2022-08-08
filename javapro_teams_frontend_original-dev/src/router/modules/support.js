export default [
  {
    path: '/support',
    name: 'Support',
    meta: {
      layout: 'form',
      notRequiresAuth: true,
      title: 'Поддержка'
    },
    component: () => import('@/pages/Support/PageSupport.vue')
  }
]
