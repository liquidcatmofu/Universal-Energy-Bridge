# Changelog

All notable changes to this project will be documented in this file.

## [Unreleased]

### Added

- Introduce the first public protocol/conversion/endpoint registration API: protocol identifiers are registry keys rather than a fixed enum, amount domains may use native numeric types, and conversion edges carry exactness/range/rounding/semantic-loss metadata.
- Add a side-aware BlockEntity endpoint registry with lazy Universal endpoint factories and generic Universal-to-protocol exporters.
- Add generic OP, Mekanism Strict Energy and Flux Networks exporters for registered Universal endpoints.
- Extend `/ueb probe` diagnostics with the matched endpoint registration, block id and BlockEntityType id.

### Fixed

- Restore visibility of Forge-attached UEB/OP/Mekanism/Flux capabilities on Trash Cans versions whose custom `getCapability` does not delegate unknown capabilities to the inherited Forge dispatcher. The compatibility mixin preserves every native Trash Cans capability result and only falls back when the native result is empty.

### Changed

- Migrate Trash Cans to the generic endpoint registry. Its OP, Mekanism and Flux Networks views are now produced by common protocol exporters rather than three Trash Cans-specific pairwise attachers.
- Treat Universal signed-long as a registered fallback protocol rather than the architectural assumption that every protocol must route through signed-long.
- Trash Cans target detection now accepts the Energy/Ultimate block registry ids in addition to the older dedicated BlockEntityType ids, while still avoiding constructor-initialized instance fields.

## [0.1.0-alpha.15] - 2026-10-04

### Fixed

- Prevent Energy Meter's `getTransferRate()` three-decimal rounding from saturating through `Math.round(double)` at `Long.MAX_VALUE`, which capped displayed native transfer rates at about 9.22 PFE/t.
- High native rates now keep the meter's existing `double` measurement value once three-decimal rounding is no longer representable as a signed `long`; lower rates retain Energy Meter's original rounding behavior.

## [0.1.0-alpha.14] - 2026-10-04

### Added

- Optional Energy Meter 1.20.1 native high-throughput passthrough for BrandonsCore OP, Mekanism Strict Energy and Flux Networks long energy.
- Energy Meter Consumer mode can consume and measure native high-throughput input without routing through Forge Energy.
- Energy Meter Transfer mode preserves the incoming native protocol on outputs that expose the same capability, including UEB-provided native views on compatible endpoints.

### Changed

- Native Energy Meter transfers use the meter's existing IN/OUT/OFF configuration and keep multiple-output distribution without converting the transfer amount into a common signed-long representation first.
- If an output does not expose the incoming native capability, the meter falls back to its Forge Energy view for that output.
- High-energy Energy Meter short formatting now handles Exa, Zetta and Yotta FE and uses scientific notation beyond that range instead of indexing past the original Peta-only suffix table.
- Native transfers feed the Energy Meter's existing measurement accumulator only after actual accepted energy is known; measurement conversion does not affect the transferred native value.


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
