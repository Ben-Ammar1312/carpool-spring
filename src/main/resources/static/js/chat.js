// chat.js
document.addEventListener('DOMContentLoaded', function() {
    let stompClient = null;

    function connect() {
        const socket = new SockJS('/ws');
        stompClient = Stomp.over(socket);

        stompClient.connect({}, function (frame) {
            console.log('Connected: ' + frame);

            // Subscribe to the chat topic for the specific chat
            stompClient.subscribe('/topic/chat/' + chatId, function (chatMessage) {
                const msg = JSON.parse(chatMessage.body);
                displayChatMessage(msg);
            });
        }, function (error) {
            console.error('STOMP error:', error);
            alert("Failed to connect to the chat server. Please try again later.");
        });
    }

    function sendMessage() {
        const messageInput = document.getElementById('message-input');
        const content = messageInput.value.trim();

        if (content && stompClient) {
            // Validate receiverId
            if (typeof receiverId !== 'number' && typeof receiverId !== 'string') {
                alert("Invalid receiver ID.");
                return;
            }

            const chatMessage = {
                senderId: userId,       // Sender is the current user
                receiverId: receiverId, // Receiver is determined by the controller
                content: content,
                rideId: rideId
            };
            stompClient.send("/app/chat/" + chatId, {}, JSON.stringify(chatMessage));
            messageInput.value = '';
        } else {
            alert("Cannot send an empty message.");
        }
    }

    function displayChatMessage(msg) {
        const messagesDiv = document.getElementById('messages');
        if (!messagesDiv) {
            console.error("Element with id 'messages' not found.");
            return;
        }

        const messageElement = document.createElement('div');

        // Determine if the message is sent or received
        const isSent = (msg.senderId === userId);
        const sender = isSent ? 'You' : receiverName; // Customize as needed

        // Format timestamp with seconds for accurate display
        const time = new Date(msg.timestamp).toLocaleTimeString([], { hour: '2-digit', minute: '2-digit', second: '2-digit' });

        // Apply CSS classes based on message type
        messageElement.classList.add(isSent ? 'sent' : 'received');

        // Set the inner HTML with sender, content, and timestamp
        messageElement.innerHTML = `<strong>${sender}</strong>: ${msg.content} <span class="text-muted" style="font-size: 0.8em;">${time}</span>`;

        messagesDiv.appendChild(messageElement); // Appends to the end, maintaining order
        messagesDiv.scrollTop = messagesDiv.scrollHeight; // Auto-scroll to the latest message
    }

    function displayNotification(notif) {
        // Implement notification display logic (e.g., show a toast)
        console.log('New Notification:', notif.message);
    }

    // Fetch chat history first
    fetch('/api/chat/history?chatId=' + chatId)
        .then(response => response.json())
        .then(data => {
            // Assuming data is ordered ascending (oldest first)
            data.forEach(message => displayChatMessage(message));
            // Now connect to WebSocket
            connect();
        })
        .catch(error => {
            console.error('Error fetching chat history:', error);
            alert("Failed to load chat history.");
        });

    // Allow sending message on Enter key
    const messageInput = document.getElementById('message-input');
    if (messageInput) {
        messageInput.addEventListener('keypress', function (e) {
            if (e.key === 'Enter') {
                e.preventDefault(); // Prevent form submission on Enter
                sendMessage();
            }
        });
    } else {
        console.error("Element with id 'message-input' not found.");
    }

    // Handle form submission
    const chatForm = document.getElementById('chat-form');
    if (chatForm) {
        chatForm.addEventListener('submit', function (e) {
            e.preventDefault(); // Prevent default form submission
            sendMessage();
        });
    } else {
        console.error("Element with id 'chat-form' not found.");
    }
});