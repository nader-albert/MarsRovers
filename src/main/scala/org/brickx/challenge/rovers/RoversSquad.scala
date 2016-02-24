package org.brickx.challenge.rovers

import akka.actor.{ActorRef, Props, ActorLogging, Actor}
import akka.util.Timeout
import com.typesafe.config.Config

import scala.concurrent.Await
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
    'S' -> ('E','W'),
    'E' -> ('N','S'),
    'W' -> ('S','N')
  )

  initializeSquad

  /**
   * receives the initial dimension text command and sends it to every rover in the squad
   * */
  override def receive: Receive =   {
    case DimensionText(command) => //println(command + " received by [" + self.path.name + "]")
      Try {
        import Integer._
        val textCoordinates = command.splitAt(2)
        (parseInt(textCoordinates._1.trim), parseInt(textCoordinates._2.trim))
      } match {
        case Failure(nfe: NumberFormatException) => log error "invalid plateau dimension"
        case Success(coordinates) =>
          rovers.foreach(_ ! PlateauDimensions(coordinates._1, coordinates._2))
      }

    case PositionText(command) if 5 == command.length => //println(command + " received by [" + self.path.name + "]")
      Try {
        import Integer._
        val textCoordinates = command.splitAt(3)._1.splitAt(2)
        (parseInt(textCoordinates._1.trim) , parseInt(textCoordinates._2.trim) )
      } match {
        case Failure(nfe: NumberFormatException) => log error "invalid rover initial position"
        case Success(coordinates) =>
          rovers.drop(lastVisitedRoverIndex).head ! Position(coordinates._1, coordinates._2, command.charAt(4))
      }

    case MotionText(command) if command.nonEmpty => //println(command + " received by [ " + self.path.name + "]")

      import scala.concurrent.duration._
      implicit val timeout = Timeout(15 seconds)

      /** blocking, waiting for the rover to reach final position*/
      Await.result(rovers.drop(lastVisitedRoverIndex).head ? toMotionSequence(command), 25 seconds) match {
        case position: FinalPosition =>
          println("rover reached its final position " + position)
          lastVisitedRoverIndex = lastVisitedRoverIndex + 1
      }

    case ResetSquad => context unbecome // to start processing a different sequence of command //TODO: reset the state of all child actors

    case unknown => log error "unknown or invalid command received " + unknown//TODO: throw an exception to its supervisor
  }

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