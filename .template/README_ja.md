# Example Mod

[English](README.md) · [変更履歴](CHANGELOG.md)

Example ModはMinecraft Java Edition 1.20.1向けのForge MODです。

## 対応環境

- Minecraft 1.20.1
- Forge 47.4.10
- Java 17 target
- Gradle実行用JDK 21

Forge 1.20.1の公式ドキュメントでは開発環境の前提としてJDK 17が案内されています。このプロジェクトではGradle自体はJDK 21で実行し、`--release 17`でJava 17向けにコンパイルします。リリース時にはJava 17より新しいclass fileが混入していないことも検証します。

公開時の環境メタデータはデフォルトで`client | server`です。クライアント専用・サーバー専用・任意導入の場合は、公開前に`gradle.properties`の`publish_environment`を変更してください。

## ビルド

JDK 21でGradleを実行します。

```bash
./gradlew clean build
```

Windowsでは次のコマンドを使用します。

```powershell
.\gradlew.bat clean build
```

成果物は次の名前で生成されます。

```text
build/libs/ExampleMod-Forge-1.20.1-0.1.0.jar
```

リリース自動化とリポジトリ設定は[リリース手順](docs/releasing.md)に記載しています。

## ライセンスと作者

作者: LiquidCatMofu

ライセンスのメタデータは`gradle.properties`の`mod_license`で指定します。テンプレートでは`All Rights Reserved`が初期値です。OSSライセンスを選択する場合は標準の`LICENSE`ファイルを追加してください。
