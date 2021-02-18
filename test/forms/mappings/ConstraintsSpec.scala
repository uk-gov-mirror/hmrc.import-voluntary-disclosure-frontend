/*
 * Copyright 2021 HM Revenue & Customs
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package forms.mappings

import models.{UnderpaymentReason, UnderpaymentReasonValue}
import org.scalatest.{MustMatchers, WordSpec}
import play.api.data.validation.{Invalid, Valid}

class ConstraintsSpec extends WordSpec with MustMatchers with Constraints {


  "firstError" must {

    lazy val first = firstError(maxLength(10, "error.length"), regexp("""^\w+$""", "error.regexp"))

    "return Valid when all constraints pass" in {
      first("foo") mustEqual Valid
    }

    "return Invalid when the first constraint fails" in {
      first("a" * 11) mustEqual Invalid("error.length", 10)
    }

    "return Invalid when the second constraint fails" in {
      first("") mustEqual Invalid("error.regexp", """^\w+$""")
    }
  }

  "minimumValue" must {

    lazy val min = minimumValue(1, "error.min")

    "return Valid for a number greater than the threshold" in {
      min(2) mustEqual Valid
    }

    "return Valid for a number equal to the threshold" in {
      min(1) mustEqual Valid
    }

    "return Invalid for a number below the threshold" in {
      min(0) mustEqual Invalid("error.min", 1)
    }
  }

  "inRange" must {

    lazy val range = inRange(1, 3, "error")

    "return Valid for a number in the range" in {
      range(2) mustEqual Valid
    }

    "return Valid for a number equal to the minimum" in {
      range(1) mustEqual Valid
    }

    "return Valid for a number equal to the maximum" in {
      range(3) mustEqual Valid
    }

    "return Invalid for a number below the minimum" in {
      range(0) mustEqual Invalid("error", 1, 3)
    }

    "return Invalid for a number below the maximum" in {
      range(4) mustEqual Invalid("error", 1, 3)
    }
  }

  "maximumValue" must {

    lazy val maxmimum = maximumValue(1, "error.max")

    "return Valid for a number less than the threshold" in {
      maxmimum(0) mustEqual Valid
    }

    "return Valid for a number equal to the threshold" in {
      maxmimum(1) mustEqual Valid
    }

    "return Invalid for a number above the threshold" in {
      maxmimum(2) mustEqual Invalid("error.max", 1)
    }
  }

  "lengthBetween" must {

    val max = 10
    val min = 1
    lazy val length = lengthBetween(min, max,  "error.lengthBetween")

    "return Valid for a string within the threshold" in {
      length("hello") mustEqual Valid
    }

    "return Valid for a string equal to the threshold" in {
      length("1234567891") mustEqual Valid
    }

    "return Invalid for a string above the threshold" in {
      length("12345678901") mustEqual Invalid("error.lengthBetween", min, max)
    }

    "return Invalid for a string below the threshold" in {
      length("") mustEqual Invalid("error.lengthBetween", min, max)
    }
  }

  "regexp" must {

    "return Valid for an input that matches the expression" in {
      val result = regexp("""^\w+$""", "error.invalid")("foo")
      result mustEqual Valid
    }

    "return Invalid for an input that does not match the expression" in {
      val result = regexp("""^\d+$""", "error.invalid")("foo")
      result mustEqual Invalid("error.invalid", """^\d+$""")
    }
  }

  "maxLength" must {

    lazy val max = maxLength(10, "error.length")

    "return Valid for a string shorter than the allowed length" in {
      max("a" * 9) mustEqual Valid
    }

    "return Valid for an empty string" in {
      max("") mustEqual Valid
    }

    "return Valid for a string equal to the allowed length" in {
      max("a" * 10) mustEqual Valid
    }

    "return Invalid for a string longer than the allowed length" in {
      max("a" * 11) mustEqual Invalid("error.length", 10)
    }
  }

  "uniqueEntry" must {

    val values = Seq("a", "b", "c", "d", "e")

    for(idx <- 1 to values.length) {
      if(idx == 3) {
        s"return valid for a value thats in the list but at the current idx $idx" in {
          uniqueEntry(values, 3, "error")("c") mustBe Valid
        }
      } else {
        s"return invalid for a value thats in the list at idx $idx" in {
          uniqueEntry(values, 1, "error")("c") mustBe Invalid("error", values)
        }
      }
    }

    for(idx <- 1 to values.length) {

      s"return valid for with idx $idx for a value not in the values sequence" in {
        uniqueEntry(values, idx, "error")("f") mustBe Valid
      }
    }
  }

  "nonEmptySet" must {

    lazy val nonEmpty = nonEmptySet("error")

    "return Valid when supplied with a Set of values" in {
      nonEmpty(Set(1,2)) mustEqual Valid
    }

    "return Invalid when an empty set is supplied" in {
      nonEmpty(Set()) mustEqual Invalid("error")
    }
  }

}
