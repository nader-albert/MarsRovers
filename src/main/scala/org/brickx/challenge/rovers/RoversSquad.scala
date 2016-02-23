package org.brickx.challenge.rovers

import akka.actor.{ActorRef, Props, ActorLogging, Actor}
import com.typesafe.config.Config

/**
 * @author nader albert
 * @since  23/02/16.
 */
class RoversSquad(squadConfig: Config) extends Actor with ActorLogging {

  var rovers: List[ActorRef] = List.empty[ActorRef]

  val directionMap = Map {
    'N' -> ('W','E')
    'S' -> ('E','w')
    'E' -> ('N','S')
    'W' -> ('N','S')
  }

  initializeSquad

  override def receive: Receive = ???

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

    for (roverIndex <- 0 to roversInSquad) {
      rovers = rovers.::(context.actorOf(Rover.props(directionMap), name = "rover_" + roverIndex))
    }
  }
}

object RoversSquad {
  def props(squadConfig: Config) = Props(classOf[RoversSquad], squadConfig)
}