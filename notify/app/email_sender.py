import smtplib, ssl
from email.mime.text import MIMEText
from email.mime.multipart import MIMEMultipart
from loguru import logger

from app.config import EMAIL_CONFIG


def send_email(subject, body, recipients):
    sender_email = EMAIL_CONFIG["sender_email"]
    password = EMAIL_CONFIG["password"]
    smpt_server = EMAIL_CONFIG["smtp_server"]
    smpt_port = EMAIL_CONFIG["smtp_port"]

    message = MIMEMultipart["alternative"]
    message["Subject"] = subject
    message["From"] = send_email
    message["To"] = recipients

    mime_html = MIMEText(body, "html")

    message.attach(mime_html)

    # Create secure connection with server and send email
    try:
        with smtplib.SMTP_SSL(
            host=smpt_server, port=smpt_port, context=context
        ) as server:
            server.login(sender_email, password)
            server.sendmail(sender_email, recipients, message.as_string())
            logger.info("Email sent successfully to {}", recipients)
    except smtplib.SMTPException as e:
        logger.error("SMTP error occurred: {}", e)
    except Exception as e:
        logger.error("Unexpected error while sending email: {}", e)
