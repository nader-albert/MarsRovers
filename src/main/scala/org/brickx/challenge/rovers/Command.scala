package org.brickx.challenge.rovers

/**
 * @author nader albert
 * @since  23/02/16.
 */
trait Command

case class SquadCommand(position: PlateauPosition, sequence: MotionSequence) extends Command

case class PlateauDimension ()

trait MotionCommand extends Command

trait PositioningCommand extends Command {
  val xCoordinate: Int
  val yCoordinate: Int
  val direction: Char
}

case object Move extends MotionCommand

trait Direction extends MotionCommand
case object Left extends Direction
case object Right extends Direction

case class MotionSequence(movements: List[MotionCommand]) extends MotionCommand

case class PlateauPosition(xCoordinate: Int, yCoordinate: Int, direction: Char) extends PositioningCommand


