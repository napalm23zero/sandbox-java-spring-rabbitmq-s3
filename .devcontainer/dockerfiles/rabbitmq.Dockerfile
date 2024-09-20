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

# RabbitMQ, the broker that never sleeps
FROM rabbitmq:3-management

LABEL maintainer="Rodrigo Dantas"
LABEL email="rodrigo.dantas@hustletech.dev"

# Setting up RabbitMQ like a pro
WORKDIR /workspace

# Expose default RabbitMQ ports
EXPOSE 5672 15672

# Note: This devcontainer is your playground. Experiment wildly, code bravely, or just casually break things to see what happens.

# For dev only! Don’t you dare go live with this!
CMD ["rabbitmq-server"]

# Note: This devcontainer is your playground. Experiment wildly, code bravely, or just casually break things to see what happens.