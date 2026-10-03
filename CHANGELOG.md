# Changelog

All notable changes to this project will be documented in this file.

## [Unreleased]

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
