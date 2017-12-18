# Change Log

This project adheres to [Semantic Versioning Scheme](http://semver.org)

## [v0.10.0] 2017-10-27

### Changes

- When instantiating an `Instance` you now provide a `locator` instead of an `instanceId`.

## [v0.9.0] 2017-09-20

### Changes

- Error responses now provide more information

## [v0.8.3] 2017-08-29

### Changes

- Corrected the error when instantiating the library - it now says it requires `instanceId` instead of `instance` field.

## [v0.8.2] 2017-08-04

### Changes

- Added support for query params in `RequestOptions` (pass in an object undert the `qs` key)

## [v0.8.1] 2017-08-02

### Changes

- Move path sanitization logic all to the `BaseClient`.
- `TokenWithExpiry` is now an exported interface.

## [v0.8.0] 2017-07-19

### Changes

- Renamed the `instance` to `instanceId` when instantiating an `Instance`. `Instance` class now has a parameter `id` that used to be `instance`.

## [v0.7.1] 2017-07-18

### Changes

- Requests now return a body as well.

## [v0.7.0] 2017-07-17

### Fixes

- Fixed the issue with path - requests now work again.

### Changes

- Removed `generateSuperUserJWT` in `Instance`.
- Allow `Authenticator` to take in custom `tokenExpiry` and `tokenLeeway` - for SuperUser requests
- Rename exported `TOKEN_EXPIRY` to `DEFAULT_TOKEN_EXPIRY`

## [v0.6.1] 2017-07-11

### Changes

- Service claims are now optional

## [v0.6.0] 2017-07-10

### Changes

- Changed the artifact name to `pusher-platform-node`
- Renamed `App` to `Instance`, `appId` to `instanceId`.
- Updated the tenancy to the upcoming standard: https://cluster.and.host/services/serviceName/serviceVersion/instanceId/...


_.. prehistory_
