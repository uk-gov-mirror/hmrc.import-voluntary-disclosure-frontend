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

package forms

import base.SpecBase
import mocks.config.MockAppConfig
import models.ContactDetails
import play.api.data.{Form, FormError}

class DeclarantContactDetailsFormProviderSpec extends SpecBase {

  private final val fullName = "fullName"
  private final val email = "email"
  private final val phoneNumber = "phoneNumber"
  private final val exampleName = "First Second"
  private final val exampleEmail = "email@email.com"
  private final val examplePhoneNumber = "0123456789"
  private final val fullNameNonEmptyKey = "declarantContactDetails.error.nameNonEmpty"
  private final val emailNonEmptyKey = "declarantContactDetails.error.emailNonEmpty"
  private final val phoneNumberNonEmptyKey = "declarantContactDetails.error.phoneNumberNonEmpty"
  private final val fullNameTooShortKey = "declarantContactDetails.error.nameMinLength"
  private final val fullNameTooLongKey = "declarantContactDetails.error.nameMaxLength"
  private final val fullNameInvalidCharactersKey = "declarantContactDetails.error.nameAllowableCharacters"
  private final val emailInvalidFormatKey = "declarantContactDetails.error.emailInvalidFormat"
  private final val phoneNumberInvalidFormatKey = "declarantContactDetails.error.phoneNumberInvalidFormat"
  private final val emailRegex = "^[a-zA-Z0-9.!#$%&'*+/=?^_`{|}~-]+@[a-zA-Z0-9](?:[a-zA-Z0-9-]{0,61}[a-zA-Z0-9])?(?:.[a-zA-Z0-9](?:[a-zA-Z0-9-]{0,61}[a-zA-Z0-9])?)*$"
  private final val phoneRegex = "^(\\+)?[0-9 ]{1,15}$"
  private final val nameRegex = "^[a-zA-Z '-]+$"

  def formBuilder(fullName: String = "", email: String = "", phoneNumber: String = ""): Map[String, String] = Map(
    "fullName" -> fullName,
    "email" -> email,
    "phoneNumber" -> phoneNumber
  )

  def formBinder(formValues: Map[String, String] = Map(fullName -> "", email -> "", phoneNumber -> "")): Form[ContactDetails] =
    new DeclarantContactDetailsFormProvider()(MockAppConfig).apply().bind(formValues)

  "Binding a form with invalid data" when {
    "no values provided" should {
      "result in a form with errors" in {
        formBinder().errors mustBe Seq(
          FormError(fullName, fullNameNonEmptyKey),
          FormError(email, emailNonEmptyKey),
          FormError(phoneNumber, phoneNumberNonEmptyKey)
        )
      }
    }

    "full name too short value" should {
      "result in a form with errors" in {
        formBinder(
          formBuilder(
            fullName = "a",
            email = exampleEmail,
            phoneNumber = examplePhoneNumber
          )
        ).errors mustBe Seq(FormError(fullName, fullNameTooShortKey))
      }
    }

    "full name too long value" should {
      "result in a form with errors" in {
        val longString = "aaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaa"
        formBinder(
          formBuilder(
            fullName = longString,
            email = exampleEmail,
            phoneNumber = examplePhoneNumber
          )
        ).errors mustBe Seq(FormError(fullName, fullNameTooLongKey))
      }
    }

    "full name contains invalid characters value" should {
      "result in a form with errors" in {
        formBinder(
          formBuilder(
            fullName = "First Last/",
            email = exampleEmail,
            phoneNumber = examplePhoneNumber
          )
        ).errors mustBe Seq(FormError(fullName, fullNameInvalidCharactersKey, Seq(nameRegex)))
      }
    }

    "email address doesn't adhere to the standard format" should {
      "result in a form with errors" in {
        formBinder(
          formBuilder(
            fullName = exampleName,
            email = "email.com",
            phoneNumber = examplePhoneNumber
          )
        ).errors mustBe Seq(FormError(email, emailInvalidFormatKey, Seq(emailRegex)))
      }
    }

    "phone number doesn't adhere to the standard format" should {
      "result in a form with errors" in {
        formBinder(
          formBuilder(
            fullName = exampleName,
            email = exampleEmail,
            phoneNumber = "++0123456789"
          )
        ).errors mustBe Seq(FormError(phoneNumber, phoneNumberInvalidFormatKey, Seq(phoneRegex)))
      }
    }

  }


  "Binding a form with valid data" should {
    val form = formBinder(formBuilder(fullName = exampleName, email = exampleEmail, phoneNumber = examplePhoneNumber))

    "result in a form with no errors" in {
      form.hasErrors mustBe false
    }

    "generate the correct model" in {
      form.value mustBe Some(ContactDetails(exampleName, exampleEmail, examplePhoneNumber))
    }

  }

  "A form built from a valid model" should {
    "generate the correct mapping" in {
      val model = ContactDetails(exampleName, exampleEmail, examplePhoneNumber)
      val form = new DeclarantContactDetailsFormProvider()(MockAppConfig).apply().fill(model)
      form.data mustBe formBuilder("First Second", "email@email.com", "0123456789")
    }
  }
}
