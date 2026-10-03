# Universal Energy Bridge

Minecraft 1.20.1 Forge向けの試作互換MODです。Forge Energyの`int`転送APIへフォールバックせず、各MODが持つlong/nativeエネルギーAPI同士を接続します。

現在の試作対応:

- Draconic Evolution Energy Pylon → Universal Energy Capability
- Draconic Evolution Energy Pylon ↔ Mekanism Strict Energy
- Draconic Evolution Energy Pylon ↔ Flux Networks long energy
- Draconic Evolution Energy Pylon ↔ AppliedFlux/AE2 外部FEストレージ
- Mekanism Induction Port / Quantum Entangloporter → Universal Energy Capability
- Mekanism Induction Port / Quantum Entangloporter ↔ BrandonsCore OP
- Quantum Entangloporter ↔ AppliedFlux/AE2 外部FEストレージ

Induction MatrixについてはAppliedFlux自身に直接統合があるため、このMODでは置き換えません。

## ビルド

リポジトリに入っているGradle Wrapperを使います。system Gradleでwrapperを作り直す必要はありません。

```bash
./gradlew clean build
```

Minecraft側のtargetはJava 17です。GitHub Actionsでも同じコマンドを実行します。

## Probe

OP権限で以下を実行できます。

```text
/ueb probe <x> <y> <z>
```

Universal Energyの保存量/容量と、`Long.MAX_VALUE`を使ったsimulate入出力量を表示します。`Integer.MAX_VALUE`で不意にclampされていないか確認する用途です。

境界値テストは`TESTPLAN.md`を参照してください。

## 現状

初期試作です。QE buffer override、GTCEu、BigIntegerによる正確な超long容量表示は、まず基本のlong経路をゲーム内で確認してから追加します。
