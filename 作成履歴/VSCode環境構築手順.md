# VS Code で Mod をデバッグ実行するまでの手順

参考記事: https://qiita.com/tester09/items/176973b74ccd2aae6a2a

## 環境情報

| 項目 | バージョン |
|------|-----------|
| Minecraft | 1.20.1 |
| Forge | 47.3.0 |
| ForgeGradle | 6.0〜6.2 |
| Java | 17 |

---

## 手順

### 1. VS Code 拡張機能をインストール

VS Code の拡張機能マーケットプレイスで以下を検索してインストール:

- **Gradle for Java**

### 2. VS Code 用の起動設定を生成

**コマンドプロンプト**を開き、`lolmod/` ディレクトリに移動して実行する。

```
cd C:\Users\narum\Documents\GitHub\MinecraftMOD_test\lolmod
gradlew.bat genVSCodeRuns
```

`BUILD SUCCESSFUL` と表示されれば完了。
初回は時間がかかる場合がある（目安: 10〜15分）。

実行後、`lolmod/.vscode/launch.json` が自動生成される。

### 3. VS Code でデバッグ実行

1. VS Code で `lolmod/` フォルダを開く
2. 「Run and Debug」パネルを開く（`Ctrl+Shift+D`）
3. ドロップダウンから **`runClient`** を選択
4. 実行ボタン（▷）を押す

---

## 注意点

### PowerShell ではなくコマンドプロンプトを使う

PowerShell は `.bat` スクリプトの実行にセキュリティポリシーが干渉する場合がある。
**`gradlew genVSCodeRuns` は必ずコマンドプロンプト（cmd.exe）で実行すること。**

- PowerShell の場合は動かないことがある
- コマンドプロンプトなら `gradlew` と打つだけで `gradlew.bat` が自動で実行される

### 実行ディレクトリに注意

`gradlew.bat` はリポジトリルートではなく **`lolmod/` ディレクトリの中**にある。
必ず `lolmod/` に移動してから実行すること。

```
# NG: リポジトリルートで実行
cd C:\Users\narum\Documents\GitHub\MinecraftMOD_test
gradlew.bat genVSCodeRuns  ← gradlew.bat が見つからない

# OK: lolmod/ で実行
cd C:\Users\narum\Documents\GitHub\MinecraftMOD_test\lolmod
gradlew.bat genVSCodeRuns
```
