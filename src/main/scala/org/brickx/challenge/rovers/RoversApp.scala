package org.brickx.challenge.rovers

import java.io.{BufferedReader, FileReader, FileNotFoundException}

import akka.actor.ActorSystem
import com.typesafe.config.{Config, ConfigFactory}

import scala.util.{Success, Failure, Try}

/**
 * @author nader albert
 * @since  23/02/16.
 */
object RoversApp extends App {

  implicit val system = ActorSystem("RoverSystem")

  val config = ConfigFactory load

  val applicationConfig: Config = config getConfig "rovers" getConfig "input_simulator"

  val defaultOutputFile = applicationConfig getConfig "files" getConfig "command-feed" getString "path"

  val fileName = args.toSeq.find(_ startsWith "-f=" ).fold(defaultOutputFile)(file => file.substring(file.indexOf("=") + 1))

  val squadConfig = applicationConfig getConfig "squad-config"

  val roversSquad = system.actorOf(RoversSquad.props(squadConfig), name = "publishing-guardian")

  Try {
    new BufferedReader(new FileReader(fileName))
  } match {
      case Failure(ex:FileNotFoundException) => println ("file not found")
      case Success(reader) =>

        val lineIterator = reader.lines.iterator

        if (lineIterator hasNext)
          lineIterator next

        while(lineIterator hasNext){

          // every couple of lines, send
          //roversSquad ! SquadCommand()
        }
    }
}
