# Changelog

All notable changes to this project will be documented in this file.

## [Unreleased]

## [0.1.0-alpha.6] - 2026-10-03

### Added

- Optional Flux Networks overflow guard, enabled by default.
- Saturating aggregation for Flux Networks network statistics at extreme native-long throughput.
- Saturating recomputation of Flux Networks' network-wide request limiter so an aggregate above `Long.MAX_VALUE` cannot wrap negative and feed back into Flux Plug receive logic.

### Changed

- Extreme Flux Networks statistics now clamp to signed-long bounds rather than displaying wrapped negative values. Individual device transfer limits are not reduced.

## [0.1.0-alpha.5] - 2026-10-03

### Added

- Recognize Mekanism Extras reinforced induction ports and extra energy cubes as native Mekanism Strict Energy endpoints.
- Expose Mekanism Extras large energy endpoints through Universal Energy, BrandonsCore OP and AppliedFlux external storage without a direct compile dependency on Mekanism Extras.

### Fixed

- Resolve Mekanism Strict Energy dynamically on every operation instead of permanently caching the first side capability result. This prevents vanilla Induction Matrix ports from getting stuck on Forge Energy after multiblock formation/reformation or side-mode changes.

## [0.1.0-alpha.4] - 2026-10-03

### Added

- Expose Flux Plugs through BrandonsCore OP backed by Flux Networks' signed-long energy capability.
- Expose the Draconic Evolution Creative Power Source through Flux Networks' signed-long energy capability.

### Fixed

- Avoid the Forge Energy / Integer.MAX_VALUE fallback when a Draconic Creative Power Source actively pushes into a Flux Plug.

## [0.1.0-alpha.3] - 2026-10-03

### Added

- Expose Mekanism Energy Cubes, including the Creative Energy Cube, as native long AppliedFlux external storage.
- Treat Energy Cubes as Universal Energy / BrandonsCore OP storage endpoints.

## [0.1.0-alpha.2] - 2026-10-03

### Fixed

- Resolve Draconic Energy Pylon OP storage lazily so Forge capability attachment during the BlockEntity superclass constructor cannot capture a null `opAdapter`.

## [0.1.0-alpha.1] - 2026-10-03

### Added

- Initial Universal Energy signed-long capability.
- Draconic Evolution, Mekanism, Flux Networks and AppliedFlux prototype adapters.
- `/ueb probe` command for transfer-boundary diagnostics.
