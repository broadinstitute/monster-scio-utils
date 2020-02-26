package org.broadinstitute.monster.common

import caseapp.core.help.Help
import caseapp.core.parser.Parser
import com.spotify.scio.ContextAndArgs

/** Entry-point for command-line applications that run scio pipelines. */
abstract class ScioApp[Args: Parser: Help] {
  /** Builder which can convert command-line args into a pipeline. */
  def pipelineBuilder: PipelineBuilder[Args]

  /**
    * Entry-point for scio CLPs.
    *
    * Parses command-line args, constructs a pipeline, runs the
    * pipeline, and waits for the pipeline to finish.
    */
  final def main(args: Array[String]): Unit = {
    val (pipelineContext, parsedArgs) = ContextAndArgs.typed[Args](args)
    pipelineBuilder.buildPipeline(pipelineContext, parsedArgs)
    pipelineContext.run().waitUntilDone()
    ()
  }
}
