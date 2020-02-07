# monster-scio-utils
Utilities used by the Monster team when writing Scio pipelines

## Motivation
We find there are a few patterns that repeat across the Scio pipelines
used to transform data during ingest. Centralizing those patterns into
a library helps us keep behavior consistent and test core functionality
in isolation.

## Using the utils
This library is published to Broad's Artifactory instance. Any project
which enables the `MonsterBasePlugin` from our
[`sbt-plugins` repo](https://github.com/broadinstitute/monster-sbt-plugins)
should be configured to pull dependencies from that repository using:
```sbt
libraryDependencies += "org.broadinstitute.monster" %% "<artifact>" % "<version>"
```

### Available modules
| Artifact name | Description |
| ------------- | ----------- |
| `msg-utils` | Utilities for parsing and inspecting MessagePack payloads |
| `scio-utils` | Utilities for Scio pipelines that parse MessagePack payloads |

## Publishing a new version
This repo uses git to manage releases. Every merge to `master` is published
to Broad's snapshot repository, using the preceding version and commit history
to construct a version number. Commits tagged with a string that matches the
pattern `v<semver-version>` are re-published to the releases repository under
the associated version.

There is no automated process for deciding the next version number, or for pushing
the tag. Make a value judgement on whether to bump the `major`, `minor`, or `patch`
component of the version, then run:
```bash
git tag vX.Y.Z
git push vX.Y.Z
```
Jenkins will pick up the new tag and run the publish operation.
