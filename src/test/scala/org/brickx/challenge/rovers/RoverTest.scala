package org.brickx.challenge.rovers

import org.scalatest.FlatSpec

/**
 * @author nader albert
 * @since  23/02/16.
 */
class RoverTest extends FlatSpec {

  "An empty Set" should "have size 1" in {
    assert(Set.empty.size == 0)
  }

  "A rover at position (3,4,N)" should "move to (3,5,N)" in {
    val movingObject = new MovingObject {
      override def directionMap: Map[Char, (Char, Char)] =
      Map (
        'N' -> ('W','E'),
        'S' -> ('E','w'),
        'E' -> ('N','S'),
        'W' -> ('S','N'))
    }

    movingObject.currentPosition = (3,4, 'N')
    movingObject.move
    assert(movingObject.currentPosition == (3,5,'N'))
  }

  "A rover at position (3,4,N)" should "move to (4,4,N)" in {
    val movingObject = new MovingObject {
      override def directionMap: Map[Char, (Char, Char)] =
        Map (
          'N' -> ('W','E'),
          'S' -> ('E','w'),
          'E' -> ('N','S'),
          'W' -> ('S','N'))
    }

    movingObject.currentPosition = (3,4,'E')
    movingObject.move
    assert(movingObject.currentPosition == (4,4,'E'))
  }
}
