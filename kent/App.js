import { useEffect, useRef, useState } from 'react';
import { FlatList, Modal, Pressable, ScrollView, StyleSheet, Text, TextInput, View } from 'react-native';
import { StatusBar } from 'expo-status-bar';

// Replace with your own server URL if needed
const CHAT_SERVER_URL = 'ws://localhost:8181';
const PEOPLE_URL = 'http://localhost:8181/people';

async function getUsername() {
  const response = await fetch(PEOPLE_URL);
  const people = await response.json();
  return people.map((person) => person.name);
}

export default function App() {
  const [username, setUsername] = useState(null);
  const [people, setPeople] = useState([]);
  const [peopleError, setPeopleError] = useState(false);
  const [nameDraft, setNameDraft] = useState('');
  const [usernames, setUsernames] = useState([]);
  const [recipient, setRecipient] = useState('all');
  const [messages, setMessages] = useState([]);
  const [draft, setDraft] = useState('');
  const socketRef = useRef(null);

  useEffect(() => {
    getUsername()
      .then((names) => setPeople(names))
      .catch(() => setPeopleError(true));
  }, []);

  useEffect(() => {
    if (!username) {
      return;
    }

    const socket = new WebSocket(
      `${CHAT_SERVER_URL}/start_web_socket?username=${encodeURIComponent(username)}`,
    );
    socketRef.current = socket;

    socket.onmessage = (event) => {
      const data = JSON.parse(event.data);

      switch (data.event) {
        case 'update-users':
          setUsernames(data.usernames);
          break;

        case 'send-message':
          setMessages((previous) => [
            ...previous,
            { username: data.username, message: data.message, recipient: data.recipient },
          ]);
          break;
      }
    };

    return () => {
      socket.close();
      socketRef.current = null;
    };
  }, [username]);

  const activeUsers = usernames.filter((name) => name !== username);

  useEffect(() => {
    if (recipient !== 'all' && !usernames.includes(recipient)) {
      setRecipient('all');
    }
  }, [usernames, recipient]);

  const confirmName = () => {
    const name = nameDraft.trim();
    if (!name) {
      return;
    }

    setUsername(name);
  };

  const sendMessage = () => {
    const message = draft.trim();
    if (!message) {
      return;
    }

    setDraft('');
    socketRef.current?.send(JSON.stringify({ event: 'send-message', recipient, message }));
  };

  return (
    <View style={styles.container}>
      <View style={styles.header}>
        <Text style={styles.title}>Deno Chat App</Text>
      </View>

      <Modal visible={!username} transparent animationType="fade" onRequestClose={() => {}}>
        <View style={styles.modalOverlay}>
          <View style={styles.modalCard}>
            <Text style={styles.modalTitle}>Please select your name</Text>
            <FlatList
              style={styles.nameList}
              data={people}
              keyExtractor={(name) => name}
              renderItem={({ item }) => (
                <Pressable
                  onPress={() => setNameDraft(item)}
                  style={[styles.nameOption, nameDraft === item && styles.nameOptionSelected]}
                >
                  <Text style={styles.nameOptionText}>{item}</Text>
                </Pressable>
              )}
              ListEmptyComponent={
                <Text style={styles.nameEmpty}>
                  {peopleError ? `Could not load users from ${PEOPLE_URL}` : 'No users available'}
                </Text>
              }
            />
            <Pressable
              style={[styles.sendButton, !nameDraft.trim() && styles.sendButtonDisabled]}
              onPress={confirmName}
            >
              <Text style={styles.sendButtonText}>Start chatting</Text>
            </Pressable>
          </View>
        </View>
      </Modal>

      <View style={styles.usersSection}>
        <Text style={styles.sectionTitle}>Users online</Text>
        <View style={styles.usersRow}>
          {activeUsers.map((name) => (
            <Text key={name} style={styles.userChip}>
              {name}
            </Text>
          ))}
        </View>
      </View>

      <ScrollView style={styles.conversation} contentContainerStyle={styles.conversationContent}>
        {messages.map((item, index) => (
          <View key={index} style={styles.message}>
            <Text style={styles.sender}>
              {item.recipient !== 'all' && item.recipient !== username
                ? `${item.username} → ${item.recipient}`
                : item.username}
            </Text>
            <Text style={styles.messageText}>{item.message}</Text>
          </View>
        ))}
      </ScrollView>

      <View style={styles.form}>
        <Text style={styles.sectionTitle}>Send to</Text>
        <View style={styles.recipientsRow}>
          <Pressable
            onPress={() => setRecipient('all')}
            style={[styles.recipient, recipient === 'all' && styles.recipientSelected]}
          >
            <Text style={styles.recipientText}>Everyone</Text>
          </Pressable>
          {activeUsers.map((name) => (
            <Pressable
              key={name}
              onPress={() => setRecipient(name)}
              style={[styles.recipient, recipient === name && styles.recipientSelected]}
            >
              <Text style={styles.recipientText}>{name}</Text>
            </Pressable>
          ))}
        </View>

        <TextInput
          style={styles.input}
          value={draft}
          onChangeText={setDraft}
          placeholder="send message"
          autoComplete="off"
          onSubmitEditing={sendMessage}
          returnKeyType="send"
        />
        <Pressable style={styles.sendButton} onPress={sendMessage}>
          <Text style={styles.sendButtonText}>Send ᯓ✉︎</Text>
        </Pressable>
      </View>

      <StatusBar style="light" />
    </View>
  );
}

