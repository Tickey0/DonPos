const myUsername = prompt('Please enter your name') || 'Anonymous'
const url = new URL(`./start_web_socket?username=${myUsername}`, location.href)
url.protocol = url.protocol.replace('http', 'ws')
const socket = new WebSocket(url)
const recipientSelector = document.getElementById('recipient')

socket.onmessage = (event) => {
  const data = JSON.parse(event.data)

  switch (data.event) {
    case 'update-users':
      updateUserList(data.usernames)
      break

    case 'send-message':
      addMessage(data.username, data.message, data.recipient)
      break
  }
}

function updateUserList(usernames) {
  const userList = document.getElementById('users')
  const selectedRecipient = recipientSelector.value
  const activeUsers = usernames.filter((username) => username !== myUsername)

  userList.replaceChildren()
  recipientSelector.replaceChildren()

  const allOption = document.createElement('option')
  allOption.value = 'all'
  allOption.textContent = 'Everyone'
  recipientSelector.appendChild(allOption)

  for (const username of activeUsers) {
    const listItem = document.createElement('li')
    listItem.textContent = username
    userList.appendChild(listItem)

    const option = document.createElement('option')
    option.value = username
    option.textContent = username
    recipientSelector.appendChild(option)
  }

  recipientSelector.value = usernames.includes(selectedRecipient) ? selectedRecipient : 'all'
}

function addMessage(username, message, recipient = 'all') {
  const template = document.getElementById('message')
  const clone = template.content.cloneNode(true)
  const sender = recipient !== 'all' && recipient !== myUsername ? `${username} → ${recipient}` : username

  clone.querySelector('span').textContent = sender
  clone.querySelector('p').textContent = message
  document.getElementById('conversation').prepend(clone)
}

const inputElement = document.getElementById('data')
inputElement.focus()

const form = document.getElementById('form')

form.onsubmit = (e) => {
  e.preventDefault()
  const message = inputElement.value.trim()
  if (!message) {
    return
  }

  inputElement.value = ''
  socket.send(JSON.stringify({
    event: 'send-message',
    recipient: recipientSelector.value,
    message,
  }))
}
