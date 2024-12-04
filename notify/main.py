from confluent_kafka import Consumer

if __name__ == '__main__':
    config = {
    'bootstrap.servers': 'localhost:9092',
    'group.id': 'notify-service',
    'auto.offset.reset': 'earliest'
    }
    
    consumer = Consumer(config)
    
    topics = "notification"

    consumer.subscribe([topics])
    try:
        while True:
            msg = consumer.poll(1.0)
            if msg is None:
                continue
            if msg.error():
                print("Error: %s" % msg.error())

            # extract the key value and print msg
            print("Consumed event from topic {topic}: key = {key:12} value = {value:12}".format(
                topic=topics, key=msg.key().decode('utf-8'), value=msg.value().decode('utf-8')))



    except KeyboardInterrupt:
        consumer.close()

    finally:
        consumer.close()
