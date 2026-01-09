package com.tttsaurus.ksml.grammar_mixin;

import com.intellij.extapi.psi.ASTWrapperPsiElement;
import com.intellij.lang.ASTNode;
import com.intellij.openapi.util.TextRange;
import com.intellij.psi.LiteralTextEscaper;
import com.intellij.psi.PsiLanguageInjectionHost;
import com.tttsaurus.ksml.grammar.psi.AnyChunk;
import org.jetbrains.annotations.NotNull;

public abstract class AnyChunkImplMixin extends ASTWrapperPsiElement implements AnyChunk {
    public AnyChunkImplMixin(@NotNull ASTNode node) {
        super(node);
    }

    @Override
    public boolean isValidHost() {
        return true;
    }

    @Override
    public PsiLanguageInjectionHost updateText(@NotNull String s) {
        return this;
    }

    @Override
    public @NotNull LiteralTextEscaper<? extends PsiLanguageInjectionHost> createLiteralTextEscaper() {
        return new LiteralTextEscaper<PsiLanguageInjectionHost>(this) {

            @Override
            public int getOffsetInHost(int offsetInDecoded, @NotNull TextRange rangeInsideHost) {
                int offset = rangeInsideHost.getStartOffset() + offsetInDecoded;
                if (offset < rangeInsideHost.getStartOffset()) return rangeInsideHost.getStartOffset();
                if (offset > rangeInsideHost.getEndOffset()) return rangeInsideHost.getEndOffset();
                return offset;
            }

            @Override
            public boolean decode(@NotNull TextRange textRange, @NotNull StringBuilder stringBuilder) {
                stringBuilder.append(textRange.substring(myHost.getText()));
                return true;
            }

            @Override
            public boolean isOneLine() {
                return false;
            }
        };
    }
}
