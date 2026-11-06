#!/bin/bash
# Runs inside LocalStack once it is ready. Creates the SQS queues (each with a DLQ),
# the S3 buckets and the verified SES sender used by the backend in the `local` profile.
set -euo pipefail

REGION=us-east-1
MAX_RECEIVE_COUNT=3

# One queue per bounded context that reacts to domain events (see docs/event-storming-context-map.excalidraw).
QUEUES=(
  notification-events
  recognition-events
  impact-events
  matching-events
)

BUCKETS=(
  elevate-profile-photos
  elevate-certificates
  elevate-impact-reports
)

SES_SENDER=no-reply@elevatementoring.local

for queue in "${QUEUES[@]}"; do
  dlq_url=$(awslocal sqs create-queue --region "$REGION" --queue-name "${queue}-dlq" --query QueueUrl --output text)
  dlq_arn=$(awslocal sqs get-queue-attributes --region "$REGION" --queue-url "$dlq_url" \
    --attribute-names QueueArn --query Attributes.QueueArn --output text)

  awslocal sqs create-queue --region "$REGION" --queue-name "$queue" \
    --attributes "{\"RedrivePolicy\":\"{\\\"deadLetterTargetArn\\\":\\\"${dlq_arn}\\\",\\\"maxReceiveCount\\\":\\\"${MAX_RECEIVE_COUNT}\\\"}\"}" \
    > /dev/null
  echo "SQS: ${queue} (DLQ: ${queue}-dlq)"
done

for bucket in "${BUCKETS[@]}"; do
  awslocal s3 mb "s3://${bucket}" --region "$REGION" > /dev/null
  echo "S3: ${bucket}"
done

awslocal ses verify-email-identity --region "$REGION" --email-address "$SES_SENDER"
echo "SES: ${SES_SENDER} verified"

touch /tmp/init-aws.done
