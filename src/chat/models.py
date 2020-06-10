from django.contrib.auth import get_user_model
from django.db import models

User = get_user_model()

# модель для хранения сообщений их дальнейшей загрузки
class Message(models.Model):
    author = models.ForeignKey(User, related_name='author_messages', on_delete=models.CASCADE)
    content = models.TextField()
    timestamp = models.DateTimeField(auto_now_add=True)

    def __str__(self):
        return self.author.username

    #загружаем последние 50 сообщений
    def last_50_messages():
        return Message.objects.order_by('-timestamp').all()

