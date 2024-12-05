import pytest
from unittest.mock import patch, MagicMock
from app.consumer import process_message, load_email_template
import json


@pytest.fixture
def sample_message():
    return {
        "action": "ACTIVATE_NEW_STAFF",
        "data": {
            "username": "hello",
            "email": "help@gmail.com",
            "activation_link": "xxx.com",
            "expired_time": "111",
        },
    }


# @patch("app.consumer.send_email")  # Mock the send_email function
# @patch(
#     "app.consumer.EMAIL_TEMPLATE", {"ACTIVATE_NEW_STAFF": "activation_template.txt"}
# )  # Mock EMAIL_TEMPLATE
# @patch(
#     "app.consumer.EMAIL_SUBJECT", {"ACTIVATE_NEW_STAFF": "Activate Your Account"}
# )  # Mock EMAIL_SUBJECT
# @patch(
#     "app.consumer.load_email_template",
#     return_value="Hello {username}, click {activation_link}",
# )  # Mock load_email_template
# def test_process_message_success(
#     mock_load_template, mock_email_subject, mock_email_template, mock_send_email
# ):
#     # Arrange: Sample Kafka message payload
#     sample_message = {
#         "action": "ACTIVATE_NEW_STAFF",
#         "data": {
#             "username": "hello",
#             "email": "help@gmail.com",
#             "activation_link": "xxx.com",
#             "expired_time": "111",
#         },
#     }

#     kafka_msg = MagicMock()  # Mock the Kafka message object
#     kafka_msg.value.return_value = json.dumps(sample_message).encode("utf-8")

#     # Act: Call the function with the mocked message
#     process_message(kafka_msg)

#     # Assert: Ensure load_email_template was called correctly
#     mock_load_template.assert_called_once_with("activation_template.txt")

#     # Assert: Check send_email was called with the correct arguments
#     mock_send_email.assert_called_once_with(
#         subject="Activate Your Account",
#         body="Hello hello, click xxx.com",
#         recipients="help@gmail.com",
#     )


def test_process_message_invalid_json():
    kafka_msg = MagicMock()
    kafka_msg.value.return_value = b"not a valid json"

    with patch("app.consumer.logger.error") as mock_logger_error:
        process_message(kafka_msg)
        mock_logger_error.assert_called_once_with(
            "Error when parsing message {message}".format(
                message="Expecting value: line 1 column 1 (char 0)"
            )
        )


def test_process_message_missing_action_key(sample_message):
    sample_message.pop("action")
    kafka_msg = MagicMock()
    kafka_msg.value.return_value = json.dumps(sample_message).encode("utf-8")

    with patch("app.consumer.logger.error") as mock_logger_error:
        process_message(kafka_msg)
        mock_logger_error.assert_called_once()


def test_process_message_missing_email_key(sample_message):
    sample_message["data"].pop("email")
    kafka_msg = MagicMock()
    kafka_msg.value.return_value = json.dumps(sample_message).encode("utf-8")

    with patch("app.consumer.logger.error") as mock_logger_error:
        process_message(kafka_msg)
        mock_logger_error.assert_called_once()


@patch(
    "app.consumer.load_email_template",
    side_effect=FileNotFoundError("Template not found"),
)
def test_process_message_template_not_found(mock_load_template, sample_message):
    kafka_msg = MagicMock()
    kafka_msg.value.return_value = json.dumps(sample_message).encode("utf-8")

    with patch("app.consumer.logger.error") as mock_logger_error:
        process_message(kafka_msg)
        mock_logger_error.assert_called_once_with(
            "Error when parsing message {message}".format(message="Template not found")
        )
