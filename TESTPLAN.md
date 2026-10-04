# Prototype test plan

## A. Capability smoke test

Run `/ueb probe <pos>` on:

- Draconic Energy Pylon
- Mekanism Induction Port
- Mekanism Quantum Entangloporter
- Trash Cans Energy Trash Can
- Trash Cans Ultimate Trash Can

Expected: all five report a Universal Energy capability.

## B. Integer boundary

Use a source/sink capable of more than `Integer.MAX_VALUE` per tick/call.

Test requests around:

- 2,147,483,646
- 2,147,483,647
- 2,147,483,648
- 4,294,967,296
- 10,000,000,000
- 1,000,000,000,000
- 1,000,000,000,000,000

Expected: no discontinuity specifically at 2,147,483,647 on Universal/Mek/FN/AppliedFlux routes.

## C. DE <-> Mek QE

1. QE A next to DE Energy Pylon.
2. QE B next to a large Mekanism source/sink.
3. Same frequency.
4. Configure energy sides explicitly; avoid INPUT_OUTPUT for the first test.
5. Raise Mekanism QE energyBuffer manually.
6. Measure Energy Core delta over exactly one server tick if possible.

Expected: limit should be QE/config/native endpoint limits, not Forge Energy int.

## D. DE <-> Flux Networks

Place Flux Point/Plug directly adjacent to Energy Pylon.

Expected: Flux Networks should find `IFNEnergyStorage`; transfer may exceed int if network/device limits allow it.

## E. AppliedFlux external storage

1. Attach ME Storage Bus to Energy Pylon.
2. Open an AppliedFlux-capable terminal.
3. Verify stored FE follows Energy Core storage.
4. Insert/extract FE from AE network.
5. Confirm no AppliedFlux storage cell is needed for the Energy Core-backed quantity.

Repeat with a Quantum Entangloporter. The Induction Port case should continue using AppliedFlux's existing dedicated implementation.

## F. Loop/thrash test

For initial testing, keep each endpoint direction explicit.

Then deliberately connect the same physical storage through multiple networks (for example QE + FN) and watch:

- server ms/t
- network transfer counters
- stored energy conservation
- repeated zero-net-flow transfers

Disable individual compat paths in the UEB config to isolate any loop.

## G. Trash Cans long sink

1. Place an Energy Trash Can or Ultimate Trash Can.
2. Disable its energy transfer limit.
3. Run `/ueb probe <pos>`.

Expected with the limit disabled:

- simulated Universal insertion with `Long.MAX_VALUE` returns `Long.MAX_VALUE`
- extraction returns 0
- stored energy is 0
- capacity is `Long.MAX_VALUE`

Then enable the Trash Cans transfer limit and set it to 10,000,000 FE/t.

Expected: simulated Universal insertion is capped at 10,000,000, preserving the Trash Cans setting instead of bypassing it.

### Trash Cans constructor/regression check

Repeat the Trash Cans tests with a current 1.1.1-series build as well as the older 1.0.18-series build.

Expected: placing Energy Trash Can and Ultimate Trash Can must not crash during BlockEntity capability gathering. The compat must not depend on Trash Cans private fields or constructor-initialized state.

### Trash Cans native route check

With the Trash Cans energy limit disabled, verify each route independently with a source/network capable of more than `Integer.MAX_VALUE` FE/t:

- Draconic/BrandonsCore OP -> Energy Trash Can
- Mekanism Strict Energy / Universal Cable -> Energy Trash Can
- Flux Networks -> Energy Trash Can
- Repeat with Ultimate Trash Can

Expected: the receiving route must use OP, Strict Energy or FN long capability respectively and must not plateau at 2,147,483,647 FE/t solely because of Forge Energy.

With the Trash Cans energy limit enabled, all native views must still respect the configured Trash Cans limit.

### Trash Cans backing-access regression

On Trash Cans 1.1.1-series, run `/ueb probe <pos>` directly on an Energy Trash Can with its transfer limit disabled.

Expected:

- `endpoint=universal_energy_bridge:trashcans_energy_sink`
- `canIn=true`
- `simIn=9223372036854775807`
- `capacity=9223372036854775807`

This verifies both the new endpoint registry match and that the UEB adapter reads Trash Cans' public limit API instead of depending on a re-entrant Forge Energy capability lookup.

Repeat with the transfer limit enabled at 10,000,000 FE/t.

Expected: `simIn=10000000`.

## H. Endpoint registry / protocol exporter regression

alpha.20 moves Trash Cans to the generic endpoint registry. With Draconic Evolution, Mekanism and Flux Networks installed, check the log during common setup.

Expected registrations include:

- endpoint `universal_energy_bridge:trashcans_energy_sink`
- protocol exporter `brandonscore:op`
- protocol exporter `mekanism:strict_energy`
- protocol exporter `fluxnetworks:energy`

The Trash Cans endpoint declares Forge Energy as native, so UEB must not attach a second Forge Energy view.

Repeat the native route checks from section G. They must work through generic exporters; the old Trash Cans-specific OP/Mek/FN attachers are no longer loaded.

For a failed `/ueb probe`, capture the complete diagnostic line. It now includes endpoint registration, block id and BlockEntityType id so matcher failures can be distinguished from capability/provider failures.


## I. Trash Cans attached-capability visibility

alpha.21 adds a narrow Trash Cans mixin for versions whose custom `getCapability` implementation hides capabilities attached through Forge's `AttachCapabilitiesEvent`.

