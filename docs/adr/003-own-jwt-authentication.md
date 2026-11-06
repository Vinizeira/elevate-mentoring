# ADR-003: Own JWT authentication instead of Cognito

- **Status:** Accepted
- **Date:** 2026-11-06

## Context

Users have four roles (STUDENT, MENTOR, COMPANY_ADMIN, PLATFORM_ADMIN) and mentors belong to a company (tenant), which must be part of every authorization decision.
Everything runs locally against LocalStack, and the free LocalStack edition does not emulate Cognito.
The project is also a portfolio piece: implementing authentication shows how it works.

## Decision

The IAM module issues its own tokens with Spring Security:

- Short-lived signed JWT access token carrying the user id, role and tenant id.
- Refresh token stored in the database (hashed), so it can be rotated and revoked.
- Passwords hashed with BCrypt.
- All timestamps in UTC.

## Consequences

- Works the same locally, in CI and on AWS, with no external identity provider.
- We own the security-sensitive code: signing key management, token expiry, refresh rotation, password storage. These need dedicated tests.
- No social login, MFA or password-reset emails for free. Those are out of the MVP; moving to Cognito or another provider later would replace the IAM module, while other modules only see the user id, role and tenant id.
