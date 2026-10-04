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
