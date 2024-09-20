import os
import pika
import time
import random

# RabbitMQ connection settings
RABBITMQ_HOST = os.getenv('RABBITMQ_HOST', 'localhost')
RABBITMQ_PORT = int(os.getenv('RABBITMQ_PORT', '5672'))
RABBITMQ_USERNAME = os.getenv('RABBITMQ_USERNAME', 'guest')
RABBITMQ_PASSWORD = os.getenv('RABBITMQ_PASSWORD', 'guest')
QUEUE_BW = 'images-bw'
QUEUE_COLORED = 'images-colored'

# Directories to save the images
BW_SAVE_DIRECTORY = os.path.join(os.getenv('IMAGE_SAVE_DIRECTORY', '/workspace/img'), 'bw')
COLORED_SAVE_DIRECTORY = os.path.join(os.getenv('IMAGE_SAVE_DIRECTORY', '/workspace/img'), 'colored')

# Ensure directories exist
os.makedirs(BW_SAVE_DIRECTORY, exist_ok=True)
os.makedirs(COLORED_SAVE_DIRECTORY, exist_ok=True)

# Set up RabbitMQ connection
def connect_rabbitmq():
    credentials = pika.PlainCredentials(RABBITMQ_USERNAME, RABBITMQ_PASSWORD)
    connection = pika.BlockingConnection(
        pika.ConnectionParameters(host=RABBITMQ_HOST, port=RABBITMQ_PORT, credentials=credentials)
    )
    channel = connection.channel()
    return connection, channel

# Callback function for consuming messages
def callback(ch, method, properties, body, save_directory):
    try:
        # Save the binary image data directly to a file
        image_path = os.path.join(save_directory, f"received_image_{random.randint(100, 999)}_{int(time.time())}.jpg")
        with open(image_path, 'wb') as image_file:
            image_file.write(body)  # Write binary data
        print(f"Image saved to {image_path} from queue '{method.routing_key}'")
    except Exception as e:
        print(f"Error saving image from queue '{method.routing_key}': {e}")
    ch.basic_ack(delivery_tag=method.delivery_tag)  # Acknowledge message

# Main function to consume from both queues
def main():
    print(f"Connecting to RabbitMQ at {RABBITMQ_HOST}:{RABBITMQ_PORT}")
    try:
        connection, channel = connect_rabbitmq()

        # Declare the queues
        channel.queue_declare(queue=QUEUE_BW, durable=True)
        channel.queue_declare(queue=QUEUE_COLORED, durable=True)

        # Consume from both queues
        channel.basic_consume(
            queue=QUEUE_BW,
            on_message_callback=lambda ch, method, properties, body: callback(ch, method, properties, body, BW_SAVE_DIRECTORY),
            auto_ack=False  # Manual acknowledgment
        )

        channel.basic_consume(
            queue=QUEUE_COLORED,
            on_message_callback=lambda ch, method, properties, body: callback(ch, method, properties, body, COLORED_SAVE_DIRECTORY),
            auto_ack=False  # Manual acknowledgment
        )

        print(' [*] Waiting for messages. To exit press CTRL+C')
        channel.start_consuming()
    except Exception as e:
        print(f"Error: {e}. Retrying in 10 seconds...")
        time.sleep(10)
        main()

if __name__ == "__main__":
    main()
