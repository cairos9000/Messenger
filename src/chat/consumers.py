from django.contrib.auth import get_user_model
from asgiref.sync import async_to_sync
from channels.generic.websocket import WebsocketConsumer
import json

from .models import Message, Chat, Contact
from .views import get_last_messages, get_user_contact, get_current_chat


User = get_user_model()


class ChatConsumer(WebsocketConsumer):

    def messages_to_json(self, messages):
        result = list()
        for message in messages:
            result.append(self.message_to_json(message))
        return result

    def message_to_json(self, message):
        print(message.contact.user.username)
        return {
            'id': message.id,
            'author': message.contact.user.username,
            'content': message.content,
            'timestamp': str(message.timestamp)
        }

    # добавляем методы, которые позволят подгрузить сообщения, то есть
    # после подключения отображаем ранние сообщения

    def fetch_messages(self, data):
        messages = get_last_messages(data['chatId'])
        content = {
            'command': 'messages',
            'messages': self.messages_to_json(messages)
        }
        self.send_message(content)

    # создание нового сообщения
    def new_message(self, data):
        user_contact = get_user_contact(data['from'])
        author_user = User.objects.filter(username=user_contact)[0]
        message = Message.objects.create(
            contact=user_contact, 
            content=data['message'])
        current_chat = get_current_chat(data['chatId'])
        current_chat.messages.add(message)
        current_chat.save()
        content = {
            'command': 'new_message',
            'message': self.message_to_json(message)
        }
        return self.send_chat_message(content)

    commands = {
        'fetch_messages': fetch_messages,
        'new_message': new_message
    }


    def connect(self):
        self.room_name = self.scope['url_route']['kwargs']['room_name']
        self.room_group_name = 'chat_%s' % self.room_name

        async_to_sync(self.channel_layer.group_add)(
            self.room_group_name,
            self.channel_name
        )
        self.accept()


    def disconnect(self, close_code):
        async_to_sync(self.channel_layer.group_discard)(
            self.room_group_name,
            self.channel_name
        )

    # получение сообщений
    def receive(self, text_data):
        data = json.loads(text_data)
        self.commands[data['command']](self, data) # указываем какую команду выызвать при получении сообщения
    
    def send_chat_message(self, message):
        async_to_sync(self.channel_layer.group_send)(
            self.room_group_name,
            {
                'type': 'chat_message',
                'message': message
            }
        )

    # отправка сообщений
    def send_message(self, message):
        self.send(text_data=json.dumps(message))



    def chat_message(self, event):
        message = event['message']
        self.send(text_data=json.dumps(message))