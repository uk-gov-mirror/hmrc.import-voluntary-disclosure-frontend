package data

import java.time.LocalDate

trait AuthConstants {

  val groupId = "00000000-0000-0000-0000-000000000666"
  val credentialRole = "User"
  val internalId = "id"
  val claimId = "abc123"
  val taxOfficeReference = "1234"
  val taxOfficeNumber = "5678"
  val empref = taxOfficeNumber + "-" + taxOfficeReference
  val companyName = "Test Business Name"
  val credId = "1234567891"
  val policyStartDate = LocalDate.parse("2020-03-01")
  val maxDailyRate: BigDecimal = 83.33
  val crn = "AA123456"
  val sautr = "1111111111"
  val validUtr = "9999999999"
  val ctutr = "2222222222"

}