The mixin must preserve every non-empty capability returned by Trash Cans itself. Only when Trash Cans returns an empty `LazyOptional` may it fall back to the inherited Forge-attached capability dispatcher.

With Trash Cans 1.1.1a:

1. Place a fresh Energy Trash Can and Ultimate Trash Can after loading alpha.21.
2. Run `/ueb probe <pos>`.
3. Test OP, Mekanism Strict Energy and Flux Networks input separately.

Expected:

- `endpoint=universal_energy_bridge:trashcans_energy_sink`
- Universal capability is present
- limit disabled: `canIn=true` and `simIn=9223372036854775807`
- OP/Mek/FN routes no longer fall back to the Trash Cans Forge Energy `int` path
- Trash Cans' own item/fluid/Forge Energy capabilities remain unchanged

Repeat once with an existing saved Trash Can and once with a newly placed block to rule out stale BlockEntity instances.


## J. Flux Plug long-buffer corruption regression

alpha.22 hardens Flux Plug's own signed-long buffer arithmetic in addition to the existing network/statistics overflow guards.

1. Use a long-capable source capable of multi-EFE/t transfer into a Flux Plug.
2. Repeatedly switch the Plug between valid networks, disconnect/reconnect it, toggle bypass/limits, and change connected Points while energy is flowing.
3. Observe the Plug buffer and transfer state without breaking the Plug.
4. Repeat with buffer values above half of Long.MAX_VALUE.

Expected:

- Plug buffer never becomes negative
- Plug continues receiving while `buffer < min(device limit, network buffer limiter)`
- buffer values above Long.MAX_VALUE / 2 do not cause the old double-subtraction receive calculation to stall the Plug
- an already-negative buffer from a previous build is repaired to 0 by the next Flux network cycle
- configured transfer limits and side-transfer accounting remain active
- no block replacement is required to resume transfer


## K. Draconic Endpoint Registry migration

alpha.23 migrates Draconic Energy Pylons and Creative OP sources from dedicated Universal/Mek/Flux attachers to the generic Endpoint Registry.

### Energy Pylon

1. Run `/ueb probe <pos>` on an Energy Pylon.
2. Test native OP access.
3. With `draconicToMekanism=true`, connect a Mekanism Strict Energy consumer/cable.
4. With `draconicToFluxNetworks=true`, connect a Flux Networks endpoint.
5. Toggle each config off independently and restart as required by Forge config loading.

Expected:

- probe reports `endpoint=universal_energy_bridge:draconic_energy_pylon`
- Universal remains lazy and functional even though capability attachment occurs before `opAdapter` initialization
- native OP is not shadowed or re-exported by UEB
- Mekanism export appears only when `draconicToMekanism` is enabled
- Flux export appears only when `draconicToFluxNetworks` is enabled
- active push behavior and source-side rate ownership remain unchanged

### Creative OP source

1. Run `/ueb probe <pos>`.
2. Test native OP and Flux Networks output.
3. Confirm no new Mekanism Strict Energy capability is introduced by this migration.

Expected:

- probe reports `endpoint=universal_energy_bridge:draconic_creative_source`
- Universal resolves native OP lazily per side
- native OP remains authoritative
- Flux export follows `draconicToFluxNetworks`
- Mekanism Strict Energy remains absent, preserving alpha.22 behavior

### Pairwise attacher removal

Check common-setup logs.

Expected:

- `DraconicEndpointCompat` is enabled as a setup-time registration
- legacy `DraconicUniversalCompat`, `DraconicMekanismCompat`, and `DraconicFluxCompat` are no longer enabled as capability attachers
- reverse-direction `MekanismDraconicCompat` and `FluxDraconicCompat` remain until their source endpoints are migrated


## L. Mekanism Energy Cube Endpoint Registry migration

alpha.24 migrates vanilla Mekanism Energy Cubes, including the Creative Energy Cube, to the generic Endpoint Registry while leaving QE, Induction Ports, Mekanism Extras and Universal Cables on the legacy path for now.

### Endpoint and sided Strict Energy

1. Place a normal Energy Cube and a Creative Energy Cube.
2. Run `/ueb probe <pos>` on each.
3. Configure different faces as input, output and disabled.
4. Reconfigure those faces while the blocks remain placed and repeat insertion/extraction tests.

Expected:

- probe reports `endpoint=universal_energy_bridge:mekanism_energy_cube`
- Universal remains available through the registered endpoint
- each Universal operation re-resolves the current sided Strict Energy capability
- input/output/disabled behavior follows Mekanism's current side configuration without replacing the block or recreating the UEB provider
- `mekanism:strict_energy` is treated as native and is never re-exported by UEB
- Forge Energy remains native and is not replaced by UEB

### OP export

With Draconic Evolution installed:

1. Enable `mekanismToDraconic`.
2. Test OP access on each configured cube face.
3. Disable `mekanismToDraconic` and restart as required.

Expected:

- OP is supplied by the generic exporter only while the config is enabled
- OP behavior follows the same dynamic Mekanism sided Strict Energy handler as Universal
- the old `MekanismDraconicCompat` does not attach a second OP view to Energy Cubes
- QE, Induction Ports, Extras endpoints and Universal Cables still use the old attacher until their migrations

### Flux and AppliedFlux preservation

Expected:

- Energy Cubes do not gain a new Flux Networks capability solely from this migration
- existing AppliedFlux external-storage behavior remains unchanged and separate from Protocol export
