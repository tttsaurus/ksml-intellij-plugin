package com.tttsaurus.ksml

import com.intellij.testFramework.TestDataPath
import com.intellij.testFramework.fixtures.BasePlatformTestCase
import com.tttsaurus.ksml.language.utils.glsl.GlslModuleCallParser

@TestDataPath($$"$CONTENT_ROOT/src/test/testData")
class GlslModuleCallParserTest : BasePlatformTestCase() {

    override fun getTestDataPath(): String = "src/test/testData"

    fun testCall1() {
        val call = GlslModuleCallParser.parse("""
            mymodule.myfunc()
        """.trimIndent())

        assertNotNull(call)
        call!!

        assertEquals("mymodule", call.moduleName)
        assertEquals("myfunc", call.functionName)
        assertEquals(0, call.arguments.size)
    }

    fun testCall2() {
        val call = GlslModuleCallParser.parse("""
            yourmodule.myfunc(0.0, vec2(1.0), vec3(0.0, 1.0, 2.0), ok(), kk(0.0, MY_CONST, ok()))
        """.trimIndent())

        assertNotNull(call)
        call!!

        assertEquals("yourmodule", call.moduleName)
        assertEquals("myfunc", call.functionName)

        assertEquals(5, call.arguments.size)
        assertEquals("0.0", call.arguments[0])
        assertEquals("vec2(1.0)", call.arguments[1])
        assertEquals("vec3(0.0, 1.0, 2.0)", call.arguments[2])
        assertEquals("ok()", call.arguments[3])
        assertEquals("kk(0.0, MY_CONST, ok())", call.arguments[4])
    }

    fun testCall3() {
        val call = GlslModuleCallParser.parse("""
            yourmodule.myfunc(
                0.0, 
                vec2(1.0), 
                vec3(0.0, 1.0, 2.0), 
                ok(), 
                kk(0.0, MY_CONST, ok())
            )
        """.trimIndent())

        assertNotNull(call)
        call!!

        assertEquals("yourmodule", call.moduleName)
        assertEquals("myfunc", call.functionName)

        assertEquals(5, call.arguments.size)
        assertEquals("0.0", call.arguments[0])
        assertEquals("vec2(1.0)", call.arguments[1])
        assertEquals("vec3(0.0, 1.0, 2.0)", call.arguments[2])
        assertEquals("ok()", call.arguments[3])
        assertEquals("kk(0.0, MY_CONST, ok())", call.arguments[4])
    }

    fun testCall4() {
        val call = GlslModuleCallParser.parse("""
            module.func(
                mat3(vec3(1.0), vec3(2.0), vec3(3.0)),
                foo(bar(baz(1, 2), vec2(3, 4))),
                arr[index + 1],
                cond ? vec4(1.0) : vec4(0.0),
                (a + b) * (c + d)
            )
        """.trimIndent())

        assertNotNull(call)
        call!!

        assertEquals("module", call.moduleName)
        assertEquals("func", call.functionName)

        assertEquals(5, call.arguments.size)
        assertEquals("mat3(vec3(1.0), vec3(2.0), vec3(3.0))", call.arguments[0])
        assertEquals("foo(bar(baz(1, 2), vec2(3, 4)))", call.arguments[1])
        assertEquals("arr[index + 1]", call.arguments[2])
        assertEquals("cond ? vec4(1.0) : vec4(0.0)", call.arguments[3])
        assertEquals("(a + b) * (c + d)", call.arguments[4])
    }

    fun testCall5() {
        val call = GlslModuleCallParser.parse("""
            lighting.pbr(
                material.layers[2],
                foo(bar[calc(1, 2)], baz{value}),
                vec4(texture(tex, uv).rgb, 1.0),
                ((x + y) * (z - w)),
                {{{abc}}},
                mat4(
                    vec4(1),
                    vec4(2),
                    vec4(3),
                    vec4(4)
                )
            )
        """.trimIndent())

        assertNotNull(call)
        call!!

        assertEquals("lighting", call.moduleName)
        assertEquals("pbr", call.functionName)

        assertEquals(6, call.arguments.size)
        assertEquals("material.layers[2]", call.arguments[0])
        assertEquals("foo(bar[calc(1, 2)], baz{value})", call.arguments[1])
        assertEquals("vec4(texture(tex, uv).rgb, 1.0)", call.arguments[2])
        assertEquals("((x + y) * (z - w))", call.arguments[3])
        assertEquals("{{{abc}}}", call.arguments[4])
        assertEquals(
            """
            mat4(
                vec4(1),
                vec4(2),
                vec4(3),
                vec4(4)
            )
            """.trimIndent().replace(Regex("\\s+"), " "),
            call.arguments[5].replace(Regex("\\s+"), " ")
        )
    }
}
