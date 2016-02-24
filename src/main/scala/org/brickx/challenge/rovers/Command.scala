package org.brickx.challenge.rovers

/**
 * @author nader albert
 * @since  23/02/16.
 */
trait Command

case object ResetSquad extends Command

trait TextCommand extends Command {
  val command: String
}
case class DimensionText(command: String) extends TextCommand
case class PositionText(command: String) extends TextCommand
case class MotionText(command: String) extends TextCommand

//case class SquadCommand(position: Position, sequence: MotionSequence) extends Command

case class PlateauDimensions(xPos: Int, yPos: Int) extends Command// 55

trait PositionCommand extends Command {
  val xCoordinate: Int
  val yCoordinate: Int
  val direction: Char
}

case class Position(xCoordinate: Int, yCoordinate: Int, direction: Char) extends PositionCommand  //12N

trait MotionCommand extends Command
case object Move extends MotionCommand

trait DirectionCommand extends MotionCommand
case object LeftD extends DirectionCommand
case object RightD extends DirectionCommand

case class MotionSequence(movements: List[MotionCommand]) extends MotionCommand



