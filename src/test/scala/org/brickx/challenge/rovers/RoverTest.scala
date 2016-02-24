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

  it should "produce NoSuchElementException when head is invoked" in {
    intercept[NoSuchElementException] {
      Set.empty.head
    }
  }
}
