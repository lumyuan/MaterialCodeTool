/*
 *    sora-editor - the awesome code editor for Android
 *    https://github.com/Rosemoe/sora-editor
 *    Copyright (C) 2020-2023  Rosemoe
 *
 *     This library is free software; you can redistribute it and/or
 *     modify it under the terms of the GNU Lesser General Public
 *     License as published by the Free Software Foundation; either
 *     version 2.1 of the License, or (at your option) any later version.
 *
 *     This library is distributed in the hope that it will be useful,
 *     but WITHOUT ANY WARRANTY; without even the implied warranty of
 *     MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 *     Lesser General Public License for more details.
 *
 *     You should have received a copy of the GNU Lesser General Public
 *     License along with this library; if not, write to the Free Software
 *     Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02110-1301
 *     USA
 *
 *     Please contact Rosemoe by email 2073412493@qq.com if you need
 *     additional information or have any questions
 */
package ly.android.material.code.langs.tiecode;

/**
 * Tokens for Java
 *
 * @author Rose
 */
@SuppressWarnings("SpellCheckingInspection")
public enum Tokens {
    WHITESPACE,
    NEWLINE,
    UNKNOWN,
    EOF,

    
    LONG_COMMENT_COMPLETE,
    LONG_COMMENT_INCOMPLETE,
    LINE_COMMENT,

    DIV,
    MULT,
    IDENTIFIER,
    INTEGER_LITERAL,
    DOT,
    MINUS,
    STRING,
    CHARACTER_LITERAL,
    LPAREN,
    RPAREN,
    LBRACE,
    RBRACE,
    LBRACK,
    RBRACK,
    SEMICOLON,
    COMMA,
    EQ,
    GT,
    LT,
    NOT,
    COMP,
    QUESTION,
    COLON,
    AND,
    OR,
    PLUS,
    XOR,
    MOD,
    FLOATING_POINT_LITERAL,

    VAR,
    SEALED,
    PERMITS,
    ABSTRACT,
    ASSERT,
    BOOLEAN,
    BYTE,
    CHAR,
    CLASS,
    DO,
    DOUBLE,
    FINAL,
    FLOAT,
    FOR,
    IF,
    INT,
    LONG,
    NEW,
    PUBLIC,
    PRIVATE,
    PROTECTED,
    PACKAGE,
    RETURN,
    STATIC,
    SHORT,
    SUPER,
    SWITCH,
    ELSE,
    VOLATILE,
    SYNCHRONIZED,
    STRICTFP,
    GOTO,
    CONTINUE,
    BREAK,
    TRANSIENT,
    VOID,
    TRY,
    CATCH,
    FINALLY,
    WHILE,
    CASE,
    DEFAULT,
    CONST,
    ENUM,
    EXTENDS,
    IMPLEMENTS,
    IMPORT,
    INSTANCEOF,
    INTERFACE,
    NATIVE,
    THIS,
    THROW,
    THROWS,
    AT,

    TRUE,
    FALSE,
    NULL,

    /*tie coed*/
    包名,
    类,
    变量,
    常量,
    方法,
    属性写,
    属性读,
    属性,
    事件,
    定义事件,
    结束,
    为,
    真,
    假,
    空,
    本对象,
    父对象,
    如果,
    则,
    否则,
    循环,
    假如,
    是,
    订阅事件,
    返回,
    创建,
    等待,
    跳过循环,
    退出循环,
    整数,
    文本,
    小数,
    单精度小数,
    长整数,
    逻辑型,
    变形体
}
