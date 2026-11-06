-- btree_gist: EXCLUDE USING gist constraints that prevent overlapping availability slots (AGD-02)
CREATE EXTENSION IF NOT EXISTS btree_gist;

-- pgcrypto: gen_random_uuid() and hashing helpers
CREATE EXTENSION IF NOT EXISTS pgcrypto;
