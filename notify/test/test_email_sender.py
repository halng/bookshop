# import smtplib
# import pytest
# from unittest.mock import patch, MagicMock
# from app.email_sender import send_email

# @pytest.fixture
# def email_config():
#     return {
#         "sender_email": "test@example.com",
#         "password": "password",
#         "smtp_server": "smtp.example.com",
#         "smtp_port": 465,
#     }

# @patch("app.email_sender.EMAIL_CONFIG")
# @patch("app.email_sender.smtplib.SMTP_SSL")
# def test_send_email_success(mock_smtp_ssl, email_config):
#     mock_server = MagicMock()
#     mock_smtp_ssl.return_value.__enter__.return_value = mock_server

#     send_email("Test Subject", "<p>Test Body</p>", "recipient@example.com")

#     mock_server.login.assert_called_once_with(email_config["sender_email"], email_config["password"])
#     mock_server.sendmail.assert_called_once()

# @patch("app.email_sender.EMAIL_CONFIG")
# @patch("app.email_sender.smtplib.SMTP_SSL")
# def test_send_email_smtp_exception(mock_smtp_ssl, email_config, caplog):
#     mock_server = MagicMock()
#     mock_smtp_ssl.return_value.__enter__.return_value = mock_server
#     mock_server.sendmail.side_effect = smtplib.SMTPException("SMTP error")

#     with caplog.at_level("ERROR"):
#         send_email("Test Subject", "<p>Test Body</p>", "recipient@example.com")

#     assert "SMTP error occurred" in caplog.text

# @patch("app.email_sender.EMAIL_CONFIG")
# @patch("app.email_sender.smtplib.SMTP_SSL")
# def test_send_email_unexpected_exception(mock_smtp_ssl, email_config, caplog):
#     mock_server = MagicMock()
#     mock_smtp_ssl.return_value.__enter__.return_value = mock_server
#     mock_server.sendmail.side_effect = Exception("Unexpected error")

#     with caplog.at_level("ERROR"):
#         send_email("Test Subject", "<p>Test Body</p>", "recipient@example.com")

#     assert "Unexpected error while sending email" in caplog.text