# Universal Energy Bridge

Prototype compatibility mod for Minecraft 1.20.1 Forge that keeps high-throughput energy transfers on long/native APIs instead of falling back to Forge Energy's `int` transfer methods.

Current prototype integrations:

- Draconic Evolution Energy Pylon → Universal Energy capability
- Draconic Evolution Energy Pylon ↔ Mekanism Strict Energy
- Draconic Evolution Energy Pylon ↔ Flux Networks long energy
- Draconic Evolution Energy Pylon ↔ AppliedFlux/AE2 external FE storage
- Mekanism Induction Port / Quantum Entangloporter → Universal Energy capability
- Mekanism Induction Port / Quantum Entangloporter ↔ BrandonsCore OP
- Quantum Entangloporter ↔ AppliedFlux/AE2 external FE storage

AppliedFlux already has a direct Induction Matrix integration, so this mod does not replace it.

## Build

Use the checked-in Gradle wrapper. Do not run the system Gradle to generate another wrapper.

```bash
./gradlew clean build
```

Java 17 is the Minecraft target. The repository CI builds the same command.

## Probe

With operator permission:

```text
/ueb probe <x> <y> <z>
```

This reports the Universal Energy view and simulates insertion/extraction with `Long.MAX_VALUE`, which is useful for spotting accidental `Integer.MAX_VALUE` clamps.

See `TESTPLAN.md` for boundary tests.

## Status

This is an early prototype. QE buffer override, GTCEu and optional exact/BigInteger capacity reporting are intentionally deferred until the core long paths are verified in-game.
