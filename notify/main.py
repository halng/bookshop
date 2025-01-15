"""
*****************************************************************************************
Copyright 2024 By ANYSHOP Project 
Licensed under the Apache License, Version 2.0;
*****************************************************************************************
"""

from app.consumer import consume
from app.config import KAFKA_CONFIG
from loguru import logger

if __name__ == "__main__":
    logger.info("Starting Notify Service")
    consume(
        server=KAFKA_CONFIG["bootstrap_server"],
        group_id=KAFKA_CONFIG["group_id"],
        topic_pattern=KAFKA_CONFIG["topic_pattern"],
    )
