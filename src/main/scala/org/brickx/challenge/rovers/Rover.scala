package org.brickx.challenge.rovers

import akka.actor._

/**
 * @author nader albert
 * @since  23/02/16.
 */
class Rover(directionMap: Map[Char, (Char,Char)]) extends Actor with ActorLogging {

  var currentPosition: (Int, Int, Char) = (-1,-1, '-')

  override def receive: Receive = {
    case motion: MotionSequence =>
      motion.movements.foreach {
        case _:Direction => steer _
        case Move => move
      }
      sender ! FinalPosition(currentPosition._1, currentPosition._2, currentPosition._3)

    case pos: PlateauPosition =>
      currentPosition = (pos.xCoordinate, pos.yCoordinate, pos.direction)

    case _ => log error "command not supported"
  }

  /**
   * steers rover direction either to the left or to the right.
   * updates the current direction according to incoming command and the current direction
   * exceptions are meant to propagate to the caller
   * @param newDirection, represents the new direction, to which the rover is required to point
   * @throws InvalidPositionException, if the current direction of the rover, cannot be found in the directionMap, i.e. not one of the 4 cardinals
   * @throws InvalidDirectionException, if the next direction to which the rover is meant to point is not Left or Right
   * */
  private def steer(newDirection: MotionCommand) =
    currentPosition = currentPosition.copy(_3
      = newDirection match {
        case Left => directionMap.get(currentPosition._3).fold {
          log error "rover at an invalid position"
          throw new InvalidPositionException
        } (leftCardinal => leftCardinal._1)

        case Right => directionMap.get(currentPosition._3).fold {
          log error "rover at an invalid position"
          throw new InvalidPositionException
        } (rightCardinal => rightCardinal._2)

        case _ => throw new InvalidDirectionException
    })

  private def move = {

  }
}

object Rover {
  def props(directionMap: Map[Char, (Char,Char)]) = Props(classOf[Rover], directionMap)
}

