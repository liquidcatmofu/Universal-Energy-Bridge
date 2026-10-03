# Minecraft 1.20.1 Forge Mod Template

Minecraft Forge 1.20.1向けの小規模MODを素早く作るためのリポジトリテンプレートです。

## 基本構成

- Minecraft 1.20.1
- Forge 47.4.10
- ForgeGradle 6.0.54
- Gradle Wrapper 8.14
- Gradle実行JDK 21
- Java target 17
- Official Mojang mappings
- MixinGradle 0.7.38 / SpongePowered Mixinを標準で有効化
- CI、およびタグ起点のGitHub Release / Modrinth / CurseForge公開

## MODを作る

GitHub Templateからリポジトリを作成した後、次を実行します。

```bash
./init.sh <mod_id> "<Mod Name>" <java.package>
```

例:

```bash
./init.sh example_tools "Example Tools" dev.liquidcatmofu.exampletools
./init.sh benri_tools "便利ツール" dev.liquidcatmofu.benritools
```

表示名にASCII英数字が含まれる場合はそこからMain class名を生成します。表示名が日本語などの非ASCII文字だけの場合は`mod_id`へfallbackし、例えば`benri_tools`から`BenriTools`を生成します。

初期化スクリプトはGradle/MODメタデータ、Java package、Main class、Mixin設定、ドキュメント雛形、成果物名を更新し、最後にテンプレート専用ファイルと自身を削除します。

初期化後は次でビルドできます。

```bash
./gradlew clean build
```

上の例では公開JARは次の形式になります。

```text
ExampleTools-Forge-1.20.1-0.1.0.jar
```

リリースタグは`MinecraftVersion-SemVer`形式（例: `1.20.1-0.1.0`）です。詳細は[リリース手順](docs/releasing.md)を参照してください。

## 初回リリース前

`gradle.properties`の`mod_description`、`mod_license`、`publish_environment`を実際のMODに合わせ、生成されたREADME/CHANGELOGを更新し、`docs/releasing.md`記載の公開用variables/secretsを設定してください。
