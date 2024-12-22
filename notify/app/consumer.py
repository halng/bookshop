"""
*****************************************************************************************
Copyright 2024 By Hal Nguyen 
Licensed under the Apache License, Version 2.0 (the "License"); 
you may not use this file except in compliance with the License.
*****************************************************************************************
"""

from loguru import logger
import json
from confluent_kafka import Consumer

from app.config import EMAIL_TEMPLATE, EMAIL_SUBJECT
from app.email_sender import send_email


def consume(server: str, topic_pattern: str, group_id: str):
    consumer_config = {
        "bootstrap.servers": server,
        "group.id": group_id,
        "auto.offset.reset": "earliest",  # Start from the earliest message if no offsets are committed
        "enable.auto.commit": True,  # Automatically commit offsets
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
                process_message(msg)

    except Exception as e:
        logger.error("Error in consume loop with message {message}".format(message=e))
    finally:
        consumer.close()


def load_email_template(template: str) -> str:
    with open(f"./email_template/{template}", "r") as f:
        return f.read()


def process_message(msg):
    """
    Function process message and send email.

    :param msg: a json data. example:
    {
          "action": "ACTIVATE_NEW_STAFF",
          "data": {
            "username": "hello",
            "email": "help@gmail.com",
            "activation_link": "xxx.com",
            "expired_time": "111"
          }
    }
    """
    try:
        json_msg = json.loads(msg.value().decode("utf-8"))
        action = json_msg["action"]

        email_template = EMAIL_TEMPLATE[action]
        email_subject = EMAIL_SUBJECT[action]

        data = json_msg["data"]
        user_email = data["email"]
        del data["email"]

        email_body = load_email_template(email_template)

        for key, value in data.items():
            email_body = email_body.replace("{" + key + "}", value)

        logger.info(
            "Process Message: Done - Starting send email with action {} for user {}",
            action,
            user_email,
        )
        send_email(subject=email_subject, body=email_body, recipients=user_email)

    except Exception as e:
        logger.error("Error when parsing message {message}".format(message=e))
