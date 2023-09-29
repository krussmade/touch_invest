from flask import Flask, request, jsonify
import json
import telebot

bot = telebot.TeleBot('6438763295:AAHL5HxU_6q9dTaO9xx5fZM3PidPFX0i0IU')

app = Flask(__name__)

@bot.message_handler(commands=['start'])
def start(message):
    bot.send_message(message.chat.id,
    "Привет! Здесь вам будут приходить сообщения"
    " о резких изменениях в вашем пакете акций")

@app.route('/', methods=['POST'])
def post_list():
    try:
        # Десериализация JSON в данные
        data = json.loads(request.data)

        # Проверка, что данные - список строк
        if isinstance(data, list) and all(isinstance(item, str) for item in data):
            sec_id = data.pop(0)
            send_hello_to_users(data, sec_id)
        else:
            return 'Invalid data. Required: List of Strings', 400
    except json.JSONDecodeError:
        return 'Invalid JSON', 400


def send_hello_to_users(user_ids, sec_id):
    for user_id in user_ids:
        bot.send_message(chat_id=user_id, text=f'Резкое изменение цены {sec_id}')

if __name__ == '__main__':
    app.run(port=5000)



