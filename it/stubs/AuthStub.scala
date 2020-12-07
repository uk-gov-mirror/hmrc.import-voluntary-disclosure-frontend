package stubs

import com.github.tomakehurst.wiremock.stubbing.StubMapping
import config.EnrolmentKeys
import data.AuthConstants
import play.api.http.Status.{OK, UNAUTHORIZED}
import play.api.libs.json.Json
import support.WireMockMethods

object AuthStub extends WireMockMethods with AuthConstants {

  private val authoriseUri = "/auth/authorise"

  def authorised(): StubMapping =
    when(method = POST, uri = authoriseUri)
      .thenReturn(status = OK, body = Json.obj(
        "affinityGroup" -> "Organisation",
        "allEnrolments" -> Json.arr(
          Json.obj(
            "key" -> EnrolmentKeys.ePAYE,
            "identifiers" -> Json.arr(
              Json.obj(
                "key" -> EnrolmentKeys.taxOfficeReference,
                "value" -> taxOfficeReference,
                "state" -> EnrolmentKeys.activated
              ),
              Json.obj(
                "key" -> EnrolmentKeys.taxOfficeNumber,
                "value" -> taxOfficeNumber,
                "state" -> EnrolmentKeys.activated
              )
            )
          )
        ),
        "internalId" -> internalId,
        "groupIdentifier" -> groupId,
        "credentialRole" -> credentialRole,
        "optionalCredentials" -> Json.obj(
          "providerId" -> credId,
          "providerType" -> "credType"
        )
      )
      )

  def unauthorised(): StubMapping =
    when(method = POST, uri = authoriseUri).thenReturn(status = UNAUTHORIZED)
}

