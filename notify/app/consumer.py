from loguru import logger
import json
from confluent_kafka import Consumer

def consume(server: str, topic_pattern: str, group_id: str):
    consumer_config = {
        'bootstrap.servers': server,
        'group.id': group_id,
        'auto.offset.reset': 'earliest',  # Start from the earliest message if no offsets are committed
        'enable.auto.commit': True  # Automatically commit offsets
    }

    consumer = Consumer(consumer_config)
    consumer.subscribe([topic_pattern])
    logger.info("Consuming messages from topic with pattern '%s'" % topic_pattern)
    try:
        while True:
            msg = consumer.poll(1.0)
            if msg is None:
                continue
            elif msg.error():
                logger.error("Consumer error: %s" % msg.error())
            else:
                logger.info("Message received: %s" % msg.value())

    except Exception as e:
        logger.error("Error in consume loop with message {message}".format(message=e))
    finally:
        consumer.close()

def process_message(msg):
    switcher_email_template = {
        "ACTIVATE_NEW_STAFF": "activate_new_staff.html",
    }



    try:
        json_msg = json.loads(msg.value().decode('utf-8'))
        action = json_msg['action']
        email_template = switcher_email_template[action]
        data = json_msg['data']


    except Exception as e:
        logger.error("Error when parsing message {message}".format(message=e))
