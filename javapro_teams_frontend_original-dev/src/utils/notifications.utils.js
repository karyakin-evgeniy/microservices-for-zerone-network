export function getRouteByNotification(notification) {
    switch (notification.event_type) {
        case 'MESSAGE':
            return { name: 'Im', params: { id: notification.entity_id } }
        default:
            return { name: 'ProfileId', params: { id: notification.entity_id } }
    }
}
