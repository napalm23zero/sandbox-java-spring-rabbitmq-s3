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

# Java 17 with Maven for building SpringBoot apps
FROM maven:3.8.7-openjdk-18-slim

LABEL maintainer="Rodrigo Dantas"
LABEL email="rodrigo.dantas@hustletech.dev"

# Installing a bunch of stuff because every ninja needs some tools.
# Maven for building, Git for tracking, Python for scripting, and Curl for... well, curling up with your code.
RUN apt-get update && \
    apt-get install -y maven git python3 curl && \
    apt-get clean && \
    rm -rf /var/lib/apt/lists/*

# Set working directory
WORKDIR /workspace

# Copy the SpringBoot project files and install dependencies
COPY . .

# Expose the default port for SpringBoot
EXPOSE 8080

# Keep the container running forever, because who shuts down a devcontainer? Seriously, keep working!
# This CMD is like the developer's equivalent of leaving the TV on for background noise.
CMD ["tail", "-f", "/dev/null"]

# Note: This devcontainer is your playground. Experiment wildly, code bravely, or just casually break things to see what happens.