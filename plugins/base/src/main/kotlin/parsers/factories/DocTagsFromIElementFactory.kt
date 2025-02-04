package org.jetbrains.dokka.base.parsers.factories

import org.jetbrains.dokka.model.doc.*
import org.intellij.markdown.IElementType
import org.intellij.markdown.MarkdownElementTypes
import org.intellij.markdown.MarkdownTokenTypes
import org.intellij.markdown.flavours.gfm.GFMElementTypes
import org.intellij.markdown.flavours.gfm.GFMTokenTypes
import org.jetbrains.dokka.base.translators.parseWithNormalisedSpaces
import org.jetbrains.dokka.links.DRI
import org.jetbrains.dokka.model.doc.DocTag.Companion.contentTypeParam
import java.lang.NullPointerException

object DocTagsFromIElementFactory {

    @Suppress("IMPLICIT_CAST_TO_ANY")
    fun getInstance(type: IElementType, children: List<DocTag> = emptyList(), params: Map<String, String> = emptyMap(), body: String? = null, dri: DRI? = null, keepFormatting: Boolean = false) =
        when(type) {
            MarkdownElementTypes.SHORT_REFERENCE_LINK,
            MarkdownElementTypes.FULL_REFERENCE_LINK,
            MarkdownElementTypes.INLINE_LINK            -> if(dri == null) A(children, params) else DocumentationLink(dri, children, params)
            MarkdownElementTypes.STRONG                 -> B(children, params)
            MarkdownElementTypes.BLOCK_QUOTE            -> BlockQuote(children, params)
            MarkdownElementTypes.CODE_SPAN              -> CodeInline(children, params)
            MarkdownElementTypes.CODE_BLOCK,
            MarkdownElementTypes.CODE_FENCE             -> CodeBlock(children, params)
            MarkdownElementTypes.ATX_1                  -> H1(children, params)
            MarkdownElementTypes.ATX_2                  -> H2(children, params)
            MarkdownElementTypes.ATX_3                  -> H3(children, params)
            MarkdownElementTypes.ATX_4                  -> H4(children, params)
            MarkdownElementTypes.ATX_5                  -> H5(children, params)
            MarkdownElementTypes.ATX_6                  -> H6(children, params)
            MarkdownElementTypes.EMPH                   -> I(children, params)
            MarkdownElementTypes.IMAGE                  -> Img(children, params)
            MarkdownElementTypes.LIST_ITEM              -> Li(children, params)
            MarkdownElementTypes.ORDERED_LIST           -> Ol(children, params)
            MarkdownElementTypes.UNORDERED_LIST         -> Ul(children, params)
            MarkdownElementTypes.PARAGRAPH              -> P(children, params)
            MarkdownTokenTypes.TEXT -> if (keepFormatting) Text(
                body.orEmpty(),
                children,
                params
            ) else body?.parseWithNormalisedSpaces(renderWhiteCharactersAsSpaces = false).orEmpty()
            MarkdownTokenTypes.HORIZONTAL_RULE          -> HorizontalRule
            MarkdownTokenTypes.HARD_LINE_BREAK          -> Br
            GFMElementTypes.STRIKETHROUGH               -> Strikethrough(children, params)
            GFMElementTypes.TABLE                       -> Table(children, params)
            GFMElementTypes.HEADER                      -> Th(children, params)
            GFMElementTypes.ROW                         -> Tr(children, params)
            GFMTokenTypes.CELL                          -> Td(children, params)
            MarkdownElementTypes.MARKDOWN_FILE          -> CustomDocTag(children, params, MarkdownElementTypes.MARKDOWN_FILE.name)
            MarkdownElementTypes.HTML_BLOCK,
            MarkdownTokenTypes.HTML_TAG,
            MarkdownTokenTypes.HTML_BLOCK_CONTENT       -> Text(body.orEmpty(), params = params + contentTypeParam("html"))
            else                                        -> CustomDocTag(children, params, type.name)
        }.let {
            @Suppress("UNCHECKED_CAST")
            when (it) {
                is List<*> -> it as List<DocTag>
                else -> listOf(it as DocTag)
            }
        }
}
