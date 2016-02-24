package org.brickx.challenge.rovers

import akka.actor._

/**
 * @author nader albert
 * @since  23/02/16.
 */
class Rover(directionMap: Map[Char, (Char,Char)]) extends Actor with ActorLogging {

  var upperRightCoordinates = (0,0)
  val invalidPosition: (Int, Int, Char) = (-1,-1,'_')
  var currentPosition: (Int, Int, Char) = invalidPosition

  /**
   * */
  override def receive: Receive = {
    case  dimension: PlateauDimensions => print(dimension)
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
        case LeftD =>
          steer(LeftD)

        case RightD =>
          steer(RightD)
      }
      sender ! FinalPosition(currentPosition._1, currentPosition._2, currentPosition._3)

    case unexpected => log error "unexpected command during motion " + unexpected
  }

  /**
   * steers rover direction either to the left or to the right.
   * updates the current direction according to incoming command and the current direction
   * exceptions are meant to propagate to the caller
   * @param newDirection, represents the new direction, to which the rover is required to point
   * @throws InvalidPositionException, if the current direction of the rover, cannot be found in the directionMap, i.e. not one of the 4 cardinals
   * @throws InvalidDirectionException, if the next direction to which the rover is meant to point is not Left or Right
   * */
  private def steer(newDirection: DirectionCommand) = {
    println("steering ")
    currentPosition = currentPosition.copy(_3
      = newDirection match {
      case LeftD => directionMap.get(currentPosition._3).fold {
        log error "rover at an invalid position"
        throw new InvalidPositionException
      }(leftCardinal => leftCardinal._1)

      case RightD => directionMap.get(currentPosition._3).fold {
        log error "rover at an invalid position"
        throw new InvalidPositionException
      }(rightCardinal => rightCardinal._2)

      case _ => throw new InvalidDirectionException
    })
  }

  //TODO: validate if the rover is still moving on the plateau, and didn't go outside, following a malicious command
  private def move = {
    println("moving ")

    currentPosition = currentPosition._3 match {
      case 'N' => currentPosition.copy(_2 = currentPosition._2 + 1)
      case 'S' => currentPosition.copy(_2 = currentPosition._2 - 1)

      case 'E' => currentPosition.copy(_1 = currentPosition._1 + 1)
      case 'W' => currentPosition.copy(_1 = currentPosition._1 - 1)

      case _ =>
        log error "invalid current direction"
        currentPosition
    }
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

