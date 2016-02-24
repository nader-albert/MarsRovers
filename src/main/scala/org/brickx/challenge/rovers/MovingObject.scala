package org.brickx.challenge.rovers

/**
 * @author nader albert
 * @since  25/02/16.
 */
trait MovingObject {
  val invalidPosition: (Int, Int, Char) = (-1,-1,'_')
  var currentPosition: (Int, Int, Char) = invalidPosition

  def directionMap: Map[Char, (Char,Char)]

  /**
   * steers rover direction either to the left or to the right.
   * updates the current direction according to incoming command and the current direction
   * exceptions are meant to propagate to the caller
   * @param newDirection, represents the new direction, to which the rover is required to point
   * @throws InvalidPositionException, if the current direction of the rover, cannot be found in the directionMap, i.e. not one of the 4 cardinals
   * @throws InvalidDirectionException, if the next direction to which the rover is meant to point is not Left or Right
   * */
  def steer(newDirection: DirectionCommand) =
    currentPosition = currentPosition.copy(_3
      = newDirection match {
      case LeftD => directionMap.get(currentPosition._3).fold {
        println ("invalid position " + currentPosition._3)
        throw new InvalidPositionException
      }(leftCardinal => leftCardinal._1)

      case RightD => directionMap.get(currentPosition._3).fold {
        println ("invalid position " + currentPosition._3)
        throw new InvalidPositionException
      }(rightCardinal => rightCardinal._2)

      case invalidDirection =>
        println ("invalid direction received " + invalidDirection)
        throw new InvalidDirectionException
    })

  //TODO: validate if the rover is still moving on the plateau, and didn't go outside, following a malicious command
  def move =
    currentPosition = currentPosition._3 match {
      case 'N' => currentPosition.copy(_2 = currentPosition._2 + 1)
      case 'S' => currentPosition.copy(_2 = currentPosition._2 - 1)

      case 'E' => currentPosition.copy(_1 = currentPosition._1 + 1)
      case 'W' => currentPosition.copy(_1 = currentPosition._1 - 1)

      case _ =>
        //log error "invalid current direction"
        currentPosition
    }

}
