package org.brickx.challenge.rovers

import akka.actor.{ActorRef, Props, ActorLogging, Actor}
import akka.util.Timeout
import com.typesafe.config.Config

import scala.concurrent.ExecutionContext
import scala.util.{Success, Failure, Try}

import akka.pattern.ask

/**
 * @author nader albert
 * @since  23/02/16.
 */
class RoversSquad(squadConfig: Config) extends Actor with ActorLogging {

  var rovers: List[ActorRef] = List.empty[ActorRef]
  var lastVisitedRoverIndex = 0

  val directionMap = Map (
    'N' -> ('W','E'),
    'S' -> ('E','w'),
    'E' -> ('N','S'),
    'W' -> ('S','N')
  )

  initializeSquad

  /**
   * receives the initial dimension text command and sends it to every rover in the squad
   * */
  override def receive: Receive =   {
    case DimensionText(command) => println(command + " received by [" + self.path.name + "]")
      Try {
        import Integer._
        val textCoordinates = command.splitAt(2)
        (parseInt(textCoordinates._1.trim), parseInt(textCoordinates._2.trim))
      } match {
        case Failure(nfe: NumberFormatException) => log error "invalid plateau dimension"
        case Success(coordinates) =>
          rovers.foreach(_ ! PlateauDimensions(coordinates._1, coordinates._2))
          //context.become(pendingInitialPosition)
      }

    case PositionText(command) if 5 == command.length => println(command + " received by [" + self.path.name + "]")
      Try {
        import Integer._
        val textCoordinates = command.splitAt(3)._1.splitAt(2)
        (parseInt(textCoordinates._1.trim) , parseInt(textCoordinates._2.trim) )
      } match {
        case Failure(nfe: NumberFormatException) => log error "invalid rover initial position"
        case Success(coordinates) =>
          rovers.drop(lastVisitedRoverIndex).head ! Position(coordinates._1, coordinates._2, command.charAt(4))
          //context.become(pendingMotionCommand)
      }

    case MotionText(command) if command.nonEmpty => println(command + " received by [ " + self.path.name + "]")

      import scala.concurrent.duration._
      import ExecutionContext.Implicits.global
      implicit val timeout = Timeout(15 seconds)

      rovers.drop(lastVisitedRoverIndex).head ? toMotionSequence(command) onComplete {
        case Success(finalPosition: FinalPosition) => println("rover " + sender.path.name + " reached its final position " + finalPosition)
          lastVisitedRoverIndex = lastVisitedRoverIndex + 1

        case Failure(exception) => println("rover " + sender.path.name + " failed in its mission !" + exception)//TODO: proper error handling should be implemented here
      }

    /*case finalPosition: FinalPosition => println("final position received " +  finalPosition)
      context unbecome //only now another rover in the squad can take over
  */

    case ResetSquad => context unbecome // to start processing a different sequence of command //TODO: reset the state of all child actors

    case unknown => log error "unknown or invalid command received " + unknown//TODO: throw an exception to its supervisor
  }

  /**
   * receives only initial position command for a specific rover in the squad. according to the lastVisitedRoverIndex state,
   * a rover is picked from the squad and chosen to be the one to be in charge of the following sequence of motion commands
   * */
  /*private def pendingInitialPosition: Receive = {
    case PositionText(command) if 5 == command.length => println("position msg received " + command)

      Try {
        import Integer._
        val textCoordinates = command.splitAt(3)._1.splitAt(2)
        (parseInt(textCoordinates._1.trim) , parseInt(textCoordinates._2.trim) )
      } match {
        case Failure(nfe: NumberFormatException) => log error "invalid rover initial position"
        case Success(coordinates) =>
          rovers.drop(lastVisitedRoverIndex).head ! Position(coordinates._1, coordinates._2, command.charAt(3))
          context.become(pendingMotionCommand)
      }

    case ResetSquad => context unbecome // to start processing a different sequence of command //TODO: reset the state of all child actors

    case unknown => log error "unknown or invalid command received " + unknown//TODO: throw an exception to its supervisor
  }*/

  /**
   * receives only motion commands meant to control the rover currently in operation
   * */
  /*private def pendingMotionCommand: Receive = {
    case MotionText(command) if command.nonEmpty => println("motionText received " + command)
      rovers.drop(lastVisitedRoverIndex).head ! command
      lastVisitedRoverIndex += 1

    case finalPosition: FinalPosition => println("final position received " +  finalPosition)
      context unbecome //only now another rover in the squad can take over

    case unknown => log error "unknown or invalid command received " + unknown //TODO: throw an exception to its supervisor
  }*/

  private def toMotionSequence(motionText: String): MotionSequence =
    MotionSequence(motionText.toList.map {
      case 'L' => LeftD
      case 'R' => RightD
      case 'M' => Move
    })

  private def initializeSquad = {
    val roversInSquad: Int =
      try {
        Integer.parseInt(squadConfig getString "number")
      } catch {
        case ne: NumberFormatException =>
          log error "configuration problem: invalid squad number "
          0
        case _:Throwable => 0
      }

    for (roverIndex <- 0 to roversInSquad -1) {
      rovers = context.actorOf(Rover.props(directionMap), name = "rover_" + roverIndex) :: rovers
    }
  }
}

object RoversSquad {
  def props(squadConfig: Config) = Props(classOf[RoversSquad], squadConfig)
}