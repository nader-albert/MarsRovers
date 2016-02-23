package org.brickx.challenge.rovers

/**
 * @author nader albert
 * @since  23/02/16.
 */
trait Event

case class FinalPosition(xCoordinate: Int, yCoordinate: Int, direction: Char) extends Event
