package org.broadinstitute.monster.common

import com.spotify.scio.ScioContext
import com.spotify.scio.coders.Coder
import com.spotify.scio.io.ClosedTap
import com.spotify.scio.values.SCollection
import io.circe.{Encoder, Printer}
import io.circe.syntax._
import org.broadinstitute.monster.common.msg.JsonParser
import ujson.StringRenderer
import upack.Msg

/** Utilities for reading/writing messages from/to storage. */
object StorageIO {
  /**
    * JSON formatter used when writing outputs.
    *
    * Attempts to make our outputs as compact as possible by dropping whitespace and
    * null values.
    */
  val circePrinter: Printer = Printer.noSpaces.copy(dropNullValues = true)

  /**
    * Read JSON-list files matching a glob pattern into a scio collection
    * for processing.
    *
    * Each line of each input file must contain a full message object, or else
    * downstream processing will break.
    *
    * TODO: Can we enforce object-ness here, so things explode at the earliest
    * possible point?
    */
  def readJsonLists(
    context: ScioContext,
    description: String,
    filePattern: String
  )(implicit coder: Coder[Msg]): SCollection[Msg] =
    context
      .withName(s"Read '$description' messages from '$filePattern'")
      .textFile(filePattern)
      .transform(s"Parse '$description' messages")(_.map(JsonParser.parseEncodedJson))

  /**
    * Write unmodeled messages to storage for use by downstream components.
    *
    * Each message will be written to a single line in a part-file under the given
    * output prefix.
    */
  def writeJsonLists(
    messages: SCollection[Msg],
    description: String,
    outputPrefix: String
  ): ClosedTap[String] =
    messages
      .transform(s"Stringify '$description' messages")(
        _.map(upack.transform(_, StringRenderer()).toString)
      )
      .withName(s"Write '$description' messages to '$outputPrefix'")
      .saveAsTextFile(outputPrefix, suffix = ".json")

  /**
    * Write modeled messages to storage for use by downstream components.
    *
    * Each message will be written to a single line in a part-file under the given
    * output prefix.
    */
  def writeJsonLists[M: Encoder](
    messages: SCollection[M],
    description: String,
    outputPrefix: String
  ): ClosedTap[String] =
    messages
      .transform(s"Stringify '$description' messages")(
        _.map(_.asJson.printWith(circePrinter))
      )
      .withName(s"Write '$description' messages to '$outputPrefix'")
      .saveAsTextFile(outputPrefix, suffix = ".json")
}
