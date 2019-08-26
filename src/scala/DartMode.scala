//
// Scaled Dart Mode - a Scaled major mode for editing Dart code
// http://github.com/scaled/dart-mode/blob/master/LICENSE

package scaled.code

import scaled._
import scaled.Matcher
import scaled.grammar._
import scaled.util.Paragrapher

@Plugin(tag="textmate-grammar")
class DartGrammarPlugin extends GrammarPlugin {
  import CodeConfig._

  override def grammars = Map("source.dart" -> "Dart.ndf")

  override def effacers = List(
    effacer("comment.line", commentStyle),
    effacer("comment.block", docStyle),
    effacer("constant", constantStyle),
    effacer("invalid", invalidStyle),
    effacer("keyword", keywordStyle),
    effacer("string", stringStyle),

    effacer("entity.name.package", moduleStyle),
    effacer("class.name", typeStyle),
    effacer("entity.name.type", typeStyle),
    effacer("entity.other.inherited-class", typeStyle),
    effacer("entity.name.function.annotation", constantStyle),
    effacer("entity.name.val-declaration", variableStyle),

    effacer("function.name", functionStyle),
    effacer("meta.method.dart", functionStyle),

    effacer("storage.modifier.import", moduleStyle),
    effacer("storage.modifier", keywordStyle),
    effacer("storage.type", typeStyle),
    effacer("support.type", constantStyle),

    // effacer("variable.import", typeStyle),
    // effacer("variable.language", constantStyle),
    effacer("variable", variableStyle)
  )

  override def syntaxers = List(
    syntaxer("comment.line", Syntax.LineComment),
    syntaxer("comment.block", Syntax.DocComment),
    syntaxer("constant", Syntax.OtherLiteral),
    syntaxer("string", Syntax.StringLiteral)
  )
}

@Major(name="dart",
       tags=Array("code", "project", "dart"),
       pats=Array(".*\\.dart"),
       ints=Array("dart"),
       desc="A major editing mode for the Dart language.")
class DartMode (env :Env) extends GrammarCodeMode(env) {
  import CodeConfig._
  import scaled.util.Chars._

  override def langScope = "source.dart"

  override protected def createIndenter = new BlockIndenter(config, Seq(
    // bump extends/implements in two indentation levels
    BlockIndenter.adjustIndentWhenMatchStart(Matcher.regexp("(extends|implements)\\b"), 2),
    // align changed method calls under their dot
    new BlockIndenter.AlignUnderDotRule(),
    // handle javadoc and block comments
    new BlockIndenter.BlockCommentRule(),
    // handle indenting switch statements properly
    new BlockIndenter.SwitchRule(),
    // handle continued statements, with some special sauce for : after case
    new BlockIndenter.CLikeContStmtRule()
  ));

  override val commenter = new Commenter() {
    override def linePrefix  = "//"
    override def blockOpen   = "/*"
    override def blockPrefix = "*"
    override def blockClose  = "*/"
    override def docOpen     = "///"
    override def docPrefix   = "///"
  }
}
