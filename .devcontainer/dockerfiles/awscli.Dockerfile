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

# AWS CLI v2 for interacting with your MinIO S3 (pretending it's real AWS)
FROM amazon/aws-cli:2.13.9

LABEL maintainer="Rodrigo Dantas"
LABEL email="rodrigo.dantas@hustletech.dev"

WORKDIR /workspace

# Keep the container running forever, because who shuts down a devcontainer? Seriously, keep working!
# This CMD is like the developer's equivalent of leaving the TV on for background noise.
CMD ["tail", "-f", "/dev/null"]

# Note: This devcontainer is your playground. Experiment wildly, code bravely, or just casually break things to see what happens.