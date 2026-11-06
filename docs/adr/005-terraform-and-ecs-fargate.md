# ADR-005: Terraform + ECS Fargate for AWS infrastructure

- **Status:** Accepted
- **Date:** 2026-11-06

## Context

The MVP must run on AWS by the end of December 2026 using RDS Postgres, SQS, S3 and SES, which the code already uses through LocalStack.
It is a personal project, so monthly cost matters, and the environment should be reproducible and easy to destroy.

## Decision

- All AWS resources are defined in Terraform under `infra/`. Nothing is created by hand in the console.
- The backend runs as a Docker image (ECR) on ECS Fargate behind an Application Load Balancer.
- The VPC has no NAT Gateway, to keep the fixed cost down.
- RDS Postgres 16 for the database; SQS (with DLQs), S3 and SES as in local development. Only endpoints and credentials change between LocalStack and AWS.
- The frontend is served from S3 + CloudFront.
- Deploys run from GitHub Actions with OIDC (no long-lived AWS keys in GitHub).
- A budget alarm is created before any other resource.

## Consequences

- The whole environment can be created and destroyed with `terraform apply` / `terraform destroy`, and changes are reviewed as code.
- Fargate means no servers to patch, but costs more per CPU hour than EC2, and cold deploys take a minute or two.
- Without a NAT Gateway, tasks need public subnets or VPC endpoints to reach AWS services and pull images. This has to be designed carefully in the Terraform VPC module.
- Terraform state must be stored remotely (S3) and kept safe.
