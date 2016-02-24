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

  val applicationConfig: Config = config getConfig "rovers"

  val inputSimulatorConfig = applicationConfig getConfig "input_simulator"
  val squadConfig = applicationConfig getConfig "squad_config"

  val defaultInputFile = inputSimulatorConfig getConfig "files" getConfig "command-feed" getString "path"

  val fileName = args.toSeq.find(_ startsWith "-f=" ).fold(defaultInputFile)(file => file.substring(file.indexOf("=") + 1))

  val roversSquad = system.actorOf(RoversSquad.props(squadConfig), name = "squad-guardian")

  //TODO: implement proper logging instead of printing on command line

  Try {
    new BufferedReader(new FileReader(fileName))
  } match {
      case Failure(ex:FileNotFoundException) => println ("file not found")
      case Success(reader) =>

        val lineIterator = reader.lines.iterator

        roversSquad ! DimensionText(lineIterator.next)

        while(lineIterator hasNext) {
          val command = lineIterator.next

          if (command.length > 0 && command.charAt(0) == 'L' || command.charAt(0) == 'M' || command.charAt(0) == 'R')
            roversSquad ! MotionText(command) //PositionText(lineIterator.next)
          else
            roversSquad ! PositionText(command)
        }
    }
}
