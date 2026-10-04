# Universal Energy Bridge

Prototype compatibility mod for Minecraft 1.20.1 Forge that keeps high-throughput energy transfers on long/native APIs instead of falling back to Forge Energy's `int` transfer methods.

Current prototype integrations include Draconic Evolution, Mekanism, Flux Networks, AppliedFlux/AE2, Trash Cans and Jade.

## Quantum Entangloporter unlimited buffer

alpha.11 adds an opt-in server/world setting that ignores Mekanism's QE frequency `energyBuffer` limit without changing the original Mekanism config or any other Mekanism energy container.

In the world's `serverconfig/universal_energy_bridge-server.toml`:

```toml
[mekanism]
quantumEntangloporterUnlimitedEnergyBuffer = true
```

Restart the world/server after changing this option. When enabled, the shared QE frequency energy buffer uses Mekanism's native `FloatingLong.MAX_VALUE`. With the default Mekanism Forge Energy conversion rate, that is roughly 7.38 EFE of FE-equivalent capacity/maximum transfer per tick per frequency.

## Build

Use the checked-in Gradle wrapper.

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
