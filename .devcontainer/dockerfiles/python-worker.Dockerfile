# ┌─────────────────────────────────────┐
# │  The Dark Side of Devcontainers     │
# │                                     │
# │  For dev and study use ONLY.        │
# │  Deploy this to production?         │
# │  I will personally find you,        │
# │  and trust me, you won’t like it.   │
# │                                     │
# │  *You've been warned, apprentice.*  │
# └─────────────────────────────────────┘

# Start with a lightweight Python image
FROM python:3.9-slim

# Set the working directory
WORKDIR /workspace

# Install necessary dependencies (pika for RabbitMQ and requests for HTTP requests)
RUN apt-get update && apt-get install -y curl && \
    pip install --no-cache-dir pika requests

# Copy the script from the host
COPY ./scripts/worker.py /workspace/scripts/worker.py
