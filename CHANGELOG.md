# Changelog

All notable changes to this project will be documented in this file.

## [Unreleased]

## [0.1.0-alpha.13] - 2026-10-04

### Added

- Expose Mekanism Universal Cables through BrandonsCore OP using each cable side's native Strict Energy capability.
- Astral Mekanism and Evolved Mekanism cables are covered automatically because they use Mekanism's TileEntityUniversalCable; Evolved Mekanism Extras cable block entities are recognized explicitly without a compile dependency.

### Changed

- Draconic active push into Universal Cables can now stay on the signed-long OP/Strict Energy path instead of falling back to Forge Energy's Integer.MAX_VALUE-per-call limit.
- Cable NORMAL/PULL/PUSH/NONE behavior remains owned by Mekanism. UEB dynamically resolves the native sided Strict Energy handler rather than reimplementing connection-mode rules.
- Active cable pull and active machine/source push remain independent transfers; when both are enabled in the same tick their transferred amounts may add together, matching native Mekanism behavior.

## [0.1.0-alpha.12] - 2026-10-03

### Added

- Exact BigInteger I/O tracking for Draconic Evolution Energy Cores.
- The Energy Core GUI now displays aggregate input/output above signed-long range instead of relying on BrandonsCore IOTracker's long-limited average.
- Exact tracking runs alongside the original DE tracker and does not alter actual energy transfer or storage.

### Changed

- Energy Core I/O keeps the original GUI behavior: normal view shows exact net I/O, while holding Shift shows exact input and output separately.
- The exact tracker mirrors BrandonsCore's 19-completed-tick averaging window but performs accumulation and averaging with BigInteger.

## [0.1.0-alpha.11] - 2026-10-03

### Added

- Optional server-side Quantum Entangloporter unlimited energy-buffer override.
- When enabled, only the shared QE frequency energy container uses Mekanism `FloatingLong.MAX_VALUE`; other Mekanism energy storage and the original Mekanism config are unchanged.
- The option is intentionally disabled by default and requires a world/server restart.

## [0.1.0-alpha.10] - 2026-10-03

### Changed

- Flux Jade Network and Last Transfer lines are now shown whenever the player can use the network, without requiring sneak/show-details.
- Flux network names in Jade use the network's configured Flux Networks color.
- Last Transfer is colored green for positive transfer, red for negative transfer and gold for zero.

### Kept in show-details mode

- Device type, priority, Power Surge and configured limit.
- Bypass Limit and Chunk Loading for access levels that can edit.

## [0.1.0-alpha.9] - 2026-10-03

### Added

- Flux Networks custom device names replace the default Jade object title even without show-details/sneak mode.
- Sneak/show-details diagnostics for Flux devices: device type, network name, raw priority, Power Surge, configured limit and last transfer.
- Bypass Limit and Chunk Loading status are shown in Jade only to FN access levels that can edit the network/device.
- Flux diagnostic data is sent only to players who can use the network; blocked players still only receive the public custom device name.

## [0.1.0-alpha.8] - 2026-10-03

### Added

- Expose Flux Plug and Flux Point through UEB Universal Energy using Flux Networks' signed-long capability.
- Jade tooltip override for UEB-backed non-Mekanism blocks, restoring Jade's standard energy bar after Mekanism's Jade integration removes it.

### Fixed

- Draconic Energy Pylons no longer keep Mekanism's Jade energy presentation merely because UEB exposes Mekanism Strict Energy on them.
- Flux Plugs can now report their signed-long buffer/limit to Jade instead of the Forge Energy 2,147,483,647 ceiling.
- Replace deprecated direct ResourceLocation construction with the 1.20.1 static factory helper.

## [0.1.0-alpha.7] - 2026-10-03

### Added

- Optional Jade integration that reports UEB Universal Energy storage using Jade's native energy bar with signed-long values.
- Expose the Draconic Evolution Creative Power Source through Universal Energy so Jade and future UEB consumers can avoid Forge Energy's int-limited view.

### Fixed

- Jade no longer falls back to the 2,147,483,647 FE Forge Energy display limit for UEB-backed energy endpoints such as Draconic Energy Pylons and Mekanism Energy Cubes.

## [0.1.0-alpha.6] - 2026-10-03

### Added

- Optional Flux Networks overflow guard, enabled by default.
- Overflow-safe Flux Networks statistics aggregation without changing high-throughput transfer limits.
- Saturating Flux Networks network request/buffer limiter aggregation.

### Fixed

- Prevent Flux Networks input/output statistics from wrapping negative when long-capability sources such as Mekanism Creative Energy Cubes exceed the intermediate accumulator range.
- Prevent Flux Networks' network buffer limiter from wrapping negative when aggregate requests exceed signed-long range.

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
