package scalafix.rewrite

import scala.meta._
import scalafix.Fixed
import scalafix.util.logger

object ProcedureSyntax extends Rewrite {
  override def rewrite(code: Input): Fixed = {
    withParsed(code) { ast =>
      val toPrepend = ast.collect {
        case t: Defn.Def if t.decltpe.exists(_.tokens.isEmpty) =>
          logger.elem(logger.details(t.body), t.decltpe, logger.details(t))
          t.body.tokens.head
      }.toSet
      val sb = new StringBuilder
      ast.tokens.foreach { token =>
        if (toPrepend.contains(token)) {
          logger.elem(token)
          if (sb.lastOption.contains(' ')) {
            sb.deleteCharAt(sb.length - 1)
          }
          sb.append(": Unit = ")
        }
        sb.append(token.syntax)
      }
      val result = sb.toString()
      Fixed.Success(result)
    }
  }
}
