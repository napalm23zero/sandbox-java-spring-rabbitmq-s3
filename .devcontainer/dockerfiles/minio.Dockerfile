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

# MinIO - the local S3 hero for when you don't have real AWS money
FROM minio/minio

LABEL maintainer="Rodrigo Dantas"
LABEL email="rodrigo.dantas@hustletech.dev"

# Create a data directory for MinIO
RUN mkdir -p /data

# Set working directory
WORKDIR /workspace

# Expose MinIO default ports
EXPOSE 9000 9001

# Start MinIO in standalone mode with web console enabled
CMD ["minio", "server", "/data", "--console-address", ":9001"]

# Note: This devcontainer is your playground. Experiment wildly, code bravely, or just casually break things to see what happens.
