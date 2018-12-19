package com.deadtoadroad.skidmark.text

import com.deadtoadroad.skidmark.text.Slugifier.slugify
import org.scalatest.FunSuite

class SlugifierSuite extends FunSuite {
  test("slugify separates title case") {
    assert(slugify("titleCase") === "title-case")
  }

  test("slugify does not separate acronyms") {
    assert(slugify("what is a RDS") === "what-is-a-rds")
  }

  test("slugify removes punctuation") {
    assert(slugify("shan't shouldn't wouldn't") === "shant-shouldnt-wouldnt")
  }

  test("slugify replaces non-word characters") {
    val nonWordCharacters = """~`!@#$%^&*()_-+={[}]|\:;"<,>.?/ """
    val actual = nonWordCharacters.map("a" + _ + "a").map(slugify)
    assert(actual.length === 32)
    actual.foreach(a => assert(a === "a-a"))
  }

  test("slugify trims dashes") {
    assert(slugify("-dashes-") === "dashes")
    assert(slugify("--dashes--") === "dashes")
  }

  test("slugify deduplicates dashes") {
    assert(slugify("lots--of---dashes  and   spaces") === "lots-of-dashes-and-spaces")
  }

  test("slugify lowers case") {
    assert(slugify("you SHALL not PASS") === "you-shall-not-pass")
  }
}
