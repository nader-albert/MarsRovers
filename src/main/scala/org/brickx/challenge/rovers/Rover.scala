package org.brickx.challenge.rovers

import akka.actor._

/**
 * @author nader albert
 * @since  23/02/16.
 */
class Rover(directions: Map[Char, (Char,Char)]) extends MovingObject with Actor with ActorLogging {

  var upperRightCoordinates = (0,0)

  override def directionMap: Map[Char, (Char,Char)] = directions

  /**
   * */
  override def receive: Receive = {
    case  dimension: PlateauDimensions => //print(dimension)
      upperRightCoordinates = (dimension.xPos, dimension.yPos)
      context become initializing

    case _ => log error "invalid dimension supported" //TODO: proper failure handling mechanism is yet to be implemented
  }

  private def initializing: Receive = {
    case pos: Position => print(pos)
      val newPosition = calculateInitialPosition(pos)
      if (isInvalidPosition(newPosition))
        log error "initial position received, didn't pass the validation" //TODO Proper failure handling mechanism has to be implemented and more elaborative error message
      else {
        currentPosition = newPosition
        context become operating
      }

    case _ => log error "invalid initial position" //TODO: proper failure handling mechanism is yet to be implemented
  }

  /**
   * enfolds the behavior of the rover while in motion. once the sequence of movements is complete, the rover is expected
   * to continue being in the same state, being ready to receive additional motion commands, until restarted by the supervisor
   * */
  private def operating: Receive = {
    case motion: MotionSequence =>
      print(motion)
      motion.movements.foreach {
        case Move => move
        case LeftD => steer(LeftD)

        case RightD => steer(RightD)
      }
      sender ! FinalPosition(currentPosition._1, currentPosition._2, currentPosition._3)

    case unexpected => log error "unexpected command during motion " + unexpected
  }

  private def calculateInitialPosition(pos: Position): (Int, Int, Char) = {
    if (pos.xCoordinate <= upperRightCoordinates._1 && pos.yCoordinate <= upperRightCoordinates._2)
      if(pos.direction == 'N' || pos.direction == 'E' || pos.direction == 'W' || pos.direction == 'S')
        (pos.xCoordinate,pos.yCoordinate,pos.direction)
      else
        invalidPosition
    else
      invalidPosition
  }

  private def isInvalidPosition(pos: (Int, Int, Char)) = pos.equals(invalidPosition)

  private def print(command: Command) = println(command + " received by [ " + self.path.name + " ]")
}

object Rover {
  def props(directionMap: Map[Char, (Char,Char)]) = Props(classOf[Rover], directionMap)
}