const styles = StyleSheet.create({
  container: {
    flex: 1,
    backgroundColor: '#f0f0f0',
  },
  header: {
    backgroundColor: '#0b0d11',
    padding: 16,
    alignItems: 'center',
  },
  title: {
    color: '#ffffff',
    fontSize: 20,
    fontWeight: 'bold',
  },
  modalOverlay: {
    flex: 1,
    justifyContent: 'center',
    padding: 24,
    backgroundColor: 'rgba(11, 13, 17, 0.6)',
  },
  modalCard: {
    backgroundColor: '#ffffff',
    borderRadius: 12,
    padding: 20,
  },
  modalTitle: {
    fontSize: 18,
    fontWeight: 'bold',
    marginBottom: 12,
  },
  nameList: {
    maxHeight: 200,
    marginBottom: 12,
  },
  nameOption: {
    borderWidth: 1,
    borderColor: '#0b0d11',
    borderRadius: 8,
    paddingVertical: 10,
    paddingHorizontal: 12,
    marginBottom: 8,
  },
  nameOptionSelected: {
    backgroundColor: '#75fead',
    borderColor: '#75fead',
  },
  nameOptionText: {
    color: '#0b0d11',
  },
  nameEmpty: {
    color: '#666666',
    paddingVertical: 12,
  },
  usersSection: {
    backgroundColor: '#ffffff',
    padding: 16,
  },
  sectionTitle: {
    fontSize: 16,
    fontWeight: 'bold',
    marginTop: 0,
    marginBottom: 8,
  },
  usersRow: {
    flexDirection: 'row',
    flexWrap: 'wrap',
    gap: 8,
  },
  userChip: {
    backgroundColor: '#aff5b4',
    color: '#0b0d11',
    borderRadius: 8,
    paddingVertical: 6,
    paddingHorizontal: 12,
    overflow: 'hidden',
  },
  conversation: {
    flex: 1,
  },
  conversationContent: {
    padding: 16,
  },
  message: {
    backgroundColor: '#ffffff',
    borderRadius: 8,
    paddingVertical: 8,
    paddingHorizontal: 16,
    marginBottom: 8,
  },
  sender: {
    fontWeight: 'bold',
    marginBottom: 4,
  },
  messageText: {
    margin: 0,
  },
  form: {
    backgroundColor: '#ffffff',
    padding: 16,
  },
  recipientsRow: {
    flexDirection: 'row',
    flexWrap: 'wrap',
    gap: 8,
    marginBottom: 12,
  },
  recipient: {
    borderWidth: 1,
    borderColor: '#0b0d11',
    borderRadius: 8,
    paddingVertical: 6,
    paddingHorizontal: 12,
  },
  recipientSelected: {
    backgroundColor: '#75fead',
    borderColor: '#75fead',
  },
  recipientText: {
    color: '#0b0d11',
  },
  input: {
    borderWidth: 1,
    borderColor: '#0b0d11',
    borderRadius: 8,
    paddingVertical: 10,
    paddingHorizontal: 16,
  },
  sendButton: {
    marginTop: 12,
    backgroundColor: '#75fead',
    borderRadius: 8,
    paddingVertical: 12,
    alignItems: 'center',
  },
  sendButtonDisabled: {
    opacity: 0.5,
  },
  sendButtonText: {
    color: '#0b0d11',
    fontWeight: 'bold',
  },
});
