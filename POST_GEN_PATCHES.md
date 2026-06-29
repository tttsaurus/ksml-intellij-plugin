See `./gradlew patchGrammarKit` / `PatchGrammarKitTask` from `build.gradle.kts`

***

`com.tttsaurus.ksml.grammar.psi.KsmlTypes`

_Before_:
```java
IElementType CODE_DECL = new KsmlElementType("CODE_DECL");
```

_After_:
```java
import com.tttsaurus.ksml.language.stub.KsmlCodeDeclElementType;

IElementType CODE_DECL = KsmlCodeDeclElementType.INSTANCE;
```

***

`com.tttsaurus.ksml.grammar.psi.impl.KsmlCodeDeclImpl`

_Add_:
```java
import com.tttsaurus.ksml.language.stub.KsmlCodeDeclStub;
import com.intellij.psi.stubs.IStubElementType;

public KsmlCodeDeclImpl(@NotNull KsmlCodeDeclStub stub, @NotNull IStubElementType<?, ?> nodeType) {
    super(stub, nodeType);
}
```
