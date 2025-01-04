

    let notificationStompClient = null;

    function connectNotifications() {
    const socket = new SockJS('/ws');
    notificationStompClient = Stomp.over(socket);

    notificationStompClient.connect({}, function (frame) {
    console.log('Connected for notifications: ' + frame);

    // Subscribe to user-specific notification channel
    const destination = '/topic/notifications/' + currentUserId;
    notificationStompClient.subscribe(destination, function (message) {
    // message.body is a JSON string
    const notificationDTO = JSON.parse(message.body);
    handleIncomingNotification(notificationDTO);
});
        const unreadCountDestination = '/topic/unreadCount/' + currentUserId;
        notificationStompClient.subscribe(unreadCountDestination, function (message) {
            const newCount = JSON.parse(message.body);
            updateUnreadMessagesBadge(newCount);
        });

}, function (error) {
    console.error('Notification STOMP error:', error);
});
}

    function handleIncomingNotification(notification) {
        console.log('New Notification:', notification);

        // Bump the badge
        const badgeEl = document.getElementById('notifications-badge');
        if (badgeEl) {
            let count = parseInt(badgeEl.innerText) || 0;
            badgeEl.innerText = count + 1;
        }}

    function updateUnreadMessagesBadge(count) {
        const unreadBadge = document.getElementById('unread-messages-badge');
        if (unreadBadge) {
            unreadBadge.textContent = count;
            unreadBadge.style.display = count > 0 ? 'inline' : 'none';
        }
    }




    // Connect once the page loads

    document.addEventListener('DOMContentLoaded', function() {
        // Connect to WebSocket as you do now
        connectNotifications(); // your existing function

        // Add a click listener to the dropdown
        const dropdownTrigger = document.getElementById('notificationsDropdown');
        if (dropdownTrigger) {
            dropdownTrigger.addEventListener('click', function () {
                loadNotifications();
            });
        }
    });

    /**
     * Fetch all notifications for the current user.
     */
    function loadNotifications() {
        fetch('/api/notifications/unread/' + currentUserId)
            .then(response => response.json())
            .then(notifications => {
                populateNotificationsDropdown(notifications);
            })
            .catch(error => {
                console.error("Error fetching notifications:", error);
            });
    }

    /**
     * Populate the dropdown with notification items.
     * You can also add links, "mark as read" buttons, etc.
     */
    function populateNotificationsDropdown(notifications) {
        const menu = document.getElementById('notifications-menu');
        if (!menu) return;

        // Clear current list
        menu.innerHTML = '';

        if (notifications.length === 0) {
            // Show a placeholder if no notifications
            menu.innerHTML = '<li><a class="dropdown-item" href="#">No notifications</a></li>';
            return;
        }

        // Build <li> items
        notifications.forEach(notification => {
            const isRead = notification.read; // boolean
            const li = document.createElement('li');

            // Use a link or just a span
            // data-notification-id to identify the notification
            li.innerHTML = `
            <a class="dropdown-item ${isRead ? '' : 'fw-bold'}"
               href="#"
               data-notification-id="${notification.id}">
               ${notification.message}
            </a>
        `;
            menu.appendChild(li);
        });

        // Add click handler to each notification item to mark as read
        menu.querySelectorAll('[data-notification-id]').forEach(item => {
            item.addEventListener('click', function (e) {
                e.preventDefault();
                const notificationId = this.getAttribute('data-notification-id');
                markNotificationAsRead(notificationId);
            });
        });
    }

    /**
     * Mark a notification as read via REST endpoint, then update UI.
     */
    function markNotificationAsRead(notificationId) {
        fetch('/api/notifications/mark-as-read/' + notificationId, {
            method: 'PUT'
        })
            .then(() => {
                // Update UI for that item
                const link = document.querySelector(`[data-notification-id="${notificationId}"]`);
                if (link) {
                    link.classList.remove('fw-bold'); // remove bold if it was unread
                }
                // Optionally, decrement the badge count
                const badgeEl = document.getElementById('notifications-badge');
                if (badgeEl) {
                    let count = parseInt(badgeEl.innerText) || 0;
                    if (count > 0) {
                        badgeEl.innerText = count - 1;
                    }
                }
            })
            .catch(error => {
                console.error("Failed to mark notification as read:", error);
            });
    }