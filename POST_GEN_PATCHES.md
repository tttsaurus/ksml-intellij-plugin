`com.tttsaurus.ksml.grammar.psi.KsmlTypes`

_Before_:
```java
IElementType CODE_DECL = new KsmlElementType("CODE_DECL");
```

_After_:
```java
IElementType CODE_DECL = KsmlCodeDeclElementType.INSTANCE;
```

***

`com.tttsaurus.ksml.grammar.psi.impl.KsmlCodeDeclImpl`

_Before_:
```java
public KsmlCodeDeclImpl(@NotNull ASTNode node) {
    super(node);
}
```

_After_:
```java
public KsmlCodeDeclImpl(@NotNull ASTNode node) {
    super(node);
}

public KsmlCodeDeclImpl(@NotNull KsmlCodeDeclStub stub, @NotNull IStubElementType<?, ?> nodeType) {
    super(stub, nodeType);
}
```
