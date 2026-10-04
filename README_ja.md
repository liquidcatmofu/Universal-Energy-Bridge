# Universal Energy Bridge

Minecraft 1.20.1 Forge向けの試作互換MODです。Forge Energyの`int`転送APIへフォールバックせず、各MODが持つlong/nativeエネルギーAPI同士を接続します。

現在はDraconic Evolution、Mekanism、Flux Networks、AppliedFlux/AE2、Trash Cans、Jadeとの互換を試作しています。

## Quantum Entangloporterのバッファ上限無視

alpha.11では、Mekanism本体の設定や他のMekanismエネルギーストレージを変更せず、QE frequencyの`energyBuffer`上限だけを無視するオプションを追加しています。ゲームバランスを変えるためデフォルトはOFFです。

ワールドの`serverconfig/universal_energy_bridge-server.toml`:

```toml
[mekanism]
quantumEntangloporterUnlimitedEnergyBuffer = true
```

変更後はワールド/サーバーを再起動してください。有効時は共有QE frequencyのエネルギーバッファがMekanism nativeの`FloatingLong.MAX_VALUE`になります。Mekanismの標準FE変換率では、FE換算で約7.38 EFEの容量・frequencyあたり最大転送量になります。

## ビルド

リポジトリに入っているGradle Wrapperを使います。

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
