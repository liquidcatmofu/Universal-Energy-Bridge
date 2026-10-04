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
