# rehab-collections

Java + AI 8 周康复训练 · 第 1 周产出（W1D1T3）。

## 跑测试

PowerShell（推荐，脚本会自己设好 JDK 21）：

```powershell
cd E:\DSH\rehab-collections
.\run-tests.ps1 test
```

或者手动：

```powershell
$env:JAVA_HOME = 'D:\develop\JDK\jdk-21'
mvn test
```

注意：PATH 上的 `java` 是 Java 8，直接 `mvn test` 会报 `invalid target release: 21`。
Maven 在 `D:\develop\Maven\apache-maven-3.9.9\bin\mvn.cmd`，已在 PATH 里。

## 目录

- `src/main/java/rehab/MyArrayList.java` — 你要实现的类，TODO 是骨架
- `src/test/java/rehab/ToolchainSmokeTest.java` — 环境自检，实现完可删
- 你的 6 个测试写在 `src/test/java/rehab/MyArrayListTest.java`

## IDE

IntelliJ IDEA 2025.1 直接 Open 这个目录，选 JDK 21（`D:\develop\JDK\jdk-21`）即可。
