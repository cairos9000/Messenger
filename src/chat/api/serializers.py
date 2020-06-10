from rest_framework import serializers

from chat.models import Chat, Contact
from chat.views import get_user_contact


class ContactSerializer(serializers.StringRelatedField):
    def to_internal_value(self, value):
        return value


class ChatSerializer(serializers.ModelSerializer):
    participants = ContactSerializer(many=True)

    class Meta:
        model = Chat
        fields = ('id', 'messages', 'participants', 'chatName')
        read_only = ('id')

    def create(self, validated_data):
        print(validated_data)
        participants = validated_data.pop('participants')
        name = validated_data.pop('chatName')
        print(name)
        chat = Chat()
        chat.chatName = name
        chat.save()
        for username in participants:
            contact = get_user_contact(username)
            chat.participants.add(contact)
        chat.save()
        return chat


class ContactSerializer2(serializers.ModelSerializer):

    participants = ContactSerializer(many=True)

    class Meta:
        model = Chat
        fields = ('id', 'messages', 'participants', 'chatName')
        read_only = ('id')

    def create(self, validated_data):
        print(validated_data)
        participants = validated_data.pop('participants')
        name = validated_data.pop('chatName')
        print(name)
        chat = Chat()
        chat.chatName = name
        chat.save()
        for username in participants:
            contact = get_user_contact(username)
            chat.participants.add(username)
        chat.save()
        return chat