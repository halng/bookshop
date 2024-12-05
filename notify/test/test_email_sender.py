# import pytest
# import unittest
# from unittest.mock import patch, MagicMock
# from app.email_sender import send_email
# from app.config import EMAIL_CONFIG
# from smtplib import SMTPException


# @patch("app.email_sender.smtplib.SMTP_SSL")
# @patch("app.email_sender.MIMEMultipart")
# @patch("app.email_sender.MIMEText")
# def test_send_email_success(self, mock_smtp_ssl, mock_mime_multipart, mock_mime_text):
#     # Arrange
#     mock_server = MagicMock()
#     mock_smtp_ssl.return_value.__enter__.return_value = mock_server

#     mock_message = MagicMock()
#     mock_mime_multipart.return_value = mock_message

#     subject = "Test Subject"
#     body = "<p>Test Body</p>"
#     recipients = "recipient@example.com"

#     # Act
#     send_email(subject, body, recipients)

#     # Assert
#     mock_mime_multipart.assert_called_once_with("alternative")
#     mock_mime_text.assert_called_once_with(body, "html")
#     mock_message.attach.assert_called_once_with(mock_mime_text.return_value)

#     mock_smtp_ssl.assert_called_once_with(
#         host=EMAIL_CONFIG["smtp_server"],
#         port=EMAIL_CONFIG["smtp_port"],
#         context=unittest.mock.ANY,  # SSL context
#     )
#     mock_server.login.assert_called_once_with(
#         EMAIL_CONFIG["sender_email"], EMAIL_CONFIG["password"]
#     )
#     mock_server.sendmail.assert_called_once_with(
#         EMAIL_CONFIG["sender_email"], recipients, mock_message.as_string()
#     )


# @patch("app.email_sender.smtplib.SMTP_SSL")
# def test_send_email_smtp_error(self, mock_smtp_ssl):
#     # Arrange
#     mock_smtp_ssl.side_effect = SMTPException("SMTP error")

#     subject = "Test Subject"
#     body = "<p>Test Body</p>"
#     recipients = "recipient@example.com"

#     # Act
#     with self.assertLogs("app.email_sender", level="ERROR") as log:
#         send_email(subject, body, recipients)

#     # Assert
#     self.assertIn("SMTP error occurred", log.output[0])


# @patch("app.email_sender.smtplib.SMTP_SSL")
# def test_send_email_unexpected_error(self, mock_smtp_ssl):
#     # Arrange
#     mock_smtp_ssl.side_effect = Exception("Unexpected error")

#     subject = "Test Subject"
#     body = "<p>Test Body</p>"
#     recipients = "recipient@example.com"

#     # Act
#     with self.assertLogs("app.email_sender", level="ERROR") as log:
#         send_email(subject, body, recipients)

#     # Assert
#     self.assertIn("Unexpected error while sending email", log.output[0])
